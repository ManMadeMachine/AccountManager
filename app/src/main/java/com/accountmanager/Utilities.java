package com.accountmanager;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Created by Aperture Science on 13.2.2016.
 */
public class Utilities {
    public static String validationErrorMessage = null;

    private static Resources resources = null;

    public static void setResources(Resources res){
        if(resources == null){
            resources = res;
        }
    }

    /**
     * * Method that validates the given input when a new account is being created. Checks that the given
     * input was not empty or null and also checks that the account number was 18 characters long.
     * The method also formats the number string into the common four-digit-groups-format.
     *
     * @param owner account owner
     * @param number account number
     * @param countries string array of country codes
     * @param numberLengths integer array of country-specific account number lengths
     * @return boolean value true if account information is valid, false otherwise
     * @throws Exception if the resources of this static class are null
     */
    public static boolean isAccountValid(String owner, String number, String[] countries, int[] numberLengths) throws Exception{
        boolean success = false;

        //Null and empty string validation
        if (owner != null && !owner.isEmpty() && number != null && !number.isEmpty()){
            //Trim every whitespace character from the number string
            number = number.replaceAll(" ", "");
            int length = number.length();

            //Read the country code (two first characters)
            String countryCode = number.substring(0, 2);

            countryCode = countryCode.toUpperCase();

            //Check the country code first
            for (int i = 0; i < countries.length; ++i){
                //If the country code is found and number length matches the found country's IBAN
                //number length, the number was valid
                if (countryCode.equals(countries[i]) && length == numberLengths[i]){
                    success = true;
                    break;
                }
            }

            //If needed, assign an error message
            if (!success){
                //Check the resources for null
                if (resources != null)
                    validationErrorMessage = resources.getString(R.string.invalid_number_error);
                else
                    throw new Exception("Resources were null!");
            }
        }
        else{
            if (resources != null)
                validationErrorMessage = resources.getString(R.string.empty_input_error); //TODO: Korvaa kaikki tälläset resurssistringeillä
            else
                throw new Exception("Resources were null!");
        }

        return success;
    }
}
