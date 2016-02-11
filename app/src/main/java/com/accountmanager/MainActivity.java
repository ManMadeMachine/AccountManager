package com.accountmanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    public static final int ADD_ACCOUNT_REQUEST = 1;

    //Array adapter for the account listing view
    ArrayAdapter<Account> listAdapter;

    //View for the account listing
    ListView accountListView;

    //List of accounts visible in the main view
    public ArrayList<Account> accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        //Test writing and reading
        //writeTestFile();
        //readTestFile();

        //Initialize the accounts List
        accounts = new ArrayList<Account>();

        //Load previously saved accounts into the accounts list
        boolean loaded = loadAccounts();

        //Create the adapter for the list view
        listAdapter = new ArrayAdapter<Account>(this, android.R.layout.simple_list_item_1, accounts);

        //Create the list view and assign the adapter to it
        accountListView = (ListView)findViewById(R.id.account_list);
        accountListView.setAdapter(listAdapter);
        accountListView.setEmptyView(findViewById(R.id.empty_item));

        //Register the list view for context menu
        registerForContextMenu(accountListView);

        Log.i(TAG, "Loaded accounts from memory: " + loaded);
    }

    /**
     * Loads previously saved accounts into the accounts list
     *
     * @return boolean returns if accounts were read from the accounts file
     */
    private boolean loadAccounts(){
        //First, check if there was a saved accounts file with a size of > 0 characters
        File f = new File(this.getFilesDir(), "accounts.txt");

        //If the file was empty, we don't need to load anything
        if (f.length() == 0){
            return false;
        }
        else{
            //There was something in the file, read accounts into the list
            //First create the BufferedReader needed to read the file contents
            try{
                BufferedReader reader = new BufferedReader(new FileReader(f));

                String line = null;

                //Read as long as there are lines
                while((line = reader.readLine()) != null){
                    //Add a new account object to the list.
                    accounts.add(new Account(line));
                }
            }
            catch(Exception ex){
                Log.i(TAG, "Error reading saved accounts!");
                ex.printStackTrace();
            }

            return true;
        }
    }

    /**
     * Reloads the accounts from memory and notifies the listview adapter
     */
    private void reloadAccounts(){

    }

    //Writes a test CSV file to the internal memory
    //Deletes the file after it has been created, so that the memory
    //won't get cluttered with test CSV files
    private void writeTestFile(){
        //Test key and value to write in the file
        String key = "Name";
        String value = "Value";

        try {
            //Create an OutputStream to the file
            FileOutputStream output = openFileOutput("accounts.txt", Context.MODE_PRIVATE);

            //Create a CSV string to be written to the file
            String csv = key + "," + value + "\n";
            String csv2 = key + "2," + value +"2\n";

            String account = "Pekka peräaho,123456789\n";

            //Write something to the file
            output.write(csv.getBytes());
            output.write(csv2.getBytes());
            output.write(account.getBytes());
            output.close();

            Log.i(TAG, "Wrote to file");

        }
        catch (Exception exception){
            Log.i(TAG, "Error writing to file!");
            exception.printStackTrace();
        }
    }

    //Test method for reading the written storage file
    private void readTestFile(){
        try{
            File f = new File(this.getFilesDir(), "accounts.txt");

            //Test file size
            Log.i(TAG, "File size when reading: " + f.length());

            //Create a buffered reader to read from the file
            BufferedReader buffer = new BufferedReader(new FileReader(f));

            String line = null;

            //Read as long as there are lines to be read
            while((line = buffer.readLine()) != null){
                //Print the lines
                //TODO: Add an account to the accounts list
                Log.i("Buffer:", line);
            }

            //Remember to close the buffer
            buffer.close();

            //f.delete();
        }
        catch(Exception ex){
            Log.i(TAG, "Error reading file!");
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //TODO: Vaihda tähän lopetusnäppäimen toiminnallisuus eli lopetus
        if (id == R.id.add_account) {
            Log.i(TAG, "Adding account..");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchAddActivity(MenuItem item){
        Intent addAccountIntent = new Intent(this, AddAccountActivity.class);
        startActivityForResult(addAccountIntent, ADD_ACCOUNT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //Check which request we're responding to
        if (requestCode == ADD_ACCOUNT_REQUEST){
            //Check if the request was successful and the account was saved!
            if (resultCode == RESULT_OK && data.getExtras().getBoolean("saved_account")){
                Log.i(TAG, "Finished adding account with result: OK!");
                //TODO: Tarkista tän järkevyys..
                //"Reload" the accounts...
                accounts.clear();
                loadAccounts();
                listAdapter.notifyDataSetChanged();
            }
            else{
                Log.i(TAG, "Finished adding account with other than OK!");
            }
        }
    }

    /**
     * Called when a list view item has been selected with a long click.
     *
     * @param menu
     * @param view
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        //Save the menu item info
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        //Check which menu item was selected
        switch(item.getItemId()){
            case R.id.edit_account:
                Log.i(TAG, "TODO: Add account editing!");
                return true;
            case R.id.copy_account_number:
                copyAccountNumber(info.id);
                return true;
            case R.id.delete_account:
                deleteAccount(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void copyAccountNumber(long id){
        Log.i(TAG, "Copying: " + accounts.get((int)id).getNumber());

        //Get a handle to the clipboard service
        ClipboardManager clipboard = (ClipboardManager)getSystemService(this.CLIPBOARD_SERVICE);

        //Copy the account number to a ClipData object
        ClipData clip = ClipData.newPlainText("account number", accounts.get((int)id).getNumber());

        //Put the clip object to the clipboard
        clipboard.setPrimaryClip(clip);

        Log.i(TAG, "Success!!");
    }

    private void deleteAccount(long id){
        Log.i(TAG, "Deleting account from row index: " + id);

        //Open the accounts file for reading and for writing
        try{
            //Create an OutputStream to write to the destination file
            FileOutputStream output = openFileOutput("accounts.txt", Context.MODE_PRIVATE);

            //First, delete the account from the accounts list. Index is the id this
            //method gets as a parameter. Conversion in this case isn't a practical problem,
            //since probably no-one knows over four billion people and thein IBAN's...
            accounts.remove((int)id);

            //Write the accounts to the file
            for(int i = 0; i < accounts.size(); ++i){
                output.write(accounts.get(i).toCSVString().getBytes());

            }

            //Notify the account listing manager about the content change
            listAdapter.notifyDataSetChanged();

            output.close();
        }
        catch(Exception ex){
            Log.e(TAG, "Error while deleting account from row index: " + id);
            ex.printStackTrace();
        }
    }
}
