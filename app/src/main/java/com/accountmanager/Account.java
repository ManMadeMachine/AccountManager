package com.accountmanager;

import android.util.Log;

import java.io.Serializable;
import java.lang.annotation.Documented;

/**
 * Created by Aperture Science on 5.2.2016.
 */
public class Account implements Serializable{
    public static final String TAG = Account.class.getName();
    private static final String COMMA = ",";
    private String owner;
    private String number;

    /**
     *
     * @param owner
     * @param number
     */
    public Account(String owner, String number){
        this.owner = owner;
        this.setNumber(number);
    }

    /**
     * Create a new account object from a CSV string.
     * @param csvString
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
     *
     * @return
     */
    public String getOwner(){
        return this.owner;
    }

    /**
     *
     * @param owner
     */
    public void setOwner(String owner){
        this.owner = owner;
    }

    /**
     *
     * @return
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

    public String toCSVString(){
        return this.owner + COMMA + this.number + System.getProperty("line.separator");
    }
    /**
     *
     */
    @Override
    public String toString(){
        return this.owner + "\n" + this.number;
    }
}
