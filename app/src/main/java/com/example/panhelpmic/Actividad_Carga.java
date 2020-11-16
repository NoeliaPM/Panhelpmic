package com.example.panhelpmic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

public class Actividad_Carga extends AppCompatActivity {
    private ProgressBar pb;
    private Async tarea;
    private Logger l = new Logger();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        l.log("Actividad_Carga:onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad__carga);
        //Enlazamos con la parte gráfica
        pb=findViewById(R.id.progressBar2);
        tarea=new Async();
        tarea.execute();
    }
    private void tareaLarga(){
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {}
    }
    private class Async extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            for (int i=0; i<=2; i++){
                tareaLarga();
                publishProgress(i*10);
                if (isCancelled())
                    break;
            }
            return true;
        }
        protected void onProgressUpdate(Integer... values){
            //Aquí actualizamos el estado de la barra de progreso con el valor recibido como parámetro
            int progreso = values[0].intValue();
            pb.setProgress(progreso);
        }
        protected void onPreExecute(){
            //Inicializamos la barra de progreso poniendo su valor máximo y a cero para comenzar
            pb.setMax(20);
            pb.setProgress(0);
        }
        protected void onPostExecute(Boolean result){
            if (result){
                Intent i=new Intent(Actividad_Carga.this, Actividad_Inicio.class);
                startActivity(i);
                finish();
            }
        }
    }
}
