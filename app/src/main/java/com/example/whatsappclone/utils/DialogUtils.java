package com.example.whatsappclone.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.whatsappclone.R;

public class DialogUtils {

    public static void showAlertaInvalidEmail(Context context, LayoutInflater inflater, Typeface font){
        /////////////// Mensaje de Alerta//////////////////////////////////////
        View layout = inflater.inflate(R.layout.mensajedeconfirmacion, null);
        TextView contenido = layout.findViewById(R.id.txt_message_dialog);
        TextView titulo = layout.findViewById(R.id.txt_title_dialog);
        Button accion = layout.findViewById(R.id.btn_action_dialog);
        contenido.setTypeface(font);
        titulo.setTypeface(font);
        accion.setTypeface(font);
        contenido.setText("prueba");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        final AlertDialog ventana = builder.show();
        ventana.setCancelable(false);
        accion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventana.dismiss();
            }
        });
        /////////////// Mensaje de Alerta//////////////////////////////////
    }


}
