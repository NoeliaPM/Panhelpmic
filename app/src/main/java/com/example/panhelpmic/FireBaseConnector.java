package com.example.panhelpmic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.panhelpmic.modelo.Anuncio;
import com.example.panhelpmic.modelo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FireBaseConnector {

    private FirebaseDatabase FBDB;
    private DatabaseReference DBR;
    private StorageReference mStorageRef;
    private User u;
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<Anuncio> anuncios = new ArrayList<Anuncio>();
    private Bitmap profileImage;
    private Logger l = new Logger();
    StorageReference listRef;



    public FireBaseConnector(){

    }

    public StorageReference getmStorageRef() {
        return mStorageRef;
    }
    public DatabaseReference getDBR(){return DBR;}

    //inicia la conexion a la base de datos
    public void initFireBase(Context c){
        try {
            FirebaseApp.initializeApp(c);
            FBDB = FirebaseDatabase.getInstance();
            DBR = FBDB.getReference();
            mStorageRef = FirebaseStorage.getInstance().getReference();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    //añade un usuario a la base de datos
    public boolean addUser(User u){
        boolean control = false;
        DBR.child("User").child(u.getNick()).setValue(u);
        return control;
    }

    //retorna un usuario del array de usuarios
    public User getUser(String nick){
        User tmp = null;
        for(User u: users){
            if(u.getNick().equals(nick)){
                tmp =  u;
            }
        }
        return tmp;
    }


    //carga en memoria los usuarios de la base de datos
    public void loadUsers(){
        l.log("FireBaseConnector:loadUsers");
        DBR.child("User").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                l.log("FireBaseConnector:loadUsers:onDataChange");
                for(DataSnapshot objS: dataSnapshot.getChildren()){
                    User u = objS.getValue(User.class);
                    l.log("FireBaseConnector:loadUsers:onDataChange" + u.toString());
                    users.add(u);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //carga en memoria los usuarios de la base de datos
    public void loadUsersToHandler(final UsersHandler uh){
        l.log("FireBaseConnector:loadUsers");
        DBR.child("User").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                l.log("FireBaseConnector:loadUsers:onDataChange");
                for(DataSnapshot objS: dataSnapshot.getChildren()){
                    User u = objS.getValue(User.class);
                    l.log("FireBaseConnector:loadUsers:onDataChange" + u.toString());
                    uh.getUsers().add(u);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //comprueba si un usuario existe en la base de datos
    public boolean chkUser(User u){
        String nick = u.getNick();
        for(User uTmp: users){
            if(nick.equals(uTmp.getNick())){
                return true;
            }
        }
        return false;
    }

    //Compruba si un usuario tiene el mismo user - pass que en la base de datos
    public boolean chkUserANdPassword(User userToChk){
        for(User u: users){
            if(userToChk.getNick().equals(u.getNick())){
                if(userToChk.getPassword().equals(u.getPassword())){
                    return true;
                }
            }
        }
        return false;
    }

    //guarda un anuncio en la base de datos
    public boolean addAnuncio(Anuncio a){
        boolean control = false;
        DBR.child("Anuncios").child(a.getTitulo()).setValue(a);
        return control;
    }

    public ArrayList<Anuncio> getAnuncios(){
        return anuncios;
    }


    //carga en memoria los anuncios de la base de datos
    public void loadAnuncios(final AnunciosHandler anunciosHandler){
        l.log("FireBaseConnector:loadAnuncios");
        DBR.child("Anuncios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                l.log("FireBaseConnector:loadAnuncios:onDataChange");
                for(DataSnapshot objS: dataSnapshot.getChildren()){
                    Anuncio a = objS.getValue(Anuncio.class);
                    anunciosHandler.getAnuncios().add(a);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                l.log("FireBaseConnector:loadAnuncios:onCancelled" + " " + databaseError.toString());
            }
        });
    }

    //retorna un anuncio de la memoria local, ojo que retorna null si el anuncio no esta en memoria
    public Anuncio getAnuncio(String titulo){
        Anuncio a = null;
        for(Anuncio atmp: anuncios){

            if(atmp.getTitulo().equals(titulo)){
                a = atmp;
            }
        }
        return a;
    }


    //Guarda una imagen en firebase, se guarda en images/profiles/ + nick del usuario (identificado unico)
    public void uploadFile(String nick, Bitmap imagen){


        StorageReference ref = mStorageRef.child("images/profiles/"+ nick + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = ref.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });

    }



    //Guarda una imagen en firebase, se guarda en images/anuncios/ + idAnuncio (identificado unico)
    public void uploadFile(User u, Anuncio a, Bitmap imagen){

        StorageReference ref = mStorageRef.child("images/anuncios/"+ u.getNick() + "-" + a.getTitulo() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = ref.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

            }
        });

    }


    public void loadProfileImageFromConectedUser(String nick) throws IOException {
        File localFile = File.createTempFile("images", "jpg");
        StorageReference ref = mStorageRef.child("images/profiles/"+ nick + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
                profileImage = BitmapFactory.decodeStream(arrayInputStream);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }


    public Bitmap getProfileImageFromConectedUser(){
        return profileImage;
    }

    public List<FileDownloadTask> getStorageTasks(){
        return listRef.getActiveDownloadTasks();

    }

    public ArrayList<Anuncio> getAnunciosByQuery(String tipoAnuncio){
        l.log("FireBaseConnector:getAnunciosByQuery:" + tipoAnuncio);
        final ArrayList<Anuncio> ar = new ArrayList<>();
        Query query = DBR.child("Anuncios").orderByChild("tipo").equalTo(tipoAnuncio);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    l.log("FireBaseConnector:getAnunciosByQuery:onDataChange");
                    for (DataSnapshot objS : dataSnapshot.getChildren()) {
                        Anuncio a = objS.getValue(Anuncio.class);
                        l.log("FireBaseConnector:getAnunciosByQuery:onDataChange:FOR" + a.toString());
                        ar.add(a);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                l.log("FireBaseConnector:getAnunciosByQuery:onCancelled" + databaseError.toString());
            }
        });
        return ar;
    }







}







