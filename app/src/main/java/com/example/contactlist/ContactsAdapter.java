package com.example.contactlist;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    Context context;
    List<Contact> contactItemList;

    MainActivityData mainActivityData;

    public ContactsAdapter(Context context, List<Contact> contactItemList, MainActivityData mainActivityData) {
        this.context = context;
        this.contactItemList = contactItemList;
        this.mainActivityData = mainActivityData;
    }
    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.contactitem, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {

        ContactDAO contactDAO = ContactDBInstance.getDatabase(context.getApplicationContext()).contactDAO();
        //holder.ContactImage.setImageResource(contactItemList.get(position).getImageID());
        holder.name.setText(contactItemList.get(position).getName());
        holder.email.setText(contactItemList.get(position).getEmail());
        holder.phoneNumber.setText(contactItemList.get(position).getPhoneNum());
        holder.ContactImage.setImageBitmap(getContactBitmap(contactItemList.get(position).getContactImage()));
        holder.deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactDAO.delete(contactItemList.get(holder.getAbsoluteAdapterPosition()));
                contactItemList.remove(holder.getAbsoluteAdapterPosition());
                notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                removeContact(holder.getAbsoluteAdapterPosition());
                ContactsAdapter contactsAdapter = new ContactsAdapter(view.getContext(), contactItemList, mainActivityData);
                contactsAdapter.notifyDataSetChanged();
                Toast.makeText(view.getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();
                Log.d("Delete", "On delete click");
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityData.setNewContact(1);
                Contact contact = contactItemList.get(holder.getBindingAdapterPosition());
                mainActivityData.setSelectedContact(contact);
                mainActivityData.setClickedValue(1);
            }
        });
    }

    public void removeContact(int position){
        if(position >= 0 && position < contactItemList.size()){
            contactItemList.remove(position);
            notifyItemRemoved(position);

        }
    }

    public Bitmap getContactBitmap(byte[] contactByteArray)
    {
        try
        {
            Bitmap photo = BitmapFactory.decodeByteArray(contactByteArray, 0, contactByteArray.length);
            return photo;
        }
        catch (Exception e)
        {

        }
        return null;
    }


    @Override
    public int getItemCount() {
        return contactItemList.size();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        public ImageView ContactImage;
        public TextView name;
        public TextView email;
        public TextView phoneNumber;
        public ImageButton deleteContact;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            ContactImage = itemView.findViewById(R.id.Contactimage);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            deleteContact = itemView.findViewById(R.id.deleteButton);

        }
    }
}
