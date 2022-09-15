package com.example.whatsappclone.framents;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ContactAdapter;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.UsersProviders;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class ContactFragment extends Fragment {


   //nos creamos una variable type view
    View view;
    RecyclerView recyclerViewContact;
    ContactAdapter contactAdapter;
    UsersProviders usersProviders;


    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_contact, container, false);
        recyclerViewContact=view.findViewById(R.id.recyclerViewContact);

        usersProviders= new UsersProviders();

        //para decirle que nuestros elementos se van a posicionar una debajo del otro
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewContact.setLayoutManager(linearLayoutManager);
        return view;
    }

    //ciclo de vida

    @Override
    public void onStart() {
        super.onStart( );
        Query query=usersProviders.getAllName();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();

        contactAdapter = new ContactAdapter(options,getContext());
        recyclerViewContact.setAdapter(contactAdapter);
        //que empiece a escuchar
        contactAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop( );
        //para que deje de escuchar
        contactAdapter.startListening();
    }
}