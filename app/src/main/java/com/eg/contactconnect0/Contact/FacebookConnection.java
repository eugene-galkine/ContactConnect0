package com.eg.contactconnect0.Contact;

import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eg.contactconnect0.Client;
import com.eg.contactconnect0.Contact.IContact;
import com.eg.contactconnect0.MainActivity;

/**
 * Created by boxes on 1/16/2017.
 */

public class FacebookConnection extends IContact
{
    public FacebookConnection(TextView dataField, CheckBox box, Button b, MainActivity ma)
    {
        super(dataField, box, b, ma, "Facebook");
    }
}
