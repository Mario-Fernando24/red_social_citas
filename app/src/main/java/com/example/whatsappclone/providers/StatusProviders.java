package com.example.whatsappclone.providers;

import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.Status;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatusProviders {

    private CollectionReference mCollection;

    public StatusProviders(){
        mCollection= FirebaseFirestore.getInstance().collection("Status");
    }

    // clase para guardar todo sobre los estados
    public Task<Void> create(Status status)
    {
        DocumentReference document = mCollection.document();
        //sabemos el id aleatorio
        status.setId(document.getId());
        return document.set(status);
    }

    //traerme todos los stados

    public Query obtenerLainformacionByTiempoLimiteStatus(){

        long now = new Date().getTime();

        //que me traiga todos los estados que no sean mayores a timestampLimit
        return mCollection.whereGreaterThan("timestampLimit",now);

    }
}
