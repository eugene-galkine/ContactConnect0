package com.eg.contactconnect0.Contact;

import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eg.contactconnect0.Client;
import com.eg.contactconnect0.Contact.IContact;
import com.eg.contactconnect0.MainActivity;
import com.eg.contactconnect0.R;

/**
 * Created by Eugene Galkine on 1/12/2017.
 */

public class PhoneConnection extends IContact
{
    private TelephonyManager tMgr;


    public PhoneConnection(TextView dataField, CheckBox box, Button b,  TelephonyManager tMgr, MainActivity ma)
    {
        super(dataField, box, b, ma, "Phone");
        this.tMgr = tMgr;
        this.inputType = InputType.TYPE_CLASS_PHONE;

        tryFillForm();
    }

    public void tryFillForm()
    {
        if (!main.mayReadPhone())
            return;

        //fill in phone number text field
        String phoneNum = tMgr.getLine1Number();
        if (phoneNum != null)
        {
            this.dataTextView.setText(phoneNum);
            //Client.instance.contactInfo("Phone", phoneNum);
        } else
        {
            //no phone num
            this.dataTextView.setText(main.getString(R.string.error_no_num));
            this.checkBox.setChecked(false);
        }
    }
}
