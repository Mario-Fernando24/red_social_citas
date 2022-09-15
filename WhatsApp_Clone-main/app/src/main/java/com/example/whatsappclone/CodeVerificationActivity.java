package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.activities.CompleteInfoActivity;
import com.example.whatsappclone.activities.HomeActivity;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.example.whatsappclone.R;
import com.google.firebase.firestore.DocumentSnapshot;

public class CodeVerificationActivity extends AppCompatActivity {

    Button mButtonCodeVerfication;
    EditText mEditTextCode;
    TextView mTextViewSMS;
    ProgressBar mProgressBar;

    String mExtraPhone;
    String mVerificationId;

    AuthProviders mAuthProvider;
    UsersProviders mUsersProvider;
    public static final String TAG= CodeVerificationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mButtonCodeVerfication = findViewById(R.id.btnCodeVerification);
        mEditTextCode = findViewById(R.id.editTextCodeVerification);
        mTextViewSMS = findViewById(R.id.textViewSms);
        mProgressBar = findViewById(R.id.mprogressBar);

        mAuthProvider = new AuthProviders();
        mUsersProvider = new UsersProviders();

        mExtraPhone = getIntent().getStringExtra("phone");
        mAuthProvider.sendCodeVerification(mExtraPhone, mCallbacks);


        mButtonCodeVerfication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mEditTextCode.getText().toString();
                if (!code.equals("") && code.length() >= 6) {
                    signIn(code);
                }
                else {
                    Toast.makeText(CodeVerificationActivity.this, "Ingresa el codigo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            mProgressBar.setVisibility(View.GONE);
            mTextViewSMS.setVisibility(View.GONE);

            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                mEditTextCode.setText(code);
                signIn(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Log.d(TAG,"MARIO");
            mProgressBar.setVisibility(View.GONE);
            mTextViewSMS.setVisibility(View.GONE);

            Toast.makeText(CodeVerificationActivity.this, "Se produjo un error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            mProgressBar.setVisibility(View.GONE);
            mTextViewSMS.setVisibility(View.GONE);

            Toast.makeText(CodeVerificationActivity.this, "El codigo se envio", Toast.LENGTH_LONG).show();
            mVerificationId = verificationId;
        }
    };

    private void signIn(String code) {
        mAuthProvider.signInPhone(mVerificationId, code).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  final  User user = new User();
                    user.setId(mAuthProvider.getIdAutenticado());
                    user.setPhone(mExtraPhone);

                    //verificar si el usuario ya existe
                    mUsersProvider.getUserInfo(mAuthProvider.getIdAutenticado()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>( ) {
                        @Override
                        public void onSuccess(DocumentSnapshot DocumentSnapshot) {
                            if(!DocumentSnapshot.exists()){
                                mUsersProvider.createe(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(CodeVerificationActivity.this, "Usuario registrado en MarioWhatsapp", Toast.LENGTH_SHORT).show();
                                        goToCompleteInfo();
                                    }
                                });
                            }else if(DocumentSnapshot.contains("username") && DocumentSnapshot.contains("image")){

                                String userNamee=DocumentSnapshot.getString("username");
                                String userImage=DocumentSnapshot.getString("image");
                                if(userNamee!=null && userImage!=null){
                                    if(!userNamee.equals("") && !userImage.equals("")){
                                        goToHomeActivity();
                                    }else{
                                        goToCompleteInfo();
                                    }
                                }else{
                                    goToCompleteInfo();
                                }



                            }

                        }
                    });

                }
                else {
                    Toast.makeText(CodeVerificationActivity.this, "No se pudo autenticar al usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToHomeActivity() {

        Intent intent = new Intent(CodeVerificationActivity.this, HomeActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToCompleteInfo() {
        Intent intent = new Intent(CodeVerificationActivity.this, CompleteInfoActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
