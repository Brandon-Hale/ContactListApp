package com.example.contactlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    ContactListFragment contactListFragment = new ContactListFragment();
    ContactCreate contactCreate = new ContactCreate();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContactDAO contactDAO = ContactDBInstance.getDatabase(getApplicationContext()).contactDAO();

        loadContactList();

        MainActivityData mainActivityData = new ViewModelProvider(this).get(MainActivityData.class);
        mainActivityData.clickedValue.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(mainActivityData.getClickedValue() == 1) {
                    loadContactCreate();
                }
                if (mainActivityData.getClickedValue() == 2)
                {
                    loadContactList();
                }
                if (mainActivityData.getClickedValue() == 3)
                {
                    loadPickContact();
                }
            }
        });
    }

    public void loadContactList(){
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.ListContainer);

        if(frag == null){
            fm.beginTransaction().add(R.id.ListContainer, contactListFragment).commit();
        }else {
            fm.beginTransaction().replace(R.id.ListContainer, contactListFragment).commit();
        }
    }

    public void loadContactCreate(){
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.ListContainer);

        if(frag == null){
            fm.beginTransaction().add(R.id.ListContainer, new ContactCreate()).commit();
        }else {
            fm.beginTransaction().replace(R.id.ListContainer, new ContactCreate()).commit();
        }
    }

    public void loadPickContact()
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.ListContainer);

        if(frag == null){
            fm.beginTransaction().add(R.id.ListContainer, new PickContactFragment()).commit();
        }else {
            fm.beginTransaction().replace(R.id.ListContainer, new PickContactFragment()).commit();
        }
    }
}