package com.accountmanager;

import android.content.Intent;
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

    public final static String SAVE_SUCCESS = "save_success";

    private String newOwner;
    private String newNumber;

    //String for a possible error message. Used in an error-toast to give better
    //information about the error in the inputs
    private String errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button createButton = (Button)findViewById(R.id.create_account);
        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Get the user input and store them
                newOwner = ((EditText)findViewById(R.id.new_owner)).getText().toString();
                newNumber = ((EditText)findViewById(R.id.new_number)).getText().toString();

                //Try to validate and save the new account.
                if (validateAccount()){
                    Log.i(TAG, "Input was correct!");

                    //Save the new account into the accounts file. If the saving is
                    //successful, we can close this activity and return to the main activity
                    if(saveAccount()){
                        finishWithResult();
                    }
                    else{
                        //If the saving failed, inform the user
                        //TODO: Toast tms., mikä ilmottaa virheestä
                        Log.e(TAG, "Jotain meni pieleen uutta tiliä tallennettaessa!!");
                    }
                }
                else{
                    Log.i(TAG, "Input was NOT correct!");
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean validateAccount(){
        boolean success = false;

        //Null and empty string validation
        if (newOwner != null && !newOwner.isEmpty() && newNumber != null && !newNumber.isEmpty()){
            //TODO: Validate number string's IBAN-format (e.g. FI-prefix and 18 digits)

            //Trim every whitespace character from the number string
            newNumber = newNumber.replaceAll(" ", "");
            int length = newNumber.length();

            //Check number length (Finnish IBAN length: 18 characters)
            if (length != 18){
                errorMessage = "Incorrect account number length!";
            }
            else {
                StringBuilder tempString = new StringBuilder();

                //Length was correct, split the number into groups of four, for readability
                for (int i = 0; i < newNumber.length(); ++i) {
                    //Check the need for a whitespace and add it if necessary
                    if ((i > 0) && (i % 4 == 0)){
                        tempString.append(' ');
                    }

                    //Always add the current character to the temporary string
                    tempString.append(newNumber.charAt(i));
                }

                //Replace the number string with the temporary string
                newNumber = tempString.toString();

                Log.i(TAG, newNumber);
                success = true;
            }
        }

        return success;
    }

    /**
     * Saves the new account to the end of the accounts file
     *
     */
    private boolean saveAccount(){
        boolean success = false;

        //Validation was done earlier, so we can create a new Account object from the field data
        Account newAccount = new Account(newOwner, newNumber);

        //Try to append to the accounts.txt file

        FileOutputStream output = null;

        try{
            output = openFileOutput("accounts.txt", MODE_APPEND);

            //append
            output.write(newAccount.toCSVString().getBytes());

            output.close();

            //If the writing is successful, return true
            success = true;
        }
        catch(FileNotFoundException fileNotFoundEx){
            fileNotFoundEx.printStackTrace();
        }
        catch(IOException ex){
            Log.i(TAG, "Error while saving new account!");
            ex.printStackTrace();
        }

        return success;
    }


    private void finishWithResult(){
        Intent data = new Intent();
        data.putExtra("saved_account", true);
        setResult(RESULT_OK, data);
        finish();
    }
}
