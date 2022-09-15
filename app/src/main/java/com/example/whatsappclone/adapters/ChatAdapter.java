package com.example.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.whatsappclone.providers.MessageProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.example.whatsappclone.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

//FirestoreRecyclerAdapter le paso por parametro el modelo usuario que es donde voy a obtener los datos de firebase
public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.ViewHolder> {

    Context context;
    AuthProviders mAuthProvider;
    UsersProviders usersProviders;
    MessageProviders messageProviders;
    User user;
    ListenerRegistration listener;
    ListenerRegistration listenerClassMessage;

    ListenerRegistration listenerEscribiendo;

    //creamos un contructor publico
    public ChatAdapter(FirestoreRecyclerOptions options, Context context){
         super(options);
         this.context=context;
        mAuthProvider= new AuthProviders();
        usersProviders = new UsersProviders();
        messageProviders = new MessageProviders();
        user = new User();
    }

   //aqui es donde vamos a establecer los valores que vienen directamente desde la base de datos
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Chat chat) {

        String idUser="";
        for(int i =0; i<chat.getIds().size(); i++){
              if(!mAuthProvider.getIdAutenticado().equals(chat.getIds().get(i))){
                  idUser=chat.getIds().get(i);
                  break;
              }
        }

        //metodo para obtener la ultima informacion mensaje y mostrarlo
        obtenerUltimoMensajeChat(holder,chat.getId());

        //le envio al metodo el objeto y el id que quiero obtener la informacion
        getUserInfo(holder,idUser);
        
        //para obtener los mensaje no leido
        getMessageNoLeido(holder,chat.getId());
        
        clickGotoo(holder,chat.getId(),idUser);

        //mostrar escribiendo en la lista de los chat cuando el usuario esta escribiendo
        showEscribiendo(holder, chat);

    }

    private void showEscribiendo(ViewHolder holder, Chat chat){

        if(chat.getEscribiendo()!=null){
            if(!chat.getEscribiendo().equals("")){
                if(!chat.getEscribiendo().equals(mAuthProvider.getIdAutenticado())){
                    holder.textviewUsernameEscribiendo.setVisibility(View.VISIBLE);
                    holder.textviewUsernameultimosms.setVisibility(View.GONE);
                }else{
                    holder.textviewUsernameEscribiendo.setVisibility(View.GONE);
                    holder.textviewUsernameultimosms.setVisibility(View.VISIBLE);
                }
            }else{
                holder.textviewUsernameEscribiendo.setVisibility(View.GONE);
                holder.textviewUsernameultimosms.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getMessageNoLeido(final ViewHolder holder, String idChat) {

        messageProviders.getMessageRecibidoNoLeido(idChat, mAuthProvider.getIdAutenticado()).addSnapshotListener(new EventListener<QuerySnapshot>( ) {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                      if(querySnapshot!=null){
                          int tam=querySnapshot.size();
                          if(tam>0){
                              //si tenemos mensajes sin leer vamos a mostrar
                              holder.frameLayoutMessageNoLeido.setVisibility(View.VISIBLE);
                              holder.textviewMensajeNoLeido.setText(String.valueOf(tam));
                              holder.textviewfecha.setTextColor(context.getResources().getColor(R.color.colorGreenAcceeee));

                          }else{
                              holder.frameLayoutMessageNoLeido.setVisibility(View.GONE);
                              holder.textviewfecha.setTextColor(context.getResources().getColor(R.color.colorGreyDart));
                          }
                      }
            }
        });
    }

    private void obtenerUltimoMensajeChat(final ViewHolder holder,String idChat) {

        listenerClassMessage=messageProviders.obtenerUltimoMessageChat(idChat).addSnapshotListener(new EventListener<QuerySnapshot>( ) {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {

                if(querySnapshot!=null){
                    int size=querySnapshot.size();
                    if(size>0){
                        //estamos obteniendo el ultimo mensaje de la base de dato
                        Message message = querySnapshot.getDocuments().get(0).toObject(Message.class);
                       holder.textviewUsernameultimosms.setText(message.getMessage());
                       holder.textviewfecha.setText(RelativeTime.timeFormatAMPM(message.getTimeTamp(),context));

                       if(message.getIdEnviar().equals(mAuthProvider.getIdAutenticado())){
                           holder.imageViewCheck.setVisibility(View.VISIBLE);
                           if(message.getStatus().equals("ENVIADO")){
                             holder.imageViewCheck.setImageResource(R.drawable.icon_marca_doble_grey);
                           }
                           else if(message.getStatus().equals("VISTO")){
                               holder.imageViewCheck.setImageResource(R.drawable.icon_marca_doble_blue);
                           }

                       }else{
                           holder.imageViewCheck.setVisibility(View.GONE);
                       }

                    }
                }
            }
        });

    }

    private void clickGotoo(ViewHolder holder, final String idChat,final String idUser){
        holder.myview.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                gotoActivityChat(idChat, idUser);
            }
        });
    }

    private void gotoActivityChat( String idChat,String idUser) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat",idChat);
        intent.putExtra("idUser",idUser);
        context.startActivity(intent);
    }

    private void getUserInfo(ViewHolder holder, String idUser) {

        listener=usersProviders.getUserInfo(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

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

    public ListenerRegistration getlistenerChat(){
        return listener;
    }

    public ListenerRegistration getlistenerMessage(){
        return listenerClassMessage;
    }





    //la parte donde necesitariamos instaciar la vista en donde queremos trabajar o el layau
       @NonNull
       @Override
       public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat,parent,false);
          //retornamos a la function que acabamos de crear y le pasamos la view del cardview_contacts
           return new ViewHolder(view);
       }


    public class ViewHolder extends RecyclerView.ViewHolder{

           TextView mUsername, textviewfecha,textviewUsernameultimosms,textviewMensajeNoLeido,textviewUsernameEscribiendo;
           CircleImageView circleImageUser;
           ImageView imageViewCheck;
           FrameLayout frameLayoutMessageNoLeido;

           //cuando presionemos en un contacto me envie a la activity de chat
        View myview;
        //nos creamos un contructor
        public ViewHolder(View view){
            super(view);

            mUsername=view.findViewById(R.id.textviewUsername);
            textviewfecha=view.findViewById(R.id.textviewfecha);
            circleImageUser=view.findViewById(R.id.circle_image_user);
            imageViewCheck=view.findViewById(R.id.imagviewcheck);
            textviewUsernameultimosms=view.findViewById(R.id.textviewUsernameUltimosms);
            frameLayoutMessageNoLeido=view.findViewById(R.id.frameLayoutMessageNoLeido);
            textviewMensajeNoLeido=view.findViewById(R.id.textviewMensajeNoLeido);
            textviewUsernameEscribiendo= view.findViewById(R.id.textviewUsernameEscribiendo);
            myview=view;

        }
    }
}
