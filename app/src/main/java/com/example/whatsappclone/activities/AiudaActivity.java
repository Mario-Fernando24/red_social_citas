package com.example.whatsappclone.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.utils.MyToolbar;

public class AiudaActivity extends AppCompatActivity {

    ImageView imageViewClose;
    private Typeface font3;
    private static  ProgressDialog progressDialog;
    AuthProviders mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aiuda);
        MyToolbar.show(this,"Perfil",true);

        imageViewClose = findViewById(R.id.imagViewClose);
        progressDialog = new ProgressDialog(AiudaActivity.this);
        progressDialog.setTitle("Cerrando...");
        progressDialog.setMessage("Te esperamos pronto");
        mAuthProvider = new AuthProviders();

        font3 = ResourcesCompat.getFont(getApplicationContext(),R.font.allianzneo_regular);


        imageViewClose.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                signOut();

            }
        });
        toolbarColorPersonalizado();
    }





    private void signOut() {
       mAuthProvider.cerrarSession();

        Intent intent = new Intent(AiudaActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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