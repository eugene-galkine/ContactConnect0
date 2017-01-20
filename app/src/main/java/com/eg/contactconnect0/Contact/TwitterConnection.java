package com.eg.contactconnect0.Contact;

import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eg.contactconnect0.Client;
import com.eg.contactconnect0.MainActivity;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Created by boxes on 1/16/2017.
 */

public class TwitterConnection extends IContact
{
    public TwitterConnection(TextView dataField, CheckBox box, TwitterLoginButton b, MainActivity ma)
    {
        super(dataField, box, b, ma, "Twitter");

        b.setCallback(new Callback<TwitterSession>()
        {
            @Override
            public void success(Result<TwitterSession> result)
            {
                TwitterSession session = result.data;

                //update server and text box
                dataTextView.setText("@" + session.getUserName());
                dataWasChanged("@" + session.getUserName());
            }

            @Override
            public void failure(TwitterException e)
            {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void setButtonListener(Button button)
    {
        //do nothing
    }
}
