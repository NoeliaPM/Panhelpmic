package com.example.panhelpmic;

import com.example.panhelpmic.modelo.User;

import java.io.Serializable;
import java.util.ArrayList;

public class UsersHandler implements Serializable {

    private ArrayList<User> users = new ArrayList<User>();

    public UsersHandler() {

    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> userts) {
        this.users = userts;
    }

    public User getUser(String nick){
        for(User u: users){
            if(u.getNick().equals(nick)){
                return u;
            }
        }
        return null;
    }
}
