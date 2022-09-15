package com.example.whatsappclone.framents;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.whatsappclone.CodeVerificationActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.CompleteInfoActivity;
import com.example.whatsappclone.activities.ProfileActivity;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.ImagProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.ArrayList;

//quiero que herede de algo llamado BottomSheetDialogFragment del paquete de android
public class ButtonSelectImag extends BottomSheetDialogFragment {

    LinearLayout mLinearLayoutDeleteImag;
    LinearLayout mlinearLayautUpdateImag;
    public static final String TAG= ButtonSelectImag.class.getSimpleName();
    ImagProviders imagProviders;
    AuthProviders authProviders;
    UsersProviders usersProviders;
    String image;


    //recibo como parametro de la activity profile la url del usuario que esta logueADO
    public static ButtonSelectImag  newIntance(String url){
        ButtonSelectImag buttonSelectImag = new ButtonSelectImag();
        Bundle args= new Bundle();
        args.putString("image",url);
        buttonSelectImag.setArguments(args);
        return  buttonSelectImag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image= getArguments().getString("image");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // le pasamos como parametro el layaut que nosotros vamos a implementar
        View view = inflater.inflate(R.layout.button_select_img_camera,container,false);

        mLinearLayoutDeleteImag= view.findViewById(R.id.linearLayautDeleteImag);
        mlinearLayautUpdateImag= view.findViewById(R.id.linearLayautUpdateImag);

        imagProviders = new ImagProviders();
        authProviders = new AuthProviders();
        usersProviders = new UsersProviders();


        mLinearLayoutDeleteImag.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"esto es una prueba");
                deleteImag();
            }
        });

        mlinearLayautUpdateImag.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Prueba para actualizar");
                updateImag();
            }
        });
        //y retornamos la vista de los botones
        return view;
    }

    private void updateImag() {
        //inicializa la libreria para seleccionar la imagen
        ((ProfileActivity)getActivity()).starPix();
    }

    private void deleteImag() {
        //borra primero la url que esta en el storage
        imagProviders.delete(image).addOnCompleteListener(new OnCompleteListener<Void>( ) {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //SI LA IMAGEN SE ELIMINO CORRECTAMENTE
                if(task.isSuccessful()){

                    usersProviders.updateImagen(authProviders.getIdAutenticado(),null).addOnCompleteListener(new OnCompleteListener<Void>( ) {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                           if(task2.isSuccessful()){
                               refrescarImagDelete();
                               Toast.makeText(getContext(),"La imegen se elimino correctamente",Toast.LENGTH_LONG).show();
                           }else{
                               Toast.makeText(getContext(),"No se pudo eliminar el dato",Toast.LENGTH_LONG).show();
                           }
                        }
                    });
                }else{

                    Toast.makeText(getContext(),"no se pudo eliminar la imagen",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //invocando
    private void refrescarImagDelete() {
        ((ProfileActivity)getActivity()).refrescarImagDelete();
    }


}
