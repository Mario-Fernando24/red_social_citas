package com.example.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.OptionsPagerAdapter;
import com.example.whatsappclone.adapters.StatusPagerAdapter;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.ImagProviders;
import com.example.whatsappclone.utils.ShadowTransformer;

import java.util.ArrayList;
import java.util.Date;

public class StatusConfirmActivity extends AppCompatActivity {


    ViewPager mViewPager;
    ArrayList<String> data;
    //Array para guardar la url y el type
    ArrayList<Status> status = new ArrayList<>();
    String mExtraChat;
    //id del usuario que va a recibir el mensaje
    String idReceiver;
    AuthProviders authProviders;
    ImagProviders imagProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_confirm);
        toolbarColorPersonalizado();
        mViewPager = findViewById(R.id.viewPager);

//      mExtraChat=getIntent().getStringExtra("mExtraChat");
//      idReceiver=getIntent().getStringExtra("idReceiver");

        authProviders = new AuthProviders();
        imagProviders = new ImagProviders();


        data = getIntent().getStringArrayListExtra("data");

        if(data!=null){
            for(int i=0; i<data.size(); i++){
                Status s=new Status();
                Long ahora=new Date().getTime();
                //tiempo que caducara nuestro estado
                Long limite=ahora+(60*1000*60*24);

                s.setIdUser(authProviders.getIdAutenticado());
                s.setComment("");
                s.setTimestamp(ahora);
                s.setTimestampLimit(limite);
                s.setUrl(data.get(i));
                status.add(s);
            }
        }

        StatusPagerAdapter pagerAdapter = new StatusPagerAdapter(
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



    //envia toda la informacion a la base de datos y al store
    public void enviarMessageButton(){

         imagProviders.uploadMultipleStatus(StatusConfirmActivity.this,status);
         finish();

    }


    public void setComment(int position, String comment){
        status.get(position).setComment(comment);
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