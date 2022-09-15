package com.example.whatsappclone.providers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.whatsappclone.activities.ChatActivity;
import com.example.whatsappclone.models.FCMBody;
import com.example.whatsappclone.models.FCMResponse;
import com.example.whatsappclone.retrofif.IFCMApi;
import com.example.whatsappclone.retrofif.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationProviders {

    //ruta donde vamos a enviar la peticion
    private String url = "https://fcm.googleapis.com";

    public  NotificationProviders() {}

    public Call<FCMResponse> sendNotification(FCMBody body) {

        Log.d("notificacion "," => "+body);
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }

    public void sendd(Context context, String tokenUsers, Map<String,String> data){
        //1 parametro to el token del usuario que le voy a enviar la notificacion
        //2 ""   prioridad high
        //ttl cuanto tiempo el envio caducara

        Log.d("fernandom token====>","   "+tokenUsers);
        Log.d("fernandom data====>","   "+data);

        FCMBody body = new FCMBody(tokenUsers,"high","4500s",data);
        sendNotification(body).enqueue(new Callback<FCMResponse>( ) {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                if (response.body()!=null)
                {
                    if(response.body().getSuccess()==1){
                        Toast.makeText(context,"Se envio correctamente",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context,"Fallo retrofit"+response.message(),Toast.LENGTH_LONG).show();
                    }
                }else{

                    Log.d("rivera","rivera"+response);
                    Toast.makeText(context,"No hubo respuesta del servidor"+response.message(),Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                //la parte donde falla el envio de la peticion al servidoe
                Toast.makeText(context,"Fallo retrofit"+t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }


}
