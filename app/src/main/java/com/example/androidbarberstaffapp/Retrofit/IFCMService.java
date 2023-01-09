package com.example.androidbarberstaffapp.Retrofit;

import com.example.androidbarberstaffapp.Model.FCMResponse;
import com.example.androidbarberstaffapp.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAqOKo_v8:APA91bGZqlFu6Q6GOIBU2WtrU4c1lvYipXz5a0AR-oA8MG2B9_JIm3T7Rh9tBqOQnypCgHGKGRDyjKTNyMaWPZDfZ5R62BVFzopLWOpddL1f_D7ZSCOn6Mm-4UYuhGIphKEdUzWU882g"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
