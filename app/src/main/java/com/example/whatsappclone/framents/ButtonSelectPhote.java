package com.example.whatsappclone.framents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
public class ButtonSelectPhote extends BottomSheetDialogFragment {


    public static final String TAG= ButtonSelectPhote.class.getSimpleName();
    ImagProviders imagProviders;
    AuthProviders authProviders;
    UsersProviders usersProviders;
    String phone;

    Button buttonSave, buttonCancel;
    EditText editTextPhone;


    //recibo como parametro de la activity profile la url del usuario que esta logueADO
    public static ButtonSelectPhote newIntance(String phone){
        ButtonSelectPhote buttonSelectImag = new ButtonSelectPhote();
        Bundle args= new Bundle();
        args.putString("phone",phone);
        buttonSelectImag.setArguments(args);
        return  buttonSelectImag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone= getArguments().getString("phone");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // le pasamos como parametro el layaut que nosotros vamos a implementar
        View view = inflater.inflate(R.layout.button_select_phone,container,false);


        imagProviders = new ImagProviders();
        authProviders = new AuthProviders();
        usersProviders = new UsersProviders();
        //y retornamos la vista de los botones

        buttonSave=view.findViewById(R.id.btnSave);
        buttonCancel=view.findViewById(R.id.btnCancel);
        editTextPhone=view.findViewById(R.id.EditTextPhone);

        buttonSave.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private void updateInfo() {

        String phonee=editTextPhone.getText().toString();
        if(!phonee.equals("")){
            usersProviders.updatePhone(authProviders.getIdAutenticado(),phonee).addOnCompleteListener(new OnCompleteListener<Void>( ) {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dismiss();

                    Toast.makeText(getContext(),"El teléfono se ha actualizado", Toast.LENGTH_LONG);
                }
            });
        }else{
            Toast.makeText(getActivity(), "telefono vacio",Toast.LENGTH_LONG);
        }

    }


    //invocando
    private void refrescarImagDelete() {
        ((ProfileActivity)getActivity()).refrescarImagDelete();
    }


}
