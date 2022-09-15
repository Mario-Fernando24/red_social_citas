package com.example.whatsappclone.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.Message;

import java.util.Date;

public class NotificationHelper extends ContextWrapper {

    //id del canal de notificaciones
    private static final  String CHANNEL_ID="com.example.whatsappclone";
    //NOMBRE DE NUESTRO PROYECTO
    private static final  String CHANNEL_NAME="WhatsApp_Clone-main";
    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }

    }

    //esto quiere decir que este metodo se ejecute en una version igual o superior a android 8
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel notificationChannel = new NotificationChannel(
            CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        //habilite las
        notificationChannel.enableLights(true);
        //habilite la vibracion
        notificationChannel.enableVibration(true);
        //habilite las luces
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }



    public NotificationManager getManager(){
        //
        if(manager==null){
            manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }



    //aqui le damos el stylo a la notificacion
    public NotificationCompat.Builder getNotification(String title, String body) {

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setColor(Color.GRAY)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }



    //metodo de la notificacion del chat personalizado
    public NotificationCompat.Builder getNotificationMessage(
            Bitmap bitmapUsuarioEnvia, String usersRecibe, String usuarioPropio,
            Message[] mensaje, NotificationCompat.Action actionResponse, NotificationCompat.Action actionStatus,
            String myMessage, PendingIntent contentIntent){

        Person myperson = new Person.Builder()
                //Nombre de nuestro usuario
                .setName("Tu")
                .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_persona))
                .build();
        //
        Person recibePerso =null;

        if (bitmapUsuarioEnvia==null){
            recibePerso = new Person.Builder()
                    //Nombre de nuestro usuario
                    .setName(usersRecibe)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_persona))
                    .build();
        }else{
            //el usuario que va a recibir la notificacion

            recibePerso = new Person.Builder()
                    //Nombre de nuestro usuario
                    .setName(usersRecibe)
                    .setIcon(IconCompat.createWithBitmap(bitmapUsuarioEnvia))
                    .build();
        }
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(recibePerso);

        for (Message m:mensaje){
            NotificationCompat.MessagingStyle.Message messageNotification = new NotificationCompat.MessagingStyle.Message(
                    //recibe el mensaje
                    m.getMessage(),
                    //en que momento se envio el mensaje
                    m.getTimeTamp(),
                    recibePerso
            );

            messagingStyle.addMessage(messageNotification);
        }

        if(!myMessage.equals("")){
            NotificationCompat.MessagingStyle.Message mymessageNotification = new NotificationCompat.MessagingStyle.Message(
                    //recibe el mensaje
                    myMessage,
                    //en que momento se envio el mensaje
                    new Date().getTime(),
                    myperson
            );
            messagingStyle.addMessage(mymessageNotification);
        }


                NotificationCompat.Builder notificationaa = new  NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setStyle(messagingStyle)
                //metodo para agregar dos opciones en las notificaciones
                .addAction(actionResponse)
                        .setContentIntent(contentIntent);

        if(actionStatus!=null){
            notificationaa.addAction(actionStatus);
        }
        return notificationaa;
    }





}
