package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ChatAdapter;
import com.example.whatsappclone.adapters.MessageAdapter;
import com.example.whatsappclone.models.Chat;
import com.example.whatsappclone.models.FCMBody;
import com.example.whatsappclone.models.FCMResponse;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.ChatProviders;
import com.example.whatsappclone.providers.FilesProviders;
import com.example.whatsappclone.providers.ImagProviders;
import com.example.whatsappclone.providers.MessageProviders;
import com.example.whatsappclone.providers.NotificationProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.example.whatsappclone.utils.AppBackgroundHelper;
import com.example.whatsappclone.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG= ImagProviders.class.getSimpleName();
    String mExtraIdUser;
    String mExtraIdChat;
    String nameUserDestinatario;
    UsersProviders usersProviders;
    AuthProviders authProviders;
    FilesProviders filesProviders;
    NotificationProviders notificationProviders;
    TextView textViewUsernam,textViewOnline;
    CircleImageView circleImagUser;
    ChatProviders chatProviders;
    EditText editextMessage;
    ImageView imgViewSend,imgViewSelectCamera,imgViewSelectFile;
    MessageProviders messageProviders;

    //obtengo la informacion del usuario del que estoy chateando
    User mmuser;
    User myUsuario;


    Timer mtimer;
    Chat mchat;

    //llamamos el adapter de message Adapter
    MessageAdapter messageAdapter;
    RecyclerView recyclerViewMessage;
    LinearLayoutManager linearLayoutManager;
    ListenerRegistration listenerEscribiendo;

    //para utilizar la camara
    Options mOptions;
    ArrayList<String> mReturnValues = new ArrayList<>();
    LinearLayout profileIndividual;


    final int ACTION_FILES=2;
    //creo un arreglo de archivos
    ArrayList<Uri> mFilesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        showChatToolbarPersonalizado(R.layout.chat_toolbar);
        toolbarColorPersonalizado();

        //recibo el id del usuario que quiero mostrar en el chat
        mExtraIdUser=getIntent().getStringExtra("idUser");
        //recibo el id del chat que quiero mostrar en el chat
        mExtraIdChat=getIntent().getStringExtra("idChat");

        editextMessage=findViewById(R.id.editextMessage);
        imgViewSend=findViewById(R.id.imgViewSend);
        imgViewSelectCamera=findViewById(R.id.imgViewSelectCamera);
        imgViewSelectFile=findViewById(R.id.imgViewSelectFile);
        profileIndividual = findViewById(R.id.profileIndividual);
        messageProviders = new MessageProviders();
        notificationProviders = new NotificationProviders();
        filesProviders = new FilesProviders();
        recyclerViewMessage = findViewById(R.id.recicleViewMessage);
        //para decir que la informacion que se mostrara en el linearLayout se mostrara uno debajo del otro
        linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessage.setLayoutManager(linearLayoutManager);
       // para llamar al adapter message utilizaremos los ciclos de vidad de las activity

        imgViewSend.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                createMessage();
            }
        });

        imgViewSelectCamera.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                starPix();
            }
        });

        imgViewSelectFile.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                selectFiles();
            }
        });


        mOptions = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(5)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(mReturnValues)                               //Pre selected Image Urls
                .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                .setMode(Options.Mode.All)                                     //Option to select only pictures or videos or both
                .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");


        profileIndividual.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                profileIndividual();
            }
        });


        usersProviders = new UsersProviders();
        authProviders = new AuthProviders();
        chatProviders = new ChatProviders();

        //creo un metodo para consultar los datos del usuario que escogi
        getUserInfo();
        //obtengo la informacion del usuario propio
        getUserInfoMyUsuario();
        //metodo para crear el chat
        chatExisteValidate();

        //metodo para saber si el usuario esta escribiendo
        escribiendoChat();



    }




    private void selectFiles() {
        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), ACTION_FILES);
    }

    private void escribiendoChat() {

        editextMessage.addTextChangedListener(new TextWatcher( ) {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
              //nos permite si el usuario esta escribiendo
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(mtimer!=null){
                if(mExtraIdChat!=null) {
                    chatProviders.updateEscribiendo(mExtraIdChat, authProviders.getIdAutenticado( ));
                    //ejecuto que el time deje de escuhar
                    mtimer.cancel();
                 }
                }

            }
          //para saber si el usuario dejo de escribir
            @Override
            public void afterTextChanged(Editable editable) {
                mtimer = new Timer();
                mtimer.schedule(new TimerTask( ) {
                    @Override
                    public void run() {
                        if(mExtraIdChat!=null){
                            //ejecutar una tarea despues de cierto tiempo
                            chatProviders.updateEscribiendo(mExtraIdChat, "");

                        }
                    }
                }
                //tiempo de retrazo
                ,3000);
            }
        });
    }


    //ciclo de vidad de la ctivity
    @Override
    protected void onStart() {
        super.onStart( );
        AppBackgroundHelper.online(ChatActivity.this,true);

        if(messageAdapter!=null){
            messageAdapter.startListening();
        }


    }

    @Override
    protected void onStop() {
        super.onStop( );
        if(messageAdapter!=null){
            messageAdapter.stopListening();
        }
        AppBackgroundHelper.online(ChatActivity.this,false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy( );
        if(listenerEscribiendo!=null){
            listenerEscribiendo.remove();
        }
    }


    private void starPix() {
        Pix.start(ChatActivity.this,mOptions);
    }




    private void createMessage() {

        String textEnviado=editextMessage.getText().toString();

        editextMessage.setText("");

        if(!textEnviado.equals("")){
            Message message = new Message();
            message.setIdChat(mExtraIdChat);
            message.setIdEnviar(authProviders.getIdAutenticado());
            message.setIdRecibido(mExtraIdUser);
            message.setMessage(textEnviado);
            message.setStatus("ENVIADO");
            message.setType("texto");
            message.setTimeTamp(new Date().getTime());


            messageProviders.create(message).addOnSuccessListener(new OnSuccessListener<Void>( ) {
                @Override
                public void onSuccess(Void unused) {

                    editextMessage.setText("");
                    //para que haga scrool cuando se envia el sms
                    if(messageAdapter!=null){
                        messageAdapter.notifyDataSetChanged();
                    }
                    chatProviders.updateNumberMessage(mExtraIdChat);
                    //llamamos al metodo de la notificacion
                    getUltimosMensaje(message);
                    Toast.makeText(ChatActivity.this,"El mensaje se creo correctamente",Toast.LENGTH_LONG);
                }
            });
        }else{
            Toast.makeText(ChatActivity.this,"Debe ingresar el mensaje",Toast.LENGTH_LONG);

        }
    }


    //metodo que nos permita obtener los ultimos 5 mensajes
    private void getUltimosMensaje(final Message message){

        messageProviders.obtenerLosUltimosCincoMensaggesChat(mExtraIdChat,authProviders.getIdAutenticado())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>( ) {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots!=null){
                    ArrayList<Message> messagesArray = new ArrayList<>();

                    for (DocumentSnapshot documentSnapshot: documentSnapshots.getDocuments()){
                        Message m = documentSnapshot.toObject(Message.class);
                        messagesArray.add(m);
                    }

                    if(messagesArray.size()==0){
                     messagesArray.add(message);
                    }

                    //voltear la lista
                    Collections.reverse(messagesArray);
                    enviarNotificacion(messagesArray);

                }
            }
        });

    }


    private void enviarNotificacion(ArrayList<Message> messages) {

        Map<String, String> data = new HashMap<>();
        data.put("title","NUEVO MENSAJE");
        data.put("body","");
        data.put("idNotification", String.valueOf(mchat.getIdNotification()));
        data.put("usersRecibe",myUsuario.getUsername());
        //usuario que envia el mensaje
        data.put("userPropio",mmuser.getUsername());
        data.put("imagenUsuarioEnvia",myUsuario.getImage());
        //pasar array a json
        Gson gson = new Gson();
        String mensajesJson=gson.toJson(messages);
        data.put("mensajesJSON",mensajesJson);

        data.put("idChat",mExtraIdChat);
        data.put("idUsuarioEnvia",mmuser.getId());

        data.put("idUsuarioRecibe",authProviders.getIdAutenticado());

        data.put("tokenSender",mmuser.getToken());
        data.put("tokenReceptor",myUsuario.getToken());
        Log.d("mario fer"," => "+myUsuario.getToken());

         notificationProviders.sendd(ChatActivity.this,mmuser.getToken(),data);

    }

    private void chatExisteValidate() {

        chatProviders.validateIdUsersTwo(authProviders.getIdAutenticado(),mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    if (queryDocumentSnapshots.size() == 0) {
                        createChat();
                    }
                    else {
                        mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();

                        getMessageChat();
                        //CUANDO TENGA EL ID DEL CHAT CORRESPONDIENTE LOS ACTUALIZO LOS MENSAJE A VISTO
                        updateStatus();
                        //metodo que me permita saber en tiempo real si el usuario esta escribiendo teniendo en cuenta que el  campo del chat "escribiendo sea diferente de vacio"
                        getChatInfoEscribiendo();
                        // Toast.makeText(ChatActivity.this, "El chat entre dos usuarios ya existe", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public void getChatInfoEscribiendo(){
        if(mExtraIdChat!=null){
            listenerEscribiendo= chatProviders.getChatEscribiendo(mExtraIdChat).addSnapshotListener(new EventListener<DocumentSnapshot>( ) {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                  if(documentSnapshot!=null){
                      if(documentSnapshot.exists()){
                           mchat = documentSnapshot.toObject(Chat.class);
                          if(mchat.getEscribiendo()!=null){
                              if(!mchat.getEscribiendo().equals("")){
                                  if(!mchat.getEscribiendo().equals(authProviders.getIdAutenticado())){
                                      textViewOnline.setText("escribiendo...");
                                  }
                                  else if(mmuser!=null){
                                      if(mmuser.getOnline()){
                                          textViewOnline.setText("en linea");
                                      }else{
                                          String tiempoo= RelativeTime.getTimeAgo(mmuser.getUltimaConexionOnline(),ChatActivity.this);
                                          if(tiempoo!=null){
                                              textViewOnline.setText(tiempoo);
                                          }

                                      }
                                   //   textViewOnline.setText("");
                                  }else{
                                      textViewOnline.setText("");
                                  }
                              } else if(mmuser!=null){
                                  if(mmuser.getOnline()){
                                      textViewOnline.setText("en linea");
                                  }else{
                                      String tiempoo= RelativeTime.getTimeAgo(mmuser.getUltimaConexionOnline(),ChatActivity.this);
                                      if(tiempoo!=null){
                                          textViewOnline.setText(tiempoo);
                                      }

                                  }
                                  //   textViewOnline.setText("");
                              }else{
                                  textViewOnline.setText("");
                              }
                          }

                      }
                  }
                }
            });
        }
    }



    private void updateStatus() {

        messageProviders.getMessageNoLeido(mExtraIdChat).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>( ) {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

               // contiene los documento que fueron encontrado en la consulta
             for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){
                 Message message= documentSnapshot.toObject(Message.class);
                 //la persona que envio no se igual al usuario que esta en sesion
                 if(!message.getIdEnviar().equals(authProviders.getIdAutenticado())){
                     messageProviders.updateStatus(message.getId(),"VISTO");
                 }
             }
            }
        });
    }

    private void getMessageChat() {
        Query query=messageProviders.idMessageByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();

        messageAdapter = new MessageAdapter(options,ChatActivity.this);
        recyclerViewMessage.setAdapter(messageAdapter);
        //que empiece a escuchar
        messageAdapter.startListening();

        //para saber en que momento se envio un nuevo mensaje al usuario
        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver( ) {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                updateStatus();
                //obtenemos el numero de los sms que tenemos
                int numMessage=messageAdapter.getItemCount();
                //obtemos la ultima posicion
                int ultimaPosicion=linearLayoutManager.findLastCompletelyVisibleItemPosition();

                //si el ultimo mensaje es igual a menos 1
                if(ultimaPosicion==-1 || (positionStart>=(numMessage-1) && ultimaPosicion==(positionStart-1))){
                    recyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void createChat() {

        mchat = new Chat();
        //combinacion de mi id con el de la persona que voy a enviarle el mensaje
        mchat.setId(authProviders.getIdAutenticado()+mExtraIdUser);
        //Hora exacta que se creo el chat
        mchat.setTimesTamp(new Date().getTime());
        //id de los usuario que estaran chateando
        ArrayList<String> ids = new ArrayList<>();
        //esto seria un usuario yo
        ids.add(authProviders.getIdAutenticado());
        //el otro usuario que le chateara
        ids.add(mExtraIdUser);
        //lo guardamos en el array
        mchat.setIds(ids);

        //
        mchat.setEscribiendo("");

        Random random = new Random();
        int numeroAleatorio=random.nextInt(10000);
        mchat.setIdNotification(numeroAleatorio);

         //=====================================================================================
        mchat.setNumberMessage(0);
         //======================================================================================

        mExtraIdChat=mchat.getId();
        //pasamos el modelo chat al create
        chatProviders.create(mchat).addOnCompleteListener(new OnCompleteListener<Void>( ) {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getMessageChat();
                Toast.makeText(ChatActivity.this,"El chat se creo correctamente",Toast.LENGTH_LONG);
            }
        });
    }

    private void getUserInfo() {
        //hacemos la consulta y nos traemos la informacion del usuario en tiempo real
        usersProviders.getUserInfo(mExtraIdUser).addSnapshotListener(new EventListener<DocumentSnapshot>( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                     if(documentSnapshot!=null){
                         if(documentSnapshot.exists()){
                             //mapeando la informacion del usuario
                             mmuser = documentSnapshot.toObject(User.class);
                             textViewUsernam.setText(mmuser.getUsername());
                             nameUserDestinatario=mmuser.getUsername();

                             if(mmuser.getOnline()){
                                 textViewOnline.setText("en linea");
                             }else{
                                 String tiempoo= RelativeTime.getTimeAgo(mmuser.getUltimaConexionOnline(),ChatActivity.this);
                                  if(tiempoo!=null){
                                      textViewOnline.setText(tiempoo);
                                  }

                             }
                             if(mmuser.getImage()!=null){
                                 if(!mmuser.getImage().equals("")){
                                     Picasso.with(ChatActivity.this).load(mmuser.getImage()).into(circleImagUser);
                                 }else{
                                     circleImagUser.setImageResource(R.drawable.ic_people_white);
                                 }
                             }else{
                                 circleImagUser.setImageResource(R.drawable.ic_people_white);
                             }


                         }
                     }
            }
        });
    }


    private void getUserInfoMyUsuario() {
        //hacemos la consulta y nos traemos la informacion del usuario en tiempo real
        usersProviders.getUserInfo(authProviders.getIdAutenticado()).addSnapshotListener(new EventListener<DocumentSnapshot>( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot!=null){
                    if(documentSnapshot.exists()){
                        //mapeando la informacion del usuario
                        myUsuario = documentSnapshot.toObject(User.class);
                        }
                    }
                }
        });
    }


    public void showChatToolbarPersonalizado(int resultado){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater =(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resultado,null);
        actionBar.setCustomView(view);

        ImageView imageView = view.findViewById(R.id.imgViewBack);
        textViewUsernam = view.findViewById(R.id.textViewUsernam);
        circleImagUser= view.findViewById(R.id.circleImagUser);
        textViewOnline= view.findViewById(R.id.textViewOnline);

        imageView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    //metodo que nos permite capturar los valores que el usuario selecciono
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mReturnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Intent intent = new Intent(ChatActivity.this, ConfirmarImagenSeleccionadaActivity.class);
            intent.putExtra("data", mReturnValues);
            intent.putExtra("mExtraChat", mExtraIdChat);
            intent.putExtra("idReceiver", mExtraIdUser);
            startActivity(intent);
        }
        //si el usuario selecciono un archivo file
        if(requestCode==ACTION_FILES && resultCode==RESULT_OK){
            mFilesList= new ArrayList<>();
            ClipData clipData = data.getClipData();

            //SELECCIONO UN SOLO ARCHIVO
            if(clipData==null){
                Uri uri = data.getData();
                mFilesList.add(uri);
            }
            //si no el usuario selecciono mas de un archivo
            else{
                //capturamos el numero de archivos que el usuario selecciono
                int count=clipData.getItemCount();
                for(int i=0;i<count; i++){
                    Uri uri = clipData.getItemAt(i).getUri();
                    mFilesList.add(uri);
                }
            }

            //guardamos el archivo
            filesProviders.saveFile(ChatActivity.this,mFilesList,mExtraIdChat,mExtraIdUser);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(ChatActivity.this,mOptions);
            }else{
                Toast.makeText(ChatActivity.this, "Por favor concede los permiso para agregar una imagen", Toast.LENGTH_LONG).show( );
            }

            return ;

        }
    }

    private void profileIndividual(){

        Intent intent = new Intent(ChatActivity.this, ProfileIndividualActivity.class);
        intent.putExtra("nameUserDestinatario", nameUserDestinatario);
        intent.putExtra("idUser", mExtraIdUser);
        startActivity(intent);

    }


    // para que la parte del toolbar donde esta la hora del dispositivo movil tenga un color personalizado
    public void toolbarColorPersonalizado(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary,this.getTheme()));
        }
        else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}