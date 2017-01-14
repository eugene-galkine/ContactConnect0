package com.eg.contactconnect0;

import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.TextView;

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
    }

    @Override
    void dataWasChanged(String t)
    {
        Client.instance.contactInfo("Email", t);
    }

    @Override
    public String getQRData()
    {
        return checkBox.isChecked() ? "Email:"+dataTextView.getText().toString() : "";
    }
}
