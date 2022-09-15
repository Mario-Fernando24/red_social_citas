package com.example.whatsappclone.retrofif;

import com.example.whatsappclone.models.FCMBody;
import com.example.whatsappclone.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA4LetaGU:APA91bGExNyjlmDVEzTi1zIqSBYqHhqYwJs-Rt43aPcNhWSVpyJcXOmRW_e657Xfqbtk7v6vICg4lcOUYTxjtTn40rMyFurKJvP6fGVAZFpQ0NSMz_y754DVVRJZGdCAHO6GMxaFYW-D"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);


}
