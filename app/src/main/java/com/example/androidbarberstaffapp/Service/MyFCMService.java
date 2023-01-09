package com.example.androidbarberstaffapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.example.androidbarberstaffapp.Common.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFCMService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Common.updateToken(this,token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Common.showNotification(this,
                new Random().nextInt(),
                remoteMessage.getData().get(Common.TITLE_KEY),
                remoteMessage.getData().get(Common.CONTENT_KEY),
                null);
    }
}
