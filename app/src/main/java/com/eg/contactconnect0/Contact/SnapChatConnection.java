package com.eg.contactconnect0.Contact;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eg.contactconnect0.MainActivity;

/**
 * Created by Eugene Galkine on 1/18/2017.
 */

public class SnapChatConnection extends IContact
{
    public SnapChatConnection(TextView dataField, CheckBox box, Button b, MainActivity ma)
    {
        super(dataField, box, b, ma, "SnapChat");
    }
}
