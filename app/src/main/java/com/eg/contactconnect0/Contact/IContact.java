package com.eg.contactconnect0.Contact;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.eg.contactconnect0.Client;
import com.eg.contactconnect0.MainActivity;

/**
 * Created by Eugene Galkine on 1/12/2017.
 */

public abstract class IContact
{
    protected TextView dataTextView;
    protected CheckBox checkBox;
    protected int inputType = 0;
    protected MainActivity main;
    protected String className;

    public IContact (TextView dataField, CheckBox box, MainActivity ma)
    {
        this.dataTextView = dataField;
        this.checkBox = box;
        this.main = ma;
    }

    void dataWasChanged(String t)
    {
        Client.instance.contactInfo(className, t);
    }

    public void editData(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit");

        // Set up the input
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | inputType);
        input.setText(dataTextView.getText().toString());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataTextView.setText(input.getText().toString());
                dataWasChanged(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        input.requestFocus();
    }

    public String getQRData()
    {
        return checkBox.isChecked() ? className+":"+dataTextView.getText().toString() : "";
    }

    public void setData(final String data)
    {
        main.runOnUiThread(new Runnable() {
            public void run() {
                dataTextView.setText(data);
            }
        });
    }

    public void checkBoxClick(String name)
    {
        PreferenceManager.getDefaultSharedPreferences(main).edit()
                .putBoolean(name, checkBox.isChecked()).apply();
    }
}