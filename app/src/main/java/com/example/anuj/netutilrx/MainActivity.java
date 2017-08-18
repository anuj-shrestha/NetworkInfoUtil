package com.example.anuj.netutilrx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Button sendButton;
    private TextView textView;
    private Disposable disposableNetworkSubscription;
    MyNetworkInfo myNetworkInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myNetworkInfo = new MyNetworkInfo(this);

        disposableNetworkSubscription = myNetworkInfo.getNetworkInfoChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> System.out.println(x));

        myNetworkInfo.registerBroadcastReceiver();

        sendButton = (Button) findViewById(R.id.send_button);
        textView = (TextView) findViewById(R.id.textView);

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String netInfo = "Network Connected: " + String.valueOf(myNetworkInfo.getIsNetworkAvailable()) + "\n"
                        + "Internet Available: " + String.valueOf(myNetworkInfo.getIsInternetAvailable()) + "\n"
                        + "Network Type: " + myNetworkInfo.getNetworkType() + "\n"
                        + "Network Name: " + myNetworkInfo.getNetworkName() + "\n"
                        + "Roaming Available: " + myNetworkInfo.isRoamingAvailable();
                textView.setText(netInfo);
                Log.d("Received Network: ", String.valueOf(myNetworkInfo.getIsNetworkAvailable()));
                Log.d("Received Internet: ", String.valueOf(myNetworkInfo.getIsInternetAvailable()));
                Log.d("Received Type: ", String.valueOf(myNetworkInfo.getNetworkType()));
                Log.d("Received Name: ", String.valueOf(myNetworkInfo.getNetworkName()));
                Log.d("Received Roaming: ", String.valueOf(myNetworkInfo.isRoamingAvailable()));

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myNetworkInfo != null) {

            disposableNetworkSubscription.dispose();
            myNetworkInfo.unregisterBroadcastReceiver();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (myNetworkInfo != null) {
//
//            disposableNetworkSubscription.dispose();
//            myNetworkInfo.unregisterBroadcastReceiver();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        disposableNetworkSubscription = myNetworkInfo.getNetworkInfoChanges()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(x -> System.out.println(x));
//
//        myNetworkInfo.registerBroadcastReceiver();
    }
}
