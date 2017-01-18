package com.eg.contactconnect0.Contact;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eg.contactconnect0.MainActivity;

/**
 * Created by Eugene Galkine on 1/18/2017.
 */

public class InstagramConnection extends IContact
{
    public InstagramConnection(TextView dataField, CheckBox box, Button button, MainActivity ma)
    {
        super(dataField, box, button, ma, "Instagram");
    }
}
