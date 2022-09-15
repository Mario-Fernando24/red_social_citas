package com.example.whatsappclone.providers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.utils.CompressorBitmapImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ImagProviders {

    StorageReference mStore;
    FirebaseStorage mFirebaseStore;
    int index = 0;
    MessageProviders messageProviders;
    StatusProviders statusProviders;


    public static final String TAG= ImagProviders.class.getSimpleName();


    public ImagProviders(){
        mFirebaseStore=FirebaseStorage.getInstance();
        messageProviders= new MessageProviders();
        statusProviders = new StatusProviders();
        mStore= mFirebaseStore.getReference();
        index=0;
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



    //para agregar los estados de los estados

    public void uploadMultipleStatus(final Context context, final ArrayList<Status> status) {

        Uri[] uri = new Uri[status.size()];

        //File  file = CompressorBitmapImage.reduceImageSize(new File(messages.get(i).getUrl()));
        File file = new  File(status.get(index).getUrl());
        uri[index] = Uri.parse("file://" + file.getPath());
        final StorageReference ref = mStore.child(uri[index].getLastPathSegment());
        ref.putFile(uri[index]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            //obtenemos la url cuando se almacena la url en store
                            status.get(index).setUrl(url);
                            //guardamos los datos en el providers StatusProviders en la posicion
                            statusProviders.create(status.get(index));
                            index++;

                            if(index==status.size()){
                                Toast.makeText(context, "Se ha creado una nueva historia", Toast.LENGTH_LONG).show( );
                            }
                            //pregunta que si el indice el menor al tamaño de nuestra lista hasta que sea igual para que deje de guardar los estados
                            if(index<status.size()){
                                uploadMultipleStatus(context, status);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }



    //metodo que nos ayude a guardar multiples archivo, imagenes de los chat de cada usuario

    public void uploadMultiple(final Context context, final ArrayList<Message> messages) {

        Uri[] uri = new Uri[messages.size()];
        for (int i = 0; i < messages.size(); i++) {
           //File  file = CompressorBitmapImage.reduceImageSize(new File(messages.get(i).getUrl()));
            File file = new  File(messages.get(i).getUrl());
            uri[i] = Uri.parse("file://" + file.getPath());
            final StorageReference ref = mStore.child(uri[i].getLastPathSegment());
            ref.putFile(uri[i]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                messages.get(index).setUrl(url);
                                messageProviders.create(messages.get(index));
                                index++;
                            }
                        });
                    }
                    else {
                        Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }

    }






}
