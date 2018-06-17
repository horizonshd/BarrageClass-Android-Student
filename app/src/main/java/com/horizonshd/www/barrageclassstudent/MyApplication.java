package com.horizonshd.www.barrageclassstudent;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class MyApplication extends Application {
    //private static final String serverURL = "http://10.63.15.130:3000";//local virtual-machine
    private static String serverURL = "http://120.79.10.123:3000";//aliyun
    private static Context context;
    private static String authenticatedId;
    private static String authenticatedAccount;
    private SharedPreferences sp;
    private static Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        authenticatedId = sp.getString("id","");
        authenticatedAccount = sp.getString("account","");
    }
    public static Context getContext(){
        return context;
    }

    public static void setAuthenticatedId(String id){
        authenticatedId = id;
    }

    public static String getAuthenticatedId(){
        return authenticatedId;
    }

    public static void setAuthenticatedAccount(String account){
        authenticatedAccount = account;
    }

    public static String getAuthenticatedAccount(){
        return authenticatedAccount;
    }

    public static String getServerURL(){
        return serverURL;
    }

    public static void createSocket(String url){
        {
            try {
                mSocket = IO.socket(url);
                mSocket.connect();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Socket getSocket(){
        return mSocket;
    }
}
