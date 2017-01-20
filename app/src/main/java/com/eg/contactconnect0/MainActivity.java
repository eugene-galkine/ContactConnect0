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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.eg.contactconnect0.Contact.EmailConnection;
import com.eg.contactconnect0.Contact.FacebookConnection;
import com.eg.contactconnect0.Contact.InstagramConnection;
import com.eg.contactconnect0.Contact.NameConnection;
import com.eg.contactconnect0.Contact.PhoneConnection;
import com.eg.contactconnect0.Contact.TwitterConnection;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_READ_SMS = 1;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "m0F47crvysltsE0WjTCi7fGrl";
    private static final String TWITTER_SECRET = "k7jlcY2hzCVGqx6AI7mpbWIyPYY47iCNdHxNNncxIgVf5NTjBx";

    public static CallbackManager callbackManager;

    private PhoneConnection phoneConnection;
    private NameConnection nameConnection;
    private EmailConnection emailConnection;
    private FacebookConnection facebookConnection;
    private TwitterConnection twitterConnection;
    private InstagramConnection instagramConnection;
    private ViewFlipper viewFlipper;
    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_main);


        callbackManager = CallbackManager.Factory.create();

        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        //twitter login button
        loginButton = (TwitterLoginButton) findViewById(R.id.twitterEditButton);
        loginButton.setTextSize(12);
        loginButton.setText("Log in");
        loginButton.setLines(1);

        //set up contact methods
        phoneConnection = new PhoneConnection(
                (TextView)findViewById(R.id.phoneTextView),
                (CheckBox)findViewById(R.id.phoneCheckBox),
                (Button)findViewById(R.id.phoneEditButton),
                (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE),
                this);
        nameConnection = new NameConnection(
                (TextView)findViewById(R.id.nameTextView),
                (CheckBox)findViewById(R.id.nameCheckBox),
                (Button)findViewById(R.id.nameEditButton),
                this);
        emailConnection = new EmailConnection(
                (TextView)findViewById(R.id.emailTextView),
                (CheckBox)findViewById(R.id.emailCheckBox),
                (Button)findViewById(R.id.emailEditButton),
                this);
        facebookConnection = new FacebookConnection(
                (TextView)findViewById(R.id.facebookTextView),
                (CheckBox)findViewById(R.id.facebookCheckBox),
                (LoginButton)findViewById(R.id.facebookEditButton),
                this);
        twitterConnection = new TwitterConnection(
                (TextView)findViewById(R.id.twitterTextView),
                (CheckBox)findViewById(R.id.twitterCheckBox),
                loginButton,
                this);
        instagramConnection = new InstagramConnection(
                (TextView)findViewById(R.id.instagramTextView),
                (CheckBox)findViewById(R.id.instagramCheckBox),
                (Button)findViewById(R.id.instagramEditButton),
                this);

        Client.instance.setMainActivity(this);

        /*try SMOKE HASH
        {
            PackageInfo info = getPackageManager().getPackageInfo("com.eg.contactconnect0",PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }*/
    }

    public void qrScanButton(View view)
    {
        //open the qr code scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a SOCIALINK QR Code");
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
            BitMatrix bitMatrix = multiFormatWriter.encode(nameConnection.getQRData()+ ":" + phoneConnection.getQRData() + ":" + emailConnection.getQRData() + ":" + facebookConnection.getQRData() + ":" + twitterConnection.getQRData() + ":" + instagramConnection.getQRData(), BarcodeFormat.QR_CODE,400,400);
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
                else if (type.equals("Email"))
                    emailConnection.setData(data);
                else if (type.equals("Facebook"))
                    facebookConnection.setData(data);
                else if (type.equals("Twitter"))
                    twitterConnection.setData(data);
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
        }

        // This is important, otherwise the result will not be passed to the fragment
        super.onActivityResult(requestCode, resultCode, data);
        //for facebook login
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //for twitter login
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
