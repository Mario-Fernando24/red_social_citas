package com.example.whatsappclone.providers;

import com.example.whatsappclone.models.StoryView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class StoryViewProviders {

    private CollectionReference mCollection;
    AuthProviders authProviders;

    public StoryViewProviders(){
        mCollection= FirebaseFirestore.getInstance().collection("StatusView");
        authProviders = new AuthProviders();
    }

    public void create(final StoryView storyView){

        //validamos si el documento ya existe en la base de datos para que no se actualice la fecha de nuevo
        mCollection.document(storyView.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>( ) {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    mCollection.document(storyView.getId()).set(storyView);
                }
            }
        });
        mCollection.document(storyView.getId()).set(storyView);
    }


    public Query getStoryByIdStory(String idStory){
       return   mCollection.whereEqualTo("idStatus",idStory).orderBy("timestamp", Query.Direction.DESCENDING);
    }



    //nos retornara los id
    public DocumentReference getStoryViewById(String id){
        return mCollection.document(id);
    }
}
