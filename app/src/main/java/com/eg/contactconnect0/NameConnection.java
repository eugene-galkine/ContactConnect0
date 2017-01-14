package com.eg.contactconnect0;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.widget.CheckBox;
import android.widget.TextView;

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

        tryFillForm();
    }

    @Override
    void dataWasChanged(String t)
    {
        Client.instance.contactInfo("Name", t);
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

    @Override
    public String getQRData()
    {
        return checkBox.isChecked() ? "Name:"+dataTextView.getText().toString() : "";
    }
}
