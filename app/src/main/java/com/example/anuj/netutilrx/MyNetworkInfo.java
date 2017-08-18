package com.example.anuj.netutilrx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by anuj on 8/16/17.
 */

public class MyNetworkInfo {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;

    private Context context;
    private boolean isNetworkAvailable;
    private boolean isInternetAvailable;

    private String networkType;
    private String networkName;
    private boolean isRoamingAvailable;

    private PublishSubject<MyNetworkInfo> changeObservable;
    private BroadcastReceiver broadcastReceiver;

    private int currentRepeatCount = 1;
    private int delayBetweenRetry = 200;

    public MyNetworkInfo(Context context) {
        this.context = context;
    }

    public boolean getIsNetworkAvailable() {
        return isNetworkAvailable;
    }

    public void setIsNetworkAvailable(boolean networkAvailable) {
        isNetworkAvailable = networkAvailable;
    }
    @Override
    public String toString() {
        return "MyNetworkInfo{" +
                "isNetworkAvailable='" + isNetworkAvailable + '\'' +"" +
                "isInternetAvailable='" + isInternetAvailable + '\'' +
                '}';
    }


    public Observable<MyNetworkInfo> getNetworkInfoChanges() {
        changeObservable = PublishSubject.create();
        return changeObservable;
    }

    public Observable<Boolean> isInternetAvailableObserver() {
        return Observable.fromCallable(this::isInternetAvailable);
    }



    public void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (broadcastReceiver != null) return;
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                NetworkInfo networkInfo = extras.getParcelable("networkInfo");

                if (networkInfo == null) return;

                if (networkInfo.isConnected()) {
                    checkForInternetConnection();
                } else {
                    setNetworkType("Not Connected");
                    setIsInternetAvailable(false);
                    changeObservable.onNext(MyNetworkInfo.this);
                }
                setIsNetworkAvailable(networkInfo.isConnected());

                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected())
                    setNetworkType("Wifi");

                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected())
                    setNetworkType("Mobile");

                setNetworkName(networkInfo.getExtraInfo());
                setRoamingAvailable(networkInfo.isRoaming());


                Log.i("net type", String.valueOf(networkInfo.getTypeName()));

            }
        };

        context.registerReceiver(broadcastReceiver, filter);
    }

    public void unregisterBroadcastReceiver() {
        context.unregisterReceiver(broadcastReceiver);
    }

    private void checkForInternetConnection() {
        currentRepeatCount = 1;
        delayBetweenRetry = 100;
        isInternetAvailableObserver().repeatWhen(observable -> observable
                .flatMap( input -> {
                    if (!isNetworkAvailable) {
                        return Observable.empty();
                    }
                    currentRepeatCount++;
                    Log.i("check net count", String.valueOf(currentRepeatCount));
                    delayBetweenRetry += 400;
                    return Observable.timer(delayBetweenRetry, TimeUnit.MILLISECONDS);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectionStatus -> {
                    changeObservable.onNext(MyNetworkInfo.this);
                });

    }

//    private boolean isInternetAvailable() {
//        try {
//            final InetAddress address = InetAddress.getByName("www.google.com");
//            setIsInternetAvailable(!address.equals(""));
//            return !address.equals("");
//        } catch (UnknownHostException e) {
//            // Log error
//        }
//        return false;
//    }


    public boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            setIsInternetAvailable(exitValue == 0);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setIsInternetAvailable(boolean internetAvailable) {
        isInternetAvailable = internetAvailable;
    }

    public boolean getIsInternetAvailable() {
        return isInternetAvailable;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public boolean isRoamingAvailable() {
        return isRoamingAvailable;
    }

    public void setRoamingAvailable(boolean roamingAvailable) {
        isRoamingAvailable = roamingAvailable;
    }
}
