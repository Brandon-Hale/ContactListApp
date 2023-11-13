package com.example.contactlist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDAO {

    @Insert
    void insert(Contact... newContact);

    @Delete
    void delete(Contact... contact);

    @Update
    void update(Contact... contact);

    @Query("SELECT * FROM contacts")
    List<Contact> getAllContacts();

    @Query("SELECT * FROM Contacts WHERE Name = :contactName")
    Contact getContactsByName(String contactName);

    @Query("SELECT * FROM Contacts WHERE Id = :contactID")
    Contact getContactsByID(String contactID);


}
