package com.example.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.framents.ButtonSelectViewSheeStatus;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.models.StoryView;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.StoryViewProviders;
import com.google.gson.Gson;

import java.net.URL;
import java.util.Date;

import jp.shts.android.storiesprogressview.StoriesProgressView;

//implementamos una interfaz StoriesProgressView.StoriesListener
public class StatusDetailActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{

     TextView textViewComment;
     ImageView imageViewStatus;
     StoriesProgressView storiesProgressView;
     View mainView;
     GestureDetector mdetector;

    int cont=0;

    Gson gson = new Gson();

    //recibiremos un array de status
    Status[] mstatuses;
    AuthProviders authProviders;
    StoryViewProviders storyViewProviders;

    ButtonSelectViewSheeStatus buttonSelectViewSheeStatus;

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mdetector.onTouchEvent(motionEvent);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);

        //nos ayuda a convertir las url a objeto bitman para mostrar las imagenes
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textViewComment = findViewById(R.id.textViewComment);
        imageViewStatus = findViewById(R.id.imageViewStatus);
        storiesProgressView = findViewById(R.id.storiesProgressView);
        mainView = findViewById(R.id.mainView);

        cont = getIntent().getIntExtra( "counterr",0);


        authProviders = new AuthProviders();
        storyViewProviders = new StoryViewProviders();


        mdetector = new GestureDetector(this,new MyGestureListener());

        //corre los estados en nuestra clase de la interfaz que implement
        storiesProgressView.setStoriesListener(this);

        //obtenemos el json que viene por parametro
        String StatusJson = getIntent().getStringExtra("status");
        Log.d("","probando"+StatusJson);
        //lo convertimos en un arreglo de type status
        mstatuses=gson.fromJson(StatusJson,Status[].class);
        Log.d("","mstatuses"+mstatuses);


        //aqui nos muestra la imagen de los estados

        //para decirle el numero de historias que tiene ese usuario
        storiesProgressView.setStoriesCount(mstatuses.length);
        //para decirle el tiempo que demorara las historias mostrandose
        storiesProgressView.setStoryDuration(5000);
        //decidimos de donde empezara a mostrar las imagenes del estado (primera)
        storiesProgressView.startStories(cont);
        //mostrar los estados en la vista
        showInfoStatusView();

        toolbarColorPersonalizado();

        mainView.setOnTouchListener(onTouchListener);
    }


    public  void showInfoStatusView(){

        try {
            URL url  = new URL(mstatuses[cont].getUrl());
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageViewStatus.setImageBitmap(image);
            textViewComment.setText(mstatuses[cont].getComment());

            //llamamos al metodo para que se guarden los usuarios que hallan visto la historia
            //le pasamos nuestro arreglo en la posicion cont
            updateStatus(mstatuses[cont]);


        }catch (Exception e){
            Toast.makeText(StatusDetailActivity.this, "Hubo un error ==> "+e.getMessage(), Toast.LENGTH_LONG).show( );
        }

    }


    //nuevo metodo que nos permita actualizar

    private void updateStatus(Status status) {

        StoryView storyView = new StoryView();
        storyView.setIdStatus(status.getId());
        storyView.setIdUser(authProviders.getIdAutenticado());
        storyView.setTimestamp(new Date().getTime());
        storyView.setId(authProviders.getIdAutenticado() + status.getId());
        storyViewProviders.create(storyView);

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

    @Override
    public void onNext() {
        //la funcion de cambiar un estado al siguiente
        cont++;
        showInfoStatusView();
    }

    @Override
    public void onPrev() {
        //la funcion de cambiar un estado al anterior
        if((cont-1)<0){
            return;
         //   Toast.makeText(StatusDetailActivity.this,"No tienes una historia anterior",Toast.LENGTH_LONG);
        }
        cont--;
        showInfoStatusView();
    }

    @Override
    public void onComplete() {
         //terminaron de mostrarse todos los estados
        finish();
    }



    public void onSwipeRight() {
        //para ir hacia atras
        storiesProgressView.reverse();

    }

    public void onSwipeLeft() {
       // para ir hacia adelante
        storiesProgressView.skip();
    }

    public void onSwipeTop() {
            //abrir un botton shee
           openButtonSheeView();
    }

    private void openButtonSheeView(){
        if(mstatuses[cont]!=null){
            //le enviamos el id de la historia que el usuario esta enviando
            buttonSelectViewSheeStatus=ButtonSelectViewSheeStatus.newIntance(mstatuses[cont].getId());
            buttonSelectViewSheeStatus.show(getSupportFragmentManager(),buttonSelectViewSheeStatus.getTag());
        }
    }

    public void onSwipeBottom() {

    }


    public void  pauseStory(){
        storiesProgressView.pause();
    }

    public void startStory(){
        storiesProgressView.resume();
    }


    //esta clase nos ayuda a detectar los gestos que el usuario haga en esta panatalla

    private class MyGestureListener  implements GestureDetector.OnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        public MyGestureListener() {
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}