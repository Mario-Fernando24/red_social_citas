package com.example.whatsappclone.framents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

//quiero que herede de algo llamado BottomSheetDialogFragment del paquete de android
public class ButtonSelectImagProfileBig extends BottomSheetDialogFragment {


    public static final String TAG= ButtonSelectImagProfileBig.class.getSimpleName();
    String image,userName;

    Button buttonCancel;
    TextView textViewName;
    ImageView circleImageePhoto;

    RadioGroup rg;


    //recibo como parametro de la activity profile la url del usuario que esta logueADO
    public static ButtonSelectImagProfileBig newIntance(String image,String userName){
        ButtonSelectImagProfileBig buttonSelectImag = new ButtonSelectImagProfileBig();
        Bundle args= new Bundle();
        args.putString("image",image);
        args.putString("userName",userName);
        buttonSelectImag.setArguments(args);
        return  buttonSelectImag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image= getArguments().getString("image");
        userName= getArguments().getString("userName");


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // le pasamos como parametro el layaut que nosotros vamos a implementar
        View view = inflater.inflate(R.layout.button_select_imagen_profile_bog,container,false);

        //y retornamos la vista de los botones
        buttonCancel=view.findViewById(R.id.btnCancel);
        textViewName = view.findViewById(R.id.textViewName);
        circleImageePhoto = view.findViewById(R.id.circleImageePhoto);

        showImage();
        buttonCancel.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }


    public void showImage(){
        textViewName.setText(userName);
        Picasso.with(getActivity()).load(image).into(circleImageePhoto);

    }


}
