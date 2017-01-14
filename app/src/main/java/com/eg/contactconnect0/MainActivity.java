package com.eg.contactconnect0;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_READ_SMS = 1;

    private PhoneConnection phoneConnection;
    private NameConnection nameConnection;
    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        //set up contact methods
        phoneConnection = new PhoneConnection(
                (TextView)findViewById(R.id.phoneTextView),
                (CheckBox)findViewById(R.id.phoneCheckBox),
                (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE),
                this);
        nameConnection = new NameConnection(
                (TextView)findViewById(R.id.nameTextView),
                (CheckBox)findViewById(R.id.nameCheckBox),
                this);

        Client.instance.setMainActivity(this);
    }

    public void phoneEditButton(View view)
    {
        phoneConnection.editData(this);
    }

    public void nameEditButton(View view)
    {
        nameConnection.editData(this);
    }

    public void qrScanButton(View view)
    {
        //open the qr code scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a ContactConnect QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    public void qrWriteButton(View view)
    {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try
        {
            //TODO don't do qr code manually
            BitMatrix bitMatrix = multiFormatWriter.encode(nameConnection.getQRData()+ ":" + phoneConnection.getQRData(), BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ((ImageView) findViewById(R.id.qrimage)).setImageBitmap(bitmap);
            viewFlipper.showNext();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void qrBackButton(View view)
    {
        viewFlipper.showPrevious();
    }

    private void processQRData(String text)
    {
        //parse the QR code message
        try
        {
            String buffer = text;
            InputContact inputContact = new InputContact();

            do
            {
                //get data type and data
                String type = buffer.substring(0, buffer.indexOf(":"));
                buffer = buffer.substring(buffer.indexOf(":") + 1);

                String data;
                if (buffer.contains(":"))
                {
                    data = buffer.substring(0, buffer.indexOf(":"));
                    buffer = buffer.substring(buffer.indexOf(":") + 1);
                } else
                    data = buffer;

                inputContact.addDataType(type, data);
            } while (buffer.contains(":"));

            //save the contact
            inputContact.addContact(this);
        } catch (Exception e)
        {
            System.out.println("ERROR PARSING QR CODE");
            //e.printStackTrace();
        }
    }

    public void recieveData(String text)
    {
        //parse user data
        try
        {
            System.out.println("USER_DATA - " + text);
            String buffer = text;

            do
            {
                //get data type and data
                String type = buffer.substring(0, buffer.indexOf(":"));
                buffer = buffer.substring(buffer.indexOf(":") + 1);

                String data;
                if (buffer.contains(":"))
                {
                    data = buffer.substring(0, buffer.indexOf(":"));
                    buffer = buffer.substring(buffer.indexOf(":") + 1);
                } else
                    data = buffer;

                //TODO add new contact types to this
                if (type.equals("Name"))
                    nameConnection.setData(data);
                else if (type.equals("Phone"))
                    phoneConnection.setData(data);
            } while (buffer.contains(":"));
        } catch (Exception e)
        {
            System.out.println("ERROR PARSING DATA FROM SERVER");
            //e.printStackTrace();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //called when code scanning done
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null)
        {
            if(result.getContents() == null)
            {
                System.out.println("Cancelled scan");
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            } else {
                System.out.println("Scanned: " + result.getContents());
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                processQRData(result.getContents());
            }
        } else
        {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
