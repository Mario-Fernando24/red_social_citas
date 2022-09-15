package com.example.whatsappclone.providers;

import com.example.whatsappclone.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class UsersProviders {

    private CollectionReference mCollection;


    //verificar si el usuario existe para que no se sobre escriba
    public DocumentReference getUserInfo(String idUsers){
        return mCollection.document(idUsers);
    }

    public UsersProviders(){
        mCollection= FirebaseFirestore.getInstance().collection("Users");
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

    //metodo para que me retorna un objeto de tipo query
    public Query getAllName(){
        return mCollection.orderBy("username");
    }





}
