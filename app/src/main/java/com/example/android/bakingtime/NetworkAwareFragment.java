package com.example.android.bakingtime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.bakingtime.utils.NetworkUtils;

public abstract class NetworkAwareFragment extends android.support.v4.app.Fragment implements NetworkUtils.NetworkBroadcastReceiver {

    protected Context mContext;

    private void checkConnection(boolean isConnected, Bundle savedInstanceState) {
        refreshUIOnNetworkStateChange();

        if(!isConnected) {
            Toast.makeText(mContext, getString(R.string.no_network_connection_str), Toast.LENGTH_SHORT).show();
            loadNoConnectionUI();
        }
    }
    abstract protected void loadNoConnectionUI();
    abstract public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
    abstract public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);
    abstract protected void refreshUIOnNetworkStateChange();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onNetworkStatusChanged(boolean isConnected) {
        checkConnection(isConnected, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            NetworkUtils.setNetworkBroadcastReceiverListener(this);
            NetworkUtils.registerNetworkBroadcastReceiver(mContext);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            mContext.unregisterReceiver(NetworkUtils.getNetworkBroadcastReceiver());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mContext.unregisterReceiver(NetworkUtils.getNetworkBroadcastReceiver());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
