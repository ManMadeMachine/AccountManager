package com.accountmanager;

import android.util.Log;

import java.io.Serializable;
import java.lang.annotation.Documented;

/**
 * Created by Aperture Science on 5.2.2016.
 */
public class Account implements Serializable{
    //Constants
    public static final String TAG = Account.class.getName();
    private static final String COMMA = ",";

    //Strings for account owner and number
    private String owner;
    private String number;

    /**
     * Constructor for Account
     * @param owner owner of the account
     * @param number account number
     */
    public Account(String owner, String number){
        this.owner = owner;
        this.setNumber(number);
    }

    /**
     * Create a new account object from a CSV string.
     *
     * @param csvString Comma-separated-value string holding the owner and number information
     */
    public Account(String csvString){
        //Trim newlines and leading spaces from the string
        csvString = csvString.trim();

        //Create a temporary array for the string parts
        String[] parts = new String[2];

        //Split the string from the comma
        parts = csvString.split(COMMA);

        //Assign the owner and number values from the array
        this.owner = parts[0];
        this.setNumber(parts[1]);
    }

    /**
     * Get method for owner.
     *
     * @return owner string
     */
    public String getOwner(){
        return this.owner;
    }

    /**
     * Set method for owner
     * @param owner new owner
     */
    public void setOwner(String owner){
        this.owner = owner;
    }

    /**
     * Get method for account number
     * @return account number as a string
     */
    public String getNumber(){
        return this.number;
    }

    /**
     * Sets the account number. Divides the number into groups of four characters before assigning it
     * to the number variable.
     *
     * @param number account number
     */
    public void setNumber(String number){
        //Trim all the existing formatting whitespace to make it easier to format the new number
        number = number.replaceAll(" ", "");

        //Temporary string builder for making the new number string
        StringBuilder tempString = new StringBuilder();

        //Split the number into the groups of four characters
        for (int i = 0; i < number.length(); ++i) {
            //Check the need for a whitespace and add it if necessary
            if ((i > 0) && (i % 4 == 0)){
                tempString.append(' ');
            }

            //Always add the current character to the temporary string
            tempString.append(number.charAt(i));
        }

        //Replace the number string with the temporary string and change the
        number = tempString.toString();
        number = number.toUpperCase();

        this.number = number;
    }

    /**
     * Method for getting the account information as a comma-separated-value string.
     * Used when an account is saved to a file.
     *
     * @return account information as a CSV string
     */
    public String toCSVString(){
        return this.owner + COMMA + this.number + System.getProperty("line.separator");
    }

    /**
     * Method for getting account as a string. Overwritten so that owner and number
     * are written to separate lines. Used by MainActivity's list view.
     *
     * @return string information of the account, on two lines
     */
    @Override
    public String toString(){
        return this.owner + System.getProperty("line.separator") + this.number;
    }
}
