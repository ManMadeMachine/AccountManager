package com.accountmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditAccountActivity extends AppCompatActivity {

    public static final String TAG = EditAccountActivity.class.getName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the main layoyt of this activity
        LinearLayout main_layout = (LinearLayout)findViewById(R.id.content_layout);

        //Get the layout inflater service
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        //Inflate the external account input view
        View input_view = inflater.inflate(R.layout.account_input, main_layout, false);

        //TODO: aseta omistajan ja tilinumeron inputteihin muokattavat arvot. Ne tulee Intentin extroilla.
        //Set the "Save" -button text to the appropriate string
        Button saveButton = (Button)input_view.findViewById(R.id.save_button);
        saveButton.setText(R.string.save);

        //Attach the account input view to the main layout
        main_layout.addView(input_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
