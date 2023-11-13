package com.example.contactlist;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ContactCreate extends Fragment {

    private static final int REQUEST_CODE = 22;
    private Button SaveButton;

    private Button takePhoto;
    private EditText ContactName;
    private EditText ContactEmail;
    private EditText ContactPhone;
    private ImageView ContactImage;
    public ContactCreate() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_create, container, false);

        MainActivityData mainActivityData = new ViewModelProvider(getActivity()).get(MainActivityData.class);
        ContactDAO contactDAO = ContactDBInstance.getDatabase(requireContext()).contactDAO();


        SaveButton = view.findViewById(R.id.SaveContact);
        ContactName = view.findViewById(R.id.EnterName);
        ContactEmail = view.findViewById(R.id.EnterEmail);
        ContactPhone = view.findViewById(R.id.EnterPhoneNum);
        ContactImage = view.findViewById(R.id.ContactImage);
        takePhoto = view.findViewById(R.id.Camera);

        try {
            if (mainActivityData.getNewContact() == 0) {
                SaveButton.setText("Save");
            }
        } catch (Exception e) {

        }
        try {
            if (mainActivityData.getNewContact() == 1) {
                Contact contact = mainActivityData.getSelectedContact();
                ContactName.setText(contact.getName());
                ContactEmail.setText(contact.getEmail());
                ContactPhone.setText(contact.getPhoneNum());
                ContactImage.setImageBitmap(getContactBitmap(contact.getContactImage()));
                SaveButton.setText("Update");
            }
        }
        catch (Exception e) {
        }

            SaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contactName = ContactName.getText().toString();
                    String contactEmail = ContactEmail.getText().toString();
                    String contactPhone = ContactPhone.getText().toString();
                    ContactImage.setDrawingCacheEnabled(true);
                    Bitmap contactPhoto = Bitmap.createBitmap(ContactImage.getDrawingCache());
                    ContactImage.setDrawingCacheEnabled(false);
                    byte[] contactByteArray = getContactByteArray(contactPhoto);

                    if (mainActivityData.getNewContact() == 0) //new
                    {
                        boolean duplicate = false;
                        for (Contact contact: contactDAO.getAllContacts()) {
                            if (contactName.equals(contact.getName()))
                            {
                                duplicate = true;
                            }
                        }
                        if (duplicate == false)
                        {
                            Contact newContact = new Contact();
                            newContact.setName(contactName);
                            newContact.setEmail(contactEmail);
                            newContact.setPhoneNum(contactPhone);
                            newContact.setContactImage(contactByteArray);
                            contactDAO.insert(newContact);
                            Toast.makeText(view.getContext(), "Contact Saved", Toast.LENGTH_SHORT).show();
                            mainActivityData.setClickedValue(2);
                        }
                        else
                        {
                            Toast.makeText(view.getContext(), "Duplicate Name", Toast.LENGTH_SHORT).show();
                        }
                    } else if (mainActivityData.getNewContact() == 1) //update
                    {
                        Contact contact = mainActivityData.getSelectedContact();
                        contact.setName(contactName);
                        contact.setEmail(contactEmail);
                        contact.setPhoneNum(contactPhone);
                        contact.setContactImage(contactByteArray);
                        contactDAO.update(contact);
                        Toast.makeText(view.getContext(), "Contact Updated", Toast.LENGTH_SHORT).show();
                        mainActivityData.setClickedValue(2);
                    }
                }
            });

            takePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(takePictureIntent, REQUEST_CODE);
                }
            });
            return view;
        }


        @Override
        public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ContactImage.setImageBitmap(photo);
            } else {

                Toast.makeText(getContext(), "Add Contact", Toast.LENGTH_SHORT).show();
                super.onActivityResult(requestCode, resultCode, data);
            }

        }

        public byte[] getContactByteArray (Bitmap photo)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }

        public Bitmap getContactBitmap(byte[] contactByteArray)
        {
            Bitmap photo = BitmapFactory.decodeByteArray(contactByteArray, 0, contactByteArray.length);
            return photo;
        }
    }