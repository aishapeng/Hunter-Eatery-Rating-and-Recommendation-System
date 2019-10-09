package com.example.hunter;

import com.google.firebase.messaging.FirebaseMessagingService;
import android.util.Log;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
    }

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//    }

}
