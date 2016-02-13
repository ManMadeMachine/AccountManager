package com.accountmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddAccountActivity extends AppCompatActivity {
    public final static String TAG = AddAccountActivity.class.getName();

    //key string for the returned result
    public final static String ACCOUNT_CREATED = "account_created";

    //New account owner and number strings
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

        //Get the main layoyt of this activity
        LinearLayout main_layout = (LinearLayout)findViewById(R.id.content_holder);

        //Get the layout inflater service
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        //Inflate the external account input view
        View input_view = inflater.inflate(R.layout.account_input, main_layout, false);

        //Set the "Save" -button text to the appropriate string
        Button createButton = (Button)input_view.findViewById(R.id.save_button);
        createButton.setText(R.string.create);

        //Set the onClick listener for the create button
        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Get the user input and store them
                newOwner = ((EditText)findViewById(R.id.owner)).getText().toString();
                newNumber = ((EditText)findViewById(R.id.number)).getText().toString();

                //Try to validate and save the new account.
                if (validateAccount() && saveAccount()){
                    //Save the new account into the accounts file. If the saving is
                    //successful, we can close this activity and return to the main activity
                    finishWithResult(true);
                }
                else{
                    //If the saving failed, inform the user
                    Toast errorToast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });

        //Attach the account input view to the main layout
        main_layout.addView(input_view);

        /*Button createButton = (Button)findViewById(R.id.create_account);

        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Method that validates the given input when a new account is being created. Checks that the given
     * input was not empty or null and also checks that the account number was 18 characters long.
     *
     * @return boolean returns true if input was valid, false otherwise.
     */
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

                success = true;
            }
        }
        else{
            errorMessage = "Fill both fields!"; //TODO: Korvaa kaikki tälläset resurssistringeillä
        }

        return success;
    }

    /**
     * Saves the new account to the end of the accounts file
     * @return boolean returns true if account was saved successfully
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

    /**
     * Method for finishing this activity with a result. The result
     */
    private void finishWithResult(boolean created){
        Intent data = new Intent();
        data.putExtra(ACCOUNT_CREATED, created);
        setResult(RESULT_OK, data);
        finish();
    }
}
