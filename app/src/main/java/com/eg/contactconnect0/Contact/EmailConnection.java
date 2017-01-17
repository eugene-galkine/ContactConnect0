package com.eg.contactconnect0.Contact;

import android.preference.PreferenceManager;
import android.text.InputType;
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
    public EmailConnection(TextView dataField, CheckBox box, MainActivity ma)
    {
        super(dataField, box, ma);

        dataField.setText(Client.instance.getUser());
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;

        boolean checked = PreferenceManager.getDefaultSharedPreferences(main)
                .getBoolean("emailCheckBox", true);
        checkBox.setChecked(checked);

        className = "Email";
    }
}
