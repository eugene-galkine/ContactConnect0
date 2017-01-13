package com.eg.contactconnect0;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Eugene Galkine on 1/12/2017.
 */

public abstract class IContact
{
    protected TextView dataTextView;
    protected CheckBox checkBox;

    public IContact (TextView dataField, CheckBox box)
    {
        this.dataTextView = dataField;
        this.checkBox = box;
    }

    public abstract void editData();

    public abstract String getQRData();
}
