package com.accountmanager;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Created by Aperture Science on 13.2.2016.
 */
public class Utilities {
    //An error message for validation errors
    public static String validationErrorMessage = null;

    //Resources, used to obtain string resources
    private static Resources resources = null;

    /**
     * Static method for setting the program resources. Needs to be called before any other
     * Utilities-class function is called. Otherwise, errors will be thrown.
     *
     * @param res Resources object
     */
    public static void setResources(Resources res){
        //Check if the resource file was not null and update if necessary
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
     * @return boolean value true if account information is valid, false otherwise
     * @throws Exception if the resources of this static class are null
     */
    public static boolean isAccountValid(String owner, String number) throws Exception{
        boolean success = false;

        //Resource arrays
        String[] countries;
        int[] numberLengths;

        //Get the resource arrays needed for validation. Throw an exception if necessary.
        if (resources != null) {
            countries = resources.getStringArray(R.array.countryCodes);
            numberLengths = resources.getIntArray(R.array.numberLengths);
        }
        else{
            throw new Exception("Resources reference of Utilities class was null! Is Utilities.setResources() function called?");
        }

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
            if (!success) {
                validationErrorMessage = resources.getString(R.string.invalid_number_error);
            }
        }
        else{
            validationErrorMessage = resources.getString(R.string.empty_input_error);
        }

        return success;
    }
}
