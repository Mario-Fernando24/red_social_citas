package com.example.whatsappclone.framents;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ProfileActivity;
import com.example.whatsappclone.activities.StatusDetailActivity;
import com.example.whatsappclone.adapters.ChatAdapter;
import com.example.whatsappclone.adapters.StatusViewAdapter;
import com.example.whatsappclone.models.Chat;
import com.example.whatsappclone.models.StoryView;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.StoryViewProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.Query;

//quiero que herede de algo llamado BottomSheetDialogFragment del paquete de android
public class ButtonSelectViewSheeStatus extends BottomSheetDialogFragment {


    public static final String TAG= ButtonSelectViewSheeStatus.class.getSimpleName();

    AuthProviders authProviders;
    UsersProviders usersProviders;
    String idStory;

    RecyclerView recyclerView;
    StatusViewAdapter statusViewAdapter;
    StoryViewProviders storyViewProviders;




    //recibo como parametro de la activity profile la url del usuario que esta logueADO
    public static ButtonSelectViewSheeStatus newIntance(String idStory){
        ButtonSelectViewSheeStatus buttonSelectImag = new ButtonSelectViewSheeStatus();
        Bundle args= new Bundle();
        args.putString("idStory",idStory);
        buttonSelectImag.setArguments(args);
        return  buttonSelectImag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idStory= getArguments().getString("idStory");



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // le pasamos como parametro el layaut que nosotros vamos a implementar
        View view = inflater.inflate(R.layout.button_sheet_view_status,container,false);
        recyclerView=view.findViewById(R.id.recicleStatusView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

                authProviders = new AuthProviders();
        usersProviders = new UsersProviders();
        storyViewProviders = new StoryViewProviders();
        //y retornamos la vista de los botones

        return view;
    }


    //cuando el usuario abra el buttonshee se pausen las historias
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog=(BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener( ) {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ((StatusDetailActivity) getActivity()).pauseStory();
            }
        });
        return dialog;
    }

    //cuando el usuario cierre el buttonsheee se sigan las historia resuman
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        ((StatusDetailActivity) getActivity()).startStory();
        super.onCancel(dialog);
    }

    //invocando
    private void refrescarImagDelete() {
        ((ProfileActivity)getActivity()).refrescarImagDelete();
    }


    @Override
    public void onStart() {
        super.onStart( );

        Query query=storyViewProviders.getStoryByIdStory(idStory);
        FirestoreRecyclerOptions<StoryView> options = new FirestoreRecyclerOptions.Builder<StoryView>()
                .setQuery(query, StoryView.class).build();


        Log.d("prueba","query"+idStory);
       statusViewAdapter = new StatusViewAdapter(options,getContext());
          recyclerView.setAdapter(statusViewAdapter);
        //que empiece a escuchar
          statusViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop( );
        statusViewAdapter.stopListening();
    }
}
