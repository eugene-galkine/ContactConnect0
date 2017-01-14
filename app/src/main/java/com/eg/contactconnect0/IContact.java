package com.eg.contactconnect0;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Eugene Galkine on 1/12/2017.
 */

public abstract class IContact
{
    protected TextView dataTextView;
    protected CheckBox checkBox;
    protected int inputType = 0;
    protected MainActivity main;

    public IContact (TextView dataField, CheckBox box, MainActivity ma)
    {
        this.dataTextView = dataField;
        this.checkBox = box;
        this.main = ma;
    }

    abstract void dataWasChanged(String t);

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

    public abstract String getQRData();
}
