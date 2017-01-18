package com.eg.contactconnect0.Contact;

import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eg.contactconnect0.Client;
import com.eg.contactconnect0.Contact.IContact;
import com.eg.contactconnect0.MainActivity;

/**
 * Created by Eugene Galkine on 1/14/2017.
 */

public class EmailConnection extends IContact
{
    public EmailConnection(TextView dataField, CheckBox box, Button b, MainActivity ma)
    {
        super(dataField, box, b, ma, "Email");

        dataField.setText(Client.instance.getUser());
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
    }
}
