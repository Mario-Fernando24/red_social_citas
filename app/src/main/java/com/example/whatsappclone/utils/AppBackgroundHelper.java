package com.example.whatsappclone.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.UsersProviders;

import java.util.List;

public class AppBackgroundHelper {


    // este metodo va a recibir dos parametros
    public static void online(Context context, boolean status){

        UsersProviders usersProviders = new UsersProviders();
        AuthProviders authProviders = new AuthProviders();

         if(authProviders.getIdAutenticado()!=null){
             //validamos si el usuario se salio de la aplicacion
             if(isApplicationSentToBackground(context)){
                 usersProviders.updateOnline(authProviders.getIdAutenticado(), status);
             }else if(status){
                 usersProviders.updateOnline(authProviders.getIdAutenticado(), status);

             }
         }

    }
    // este metodo isApplicationSentToBackground me va ayudar saber si el usuario minimizo o cerro  la aplicacion
    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}


