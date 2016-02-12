package com.accountmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditAccountActivity extends AppCompatActivity {

    public static final String TAG = EditAccountActivity.class.getName();
    public static final String EDIT_SUCCESS = "edit_success"; //TODO: onko hyvä vai olisiko UPDATED parempi?

    //Strings to hold the account data from the intent that started this activity
    private String owner;
    private String number;

    String newOwner;
    String newNumber;

    //The input fields of the view
    private EditText ownerInput;
    private EditText numberInput;

    //Error message string for error information
    private String errorMessage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the account data from the intent
        Intent receicedIntent = getIntent();
        this.owner = receicedIntent.getExtras().getString(MainActivity.OWNER_MESSAGE);
        this.number = receicedIntent.getExtras().getString(MainActivity.NUMBER_MESSAGE);

        //Get the main layoyt of this activity
        LinearLayout main_layout = (LinearLayout)findViewById(R.id.content_holder);

        //Get the layout inflater service
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        //Inflate the external account input view
        View input_view = inflater.inflate(R.layout.account_input, main_layout, false);

        //Set the "Save" -button text to the appropriate string
        Button saveButton = (Button)input_view.findViewById(R.id.save_button);
        saveButton.setText(R.string.save);

        ownerInput = (EditText)input_view.findViewById(R.id.owner);
        numberInput = (EditText)input_view.findViewById(R.id.number);

        //Set the EditText-objects inputs to the received account data
        ownerInput.setText(owner);
        numberInput.setText(number);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Update the owner and number variables to the user inputs
                newOwner = ownerInput.getText().toString();
                newNumber = numberInput.getText().toString();

                //First check if some information was actually updated
                if (newOwner.equals(owner) && newNumber.equals(number)){
                    finishWithResult(false);
                }
                else{
                    //Information was updated, validate and finish with the updated information
                    validateAccount();
                    finishWithResult(true);
                }
            }
        });

        //Attach the account input view to the main layout
        main_layout.addView(input_view);

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

                Log.i(TAG, "New nr: " + newNumber);
                success = true;
            }
        }

        return success;
    }

    private void saveAccount(){

    }

    private void finishWithResult(boolean updated){
        //Intent for returning data to the main activity
        Intent data = new Intent();
        data.putExtra(EDIT_SUCCESS, updated);  //Was the edit done successfully

        //Check if the account information was modified and add the new account information
        if (updated) {
            data.putExtra("id", getIntent().getExtras().getInt("id")); //TODO: korvaa "id" jollain vakiolla
            data.putExtra(MainActivity.OWNER_MESSAGE, newOwner);
            data.putExtra(MainActivity.NUMBER_MESSAGE, newNumber);
        }

        //Set the result
        setResult(RESULT_OK, data);
        finish();
    }
}
