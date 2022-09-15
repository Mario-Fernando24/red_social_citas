package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsappclone.activities.HomeActivity;
import com.example.whatsappclone.providers.AuthProviders;
import com.hbb20.CountryCodePicker;

public class MainActivity extends AppCompatActivity {

     Button mButtonSetCode;
     EditText mEditTextPhone;
     CountryCodePicker mcountryCodePicker;
    AuthProviders mAuthProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSetCode =findViewById(R.id.mButtonSetCode);
        mEditTextPhone=findViewById(R.id.editTextPhone);
        mcountryCodePicker = findViewById(R.id.ccp);

        mAuthProvider = new AuthProviders();


        mButtonSetCode.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                getData();

            }
        });
    }



    private void getData(){
//3104688649
        String code = mcountryCodePicker.getSelectedCountryCodeWithPlus();
        String phone = mEditTextPhone.getText().toString();

        if(phone.equals("")){
            Toast.makeText(this,"Debe insertar un numero",Toast.LENGTH_LONG).show();
        }else{
            goToCodeVerificationActivity(code+phone);
        }
    }

    //METODO PARA validar si el usuario ya se autentico ciclo de vida de android
    //se ejecuta antes de oncreate
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.autenticationUser()!=null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class );
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void goToCodeVerificationActivity(String phone){

        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class );
        intent.putExtra("phone",phone);
        startActivity(intent);
    }


}