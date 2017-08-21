# NetworkInfoUtil

A network util to receive network and internet information.

Using rxjava observables this util class keeps checking availabilty of internet throughout the app runtime and keeps storing current network information in private fields which can be accessed through getters at any time from the object of this util class. 

To use this NetworkInfoUtil class, Create an object of this class in your activity/fragment and subscribe to getNetworkInfoChanges method. 

Then register broadcast receiver by calling registerBroadcastReceiver method of this class.

```
 MyNetworkInfo myNetworkInfo;
 private Disposable disposableNetworkSubscription;

 
 disposableNetworkSubscription = myNetworkInfo.getNetworkInfoChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> System.out.println(x));

 myNetworkInfo.registerBroadcastReceiver();
```
Now we can get information about our network by calling getters of this same class.
```
  Log.i("Is Network Available: ", String.valueOf(myNetworkInfo.getIsNetworkAvailable()));
  Log.i("Is Internet Available: ", String.valueOf(myNetworkInfo.getIsInternetAvailable()));
  Log.i("Network Type: ", myNetworkInfo.getNetworkType());
  Log.i("Network Name: ", myNetworkInfo.getNetworkName());
  Log.i("Is Roaming Available: ", String.valueOf(myNetworkInfo.isRoamingAvailable()));
```

Lastly don't forget to unsubscribe or dispose our subscription and unregister our broadcast receiver. To dispose subscription cast getNetworkInfoChanges as a disposable and call its dispose method. To unregister broadcast receiver simply call unregisterBroadcastReciever method provided by our util class.
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
