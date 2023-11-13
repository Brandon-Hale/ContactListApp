package com.example.contactlist;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends Fragment {

    private RecyclerView rv;
    private Button addButton;
    private Button pickContacts;

    public ContactListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);


        //Create an instance of the Contact database
        ContactDAO contactDAO = ContactDBInstance.getDatabase(requireContext()).contactDAO();
        MainActivityData mainActivityData = new ViewModelProvider(getActivity()).get(MainActivityData.class);

        //initialise the UI components
        addButton = view.findViewById(R.id.AddContact);
        pickContacts = view.findViewById(R.id.PickContact);
        //Create a list of contacts
        List<Contact> contactList = contactDAO.getAllContacts();

        ContactsAdapter contactsAdapter = new ContactsAdapter(getContext(), contactList, mainActivityData);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityData.setNewContact(0);
                mainActivityData.setClickedValue(1);
                Toast.makeText(view.getContext(), "Add Contact", Toast.LENGTH_SHORT).show();
                contactsAdapter.notifyDataSetChanged();
            }
        });

        pickContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityData.setClickedValue(3);
                Toast.makeText(view.getContext(), "Pick Contact", Toast.LENGTH_SHORT).show();
            }
        });

        rv = (RecyclerView) view.findViewById(R.id.contactlistRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new ContactsAdapter(getContext(), contactList, mainActivityData));

        return view;
    }
}