package com.lamont.demo.itunetop100;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.lamont.demo.itunetop100.Utils.NetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NetBroadcastReceiver extends BroadcastReceiver {
    private static String TAG = "NetBroadcastReceiver";
    private static final List<INetEvevtLister> mNetChangeListeners = Collections.synchronizedList(new ArrayList<INetEvevtLister>());

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive -- " +intent.getAction());
        // 如果相等的話就說明網絡狀態發生了變化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetUtil.getNetWorkState(context);
            // 接口回調傳過去狀態的類型
            Log.d(TAG, "onNetChange netWorkState : -- " + netWorkState);
            synchronized (mNetChangeListeners) {
                for (INetEvevtLister listener : mNetChangeListeners) {
                    listener.onNetChange(netWorkState);
                }
            }
        }
    }

    public interface INetEvevtLister {
        public void onNetChange(int netMobile);
    }

    static public void registerNetListener(INetEvevtLister listener) {
        if (!mNetChangeListeners.contains(listener)) {
            mNetChangeListeners.add(listener);
        }
    }

    static public void unregisterNetListener(INetEvevtLister listener) {
        mNetChangeListeners.remove(listener);
    }

}