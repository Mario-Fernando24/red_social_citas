package com.example.whatsappclone.providers;

import com.example.whatsappclone.models.Chat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatProviders {

    CollectionReference mCollectionReference;

    public ChatProviders(){
        mCollectionReference= FirebaseFirestore.getInstance().collection("Chat");
    }
    //creamos la informacion en la base de datos
    public Task<Void> create(Chat chat){
        return mCollectionReference.document(chat.getId()).set(chat);
    }

    public Query validateIdUsersTwo(String idUser1, String idUser2){

        ArrayList<String> ids = new ArrayList<>();

        //pueden tener dos combinaciones
                ids.add(idUser1 + idUser2);
                ids.add(idUser2 + idUser1);

        //hago la consulta para saber ya existe ese id
        return mCollectionReference.whereIn("id",ids);
    }

    //consulta para tener mis chat que contengan mi id
    public Query getUserChatId(String id){
  //que me traigan los chat que tengas mas de 1 menssaje
        return mCollectionReference.whereArrayContains("ids",id).whereGreaterThanOrEqualTo("numberMessage",1);
    }

    public void updateNumberMessage(final String idChat){


        mCollectionReference.document(idChat).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>( ) {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                     if(documentSnapshot.contains("numberMessage")){
                         long contador=documentSnapshot.getLong("numberMessage")+1;
                         Map<String,Object> map = new HashMap<>();
                         map.put("numberMessage",contador);
                         mCollectionReference.document(idChat).update(map);
                     }else{

                         Map<String,Object> map = new HashMap<>();
                         map.put("numberMessage",1);
                         mCollectionReference.document(idChat).update(map);
                     }
                }
            }
        });



    }

    //netodo que nos permita actualizar el valor del valor de escribiendo para mostrar cual de los usuario esta escribiendo
    public Task<Void> updateEscribiendo(String idChat, String idUser){
        Map<String,Object> map = new HashMap<>();
        map.put("escribiendo",idUser);

        return mCollectionReference.document(idChat).update(map);
    }


    //metodo para saber por medio del idChat la variable escribiendo para saber si el usuario esta escribiendo si o no
    public DocumentReference getChatEscribiendo(String idChat){

        return mCollectionReference.document(idChat);
    }

}
