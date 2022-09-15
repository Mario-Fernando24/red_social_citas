package com.example.whatsappclone.providers;

import android.app.Activity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthProviders {

    private FirebaseAuth mAuth;

    public AuthProviders() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void sendCodeVerification(String phone, PhoneAuthProvider.OnVerificationStateChangedCallbacks callback) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                (Activity) TaskExecutors.MAIN_THREAD,
                callback
        );

    }

    public Task<AuthResult> signInPhone(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        return mAuth.signInWithCredential(credential);
    }

    public String getIdAutenticado(){
        if(mAuth.getCurrentUser()!=null) {
            return mAuth.getCurrentUser( ).getUid( );
        }else{
            return null;
        }
    }

    //metodo para saber si el usuario ya se autentico
    public FirebaseUser autenticationUser(){
        //VA A EXISTIR SIEMPRE Y CUANDO EL USUARIO HAYA INICIADO SESSION CORRECTAMENTE FALSE NULL
       return mAuth.getCurrentUser();
    }

    public void cerrarSession(){
        mAuth.signOut();
    }

}
