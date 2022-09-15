package com.example.whatsappclone.framents;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ChatAdapter;
import com.example.whatsappclone.adapters.ContactAdapter;
import com.example.whatsappclone.models.Chat;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.ChatProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

    View mview;
    RecyclerView recyclerViewChat;
    ChatAdapter chatAdapter;
    UsersProviders usersProviders;
    ChatProviders chatProviders;
    AuthProviders authProviders;


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview= inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerViewChat=mview.findViewById(R.id.recyclerViewChat);

        usersProviders= new UsersProviders();
        chatProviders = new ChatProviders();
        authProviders = new AuthProviders();

        //para decirle que nuestros elementos se van a posicionar una debajo del otro
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewChat.setLayoutManager(linearLayoutManager);

        return mview;
    }



    //ciclo de vida
    @Override
    public void onStart() {
        super.onStart( );
        Query query=chatProviders.getUserChatId(authProviders.getIdAutenticado());
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class).build();

        chatAdapter = new ChatAdapter(options,getContext());
        recyclerViewChat.setAdapter(chatAdapter);
        //que empiece a escuchar
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop( );
        //para que deje de escuchar
        chatAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy( );
        if(chatAdapter.getlistenerChat()!=null){
            chatAdapter.getlistenerChat().remove();
        }

        if(chatAdapter.getlistenerMessage()!=null){
            chatAdapter.getlistenerMessage().remove();
        }



    }
}