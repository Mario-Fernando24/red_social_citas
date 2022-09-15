package com.example.whatsappclone.providers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.whatsappclone.utils.CompressorBitmapImage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImagProviders {

    StorageReference mStore;
    FirebaseStorage mFirebaseStore;
    public static final String TAG= ImagProviders.class.getSimpleName();
    public ImagProviders(){
        mFirebaseStore=FirebaseStorage.getInstance();
        mStore= mFirebaseStore.getReference();
    }


    // metodo para guardar la imagen en firebase
    public UploadTask saveImag(Context context, File file)
    {
        Log.d(TAG,"MARIO"+file.getPath());

        //envio por parametro al metodo el contexto, imagen, y el tamaño con el que quiero que se guarde la imagen en mi storage
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500,500);
        StorageReference storage = mStore.child(new Date()+".jpg");
        mStore = storage;
        UploadTask task = storage.putBytes(imageByte);

        return  task;
    }


    //nos retornara la url de la imagen que se acabo de almacenar
    public Task<Uri> getDownloadUrl(){
        return mStore.getDownloadUrl();
    }

    //metodo de eliminar una imagen por medio de la url localstorage
    public Task<Void> delete(String url){
       return mFirebaseStore.getReferenceFromUrl(url).delete();
    }
}
