package com.example.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.OptionsPagerAdapter;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.ImagProviders;
import com.example.whatsappclone.utils.ShadowTransformer;

import java.util.ArrayList;
import java.util.Date;

public class  ConfirmarImagenSeleccionadaActivity extends AppCompatActivity {

    ViewPager mViewPager;
    ArrayList<String> data;
    //Array para guardar la url y el type
    ArrayList<Message> messages = new ArrayList<>();
    String mExtraChat;
    //id del usuario que va a recibir el mensaje
    String idReceiver;
    AuthProviders authProviders;
    ImagProviders imagProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_imagen_seleccionada);
        toolbarColorPersonalizado();
        mViewPager = findViewById(R.id.viewPager);

        data = getIntent().getStringArrayListExtra("data");
        mExtraChat=getIntent().getStringExtra("mExtraChat");
        idReceiver=getIntent().getStringExtra("idReceiver");
        authProviders = new AuthProviders();
        imagProviders = new ImagProviders();

        if(data!=null){
            for(int i=0; i<data.size(); i++){
                Message m=new Message();
                m.setIdChat(mExtraChat);
                m.setIdEnviar(authProviders.getIdAutenticado());
                m.setIdRecibido(idReceiver);
                m.setStatus("ENVIADO");
                m.setTimeTamp(new Date().getTime());
                m.setType("imagen");
                m.setUrl(data.get(i));
                m.setMessage("\uD83D\uDCF7image");

                messages.add(m);
            }
        }

        OptionsPagerAdapter pagerAdapter = new OptionsPagerAdapter(
                getApplicationContext(),
                getSupportFragmentManager(),
                dpToPixels(2, this),
                data
        );
        ShadowTransformer transformer = new ShadowTransformer(mViewPager, pagerAdapter);
        transformer.enableScaling(true);

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageTransformer(false, transformer);
    }


    public void enviarMessageButton(){
        imagProviders.uploadMultiple(ConfirmarImagenSeleccionadaActivity.this,messages);
        finish();

    }


    public void setMessages(int position, String message){
        messages.get(position).setMessage(message);
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }


    // para que la parte del toolbar donde esta la hora del dispositivo movil tenga un color personalizado
    public void toolbarColorPersonalizado(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.black,this.getTheme()));
        }
        else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
    }
}