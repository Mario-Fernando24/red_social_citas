package com.example.whatsappclone.framents;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ConfirmarImagenSeleccionadaActivity;
import com.example.whatsappclone.adapters.CardAdapter;

import java.io.File;


public class ImagePageFragment extends Fragment {

    View mView;
    CardView mCardViewOptions;
    ImageView imageViewPintureeee,imgViewBack,imgViewSendd;
    LinearLayout linearLayouViewPage;
    EditText EditTextCommente;



    public static Fragment newInstance(int position, String imagepath, int size) {
        ImagePageFragment fragment = new ImagePageFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("image", imagepath);
        args.putInt("sizee", size);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_image_page, container, false);
        mCardViewOptions = mView.findViewById(R.id.mcardViewOption);
        imageViewPintureeee =mView.findViewById(R.id.imageViewPintureeee);
        linearLayouViewPage=mView.findViewById(R.id.linearLayouViewPage);
        imgViewBack=mView.findViewById(R.id.imgViewBack);
        EditTextCommente=mView.findViewById(R.id.EditTextCommente);
        imgViewSendd=mView.findViewById(R.id.imgViewSendd);

        mCardViewOptions.setMaxCardElevation(mCardViewOptions.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        String imagePath=getArguments().getString("image");
        int sizeee=getArguments().getInt("sizee");
        int posicion=getArguments().getInt("position");
        if(sizeee==1){
            linearLayouViewPage.setPadding(0,0,0,0);
            ViewGroup.MarginLayoutParams params =(ViewGroup.MarginLayoutParams) imgViewBack.getLayoutParams();
            params.leftMargin=20;
            params.topMargin=35;
        }

        if(imagePath!=null){
            File file = new File(imagePath);
            imageViewPintureeee.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }

        imgViewBack.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        EditTextCommente.addTextChangedListener(new TextWatcher( ) {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //me permite saber cuando el usuario typea escribe e
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((ConfirmarImagenSeleccionadaActivity) getActivity()).setMessages(posicion,charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgViewSendd.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                ((ConfirmarImagenSeleccionadaActivity) getActivity()).enviarMessageButton();
            }
        });

        return mView;
    }

    public CardView getCardView() {
        return mCardViewOptions;
    }


}