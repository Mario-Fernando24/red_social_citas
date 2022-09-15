package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.framents.ButtonSelectGenero;
import com.example.whatsappclone.framents.ButtonSelectImag;
import com.example.whatsappclone.framents.ButtonSelectInfo;
import com.example.whatsappclone.framents.ButtonSelectPhote;
import com.example.whatsappclone.framents.ButtonSelectUsername;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.ImagProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.example.whatsappclone.utils.AppBackgroundHelper;
import com.example.whatsappclone.utils.MyToolbar;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import android.provider.ContactsContract;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG= ButtonSelectImag.class.getSimpleName();

   FloatingActionButton mFabSelectImag;
   ButtonSelectImag buttonSelectImag;
   ButtonSelectUsername buttonSelectUsername;
   ButtonSelectInfo buttonSelectInfo;
   ButtonSelectPhote buttonSelectPhote;
   ButtonSelectGenero buttonSelectGenero;
   //creamos una variable para obtener los datos de la persona que esta logueada
    UsersProviders usersProviders;
    AuthProviders authProviders;
    TextView TextviewUsername, TextviewInfo, TextviewPhoto,textViewGenero,contactos_home;
    CircleImageView circleImageView;
    User muser;
    ImageView EditUsername,editButtonInfo,editButtonPhote, editButtonGenero;
    Options options;
    ArrayList<String> returnValideValues = new ArrayList<>();
    File imgFile;

    ImagProviders imagProviders;

    //destruir el Listern que esta escuchando en el servicio
    ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        MyToolbar.show(this,"Perfil",true);

        usersProviders = new UsersProviders();
        authProviders = new AuthProviders();

        mFabSelectImag = findViewById(R.id.fabSelectImag);
        TextviewUsername = findViewById(R.id.textViewUsername);
        TextviewInfo = findViewById(R.id.textViewInfo);
        TextviewPhoto = findViewById(R.id.textViewPhone);
        circleImageView = findViewById(R.id.circleImageePhoto);
        EditUsername= findViewById(R.id.EditUsername);
        editButtonInfo=findViewById(R.id.editButtonInfo);
        editButtonPhote=findViewById(R.id.editButtonPhote);
        editButtonGenero= findViewById(R.id.editButtonGenero);
        textViewGenero=findViewById(R.id.textViewGenero);


        toolbarColorPersonalizado();

        options = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(returnValideValues)                               //Pre selected Image Urls
                .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                .setMode(Options.Mode.All)                                     //Option to select only pictures or videos or both
                .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");

        mFabSelectImag.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                openButtonSelectImag();
            }
        });


        EditUsername.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                openButtonSelectUsername();
            }
        });

        editButtonInfo.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                openButtonSelectInfo();
            }
        });


        editButtonPhote.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                openButtonSelectPhoto();
            }
        });

        editButtonGenero.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                openButtonSelectGenero();
            }
        });

        //Metodo para obtener la informacion
        getUserInfo();
    }



    @Override
    protected void onStart() {
        super.onStart( );
        AppBackgroundHelper.online(ProfileActivity.this,true);
    }

    @Override
    protected void onStop() {
        super.onStop( );
        AppBackgroundHelper.online(ProfileActivity.this,false);
    }

    //ciclo de vida de android llamado escuchador de evento en tiempo real
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

    private void getUserInfo() {
        imagProviders = new ImagProviders();
        listenerRegistration=usersProviders.getUserInfo(authProviders.getIdAutenticado()).addSnapshotListener(new EventListener<DocumentSnapshot>( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot!=null){
                    //obtenemos el documento del usuario que esta logueado para poderlo mostrar en la actividad del perfil
                    if(documentSnapshot.exists()){
                        //toda la informacion de la base de datos en una clase de type users
                        muser = documentSnapshot.toObject(User.class);
                        TextviewUsername.setText(muser.getUsername());
                        TextviewPhoto.setText(muser.getPhone());
                        TextviewInfo.setText(muser.getInfo());
                        if(muser.getGenero().equals("1")){
                            textViewGenero.setText("Masculino");
                        }if(muser.getGenero().equals("2")){
                            textViewGenero.setText("Femenino");
                        }
                        else if(muser.getGenero().equals("3")){
                            textViewGenero.setText("No definido");
                        }

                        if(muser.getImage()!=null && !muser.getImage().equals("")){
                            //  circleImageView  into es para decirle donde la queremos mostrar
                            Picasso.with(ProfileActivity.this).load(muser.getImage()).into(circleImageView);
                        }
                    }
                }

            }
        });
    }

    //metodo para poder abrir el botton donde est para agregar una imagen y eliminarla
    private void openButtonSelectImag() {
        if(muser!=null){
            buttonSelectImag = ButtonSelectImag.newIntance(muser.getImage());
            buttonSelectImag.show(getSupportFragmentManager(), buttonSelectImag.getTag());
        }else{
            Toast.makeText(ProfileActivity.this,"La información no se pudo cargar",Toast.LENGTH_LONG).show();
        }
    }


    private void openButtonSelectUsername() {
        if(muser!=null){
            buttonSelectUsername = ButtonSelectUsername.newIntance(muser.getUsername());
            buttonSelectUsername.show(getSupportFragmentManager(), buttonSelectUsername.getTag());
        }else{
            Toast.makeText(ProfileActivity.this,"La información no se pudo cargar",Toast.LENGTH_LONG).show();
        }
    }

    private void openButtonSelectInfo() {
        if(muser!=null){
            buttonSelectInfo = ButtonSelectInfo.newIntance(muser.getInfo());
            buttonSelectInfo.show(getSupportFragmentManager(), buttonSelectInfo.getTag());
        }else{
            Toast.makeText(ProfileActivity.this,"La información no se pudo cargar",Toast.LENGTH_LONG).show();
        }
    }


    private void openButtonSelectPhoto() {
        if(muser!=null){
            buttonSelectPhote = ButtonSelectPhote.newIntance(muser.getPhone());
            buttonSelectPhote.show(getSupportFragmentManager(), buttonSelectPhote.getTag());
        }else{
            Toast.makeText(ProfileActivity.this,"La información no se pudo cargar",Toast.LENGTH_LONG).show();
        }
    }



    public void openButtonSelectGenero(){
        if(muser!=null){
            buttonSelectGenero = ButtonSelectGenero.newIntance(muser.getGenero());
            buttonSelectGenero.show(getSupportFragmentManager(), buttonSelectGenero.getTag());
        }else{
            Toast.makeText(ProfileActivity.this,"La información no se pudo cargar",Toast.LENGTH_LONG).show();
        }
    }





    //metodo cuando se elimine la imagen se actualice correctamente
    public void refrescarImagDelete(){
        circleImageView.setImageResource(R.drawable.ic_people_white);
    }


    public void starPix() {
        Pix.start(ProfileActivity.this,options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValideValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            imgFile= new File(returnValideValues.get(0));
            circleImageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            //cuando el usuario seleccione la imagen llamamos al metodo
            saveImage();
            //para que actualice la imagen
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(ProfileActivity.this,options);
            }else{
                Toast.makeText(ProfileActivity.this, "Por favor concede los permiso para agregar una imagen", Toast.LENGTH_LONG).show( );
            }

            return ;

        }
    }

    private void saveImage() {


        imagProviders = new ImagProviders();
        imagProviders.saveImag(ProfileActivity.this,imgFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>( ) {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    imagProviders.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>( ) {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG,"url mario");
                            String url = uri.toString();
                                usersProviders.updateImagen(authProviders.getIdAutenticado(),url).addOnCompleteListener(new OnCompleteListener<Void>( ) {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   Log.d(TAG,"task success mario");
                                   Toast toast = Toast.makeText(ProfileActivity.this,"La imagen se actualizo correctamente",Toast.LENGTH_LONG);
                                   toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                   toast.show();

                               }
                           });

                        }
                    });
                }else{

                    Toast.makeText(ProfileActivity.this, "No se pudo almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });

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


    @RequiresApi(api = Build.VERSION_CODES.M)

    private void permisosss(){

        if (ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //SOLICITAR PERMISO
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 123);
        }else{
            //CONCEDIDO
            Toast.makeText(getApplication(),"Permiso concedidos", Toast.LENGTH_LONG).show();
           // renderContact(uri);
        }

    }

}
