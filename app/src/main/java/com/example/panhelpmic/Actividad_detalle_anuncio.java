package com.example.panhelpmic;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.panhelpmic.modelo.Anuncio;
import com.example.panhelpmic.modelo.User;
import com.google.firebase.storage.StorageReference;

public class Actividad_detalle_anuncio extends AppCompatActivity {

    private TextView tv_titulo,tv_descripcion,tv_anunciante, tv_direccion,tv_dir,tv_fecha,tv_hora,tv_ad_nombre,tv_ad_img, tv_ad_telefono,tv_ad_email;
    private Button contactar;
    private ImageView imagen;
    private User usuarioConectado;
    private Anuncio a;
    private FireBaseConnector fbc = new FireBaseConnector();
    private Logger l = new Logger();
    private String llamante = "";
    private AnunciosHandler ah;
    private UsersHandler uh;
    private User usuarioAnuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_detalle_anuncio);
        l.log("Actividad_detalle_anuncio:onCreate");

        usuarioConectado = (User) getIntent().getExtras().get(Constantes.USUARIO_CONECTADO);
        a = (Anuncio) getIntent().getExtras().get(Constantes.ANUNCIO);
        ah = (AnunciosHandler) getIntent().getExtras().get(Constantes.ANUNCIOS_HANDLER);
        uh = (UsersHandler) getIntent().getExtras().get(Constantes.USERS_HANDLER);
        llamante = (String) getIntent().getExtras().get("llamante");
        l.log("Actividad_detalle_anuncio:onCreate: Anuncio: " + a.toString());

        //obetenemos el usuario del anuncio
        for(User u: uh.getUsers()){
            if(u.getNick().equals(a.getAutorNick())){
                usuarioAnuncio = u;
            }
        }

        if(usuarioAnuncio == null){
            usuarioAnuncio = usuarioConectado;
        }
        l.log("Actividad_detalle_anuncio:onCreate: Anuncio: " + usuarioAnuncio.toString());

        //Enlazamos con la parte gráfica
        tv_titulo = findViewById(R.id.textView_titulo_anuncio);
        tv_descripcion = findViewById(R.id.textView_descripcion);
        tv_fecha = findViewById(R.id.textView_Fecha);
        tv_hora = findViewById(R.id.textView_hora);
        tv_anunciante = findViewById(R.id.textView_anunciante);
        tv_direccion = findViewById(R.id.textView_direccion);
        tv_dir=findViewById(R.id.textView24);
        contactar = findViewById(R.id.button_solicitar_servicio);

        //Flecha hacia atrás en la actionBar
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_titulo.setText( a.getTitulo());
        tv_descripcion.setText( a.getDescripcion());
        tv_anunciante.setText(a.getAutorNick());
        tv_direccion.setText(a.getDireccion());
        tv_fecha.setText( a.getFecha());
        tv_hora.setText( a.getHora());

        fbc.initFireBase(getApplicationContext());
        if(a.isTieneImagen()){
            l.log("Actividad_detalle_anuncio:onCreate:Accediendo a la imagen del anuncio... " + a.getAutorNick() + "-" + a.getTitulo() + ".jpg");
            StorageReference storageReference = fbc.getmStorageRef().child("images/anuncios/" + a.getAutorNick() + "-" + a.getTitulo() + ".jpg");
            ImageView imageView = findViewById(R.id.imageView_anuncio);
            Glide.with(getApplicationContext() )
                    .load(storageReference)
                    .into(imageView);
        }else{
            l.log("Actividad_detalle_anuncio:onCreate:a.isTieneImagen():ELSE");
            StorageReference storageReference = fbc.getmStorageRef().child("images/anuncios/" + "imagen_por_defecto" + ".jpg");
            ImageView imageView = findViewById(R.id.imageView_anuncio);
            Glide.with(getApplicationContext() )
                    .load(storageReference)
                    .into(imageView);
        }
        if(Constantes.TIPO_ANUNCIO_ENTRETENIMIENTO.equals(a.getTipo())){
            l.log("Actividad_detalle_anuncio:onCreate:Constantes.TIPO_ANUNCIO_ENTRETENIMIENTO.equals(a.getTipo())");
            tv_hora.setVisibility(View.VISIBLE);
            tv_fecha.setVisibility(View.VISIBLE);
            contactar.setVisibility(View.INVISIBLE);
            tv_direccion.setVisibility(View.INVISIBLE);
            tv_dir.setVisibility(View.INVISIBLE);
        }
        //Si el anunciante es el mismo que el usuario conectado se quita el boton contactar
        if(a.getAutorNick().equals(usuarioConectado.getNick())){
            l.log("Actividad_detalle_anuncio:onCreate:a.getAutorNick().equals(usuarioConectado.getNick())");
            contactar.setVisibility(View.INVISIBLE);
        }
        //Onclick botón contactar
        contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_detalle_anuncio:onCreate:contactar.setOnClickListener");
                AlertDialog.Builder builder = new AlertDialog.Builder(Actividad_detalle_anuncio.this);
                LayoutInflater li = getLayoutInflater();
                View view = li.inflate(R.layout.alertdialog_contacto, null);
                tv_ad_nombre=view.findViewById(R.id.textView_nombre_ad);
                tv_ad_email=view.findViewById(R.id.textView_email_ad);
                tv_ad_telefono=view.findViewById(R.id.textView_telefono_Ad);
                imagen=view.findViewById(R.id.imageView_alert);
                tv_ad_nombre.setText(""+usuarioAnuncio.getNombre());
                tv_ad_telefono.setText(""+usuarioAnuncio.getTelefono());
                tv_ad_email.setText(""+usuarioAnuncio.getEmail());
                if(usuarioConectado.isTieneImagen()){
                    l.log("Fragment_perfil_usuario:onCreateView" + " usuarioConectado: TRY" );
                    StorageReference storageReference = fbc.getmStorageRef().child("images/profiles/" + usuarioAnuncio.getNick() + ".jpg");
                    Glide.with(getApplicationContext()/* context */)
                            .load(storageReference)
                            .apply(new RequestOptions().override(100, 100))
                            .into(imagen);
                }else{
                    l.log("Fragment_perfil_usuario:onCreateView" + " usuarioConectado: Catch" );
                    StorageReference storageReference = fbc.getmStorageRef().child("images/profiles/" + "imagen_defecto" + ".png");
                    Glide.with(getApplicationContext())
                            .load(storageReference)
                            .apply(new RequestOptions().override(100, 100))
                            .into(imagen);
                }
                builder.setView(view);
                builder.setNegativeButton(R.string.cerrar, null);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public boolean onSupportNavigateUp(){
        l.log("Actividad_detalle_anuncio:onSupportNavigateUp");
        l.log("Actividad_detalle_anuncio:onSupportNavigateUp: " + llamante);
        if("Fragment_Crear_Anuncios".equals(llamante)){
            Intent i=new Intent(this, Actividad_Principal.class);
            i.putExtra(Constantes.USUARIO_CONECTADO, usuarioConectado);
            i.putExtra(Constantes.ANUNCIOS_HANDLER, ah);
            i.putExtra(Constantes.USERS_HANDLER, uh);
            startActivityForResult(i,0);
        }else{
            finish();
        }

        return true;
    }

    public void onBackPressed() {
        l.log("Actividad_detalle_anuncio:onBackPressed");
        l.log("Actividad_detalle_anuncio:onSupportNavigateUp: " + llamante);
        if("Fragment_Crear_Anuncios".equals(llamante)){
            Intent i=new Intent(this, Actividad_Principal.class);
            i.putExtra(Constantes.USUARIO_CONECTADO, usuarioConectado);
            i.putExtra(Constantes.ANUNCIOS_HANDLER, ah);
            i.putExtra(Constantes.USERS_HANDLER, uh);
            startActivityForResult(i,0);
        }else{
            finish();
        }

    }
}
