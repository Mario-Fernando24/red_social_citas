package com.example.whatsappclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

//FirestoreRecyclerAdapter le paso por parametro el modelo usuario que es donde voy a obtener los datos de firebase
public class ContactAdapter extends FirestoreRecyclerAdapter<User,ContactAdapter.ViewHolder> {

    Context context;

    //creamos un contructor publico
    public ContactAdapter(FirestoreRecyclerOptions options, Context context){
         super(options);
         this.context=context;
    }

   //aqui es donde vamos a establecer los valores que vienen directamente desde la base de datos
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User user) {
        holder.mUsernameContact.setText(user.getUsername());
        holder.mInfoContact.setText(user.getInfo());
        if(user.getImage()!=null){
            if(!user.getImage().equals("")){
                Picasso.with(context).load(user.getImage()).into(holder.circleImageContact);
            }else{
                holder.circleImageContact.setImageResource(R.drawable.ic_people_white);
            }
        }else{
            holder.circleImageContact.setImageResource(R.drawable.ic_people_white);
        }
    }


       //la parte donde necesitariamos instaciar la vista en donde queremos trabajar o el layau
       @NonNull
       @Override
       public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contacts,parent,false);
          //retornamos a la function que acabamos de crear y le pasamos la view del cardview_contacts
           return new ViewHolder(view);
       }


    public class ViewHolder extends RecyclerView.ViewHolder{

           TextView mUsernameContact, mInfoContact;
           CircleImageView circleImageContact;
        //nos creamos un contructor
        public ViewHolder(View view){
            super(view);

            mUsernameContact=view.findViewById(R.id.textviewUsernamecontant);
            mInfoContact=view.findViewById(R.id.textviewInfocontant);
            circleImageContact=view.findViewById(R.id.circle_image_user);

        }
    }
}
