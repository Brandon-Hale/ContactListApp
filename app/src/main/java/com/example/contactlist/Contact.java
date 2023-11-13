package com.example.contactlist;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private long Id;

    @ColumnInfo(name = "ContactImage")
    private byte[] contactImage;

    @ColumnInfo(name = "Name")
    private String name;
    @ColumnInfo(name = "Email")
    private String email;

    @ColumnInfo(name = "PhoneNumber")
    private String phoneNum;
    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public byte[] getContactImage() {
        return contactImage;
    }

    public void setContactImage(byte[] contactImage) {
        this.contactImage = contactImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

}
