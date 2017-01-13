package com.eg.contactconnect0;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_READ_SMS = 1;

    private PhoneConnection phoneConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up contact methods
        phoneConnection = new PhoneConnection(
                (TextView)findViewById(R.id.phoneTextView),
                (CheckBox)findViewById(R.id.phoneCheckBox),
                (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE),
                this);
    }

    public void phoneEditButton(View view)
    {
        phoneConnection.editData();
    }

    public boolean mayReadPhone()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            return true;
        }
        if (checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_SMS))
        {
            Snackbar.make(findViewById(R.id.phoneTextView), R.string.sms_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener()
                    {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v)
                        {
                            requestPermissions(new String[]{READ_SMS}, REQUEST_READ_SMS);
                        }
                    });
        } else
        {
            requestPermissions(new String[]{READ_SMS}, REQUEST_READ_SMS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_READ_SMS)
        {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                phoneConnection.tryFillForm();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        //pause client
        Client.instance.close();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //resume client
        Client.instance.resume();
    }
}
