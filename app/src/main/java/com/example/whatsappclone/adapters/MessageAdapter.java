package com.example.whatsappclone.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ChatActivity;
import com.example.whatsappclone.models.Chat;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.example.whatsappclone.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

//FirestoreRecyclerAdapter le paso por parametro el modelo usuario que es donde voy a obtener los datos de firebase
public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolder> {

    Context context;
    AuthProviders mAuthProvider;
    UsersProviders usersProviders;
    User user;
    ListenerRegistration listener;

    //creamos un contructor publico
    public MessageAdapter(FirestoreRecyclerOptions options, Context context){
         super(options);
         this.context=context;
        mAuthProvider= new AuthProviders();
        usersProviders = new UsersProviders();
        user = new User();
    }

   //aqui es donde vamos a establecer los valores que vienen directamente desde la base de datos
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Message message) {

          holder.textViewMessage.setText(message.getMessage());
          holder.textViewDate.setText(RelativeTime.timeFormatAMPM(message.getTimeTamp(),context));

          //para colocar el corner right o left
        //tenemos que saber si nosotros somos los que mandamos el mensaje
        if(message.getIdEnviar().equals(mAuthProvider.getIdAutenticado())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    //recibe dos parametros
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            //QUE NUESTRO MENSAJE SE POSECIONE A LA DERECHA
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //POSECIONE UNOS MARGENES
            params.setMargins(150,0,0,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30,20,50,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_right));
            holder.textViewMessage.setTextColor(Color.BLACK);
            holder.textViewDate.setTextColor(Color.DKGRAY);
            holder.imageViewCheck.setVisibility(View.VISIBLE);
        }else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    //recibe dos parametros
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            //QUE NUESTRO MENSAJE SE POSECIONE A LA DERECHA
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            //POSECIONE UNOS MARGENES
            params.setMargins(0,0,150,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(80,20,30,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_left));
            holder.textViewMessage.setTextColor(Color.BLACK);
            holder.textViewDate.setTextColor(Color.DKGRAY);
            holder.imageViewCheck.setVisibility(View.GONE);
        }


        if(message.getStatus().equals("ENVIADO")){
            holder.imageViewCheck.setImageResource(R.drawable.icon_marca_doble_grey);
        }
        if(message.getStatus().equals("VISTO")){
            holder.imageViewCheck.setImageResource(R.drawable.icon_marca_doble_blue);
        }


        showDocumento(holder,message);
        showImage(holder,message);
        descargarFile(holder, message);


    }

    private void descargarFile(ViewHolder holder, Message message) {
        holder.myview.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                if(message.getType().equals("documento")){
                    File file = new File(context.getExternalFilesDir(null),"file");
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(message.getUrl()))
                            .setTitle(message.getMessage())
                            .setDescription("Descargando...")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setDestinationUri(Uri.fromFile(file))
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true);

                    DownloadManager downloadManager =(DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                    //PARA QUE EMPIECE HACER LA DESCARGA
                    downloadManager.enqueue(request);
                }
            }
        });
    }


    private void showImage(ViewHolder holder, Message message) {

        if (message.getType().equals("imagen")) {
            if (message.getUrl() != null) {
                if (!message.getUrl().equals("")) {
                    holder.imageViewMessage.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(message.getUrl()).into(holder.imageViewMessage);

                    if (message.getMessage().equals("\uD83D\uDCF7imagen")) {
                        holder.textViewMessage.setVisibility(View.GONE);
                        //holder.textViewDate.setPadding(0,0,10,0);
                        ViewGroup.MarginLayoutParams marginDate = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
                        ViewGroup.MarginLayoutParams marginCheck = (ViewGroup.MarginLayoutParams) holder.imageViewCheck.getLayoutParams();
                        marginDate.topMargin = 15;
                        marginCheck.topMargin = 15;

                    }
                    else {
                        holder.textViewMessage.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    holder.imageViewMessage.setVisibility(View.GONE);
                    holder.textViewMessage.setVisibility(View.VISIBLE);
                }
            }
            else {
                holder.imageViewMessage.setVisibility(View.GONE);
                holder.textViewMessage.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.imageViewMessage.setVisibility(View.GONE);
            holder.textViewMessage.setVisibility(View.VISIBLE);
        }

    }

    private void showDocumento(ViewHolder holder, Message message) {

        if(message.getType().equals("documento")){
            if(message.getUrl()!=null){
                if(!message.getUrl().equals("")){
                    holder.LinearLayoutDocuments.setVisibility(View.VISIBLE);
                }else{
                    holder.LinearLayoutDocuments.setVisibility(View.GONE);
                }
            }
            else{
            holder.LinearLayoutDocuments.setVisibility(View.GONE);
        }
        }else{
            holder.LinearLayoutDocuments.setVisibility(View.GONE);
        }
    }


    public ListenerRegistration getlistenerChat(){
        return listener;
    }



    //la parte donde necesitariamos instaciar la vista en donde queremos trabajar o el layau
       @NonNull
       @Override
       public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message,parent,false);
          //retornamos a la function que acabamos de crear y le pasamos la view del cardview_contacts
           return new ViewHolder(view);
       }


    public class ViewHolder extends RecyclerView.ViewHolder{


           TextView  textViewMessage,textViewDate;
           ImageView imageViewCheck, imageViewMessage;
           LinearLayout linearLayoutMessage,LinearLayoutDocuments;

           //cuando presionemos en un contacto me envie a la activity de chat
        View myview;
        //nos creamos un contructor
        public ViewHolder(View view){
            super(view);

            textViewMessage=view.findViewById(R.id.textViewMessage);
            textViewDate=view.findViewById(R.id.textViewDate);
            imageViewCheck=view.findViewById(R.id.imagViewCheckk);
            imageViewMessage=view.findViewById(R.id.imageViewMessage);
            linearLayoutMessage=view.findViewById(R.id.linearLayautMessage);
            LinearLayoutDocuments=view.findViewById(R.id.LinearLayoutDocuments);
            myview=view;

        }
    }
}
