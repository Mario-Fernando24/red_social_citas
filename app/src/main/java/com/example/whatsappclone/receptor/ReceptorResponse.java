package com.example.whatsappclone.receptor;

import static com.example.whatsappclone.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ChatActivity;
import com.example.whatsappclone.channel.NotificationHelper;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.providers.MessageProviders;
import com.example.whatsappclone.providers.NotificationProviders;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReceptorResponse extends BroadcastReceiver {


    //vamos a poder optener los parametros que mandamos
    @Override
    public void onReceive(Context context, Intent intent) {

        //obtengo el texto escrito por el usuario
        String message =getMessageText(intent).toString();

        int idNoti=intent.getExtras().getInt("idNotification");
        String usernameSender=intent.getExtras().getString("usernameSender");
        String usernameReceptor=intent.getExtras().getString("usernameReceptor");
        String messageJSON=intent.getExtras().getString("messages");

        String idChat=intent.getExtras().getString("idChat");
        String idUsuarioEnvia=intent.getExtras().getString("idUsuarioEnvia");
        String idUsuarioRecibe=intent.getExtras().getString("idUsuarioRecibe");
        String tokenSender= intent.getExtras().getString("tokenSender");
        String tokenReceptor= intent.getExtras().getString("tokenReceptor");


        Log.d("fredyyyyyyy ", "fea"+intent.getExtras().getString("tokenn"));


        Gson gson = new Gson();
        Message[] messagess=gson.fromJson(messageJSON,Message[].class);

        NotificationHelper helper = new NotificationHelper(context);

        //nos creamos PARA LAS OPCIONES EN LAS NOTIFICACIONES
        Intent intentResponse = new Intent(context, ReceptorResponse.class);
        intentResponse.putExtra("idNotification",idNoti);
        intentResponse.putExtra("messages",messageJSON);
        intentResponse.putExtra("usernameSender",usernameSender);
        intentResponse.putExtra("usernameReceptor",usernameReceptor);

        intentResponse.putExtra("idChat",idChat);
        intentResponse.putExtra("idUsuarioEnvia",idUsuarioEnvia);
        intentResponse.putExtra("idUsuarioRecibe",idUsuarioRecibe);

        intentResponse.putExtra("tokenSender",tokenSender);
        intentResponse.putExtra("tokenReceptor",tokenReceptor);


        PendingIntent pendingIntent =PendingIntent.getBroadcast(context,idNoti,intentResponse,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        NotificationCompat.Action actionResponse = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent
        )
                .addRemoteInput(remoteInput)
                .build();


        NotificationCompat.Action actionStatus = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Marcar como leido",
                pendingIntent
        )
                .addRemoteInput(remoteInput)
                .build();

        Intent chatIntent = new Intent(context, ChatActivity.class);
        chatIntent.putExtra("idUser", idUsuarioEnvia);
        chatIntent.putExtra("idChat", idChat);
        PendingIntent contentIntent = PendingIntent.getActivity(context, idNoti, chatIntent, PendingIntent.FLAG_ONE_SHOT);

        //CUANDO SE RESPONDE POR LA NOTIFICACION OCULTAMOS EL MARCAR MENSAJES COMO LEIDO

        NotificationCompat.Builder builder = helper.getNotificationMessage(null,usernameSender,usernameReceptor,
                messagess,actionResponse,null,message,contentIntent);
        helper.getManager().notify( idNoti,builder.build());

        if(!message.equals("")) {
            Message mymessagee = new Message( );
            mymessagee.setIdChat(idChat);
            mymessagee.setIdEnviar(idUsuarioEnvia);
            mymessagee.setIdRecibido(idUsuarioRecibe);
            mymessagee.setMessage(message);
            mymessagee.setStatus("ENVIADO");
            mymessagee.setType("texto");
            mymessagee.setTimeTamp(new Date( ).getTime( ));


            createMessage(mymessagee);

            ArrayList<Message> messageArrayList = new ArrayList<>( );
            messageArrayList.add(mymessagee);
            enviarNotificacion(context, messageArrayList,idNoti,usernameReceptor,
                    usernameSender,idChat,idUsuarioEnvia,idUsuarioRecibe,tokenSender,tokenReceptor);
            Log.d("mario", "=> " + message);
        }
    }



    private void createMessage(Message message) {

            MessageProviders  messageProviders = new MessageProviders();
            messageProviders.create(message);
    }


    private void enviarNotificacion(Context context,ArrayList<Message> messages,
                                    int idNotification, String usernameReceptor, String usernameEnviar,
                                    String idChat,String idUsuarioEnvia, String idUsuarioRecibe,
                                    String tokenSender,String tokenReceptor) {

        Map<String, String> data = new HashMap<>();
        data.put("title","NUEVO MENSAJE");
        data.put("body","");
        data.put("idNotification", String.valueOf(idNotification));
        data.put("usersRecibe",usernameReceptor);
        //usuario que envia el mensaje
        data.put("userPropio",usernameEnviar);
        data.put("imagenUsuarioEnvia",null);
        //pasar array a json
        Gson gson = new Gson();
        String mensajesJson=gson.toJson(messages);
        data.put("mensajesJSON",mensajesJson);

        data.put("idChat",idChat);
      //  data.put("idUsuarioEnvia",idUsuarioRecibe);
        //  data.put("idUsuarioRecibe",idUsuarioEnvia);
         data.put("idUsuarioEnvia",idUsuarioEnvia);
         data.put("idUsuarioRecibe",idUsuarioRecibe);


        data.put("tokenSender",tokenReceptor);
        data.put("tokenReceptor",tokenSender);


        NotificationProviders notificationProviders = new NotificationProviders();
        notificationProviders.sendd(context,tokenSender,data);

    }

    //hacemos un metodo privado para poder capturar el mensaje de la notificacion y poderla enviarla al usuario destiino

    private CharSequence getMessageText(Intent intent){

        Bundle remoteInput = RemoteInput .getResultsFromIntent(intent);

        if (remoteInput!=null){
            return remoteInput.getCharSequence(NOTIFICATION_REPLY);
        }
        else{
            return null;
        }
    }
}
