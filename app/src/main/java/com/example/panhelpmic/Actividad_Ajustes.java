package com.example.panhelpmic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Locale;

public class Actividad_Ajustes extends AppCompatActivity {
    private Button boton_sonido, boton_salir;
    private Spinner spinner_idioma;
    private boolean sonido=false;
    int actividad=0;
    private Logger l = new Logger();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_ajustes);
        l.log("Actividad_Ajustes:onCreate");
        //Enlazamos elementos con la parte gráfica
        boton_sonido=findViewById(R.id.button_musica);
       // boton_salir=findViewById(R.id.button_salir);
        spinner_idioma=findViewById(R.id.spinner_idioma);

        //Optenemos el bundle para saber de qué actividad viene el usuario
        Bundle b=getIntent().getExtras();
        actividad=b.getInt("actividad");

        //Creamos el array adapter del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_idioma.setAdapter(adapter);

        //Evento qué spinner está seleccionado
        spinner_idioma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if((""+parent.getItemAtPosition(position)).equals(getString(R.string.esp))){
                    Locale localizacion = new Locale("es_ES");
                    Locale.setDefault(localizacion);
                    Configuration config = new Configuration();
                    config.locale = localizacion;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                }
                else{
                    Locale localizacion = new Locale("en_EN");
                    Locale.setDefault(localizacion);
                    Configuration config = new Configuration();
                    config.locale = localizacion;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {            }
        });

        //OnClick botón sonido
        boton_sonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Registro:botonSonido:setOnClickListener");
                if(sonido){
                    apagaMusica(v);
                    sonido=false;
                    boton_sonido.setBackgroundResource(R.drawable.soundoff);
                }
                else{
                    enciendeMusica(v);
                    sonido=true;
                    boton_sonido.setBackgroundResource(R.drawable.soundon);
                }
            }
        });
    }

    public void enciendeMusica(View v){
        l.log("Actividad_Registro:enciendeMusica");
        Intent miMusica=new Intent(v.getContext(),Servicio_musica.class);
        startService(miMusica);
    }

    public void apagaMusica(View v){
        l.log("Actividad_Registro:apagaMusica");
        Intent miMusica=new Intent(v.getContext(),Servicio_musica.class);
        stopService(miMusica);
    }
}