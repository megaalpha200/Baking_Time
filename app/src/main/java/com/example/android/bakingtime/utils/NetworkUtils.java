package com.example.android.bakingtime.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public static boolean isOnline = true;
    private static NetworkBroadcastReceiver networkBroadcastReceiverListener;
    private static BroadcastReceiver networkBroadcastReceiver;

    public static BroadcastReceiver getNetworkBroadcastReceiver() {
        return networkBroadcastReceiver;
    }

    public static void setNetworkBroadcastReceiverListener(NetworkBroadcastReceiver listener) {
        networkBroadcastReceiverListener = listener;
    }

    public static void registerNetworkBroadcastReceiver(Context context) {
        networkBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Source: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                isOnline = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (networkBroadcastReceiverListener != null) {
                    networkBroadcastReceiverListener.onNetworkStatusChanged(isOnline);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkBroadcastReceiver, intentFilter);
    }

    public interface NetworkBroadcastReceiver {
        void onNetworkStatusChanged(boolean isConnected);
    }
}
