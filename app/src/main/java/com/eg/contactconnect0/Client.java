package com.eg.contactconnect0;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Eugene Galkine on 8/3/2016.
 */

public class Client
{
    public static final Client instance = new Client();

    private DataOutputStream OutToServer;
    private BufferedReader InFromServer;
    private Socket ClientSocket;
    private String UserName, Password;
    private boolean hasResponse, connectionAccepted;
    private MainActivity mainActivity;
    private Object lockObj;

    private Client()
    {
        this.UserName = "";
        this.Password = "";
        lockObj = new Object();
    }

    public void connect (String userName, String password)
    {
        this.UserName = userName;
        this.Password = password;
        hasResponse = false;
        connectionAccepted = false;
        connect();
    }

    private void connect()
    {
        if (UserName.equals("") && Password.equals(""))
            return;

        if (ClientSocket != null)
        {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace())
            {
                System.out.println(ste);
            }
            return;
        }

        clientConnectThread connectThread = new clientConnectThread();
        connectThread.start();
    }

    public void close()
    {
        System.out.println("disconnecting from server");

        //Don't try to close connection if we never even connected to begin with
        if (ClientSocket == null)
            return;

        try
        {
            //InFromServer.close();
            if (OutToServer != null)
                OutToServer.close();

            if (ClientSocket != null)
                ClientSocket.close();

            ClientSocket = null;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("disconnected from server");
    }

    public void resume()
    {
        new waitToSendThread("REQUEST_DATA").start();
        connect();
    }

    private void sendMessage (String s)
    {
        if (ClientSocket == null)
            return;

        try
        {
            OutToServer.writeBytes(s + '\n');
        } catch (Exception e) {e.printStackTrace();}
    }

    public void contactInfo (String type, String id)
    {
        //if we aren't offline and we aren't connected, wait for connection
        if (!UserName.equals("") && ClientSocket == null)
            new waitToSendThread("CONTACT_INFO" + type + ":" + id).start();
        else
            sendMessage("CONTACT_INFO" + type + ":" + id);
    }

    public boolean isHasResponse()
    {
        return hasResponse;
    }

    public boolean isConnectionAccepted()
    {
        return connectionAccepted;
    }

    public void setMainActivity(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        sendMessage("REQUEST_DATA");
    }

    public String getUser()
    {
        return UserName;
    }

    private class clientListenThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                String in;
                while ((in = InFromServer.readLine()) != null)
                {
                    if (!ClientSocket.isConnected())
                        break;

                    System.out.println("received from server: " + in);
                    if (in.startsWith("CONNECTION_ACCEPTED"))
                    {
                        //MainActivity.getInstance().ConnectionEstablished();
                        connectionAccepted = true;
                        hasResponse = true;
                        synchronized (lockObj)
                        {
                            lockObj.notify();
                        }
                    } else if (in.startsWith("CONNECTION_DENIED"))
                    {
                        //MainActivity.getInstance().ConnectionDenied();
                        connectionAccepted = false;
                        hasResponse = true;
                        close();
                    } else if (in.startsWith("USER_DATA"))
                    {
                        mainActivity.recieveData(in.substring("USER_DATA".length()));
                    }
                }
            } catch (Exception e)
            {
                //e.printStackTrace();
                System.out.println("Client Input thread had exception");
            }
        }
    }

    private class clientConnectThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                InetAddress serverAddr = InetAddress.getByName("162.206.133.148");
                ClientSocket = new Socket();//serverAddr, 6005);
                ClientSocket.connect(new InetSocketAddress(serverAddr, 6015), 1000);

                OutToServer = new DataOutputStream(ClientSocket.getOutputStream());
                InFromServer = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
                hasResponse = false;
                sendMessage("REQUEST_CONNECTION"+UserName+":"+Password);
                clientListenThread listenThread = new clientListenThread();
                listenThread.start();
            } catch (IOException e)
            {
                System.out.println("CONNECTION TO SERVER FAILED: " + e.getMessage());
                e.printStackTrace();
                //if (e.getClass().getCanonicalName().equals(SocketTimeoutException.class.getCanonicalName()))
                  //  LoginActivity.getInstance().ConnectionError("Connection Timed Out");
                //else if (e.getClass().getCanonicalName().equals(android.system.ErrnoException.class.getCanonicalName()))
                //    MainActivity.getInstance().ConnectionError("Network is Unreachable");
                //else
                //    e.printStackTrace();
            }
        }
    }

    private class waitToSendThread extends Thread
    {
        private String dataToSend;

        public waitToSendThread(String input)
        {
            dataToSend = input;
        }

        @Override
        public void run()
        {
            //wait until we have network connection before sending the message
            while (ClientSocket == null)
            {
                try
                {
                    synchronized (lockObj)
                    {
                        lockObj.wait();
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            sendMessage(dataToSend);
        }
    }
}
