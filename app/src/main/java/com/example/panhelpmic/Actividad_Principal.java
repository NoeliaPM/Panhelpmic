package com.example.panhelpmic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.panhelpmic.modelo.Anuncio;
import com.example.panhelpmic.modelo.User;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;


import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.IOException;
import java.util.ArrayList;

public class Actividad_Principal extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private User usuarioConectado;
    private Logger l = new Logger();
    private FireBaseConnector fbc = new FireBaseConnector();
    private AnunciosHandler ah;
    private UsersHandler uh;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        l.log("Actividad_Principal:onCreate");
        setContentView(R.layout.actividad__principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usuarioConectado = (User) getIntent().getExtras().get(Constantes.USUARIO_CONECTADO);
        ah = (AnunciosHandler) getIntent().getExtras().get(Constantes.ANUNCIOS_HANDLER);
        uh = (UsersHandler) getIntent().getExtras().get(Constantes.USERS_HANDLER);
        fbc.initFireBase(getApplicationContext());
        fbc.loadUsers();

        try {
            fbc.loadProfileImageFromConectedUser(usuarioConectado.getNick());
            l.log("Actividad_Principal:onCreate:fbc.loadProfileImageFromConectedUser(usuarioConectado.getNick());" );
        } catch (IOException e) {
            e.printStackTrace();
            l.log("Actividad_Principal:onCreate:fbc.loadProfileImageFromConectedUser(usuarioConectado.getNick()):e.printStackTrace()" + e.getStackTrace().toString() );
        }

        //Enlace con la parte gráfica del menú
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.fragment_anuncios_basicos,R.id.fragment_anuncios_profesionales,R.id.fragment_anuncios_entretenimiento, R.id.fragment_Crear_Anuncios, R.id.fragment_mapa2, R.id.fragment_perfil_usuario)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        l.log("Actividad_Principal:onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.actividad__principal, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem opcion){
        int id=opcion.getItemId();
        if(id==R.id.action_settings){
            l.log("Actividad_Principal:onOptionsItemSelected:action_settings");
            abrirConfiguracion();
            return true;
        }
        else if(id==R.id.cerrar_sesion){
            l.log("Actividad_Principal:onOptionsItemSelected:cerrar_sesion");
            cerrarSesion();
            return true;
        }
        else
            return super.onOptionsItemSelected(opcion);
    }
    private void abrirConfiguracion(){
        Intent i=new Intent(this,Actividad_Ajustes.class);
        i.putExtra("actividad",2);
        startActivityForResult(i,0);
    }
    public void cerrarSesion(){
        finish();
        Intent i=new Intent(this,Actividad_Inicio.class);
        startActivityForResult(i,0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        l.log("Actividad_Principal:onSupportNavigateUp");
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public User getUsuarioConectado(){
        return usuarioConectado;
    }
    public ArrayList<Anuncio> getAnuncios(){
        return ah.getAnuncios();
    }
    public FireBaseConnector getFbc(){
        return fbc;
    }
    public AnunciosHandler getAnunciosHandler(){
        return ah;
    }
    public UsersHandler getUsersHandler(){
        return uh;
    }



}
