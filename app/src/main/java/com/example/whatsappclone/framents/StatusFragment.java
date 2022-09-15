package com.example.whatsappclone.framents;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ProfileActivity;
import com.example.whatsappclone.activities.StatusConfirmActivity;
import com.example.whatsappclone.adapters.StatusAdapter;
import com.example.whatsappclone.models.Status;
import com.example.whatsappclone.providers.StatusProviders;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class StatusFragment extends Fragment {


    //nos creamos una variable type view
    View view;
    //creamos nuestro linearLayout
    LinearLayout mlinearLayoutAddStatus;

    //variables para agregas las imagenes
    Options options;
    ArrayList<String> returnValideValues = new ArrayList<>();
    //donde se guardaran los estados de los usuario que no se repitan
    ArrayList<Status> arraystatusNoRepetidos;
    File imgFile;

    RecyclerView recyclerView;
    StatusAdapter statusAdapter;
    StatusProviders statusProviders;
    Gson gson = new Gson();
    ListenerRegistration listenerRegistrationStatus;

    public StatusFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_status, container, false);
        mlinearLayoutAddStatus=view.findViewById(R.id.linearLayoutAddStatus);


        recyclerView = view.findViewById(R.id.recicleViewStatus);
        statusProviders = new StatusProviders();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);



        options = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(5)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(returnValideValues)                               //Pre selected Image Urls
                .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                .setMode(Options.Mode.All)                                     //Option to select only pictures or videos or both
                .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");

        mlinearLayoutAddStatus.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                starPix();
            }
        });

        getStatus();

        itervaloTime();
        return view;
    }

    private void itervaloTime() {

        new Timer().scheduleAtFixedRate(new TimerTask( ) {
            @Override
            public void run() {

                if(arraystatusNoRepetidos!=null){
                    for (int i=0; i<arraystatusNoRepetidos.size(); i++){
                        if(arraystatusNoRepetidos.get(i).getJson()!=null){
                            Status[] statusJson =gson.fromJson(arraystatusNoRepetidos.get(i).getJson(),Status[].class);

                            for(int m=0; m<statusJson.length; m++){
                                long now = new Date().getTime();
                                //si la hora actual es mayor que el limite de los estado que este actualizando y borrando el escuchador listener
                                if(now>statusJson[m].getTimestampLimit()){
                                    if(listenerRegistrationStatus!=null){
                                        listenerRegistrationStatus.remove();
                                    }
                                    getStatus();
                                }
                            }
                        }
                    }
                }

            }
        },0,60000

        );
    }

    private void getStatus() {

        listenerRegistrationStatus=statusProviders.obtenerLainformacionByTiempoLimiteStatus().addSnapshotListener(new EventListener<QuerySnapshot>( ) {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                 if(querySnapshot!=null){
                     ArrayList<Status> statusArrayList = new ArrayList<>();

                     arraystatusNoRepetidos = new ArrayList<>();
                     //hacemos un for para recorrer cada uno de los documentos
                     for (DocumentSnapshot d:querySnapshot.getDocuments()){
                          Status s = d.toObject(Status.class);
                          statusArrayList.add(s);
                     }


                     for (Status status: statusArrayList){
                         boolean auxiliar=false;
                         for (Status s: arraystatusNoRepetidos){
                              if(s.getIdUser().equals(status.getIdUser())){

                                  auxiliar=true;
                                  break;
                              }
                         }

                         //si no fue encontrado
                         if(!auxiliar){
                             arraystatusNoRepetidos.add(status);
                         }
                     }


                     //empaquetamos todos los estados de cada usuario
                     for(Status noRepit:arraystatusNoRepetidos){
                         ArrayList<Status> statusListEmpaquetado = new ArrayList<>();
                         for (Status s: statusArrayList){
                             if(s.getIdUser().equals(noRepit.getIdUser())){
                                 statusListEmpaquetado.add(s);
                             }
                         }
                         //PASAMOS ESTA LISTA A UN TIPO JSON
                         String statusJSON =gson.toJson(statusListEmpaquetado);
                         noRepit.setJson(statusJSON);

                     }

                     //listamos los datos en nuestro adaptador
                     statusAdapter = new StatusAdapter(getActivity(),arraystatusNoRepetidos);
                     recyclerView.setAdapter(statusAdapter);
                 }

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy( );
        if(listenerRegistrationStatus!=null){
            listenerRegistrationStatus.remove();
        }
    }

    public void starPix() {
        Pix.start(StatusFragment.this,options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValideValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            Intent intent = new Intent( getContext(), StatusConfirmActivity.class);
            //le envio la data que son las imagenes que tengo
            intent.putExtra("data",returnValideValues);
            startActivity(intent);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(StatusFragment.this,options);
            }else{
                Toast.makeText(getContext(), "Por favor concede los permiso para agregar una imagen", Toast.LENGTH_LONG).show( );
            }

            return ;

        }
    }

}