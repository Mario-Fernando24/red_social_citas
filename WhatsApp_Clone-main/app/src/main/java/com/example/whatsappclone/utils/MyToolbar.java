package com.example.whatsappclone.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsappclone.R;

public class MyToolbar {
    //creamos un metodo public que no devuelva ningun valor
    //recibe como parametro la actividad, el titulo y la fecha para ir hacia atras

    public static void show(AppCompatActivity activity, String title, boolean upButton){

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}
