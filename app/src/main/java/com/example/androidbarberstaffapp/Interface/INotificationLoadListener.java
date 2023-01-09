package com.example.androidbarberstaffapp.Interface;

import com.example.androidbarberstaffapp.Model.MyNotification;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface INotificationLoadListener {
    void onNotificationLoadSuccess(List<MyNotification> myNotificationList, DocumentSnapshot lastDocument);
    void onNotificationLoadFailed(String message);
}
