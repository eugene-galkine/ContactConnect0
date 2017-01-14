package com.eg.contactconnect0;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by Eugene Galkine on 1/14/2017.
 */

public class InputContact
{
    String name, phone, email;

    public InputContact()
    {

    }

    public void addDataType(String type, String data)
    {
        if (type.equals("Name"))
        {
            name = data;
        } else if (type.equals("Phone"))
        {
            phone = data;
        } else if (type.equals("Email"))
        {
            email = data;
        }
    }

    public void addContact(MainActivity context)
    {
        //add the contact
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        contactIntent
                .putExtra(ContactsContract.Intents.Insert.NAME, name)
                .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, email);

        context.startActivityForResult(contactIntent, 1);
    }
}
