package com.eg.contactconnect0.Contact;

import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eg.contactconnect0.Client;
import com.eg.contactconnect0.Contact.IContact;
import com.eg.contactconnect0.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

/**
 * Created by boxes on 1/16/2017.
 */

public class FacebookConnection extends IContact
{
    public FacebookConnection(final TextView dataField, CheckBox box, LoginButton b, final MainActivity ma)
    {
        super(dataField, box, b, ma, "Facebook");

        b.registerCallback(MainActivity.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                //LoginManager.getInstance().logInWithReadPermissions(ma, Arrays.asList("public_profile"));
                //System.out.println("logined to facebook");
                Profile profile = Profile.getCurrentProfile();

                if (profile != null)
                {
                    dataWasChanged(profile.getName());
                    dataField.setText(profile.getName());
                }
            }

            @Override
            public void onCancel()
            {
                // App code
            }

            @Override
            public void onError(FacebookException e)
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
