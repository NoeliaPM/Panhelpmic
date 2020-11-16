package com.example.panhelpmic;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.panhelpmic.modelo.User;

public class Actividad_Inicio extends AppCompatActivity {

    private EditText etUsuario, etContra;
    private FireBaseConnector fbc = new FireBaseConnector();
    private AnunciosHandler ah;
    private UsersHandler uh;
    private Logger l = new Logger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        l.log("Actividad_Inicio:onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_inicio);
        Button botonInicio = findViewById(R.id.button_inicio);
        Button botonRegistro = findViewById(R.id.button_registro);
        Button botonAjustes = findViewById(R.id.button_ajustes);
        etUsuario = findViewById(R.id.editText_user);
        etContra = findViewById(R.id.editText_contra);
        ah = new AnunciosHandler();
        uh = new UsersHandler();
        fbc.initFireBase(getApplicationContext());
        fbc.loadUsers();
        fbc.loadUsersToHandler(uh);
        fbc.loadAnuncios(ah);

        //Onclick inicio
        botonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Inicio:botonInicio.setOnClickListener");
                String usuario = etUsuario.getText().toString();
                String contrasena = etContra.getText().toString();
                User u = new User(usuario, contrasena);
                boolean valid = fbc.chkUserANdPassword(u);
                if(valid){
                    l.log("Actividad_Inicio:botonInicio.setOnClickListener:valid");
                    User usuarioConectado = fbc.getUser(u.getNick());
                    Intent i=new Intent(v.getContext(),Actividad_Principal.class);
                    i.putExtra(Constantes.USUARIO_CONECTADO, usuarioConectado);
                    i.putExtra(Constantes.ANUNCIOS_HANDLER, ah);
                    i.putExtra(Constantes.USERS_HANDLER, uh);
                    startActivityForResult(i,0);
                }else{
                    l.log("Actividad_Inicio:botonInicio.setOnClickListener:not_valid");
                    Toast toast=Toast.makeText(getApplicationContext(),R.string.user_incorrecto,Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        //Onclick registro
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Inicio:botonRegistro.setOnClickListener");
                Intent i=new Intent(v.getContext(),Actividad_Registro.class);
                i.putExtra(Constantes.ANUNCIOS_HANDLER, ah);
                i.putExtra(Constantes.USERS_HANDLER, uh);
                startActivity(i);
            }
        });

        //Onclick ajustes
        botonAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Inicio:botonAjustes.setOnClickListener:onClick");
                Intent i=new Intent(v.getContext(),Actividad_Ajustes.class);
                i.putExtra("actividad",1);
                startActivityForResult(i,0);
            }
        });
    }
}
