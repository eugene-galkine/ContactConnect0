package com.eg.contactconnect0.Contact;

import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eg.contactconnect0.Client;
import com.eg.contactconnect0.Contact.IContact;
import com.eg.contactconnect0.MainActivity;

/**
 * Created by Eugene Galkine on 1/13/2017.
 */

public class NameConnection extends IContact
{
    public NameConnection(TextView dataField, CheckBox box, MainActivity ma)
    {
        super(dataField, box, ma);

        boolean checked = PreferenceManager.getDefaultSharedPreferences(main)
                .getBoolean("nameCheckBox", true);
        checkBox.setChecked(checked);

        className = "Name";

        tryFillForm();
    }

    public void tryFillForm()
    {
        //try to get the owner's name
        Cursor c = main.getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        if (c.moveToFirst())
        {
            dataTextView.setText(c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME)));
            //Client.instance.contactInfo("Name", dataTextView.getText().toString());
            c.close();
        }
    }
}
