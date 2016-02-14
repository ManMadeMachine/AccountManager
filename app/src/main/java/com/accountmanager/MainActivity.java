package com.accountmanager;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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
    //Constants
    public static final String TAG = MainActivity.class.getName();
    public static final int ADD_ACCOUNT_REQUEST = 1;
    public static final int EDIT_ACCOUNT_REQUEST = 2;

    //Account edit intent message keys
    public static final String OWNER_MESSAGE = "owner";
    public static final String NUMBER_MESSAGE = "number";
    public static final String ACCOUNTS_FILENAME = "accounts.txt";
    public static final String ROW_INDEX = "row_index";

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

        //Set the Utility class resources
        Utilities.setResources(getResources());
    }

    /**
     * Loads previously saved accounts into the accounts list
     *
     * @return boolean returns true if accounts were read from the accounts file, false otherwise
     */
    private boolean loadAccounts(){
        //First, check if there was a saved accounts file with a size of > 0 characters
        File f = new File(this.getFilesDir(), ACCOUNTS_FILENAME);

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
                ex.printStackTrace();
            }

            return true;
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

        //Check the menu item id and act accordingly
        if (id == R.id.add_account){
            startAddActivity(item);
        }
        else if (id == R.id.exit) {
            Log.i(TAG, "Exiting..");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for starting the account addition activity
     * @param item the menu item that was pressed
     */
    public void startAddActivity(MenuItem item){
        Intent addAccountIntent = new Intent(this, AddAccountActivity.class);
        startActivityForResult(addAccountIntent, ADD_ACCOUNT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //Check which request we're responding to
        if (requestCode == ADD_ACCOUNT_REQUEST){
            //Check if the request was successful and the account was created!
            if (resultCode == RESULT_OK && data.getExtras().getBoolean(AddAccountActivity.ACCOUNT_CREATED)){
                //"Reload" the accounts...
                accounts.clear();
                loadAccounts();
                listAdapter.notifyDataSetChanged();
            }
        }
        else if (requestCode == EDIT_ACCOUNT_REQUEST){
            if (resultCode == RESULT_OK){
                //Check if the account was updated and apply the updates
                if(data.getExtras().getBoolean(EditAccountActivity.EDIT_SUCCESS)){
                    int id = data.getExtras().getInt(MainActivity.ROW_INDEX);
                    String owner = data.getExtras().getString(OWNER_MESSAGE);
                    String number = data.getExtras().getString(NUMBER_MESSAGE);

                    applyAccountUpdates(id, owner, number);
                }
            }
        }
    }

    /**
     * Method for applying the updated account information.
     * @param id row index in the list
     * @param owner new owner string
     * @param number new number string
     */
    private void applyAccountUpdates(int id, String owner, String number){
        //Update the accounts list
        accounts.get(id).setOwner(owner);
        accounts.get(id).setNumber(number);

        //Basically just write the whole list again..
        try{
            FileOutputStream output = openFileOutput(ACCOUNTS_FILENAME, Context.MODE_PRIVATE);

            for (int i = 0; i < accounts.size(); ++i){
                output.write(accounts.get(i).toCSVString().getBytes());
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        //Remember to notify the list adapter!!
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Called when a list view item has been selected with a long click.
     *
     * @param menu context menu
     * @param view view
     * @param menuInfo menu information
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);

        //inflate the menu
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
                startEditActivity(info.id);
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

    /**
     * Method for starting the account editing activity
     * @param id row index of the item selected
     */
    private void startEditActivity(long id){
        //Create an intent and start the account editing activity
        Intent editIntent = new Intent(this, EditAccountActivity.class);

        //Add the account information to the intent
        editIntent.putExtra(ROW_INDEX, (int)id);
        editIntent.putExtra(OWNER_MESSAGE, accounts.get((int)id).getOwner());
        editIntent.putExtra(NUMBER_MESSAGE, accounts.get((int) id).getNumber());
        startActivityForResult(editIntent, EDIT_ACCOUNT_REQUEST);
    }

    /**
     * Method that copies the selected account number to the system clipboard
     *
     * @param id row index of the selected account
     */
    private void copyAccountNumber(long id){
        //Get a handle to the clipboard service
        ClipboardManager clipboard = (ClipboardManager)getSystemService(this.CLIPBOARD_SERVICE);

        //Copy the account number to a ClipData object
        ClipData clip = ClipData.newPlainText("account number", accounts.get((int)id).getNumber());

        //Put the clip object to the clipboard
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Method for deleting an account.
     *
     * @param id selected account row index
     */
    private void deleteAccount(long id){
        //Open the accounts file for reading and for writing
        try{
            //Create an OutputStream to write to the destination file
            FileOutputStream output = openFileOutput(ACCOUNTS_FILENAME, Context.MODE_PRIVATE);

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
            ex.printStackTrace();
        }
    }
}
