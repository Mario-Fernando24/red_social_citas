package com.example.whatsappclone.providers;

import com.example.whatsappclone.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProviders {

    private CollectionReference mCollection;

    public UsersProviders(){
        mCollection= FirebaseFirestore.getInstance().collection("Users");
    }
    //verificar si el usuario existe para que no se sobre escriba
    public DocumentReference getUserInfo(String idUsers){
        return mCollection.document(idUsers);
    }



    //metodo que me permita crear un usuario en la base de dato
    public Task<Void> createe(User user){
        return mCollection.document(user.getId()).set(user);
    }

    //metodo para actualizar en la entidad user el campo userName activity CompleteInfoActivity
    public Task<Void> update(User user){

        Map<String, Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("image",user.getImage());
        map.put("genero",user.getGenero());
        return mCollection.document(user.getId()).update(map);
    }

    //metodo para eliminar el campo de img
    public Task<Void>  updateImagen(String id, String url){

        Map<String, Object> map = new HashMap<>();
        map.put("image",url);
        return  mCollection.document(id).update(map);

    }

    public Task<Void>  updateUsername(String id, String username){

        Map<String, Object> map = new HashMap<>();
        map.put("username",username);
        return  mCollection.document(id).update(map);

    }

    public Task<Void>  updateInfo(String id, String info){


        Map<String, Object> map = new HashMap<>();
        map.put("info",info);
        return  mCollection.document(id).update(map);

    }

    public Task<Void>  updatePhone(String id, String phone){

        Map<String, Object> map = new HashMap<>();
        map.put("phone",phone);
        return  mCollection.document(id).update(map);

    }

    public Task<Void>  updateGenero(String id, String genero){

        Map<String, Object> map = new HashMap<>();
        map.put("genero",genero);
        return  mCollection.document(id).update(map);

    }

    //metodo para que me retorna un objeto de tipo query
    public Query getAllName(){
        return mCollection.orderBy("username");
    }

    //metodo que nos permita actualizar la informacion del usuario online
    public  Task<Void> updateOnline(String idUsuario, Boolean status){

        Map<String, Object> map = new HashMap<>();
        map.put("online",status);
        map.put("ultimaConexionOnline",new Date().getTime());

        return  mCollection.document(idUsuario).update(map);

    }


    //metodo para las notificacion de dispositivo a dispositivo
    //lo utilizamos en la activityHome
    public void createToken(final String idUser){

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>( ) {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                //creamos el token
                String token= instanceIdResult.getToken();

                //actualizamos en la base de dato el token
                Map<String, Object> map = new HashMap<>();
                map.put("token",token);
                mCollection.document(idUser).update(map);
            }
        });
    }





}
