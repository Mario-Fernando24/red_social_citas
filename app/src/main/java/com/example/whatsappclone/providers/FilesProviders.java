package com.example.whatsappclone.providers;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class FilesProviders {

    StorageReference storageReference;
    MessageProviders messageProviders;
    AuthProviders authProviders;

    public FilesProviders(){
        storageReference= FirebaseStorage.getInstance().getReference();
        messageProviders = new MessageProviders();
        authProviders = new AuthProviders();
    }

    //metodo que me permita guardar esos archivos
    public void saveFile(final Context context, ArrayList<Uri> files, String idChat, String idUserRecibe){

        for(int i=0; i<files.size(); i++){
           final Uri f=files.get(i);
          final  StorageReference refer =storageReference.child(FileUtil.getFileName(context,f));
            refer.putFile(f).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>( ) {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                          if(task.isSuccessful()){
                               refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>( ) {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                        //capturamos la url que es la que vamos a guardar en la base de datos
                                       String urlm=uri.toString();
                                       Message message = new Message();
                                       message.setIdChat(idChat);
                                       message.setIdRecibido(idUserRecibe);
                                       message.setIdEnviar(authProviders.getIdAutenticado());
                                       message.setType("documento");
                                       message.setUrl(urlm);
                                       message.setStatus("ENVIADO");
                                       message.setTimeTamp(new Date().getTime());
                                       message.setMessage(FileUtil.getFileName(context,f));

                                       messageProviders.create(message);
                                   }
                               });
                          }else{
                              Toast.makeText(context.getApplicationContext( ), "No se pudo guardar el archivo",Toast.LENGTH_LONG).show();
                          }
                }
            });
        }
    }
}
