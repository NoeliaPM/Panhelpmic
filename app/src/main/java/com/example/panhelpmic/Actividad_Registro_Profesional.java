package com.example.panhelpmic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.panhelpmic.modelo.User;

public class Actividad_Registro_Profesional extends AppCompatActivity {
    private RadioButton rb_sanitario, rb_abogado;
    private Button boton_confirmar, boton_continuar, boton_ayuda;
    private EditText et_especialidad;
    private boolean confirmar,check;
    private FireBaseConnector fbc = new FireBaseConnector();
    private User usuarioConectado;
    private AnunciosHandler ah;
    private Logger l = new Logger();
    private UsersHandler uh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        l.log("Actividad_Registro_Profesional:onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad__registro__profesional);
        usuarioConectado = (User) getIntent().getExtras().get("usuarioConectado");
        ah = (AnunciosHandler) getIntent().getExtras().get(Constantes.ANUNCIOS_HANDLER);
        uh = (UsersHandler) getIntent().getExtras().get(Constantes.USERS_HANDLER);
        fbc.initFireBase(getApplicationContext());
        fbc.loadUsers();
        rb_sanitario=findViewById(R.id.radioButton_Sanitario);
        rb_abogado=findViewById(R.id.radioButton_Abogado);
        boton_confirmar=findViewById(R.id.button_Confirmar);
        boton_continuar=findViewById(R.id.button_continuarP);
        boton_ayuda=findViewById(R.id.button_Ayuda);
        et_especialidad=findViewById(R.id.editText_Especialidad);
        confirmar=false;
        check=false;
        //Onclick botón confirmar
        boton_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Registro_Profesional:botonConfirmar.setOnClickListener");
                //Si es un sanitario
                if(rb_sanitario.isChecked()){
                    l.log("Actividad_Registro_Profesional:botonConfirmar.setOnClickListener:Sanitario");
                    check=true;
                    usuarioConectado.setTipoProfesional("Sanitario");
                    et_especialidad.setVisibility(View.VISIBLE);
                }
                //Si es un abogado
                if(rb_abogado.isChecked()){
                    l.log("Actividad_Registro_Profesional:botonConfirmar.setOnClickListener:Abogado");
                    usuarioConectado.setTipoProfesional("Abogado");
                    check=true;
                }
                if(check){
                    l.log("Actividad_Registro_Profesional:botonConfirmar.setOnClickListener:valid:check");
                    //Mostramos el botón de continuar
                    boton_continuar.setVisibility(View.VISIBLE);
                }
                else{
                    l.log("Actividad_Registro_Profesional:botonConfirmar.setOnClickListener:not_valid:uncheck");
                    Toast toast= Toast.makeText(getApplicationContext(),R.string.tqsutdp, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        //OnClick botón continuar
        boton_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Registro_Profesional:botonContinuar.setOnClickListener");
                if(et_especialidad.getText().toString().isEmpty()){
                    l.log("Actividad_Registro_Profesional:botonConfirmar.setOnClickListener:not_valid:especialidad");
                    Toast toast= Toast.makeText(getApplicationContext(),R.string.tqite, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    l.log("Actividad_Registro_Profesional:botonConfirmar.setOnClickListener:valid:especialidad");
                    String especialidad = et_especialidad.getText().toString();
                    usuarioConectado.setEspecialidad(especialidad);
                    fbc.addUser(usuarioConectado);
                    Toast toast= Toast.makeText(getApplicationContext(),R.string.ucc, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent i=new Intent(v.getContext(),Actividad_Principal.class);
                    i.putExtra("usuarioConectado", usuarioConectado);
                    i.putExtra(Constantes.ANUNCIOS_HANDLER, ah);
                    fbc.loadUsersToHandler(uh);
                    i.putExtra(Constantes.USERS_HANDLER, uh);
                    startActivityForResult(i,0);
                    finish();
                }
            }
        });
        //OnClick botón ayuda
        boton_ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Registro_Profesional:botonAyuda.setOnClickListener");
                AlertDialog.Builder builder= new AlertDialog.Builder(Actividad_Registro_Profesional.this);
                LayoutInflater li=getLayoutInflater();
                View view=li.inflate(R.layout.alertdialog_ayudaprofesionales, null);
                builder.setView(view);
                AlertDialog alertDialog=builder.create();
                builder.setNegativeButton(R.string.cerrar,null);
                builder.setCancelable(false);
                final AlertDialog dialog=builder.create();
                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.morado);
                    }
                });
                dialog.show();
            }
        });
    }
}
