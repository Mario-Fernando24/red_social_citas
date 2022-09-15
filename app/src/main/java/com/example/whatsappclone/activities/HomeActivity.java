package com.example.whatsappclone.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ViewPageAdapter;
import com.example.whatsappclone.framents.ChatFragment;
import com.example.whatsappclone.framents.ContactFragment;
import com.example.whatsappclone.framents.PhotoFragment;
import com.example.whatsappclone.framents.StatusFragment;
import com.example.whatsappclone.providers.AuthProviders;
import com.example.whatsappclone.providers.UsersProviders;
import com.example.whatsappclone.utils.AppBackgroundHelper;
import com.google.android.material.tabs.TabLayout;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class HomeActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    AuthProviders mAuthProvider;
    MaterialSearchBar mSearchBar;

    TabLayout mTabLayout;
    ViewPager mViewPager;

    ChatFragment mChatsFragment;
    ContactFragment mContactsFragments;
    StatusFragment mStatusFragment;
    PhotoFragment mPhotoFragmento;
    private int mselect=0;

    UsersProviders usersProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSearchBar = findViewById(R.id.searchBar);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        usersProviders = new UsersProviders();

        //le decimos el numero de fragmentos que vamos utilizar
        mViewPager.setOffscreenPageLimit(3);

        //nos creamos un objeto que fue la clase que creamos del adapter para los frament
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        //inicializo los Fragment
        mChatsFragment = new ChatFragment();
        mContactsFragments = new ContactFragment();
        mStatusFragment = new StatusFragment();
        //mPhotoFragmento = new PhotoFragment();

       // adapter.addFragment(mPhotoFragmento,"");
        adapter.addFragment(mChatsFragment, "CHATS");
        adapter.addFragment(mStatusFragment, "ESTADOS");
        adapter.addFragment(mContactsFragments, "CONTACTOS");

        mViewPager.setAdapter(adapter);
        // CUANDO SE ABRA LA APP SEA EL FRAMENT QUE ESTA EN LA POSICION 1
        mViewPager.setCurrentItem(mselect);

        //para mostrar
         mTabLayout.setupWithViewPager(mViewPager);

        //ejecuto la funcion para mostrar la imagen de la camara
        // setupTabIcon();


        toolbarColorPersonalizado();

        mSearchBar.setOnSearchActionListener(this);
        mSearchBar.inflateMenu(R.menu.main_menu);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
             //   if (item.getItemId() == R.id.itemCerrarSession) {
                //     signOut();
                //}
                if(item.getItemId() == R.id.itemAyudaa){
                    goToAyad();
                }
                if(item.getItemId() == R.id.itemProfile){
                    goToProfile();
                }

                return true;
            }
        });

        mAuthProvider = new AuthProviders();

        //llamamos a un metodo para crear el token de las notificaciones
        createToken();

    }



    //funcion para crear el token para la notificacion de dispositivo a dispositivo
    private void createToken() {
        usersProviders.createToken(mAuthProvider.getIdAutenticado());
    }

    //cuando el usuario entra al home el estado online entra a true
    @Override
    protected void onStart() {
        super.onStart( );
        AppBackgroundHelper.online(HomeActivity.this,true);
    }

    //cuando el usuario se sale de la aplicacion llamo al metodo le envio por parametro el id mio y el estado en false
    @Override
    protected void onStop() {
        super.onStop( );
        AppBackgroundHelper.online(HomeActivity.this,false);
    }

    private void goToProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void goToAyad() {
        Intent intent = new Intent(HomeActivity.this, AiudaActivity.class);
        startActivity(intent);
    }

    private void setupTabIcon() {
        //el indice en el cual queremos trabajar
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_camera_alt_24);
        LinearLayout linearLayout = ((LinearLayout) ((LinearLayout) mTabLayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.weight= 0.5f;
        linearLayout.setLayoutParams(layoutParams);
    }

    private void signOut() {
        mAuthProvider.cerrarSession();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    // para que la parte del toolbar donde esta la hora del dispositivo movil tenga un color personalizado
    public void toolbarColorPersonalizado(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary,this.getTheme()));
        }
        else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}