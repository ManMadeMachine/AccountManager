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
        this.number = number;
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
        this.number = parts[1];
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
     *
     * @param number
     */
    public void setNumber(String number){
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
