package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.ImagProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteInfoActivity extends AppCompatActivity {
    public static final String TAG= ImagProviders.class.getSimpleName();

    TextInputEditText textInputUsername;
    Button mButtonConfirmar;
    UsersProviders usersProviders;
    AuthProviders mAuthProvider;
    ImagProviders imagProviders;
    CircleImageView circleImageView;
    String userName="";
    ProgressDialog progressDialog;

    Options options;
    ArrayList<String> returnValideValues = new ArrayList<>();
    File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);

        textInputUsername=findViewById(R.id.textInputUsername);
        mButtonConfirmar=findViewById(R.id.idButtonConfirmar);
        circleImageView=findViewById(R.id.circleImagPhoto);

        usersProviders = new UsersProviders();
        mAuthProvider = new AuthProviders();
        imagProviders  = new ImagProviders();
        progressDialog = new ProgressDialog(CompleteInfoActivity.this);
        progressDialog.setTitle("Espere un momento");
        progressDialog.setMessage("Guardando Información");


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




        circleImageView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {

                starPix();
            }
        });

        mButtonConfirmar.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                userName= textInputUsername.getText().toString();
                if(!userName.equals("") && imgFile!=null){
                    saveImage();
                }else{
                    Toast.makeText(CompleteInfoActivity.this, "no debe tener campos vacios",Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private void starPix() {
        Pix.start(CompleteInfoActivity.this,options);
    }

    private void updateUserInfo(String url) {

        userName= textInputUsername.getText().toString();


            User user = new User();
            user.setUsername(userName);
            user.setId(mAuthProvider.getIdAutenticado());
            user.setImage(url);
            usersProviders.update(user).addOnSuccessListener(new OnSuccessListener<Void>( ) {
                @Override
                public void onSuccess(Void unused) {
                    goToHomeActivity();
                }
            });

    }



    public void goToHomeActivity(){

        progressDialog.dismiss();
        Toast.makeText(CompleteInfoActivity.this, "Nombre del usuario modificado correctamente", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CompleteInfoActivity.this,HomeActivity.class);
        //eliminar todo el historial de pantalla
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    private void saveImage() {

        progressDialog.show();
        Log.d(TAG,"MARIO");
        imagProviders.saveImag(CompleteInfoActivity.this,imgFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>( ) {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
               if(task.isSuccessful()){
                   imagProviders.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>( ) {
                       @Override
                       public void onSuccess(Uri uri) {
                           String url = uri.toString();
                           updateUserInfo(url);

                       }
                   });
               }else{
                   progressDialog.dismiss();
                   Toast.makeText(CompleteInfoActivity.this, "No se pudo almacenar la imagen", Toast.LENGTH_LONG).show();
               }
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValideValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            imgFile= new File(returnValideValues.get(0));
            circleImageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(CompleteInfoActivity.this,options);
        }else{
                Toast.makeText(CompleteInfoActivity.this, "Por favor concede los permiso para agregar una imagen", Toast.LENGTH_LONG).show( );
            }

            return ;

            }
        }
    }



