package com.example.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.StoryView;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.MessageProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.example.whatsappclone.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

//FirestoreRecyclerAdapter le paso por parametro el modelo usuario que es donde voy a obtener los datos de firebase
public class StatusViewAdapter extends FirestoreRecyclerAdapter<StoryView, StatusViewAdapter.ViewHolder> {

    Context context;
    AuthProviders mAuthProvider;
    UsersProviders usersProviders;
    MessageProviders messageProviders;
    User user;

    //creamos un contructor publico
    public StatusViewAdapter(FirestoreRecyclerOptions options, Context context){
         super(options);
         this.context=context;
        mAuthProvider= new AuthProviders();
        usersProviders = new UsersProviders();
        messageProviders = new MessageProviders();
        user = new User();
    }

   //aqui es donde vamos a establecer los valores que vienen directamente desde la base de datos
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final StoryView storyView) {

        String relatimeTime= RelativeTime.timeFormatAMPM(storyView.getTimestamp(),context);
        holder.textviewfecha.setText(relatimeTime);
        //le envio al metodo el objeto y el id que quiero obtener la informacion
        getUserInfo(holder,storyView.getIdUser());


    }


    private void getUserInfo(ViewHolder holder, String idUser) {

        usersProviders.getUserInfo(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>( ) {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot!=null){
                    if(documentSnapshot.exists()){
                        //obtenemos la informacion del usuario por id
                        user=documentSnapshot.toObject(User.class);
                        holder.mUsername.setText(user.getUsername());

                        if(user.getImage()!=null){
                            if(!user.getImage().equals("")){
                                Picasso.with(context).load(user.getImage()).into(holder.circleImageUser);
                            }else{
                                holder.circleImageUser.setImageResource(R.drawable.ic_people_white);
                            }
                        }else{
                            holder.circleImageUser.setImageResource(R.drawable.ic_people_white);
                        }

                    }
                }
            }
        });

    }



    //la parte donde necesitariamos instaciar la vista en donde queremos trabajar o el layau
       @NonNull
       @Override
       public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_status_view,parent,false);
          //retornamos a la function que acabamos de crear y le pasamos la view del cardview_contacts
           return new ViewHolder(view);
       }


    public class ViewHolder extends RecyclerView.ViewHolder{

           TextView mUsername, textviewfecha;
           CircleImageView circleImageUser;

        View myview;
        //nos creamos un contructor
        public ViewHolder(View view){
            super(view);

            mUsername=view.findViewById(R.id.textviewUsername);
            textviewfecha=view.findViewById(R.id.textviewfecha);
            circleImageUser=view.findViewById(R.id.circle_image_user);
            myview=view;

        }
    }
}
