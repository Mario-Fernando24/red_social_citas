package com.example.whatsappclone.providers;

import com.example.whatsappclone.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class MessageProviders {

    private CollectionReference mCollection;

    public MessageProviders(){
        mCollection= FirebaseFirestore.getInstance().collection("Message");
    }

    public Task<Void> create(Message message)
    {
        DocumentReference document = mCollection.document();
        //sabemos el id aleatorio
        message.setId(document.getId());
        return document.set(message);
    }

    //obtener los msm por chat
    public Query idMessageByChat(String idChat){
        return mCollection.whereEqualTo("idChat",idChat)
                .orderBy("timeTamp",Query.Direction.ASCENDING);
    }

    //id del chat y el id de la persona que envia
    public Query obtenerLosUltimosCincoMensaggesChat(String idChat, String idPeopleEnvia){
        return mCollection
                .whereEqualTo("idChat",idChat)
                .whereEqualTo("idEnviar",idPeopleEnvia)
                .whereEqualTo("status","ENVIADO")
                .orderBy("timeTamp",Query.Direction.DESCENDING)
                .limit(5);
    }

    //function for actualizar el estado
    public  Task<Void> updateStatus(String idMessage, String status){

        Map<String, Object> map =new HashMap<>();
        map.put("status",status);

        return mCollection.document(idMessage).update(map);
    }

    // funcion para saber que Message collecion vamos a actualizar
    public Query getMessageNoLeido(String idChat){
        //queremos obtener los mensajes no leido de un chat
        return mCollection.whereEqualTo("idChat",idChat).whereEqualTo("status","ENVIADO");
    }

    public Query getMessageRecibidoNoLeido(String idChat, String idUsersRecibido){
        //queremos obtener los mensajes no leido de un chat
        return mCollection.whereEqualTo("idChat",idChat).whereEqualTo("status","ENVIADO").whereEqualTo("idRecibido",idUsersRecibido);
    }

    //metodo para que me retorne el ultimo mensaje de un chat
    public Query obtenerUltimoMessageChat(String idChat){

        return mCollection.whereEqualTo("idChat",idChat).orderBy("timeTamp",Query.Direction.DESCENDING).limit(1);
    }
}
