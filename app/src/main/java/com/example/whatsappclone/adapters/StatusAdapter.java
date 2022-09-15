package com.example.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ChatActivity;
import com.example.whatsappclone.activities.StatusDetailActivity;
import com.example.whatsappclone.models.Chat;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.MessageProviders;
import com.example.whatsappclone.providers.StoryViewProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.example.whatsappclone.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    FragmentActivity context;
    AuthProviders mAuthProvider;
    UsersProviders usersProviders;
    MessageProviders messageProviders;
    StoryViewProviders storyViewProviders;
    User user;

    //recibimos un arrayList de tipo status
    ArrayList<Status> statusArrayList;

    Gson gson = new Gson();

    //creamos un contructor publico
    public StatusAdapter(FragmentActivity context, ArrayList<Status> arrayList){

        this.context=context;
        this.statusArrayList=arrayList;
        mAuthProvider= new AuthProviders();
        usersProviders = new UsersProviders();
        messageProviders = new MessageProviders();
        storyViewProviders = new StoryViewProviders();
        user = new User();

    }


    //metodo para obtener la informacion de los usuarios para mostrarlo en los estados

    private void getUserInfo(ViewHolder holder, String idUser) {

        Log.d("","=================");
        Log.d("","mario"+idUser);
        Log.d("","=================");
        usersProviders.getUserInfo(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot!=null){
                    if(documentSnapshot.exists()){
                        //obtenemos la informacion del usuario por id
                        user=documentSnapshot.toObject(User.class);
                        holder.textviewUsername.setText(user.getUsername());


                    }
                }

            }
        });

    }






    //la parte donde necesitariamos instaciar la vista en donde queremos trabajar o el layau
       @NonNull
       @Override
       public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_status,parent,false);
          //retornamos a la function que acabamos de crear y le pasamos la view del cardview_contacts
           return new ViewHolder(view);
       }


       //
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       final Status[] statusJson =  gson.fromJson(statusArrayList.get(position).getJson(),Status[].class);
        //tratar de partir en cuantas porciones se dividira el circleStatus
        holder.circular_status_view.setPortionsCount(statusJson.length);

        setPorcionColor(statusJson,holder,position);
        setImageDate(statusJson, holder);
        //llamamos a nuestro metodo que nos trae toda la informacion de los usuarios y le pasamos por parametro
        //el holder y el id del usuario (trabajando con el libreria de java nativa
        getUserInfo(holder, statusArrayList.get(position).getIdUser());


        holder.myview.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( context, StatusDetailActivity.class);
                // le pasamos por parametros todos los estados que ese usuario ha publicado
                intent.putExtra("status",statusArrayList.get(position).getJson());

                //significa que el usuario ya observo todos los estados
                if((statusArrayList.get(position).getCounterr()==statusJson.length) || statusArrayList.get(position).getCounterr()==0){
                   intent.putExtra("counterr",0);
                }else{
                    intent.putExtra("counterr",statusArrayList.get(position).getCounterr());
                }

                context.startActivity(intent);
            }
        });

    }

    private void setPorcionColor(Status[] statusJson, ViewHolder holder, int position) {

        //recorremos cada uno de los estados
        for (int i=0; i<statusJson.length; i++){
            //le pasamos como parametro la convinacion entre el id de la persona autenticada y id del estado
            int finalI = i;
            storyViewProviders.getStoryViewById(mAuthProvider.getIdAutenticado() + statusJson[i].getId()).addSnapshotListener(context,new EventListener<DocumentSnapshot>( ) {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                             if (documentSnapshot!=null){
                                 //para saber si el usuario ya miro un estado
                                 if(documentSnapshot.exists()){
                                     //que vio el estado
                                     holder.circular_status_view.setPortionColorForIndex(finalI,context.getResources().getColor(R.color.colorGreyStatus));
                                     //la ultima posicion que quedo (el ultimo estado que vio
                                     statusArrayList.get(position).setCounterr(finalI+1);

                                 }else{
                                     //que no ha visto el estado
                                     holder.circular_status_view.setPortionColorForIndex(finalI,context.getResources().getColor(R.color.colorGreenStatus));

                                 }
                             }else{
                                 holder.circular_status_view.setPortionColorForIndex(finalI,context.getResources().getColor(R.color.colorGreenStatus));
                                 statusArrayList.get(position).setCounterr(0);
                             }
                }
            });
        }
    }

    private void setImageDate(Status[] statusJson, ViewHolder holder) {

        if(statusJson.length>0){
            Picasso.with(context).load(statusJson[statusJson.length-1].getUrl()).into(holder.circleImageUser);
            String relativeTime = RelativeTime.timeFormatAMPM(statusJson[statusJson.length-1].getTimestamp(),context);
            holder.textviewDate.setText(relativeTime);
        }
    }

    @Override
    public int getItemCount() {
        return statusArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

           TextView textviewUsername,textviewDate;
           CircleImageView circleImageUser;
           CircularStatusView circular_status_view;


           //cuando presionemos en un contacto me envie a la activity de chat
        View myview;
        //nos creamos un contructor
        public ViewHolder(View view){
            super(view);

            textviewUsername=view.findViewById(R.id.textviewUsername);
            textviewDate=view.findViewById(R.id.textviewDate);
            circleImageUser=view.findViewById(R.id.circle_image_user);
            circular_status_view =view.findViewById(R.id.circular_status_view);
            myview=view;

        }
    }
}
