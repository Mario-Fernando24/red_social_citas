package com.example.whatsappclone.receptor;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.providers.MessageProviders;
import com.google.gson.Gson;

public class MarcarComoLeido extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
             updateStatus(context, intent);
        Log.d("marca como no leido","Marcar como no leido maio");
    }
    private void updateStatus(Context context, Intent intent) {

        int id = intent.getExtras().getInt("idNotification");
        String messagesJSON = intent.getExtras().getString("messages");
        MessageProviders messagesProvider = new MessageProviders();

        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);

        for(Message m: messages) {
            messagesProvider.updateStatus(m.getId(), "VISTO");
        }


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);

    }
}
