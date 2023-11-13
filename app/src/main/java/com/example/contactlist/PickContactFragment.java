package com.example.contactlist;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PickContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickContactFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button pickContact;
    private Button saveContact;
    private TextView contactName;
    private TextView contactPhone;
    private TextView contactEmail;
    private ImageView contactImage;
    int contactId;
    private static final int REQUEST_READ_CONTACT_PERMISSION = 3;
    public PickContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PickContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickContactFragment newInstance(String param1, String param2) {
        PickContactFragment fragment = new PickContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ActivityResultLauncher<Intent> pickContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    processPickContactResult(data);
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pick_contact, container, false);
        MainActivityData mainActivityData = new ViewModelProvider(getActivity()).get(MainActivityData.class);
        ContactDAO contactDAO = ContactDBInstance.getDatabase(requireContext()).contactDAO();

        pickContact = view.findViewById(R.id.PickContact);
        saveContact = view.findViewById(R.id.SaveContact);
        contactName = view.findViewById(R.id.EnterName);
        contactEmail = view.findViewById(R.id.EnterEmail);
        contactPhone = view.findViewById(R.id.EnterPhoneNum);
        contactImage = view.findViewById(R.id.ContactImage);

        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean duplicate = false;
                String name = contactName.getText().toString();
                String phone = contactPhone.getText().toString();
                String email = contactEmail.getText().toString();
                contactImage.setDrawingCacheEnabled(true);
                Bitmap contactPhoto = Bitmap.createBitmap(contactImage.getDrawingCache());
                contactImage.setDrawingCacheEnabled(false);
                byte[] image = getContactByteArray(contactPhoto);

                for (Contact contact: contactDAO.getAllContacts()) {
                    if (name.equals(contact.getName()))
                    {
                        duplicate = true;
                    }
                }

                if (duplicate == false)
                {
                    mainActivityData.setNewContact(0);
                    Contact newContact = new Contact();
                    newContact.setName(name);
                    newContact.setEmail(email);
                    newContact.setPhoneNum(phone);
                    newContact.setContactImage(image);
                    contactDAO.insert(newContact);
                    Toast.makeText(view.getContext(), "Contact Saved", Toast.LENGTH_SHORT).show();
                    mainActivityData.setClickedValue(2);
                }
                else
                {
                    Toast.makeText(view.getContext(), "Duplicate Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pickContact.setOnClickListener(new View.OnClickListener() { //change here
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            REQUEST_READ_CONTACT_PERMISSION);
                }
                else {
                    pickContactButtonClicked();
                }
            }
        });

        return view;
    }

    public byte[] getContactByteArray (Bitmap photo)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void pickContactButtonClicked()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        pickContactLauncher.launch(intent);
    }

    private void getPhoneNumber(){
        String result="";
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] queryFields = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        String whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
        String [] whereValues = new String[]{
                String.valueOf(this.contactId)
        };
        Cursor c = getActivity().getContentResolver().query(
                phoneUri, queryFields, whereClause,whereValues, null);
        try{
            c.moveToFirst();
            do{
                String phoneNumber = c.getString(0);
                result = result+phoneNumber+" ";
            }
            while (c.moveToNext());

        }
        finally {
            c.close();
        }

        contactPhone.setText(result);
        contactPhone.setVisibility(View.VISIBLE);
    }

    private void moreButtonClicked(){
        String result="";
        Uri emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] queryFields = new String[] {
                ContactsContract.CommonDataKinds.Email.ADDRESS
        };

        String whereClause = ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?";
        String [] whereValues = new String[]{
                String.valueOf(this.contactId)
        };
        Cursor c = getActivity().getContentResolver().query(
                emailUri, queryFields, whereClause,whereValues, null);
        try{
            c.moveToFirst();
            do{
                String emailAddress = c.getString(0);
                result = result+emailAddress+" ";
            }
            while (c.moveToNext());

        }
        finally {
            c.close();
        }

        contactEmail.setText(result);
        contactEmail.setVisibility(View.VISIBLE);

        getPhoneNumber();
    }

    private void processPickContactResult(Intent data){
        Uri contactUri = data.getData();
        String[] queryFields = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI
        };
        Cursor c = getActivity().getContentResolver().query(
                contactUri, queryFields, null, null, null);
        try {
            if (c.getCount() > 0) {
                c.moveToFirst();
                this.contactId = c.getInt(0);         // ID first
                String contactN = c.getString(1); // Name second

                String photoUriString = c.getString(2);
                if (photoUriString != null)
                {
                    try {
                        Uri photoUri = Uri.parse(photoUriString);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                        contactImage.setImageBitmap(bitmap);
                    }
                    catch (IOException e)
                    {

                    }
                }
                else
                {
                    contactImage.setImageResource(R.mipmap.ic_launcher);
                }

                getPhoneNumber();
                moreButtonClicked();
                contactName.setVisibility(View.VISIBLE);
                contactName.setText(contactN);
                contactName.setVisibility(View.VISIBLE);
            }
        }
        finally {
            c.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_READ_CONTACT_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Contact Reading Permission Granted",
                        Toast.LENGTH_SHORT).show();
                moreButtonClicked();
            }
        }
    }


}