package com.example.whatsappclone.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ChatActivity;
import com.example.whatsappclone.channel.NotificationHelper;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.receptor.MarcarComoLeido;
import com.example.whatsappclone.receptor.ReceptorResponse;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {


    public static final String NOTIFICATION_REPLY="NotificationReply";

    //sirve para crear token de notificaciones de dispositivo a dispositivo
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    //metodo que nos ayuda a recibir los datos que se envian en las notificaciones
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //capturar la informacion que nos envian en la notificacion
        Map<String,String>  data= remoteMessage.getData();
         //vamos a obtener los datos que enviamos atravez de la notificacion

        Log.d("negocio"," "+data);
        String title=data.get("title");
        String body=data.get("body");
        String idNotification=data.get("idNotification");
        String usersRecibe=data.get("usersRecibe");
        String userPropio = data.get("userPropio");

        if (title!=null){

            if(title.equals("NUEVO MENSAJE")){
                getImagenEnvia(data);
            }else{
                  showNotification(title, body,idNotification);
            }
        }

    }

    private void showNotification(String title, String body, String idNotification) {

        NotificationHelper helper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = helper.getNotification(title,body);
        helper.getManager().notify( Integer.parseInt(idNotification),builder.build());
    }


    private void getImagenEnvia(final Map<String, String> data){
        String imagenUsuarioEnvia = data.get("imagenUsuarioEnvia");


        if (imagenUsuarioEnvia==null){
            showNotificationMessage(data,null);
            return;
        }
        if (imagenUsuarioEnvia.equals("")){
            showNotificationMessage(data,null);
            return;
        }


        new Handler( Looper.getMainLooper())
                .post(new Runnable( ) {
                    @Override
                    public void run() {
                        Picasso.with(getApplicationContext())
                                .load(imagenUsuarioEnvia)
                                .into(new Target( ) {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        showNotificationMessage(data,bitmap);
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {
                                        showNotificationMessage(data,null);
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    }
                });


    }


    private void showNotificationMessage(Map<String, String> data, Bitmap imagen) {


        String body=data.get("body");
        String idNotificatio=data.get("idNotification");
        String usersRecibe=data.get("usersRecibe");
        String userPropio = data.get("userPropio");
        String mensajesJSON = data.get("mensajesJSON");
        String imagenUsuarioEnvia = data.get("imagenUsuarioEnvia");

        String idChat=data.get("idChat");
        String idUsuarioEnvia=data.get("idUsuarioEnvia");
        String idUsuarioRecibe=data.get("idUsuarioRecibe");
        String tokenSender=data.get("tokenSender");
        String tokenReceptor=data.get("tokenReceptor");



        int idNotificacionn=Integer.parseInt(idNotificatio);
        //convierto de string a un arreglo
        Gson gson = new Gson();
        Message[] messagess=gson.fromJson(mensajesJSON,Message[].class);

        NotificationHelper helper = new NotificationHelper(getBaseContext());

        //nos creamos PARA LAS OPCIONES EN LAS NOTIFICACIONES
        Intent intentResponse = new Intent(this, ReceptorResponse.class);
        intentResponse.putExtra("idNotification",idNotificacionn);
        intentResponse.putExtra("messages",mensajesJSON);
        intentResponse.putExtra("usernameSender",userPropio);
        intentResponse.putExtra("usernameReceptor",usersRecibe);

        intentResponse.putExtra("idChat",idChat);
        intentResponse.putExtra("idUsuarioEnvia",idUsuarioEnvia);
        intentResponse.putExtra("idUsuarioRecibe",idUsuarioRecibe);

        intentResponse.putExtra("tokenSender",tokenSender);
        intentResponse.putExtra("tokenReceptor",tokenReceptor);


        PendingIntent pendingIntent =PendingIntent.getBroadcast(this,idNotificacionn,intentResponse,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        NotificationCompat.Action actionResponse = new NotificationCompat.Action.Builder(
               R.mipmap.ic_launcher,
                "Responder",
                pendingIntent
        )
                .addRemoteInput(remoteInput)
                .build();

        //nos creamos PARA LAS OPCIONES EN LAS NOTIFICACIONES
        Intent intentStatus = new Intent(this, MarcarComoLeido.class);
        intentStatus.putExtra("messages",mensajesJSON);

        PendingIntent pendingIntentStatus =PendingIntent.getBroadcast(this,idNotificacionn,intentStatus,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action actionStatus = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Marcar como leido",
                pendingIntentStatus
        ).build();

        Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
        chatIntent.putExtra("idUser", idUsuarioRecibe);
        chatIntent.putExtra("idChat", idChat);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), idNotificacionn, chatIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder builder = helper.getNotificationMessage(imagen,usersRecibe,userPropio,messagess,actionResponse,actionStatus,"",contentIntent);
        helper.getManager().notify( idNotificacionn,builder.build());
    }


}
