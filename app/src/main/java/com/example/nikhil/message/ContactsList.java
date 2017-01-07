package com.example.nikhil.message;

import java.util.ArrayList;

/**
 * Created by nikhil on 14/12/16.
 */

public class ContactsList {

    public ArrayList<Contact> contactArrayList;

    ContactsList(){

        contactArrayList = new ArrayList<Contact>();
    }

    public int getCount(){

        return contactArrayList.size();
    }

    public void addContact(Contact contact){
        contactArrayList.add(contact);
    }

    public  void removeContact(Contact contact){
        contactArrayList.remove(contact);
    }

    public Contact getContact(int id){

        for(int i=0;i<this.getCount();i++){
            if(Integer.parseInt(contactArrayList.get(i).id)==id)
                return contactArrayList.get(i);
        }

        return null;
    }
}
