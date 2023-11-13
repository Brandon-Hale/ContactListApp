package com.example.contactlist;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityData extends ViewModel {
    public MutableLiveData<Integer> clickedValue;
    public MutableLiveData<Integer> newContact;
    public MutableLiveData<Contact> selectedContact;

    public MainActivityData(){
        clickedValue = new MediatorLiveData<Integer>();
        selectedContact = new MediatorLiveData<Contact>();
        newContact = new MediatorLiveData<Integer>();
        clickedValue.setValue(0);
        newContact.setValue(0);
    }
    public int getClickedValue(){
        return clickedValue.getValue();
    }
    public void setClickedValue(int value){
        clickedValue.setValue(value);
    }

    public int getNewContact()
    {
        return newContact.getValue();
    }

    public void setNewContact(int value)
    {
        newContact.setValue(value);
    }

    public Contact getSelectedContact()
    {
        return selectedContact.getValue();
    }
    public void setSelectedContact(Contact contact)
    {
        selectedContact.setValue(contact);
    }

}
