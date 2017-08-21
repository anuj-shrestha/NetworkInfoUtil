# NetworkInfoUtil

A network utility to receive network and internet information.

This utility class leverages `RxJava` `Observables` to provide internet connectivity and network information, throughout the app runtime.

To use this `NetworkInfoUtil` class, create an object of this class in your `activity/fragment` and `subscribe` to the `getNetworkInfoChanges` method. 

Then, register the broadcast receiver by calling the `registerBroadcastReceiver` on the object.

```
 MyNetworkInfo myNetworkInfo;
 private Disposable disposableNetworkSubscription;

 disposableNetworkSubscription = myNetworkInfo.getNetworkInfoChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> System.out.println(x));

 myNetworkInfo.registerBroadcastReceiver();
```
Network information is now exposed via several getters on the object.

```
  Log.i("Is Network Available: ", String.valueOf(myNetworkInfo.getIsNetworkAvailable()));
  Log.i("Is Internet Available: ", String.valueOf(myNetworkInfo.getIsInternetAvailable()));
  Log.i("Network Type: ", myNetworkInfo.getNetworkType());
  Log.i("Network Name: ", myNetworkInfo.getNetworkName());
  Log.i("Is Roaming Available: ", String.valueOf(myNetworkInfo.isRoamingAvailable()));
```

Lastly, don't forget to unsubscribe from the Observable by calling `dispose`, and unregister the broadcast receiver. To dispose the subscription, cast `getNetworkInfoChanges` as a disposable and call its `dispose` method. To unregister the broadcast receiver simply call `unregisterBroadcastReciever` method provided by our util class.
```
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (myNetworkInfo != null) {
            disposableNetworkSubscription.dispose();
            myNetworkInfo.unregisterBroadcastReceiver();
        }
    }
```
