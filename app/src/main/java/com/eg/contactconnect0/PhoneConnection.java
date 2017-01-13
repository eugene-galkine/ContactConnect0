package com.eg.contactconnect0;

import android.telephony.TelephonyManager;
import android.widget.CheckBox;
import android.widget.TextView;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by Eugene Galkine on 1/12/2017.
 */

public class PhoneConnection extends IContact
{
    private TelephonyManager tMgr;
    private MainActivity main;
    private String phoneNum;


    public PhoneConnection(TextView dataField, CheckBox box, TelephonyManager tMgr, MainActivity ma)
    {
        super(dataField, box);
        this.tMgr = tMgr;
        this.main = ma;

        tryFillForm();
    }

    public void tryFillForm()
    {
        if (!main.mayReadPhone())
            return;

        //fill in phone number text field
        phoneNum = null;
        phoneNum = tMgr.getLine1Number();
        if (phoneNum != null)
        {
            this.dataTextView.setText(phoneNum);
            Client.instance.contactInfo("Phone", phoneNum);
        }
        else
        {
            //no phone num
            this.dataTextView.setText(main.getString(R.string.error_no_num));
            this.checkBox.setChecked(false);
        }
    }

    @Override
    public void editData()
    {

    }

    @Override
    public String getQRData()
    {
        return "Phone:"+phoneNum;
    }
}
