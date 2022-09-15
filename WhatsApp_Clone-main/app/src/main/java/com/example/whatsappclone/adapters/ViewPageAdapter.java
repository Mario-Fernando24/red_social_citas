package com.example.whatsappclone.adapters;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

//hereda de la clase FramgmentPagerAdapter

public class ViewPageAdapter extends FragmentPagerAdapter {

    //nos creamos arreglo de type Fragmen
    List<Fragment> mFragmentList = new ArrayList<>();
    //creamos una list de string
    List<String> mFragmentTitleList = new ArrayList<>();

    //creamos nuestros contructor y recibe por parametro nuestro framengManager al super le pasamos el manager
    public ViewPageAdapter(FragmentManager manager) {
        super(manager);
    }

    //metodo para agregar los frament y recibe por parametro el frament y el titulo de cada frament
    public void addFragment(Fragment fragment, String title) {
        //añadimos el frangmento
        mFragmentList.add(fragment);
        //le añadimos el titulo al frament
        mFragmentTitleList.add(title);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        //le pasamos la posicion que recibimos por parametros
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        //retornamos el tamaño del array
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}