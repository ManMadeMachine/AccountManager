package com.accountmanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddAccountActivity extends AppCompatActivity {
    public final static String TAG = AddAccountActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        Button createButton = (Button)findViewById(R.id.create_account);
        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (validateInput()){
                    Log.i(TAG, "Input was correct!");

                    //Save the new account into the accounts file
                    saveAccount();
                }
                else{
                    Log.i(TAG, "Input was NOT correct!");
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean validateInput(){
        EditText ownerText = (EditText)findViewById(R.id.new_owner);
        EditText numberText = (EditText)findViewById(R.id.new_number);

        String owner = ownerText.getText().toString();
        String number = numberText.getText().toString();

        boolean isValid = false;

        //Null and empty string validation
        if (owner != null && !owner.isEmpty() && number != null && !number.isEmpty()){
            //TODO: Validate number string's IBAN-format (e.g. FI-prefix and 18 digits)
            //IBAN-format validation
            int length = 0;

            //Calculate the ACTUAL digits (ignore whitespace)
            for (int i = 0; i < number.length(); ++i){
                //If the character is not a whitespace character
                if (!(number.charAt(i) == ' ')){
                    length++;
                }
            }

            isValid = (length == 18);
        }

        return isValid;
    }

    /**
     * Saves the new account to the end of the accounts file
     *
     */

    private void saveAccount(){
        //Get the account data from the edittext-fields
        String owner = ((EditText)findViewById(R.id.new_owner)).getText().toString();
        String number = ((EditText)findViewById(R.id.new_number)).getText().toString();

        //Validation was done earlier, so we can create a new Account object from the field data
        Account newAccount = new Account(owner, number);

        //Try to append to the accounts.txt file

        FileOutputStream output = null;

        try{
            output = openFileOutput("accounts.txt", MODE_APPEND);

            //append
            output.write(newAccount.toCSVString().getBytes());

            output.close();
        }
        catch(FileNotFoundException fileNotFoundEx){
            fileNotFoundEx.printStackTrace();
        }
        catch(IOException ex){
            Log.i(TAG, "Error while saving new account!");
            ex.printStackTrace();
        }
    }



}
