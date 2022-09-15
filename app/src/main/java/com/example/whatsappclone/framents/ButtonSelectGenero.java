package com.example.whatsappclone.framents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ProfileActivity;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.ImagProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

//quiero que herede de algo llamado BottomSheetDialogFragment del paquete de android
public class ButtonSelectGenero extends BottomSheetDialogFragment {


    public static final String TAG= ButtonSelectGenero.class.getSimpleName();
    ImagProviders imagProviders;
    AuthProviders authProviders;
    UsersProviders usersProviders;
    String genero;

    Button buttonSave, buttonCancel;
    EditText EditTextGenero;

    RadioGroup rg;


    //recibo como parametro de la activity profile la url del usuario que esta logueADO
    public static ButtonSelectGenero newIntance(String genero){
        ButtonSelectGenero buttonSelectImag = new ButtonSelectGenero();
        Bundle args= new Bundle();
        args.putString("genero",genero);
        buttonSelectImag.setArguments(args);
        return  buttonSelectImag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        genero= getArguments().getString("genero");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // le pasamos como parametro el layaut que nosotros vamos a implementar
        View view = inflater.inflate(R.layout.button_select_genero,container,false);


        imagProviders = new ImagProviders();
        authProviders = new AuthProviders();
        usersProviders = new UsersProviders();
        //y retornamos la vista de los botones

        buttonSave=view.findViewById(R.id.btnSave);
        buttonCancel=view.findViewById(R.id.btnCancel);
       // EditTextGenero=view.findViewById(R.id.EditTextGenero);

        rg = view.findViewById(R.id.group_no_1);
        rg.setOnCheckedChangeListener(onCheckedChangeListener);



        buttonCancel.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }


    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            String genero="";
            switch (checkedId) {
                case R.id.btnMasculino:
                    // Write your code here
                    genero="1";
                    updateInfo(genero);
                    break;
                case R.id.btnFemenino:
                    genero="2";
                    updateInfo(genero);
                    break;
                case R.id.btnOtros:
                    genero="3";
                    updateInfo(genero);
                    break;


                default:
                    Toast.makeText(getActivity(), "Selecciona una opción", Toast.LENGTH_SHORT).show( );

                    break;
            }
        }
    };

    private void updateInfo(String genero) {



            usersProviders.updateGenero(authProviders.getIdAutenticado(),genero).addOnCompleteListener(new OnCompleteListener<Void>( ) {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getContext(),"El Genero se ha actualizado", Toast.LENGTH_LONG);
                    dismiss();
                }
            });


    }


    //invocando
    private void refrescarImagDelete() {
        ((ProfileActivity)getActivity()).refrescarImagDelete();
    }


}
