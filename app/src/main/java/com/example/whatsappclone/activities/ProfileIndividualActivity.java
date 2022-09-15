package com.example.whatsappclone.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.framents.ButtonSelectImagProfileBig;
import com.example.whatsappclone.framents.ButtonSelectInfo;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.UsersProviders;
import com.example.whatsappclone.utils.MyToolbar;
import com.example.whatsappclone.utils.RelativeTime;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileIndividualActivity extends AppCompatActivity {

    String IdUser, nameUserDestinatario, numPhonee;
    User mmuser;

    UsersProviders usersProviders;
    TextView textViewUsername, textViewInfo, textViewPhone, textViewGenero;
    CircleImageView circleImagUser;
    ImageView callPhone;
    ButtonSelectImagProfileBig buttonSelectImagProfileBig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_individual);

        textViewUsername = findViewById(R.id.textViewUsername);
        textViewInfo = findViewById(R.id.textViewInfo);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewGenero = findViewById(R.id.textViewGenero);
        circleImagUser = findViewById(R.id.circleImageePhoto);
        callPhone = findViewById(R.id.callPhone);


        IdUser = getIntent( ).getStringExtra("idUser");
        nameUserDestinatario = getIntent( ).getStringExtra("nameUserDestinatario");


        MyToolbar.show(this, nameUserDestinatario, true);

        toolbarColorPersonalizado( );
        mmuser = new User( );
        usersProviders = new UsersProviders( );
        getUserInfo(IdUser);


        callPhone.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                makeCall(numPhonee);
            }
        });

        circleImagUser.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                openImagenBig();
            }
        });

    }

    private void openImagenBig() {
        if(mmuser!=null){
            buttonSelectImagProfileBig = ButtonSelectImagProfileBig.newIntance(mmuser.getImage(),mmuser.getUsername());
            buttonSelectImagProfileBig.show(getSupportFragmentManager(), buttonSelectImagProfileBig.getTag());
        }else{
            Toast.makeText(this,"La imagen no se pudo mostrar",Toast.LENGTH_LONG).show();
        }
    }

    //Retroceda a la pantalla anterior
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void getUserInfo(String IdUser) {
        //hacemos la consulta y nos traemos la informacion del usuario en tiempo real
        usersProviders.getUserInfo(IdUser).addSnapshotListener(new EventListener<DocumentSnapshot>( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot!=null){
                    if(documentSnapshot.exists()){
                        //mapeando la informacion del usuario
                        mmuser = documentSnapshot.toObject(User.class);
                        textViewUsername.setText(mmuser.getUsername());
                        textViewInfo.setText(mmuser.getInfo());
                        numPhonee=mmuser.getPhone();
                        textViewPhone.setText(mmuser.getPhone());
                        if(mmuser.getGenero().equals("1")){
                            textViewGenero.setText("Masculino");
                        } if(mmuser.getGenero().equals("2")){
                            textViewGenero.setText("Femenino");
                        } if(mmuser.getGenero().equals("3")){
                            textViewGenero.setText("No definido");
                        }

                        Log.d("profile", "profile" +mmuser.getImage());
                        if(mmuser.getImage()!=null){
                            if(!mmuser.getImage().equals("")){
                                Picasso.with(ProfileIndividualActivity.this).load(mmuser.getImage()).into(circleImagUser);
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




    private void makeCall(String numPhonee){
        Uri phoneNumber = Uri.parse("tel:"+numPhonee );
        Intent callIntent = new Intent(Intent.ACTION_DIAL, phoneNumber);
        startActivity(callIntent);

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