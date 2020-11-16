package com.example.panhelpmic;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.panhelpmic.modelo.Anuncio;
import com.example.panhelpmic.modelo.User;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Crear_Anuncios extends Fragment{
    private EditText et_titulo,et_descripcion, et_fecha, et_hora;
    private Button boton_cargar_foto, boton_publicar, boton_hora, boton_fecha, boton_basico,boton_profesional,boton_entretenimiento;
    private ImageView imagen;
    private static final int GALLERY_INTENT=1;
    private boolean entretenimiento=false, b_basico=false,b_entretenimiento=false,b_profesional=false;
    private Bitmap imagenAnuncio;
    private View item;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private FireBaseConnector fbc;
    private Logger l = new Logger();
    private User usuarioConectado;
    private ArrayList<Anuncio> anuncios;
    private AnunciosHandler ah;
    private int hora, min, dia, mes, ano;
    private UsersHandler uh;

    public Fragment_Crear_Anuncios() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        l.log("Fragment_Crear_Anuncios:onCreateView");
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        activity.getSupportActionBar().setTitle(R.string.fca);
        usuarioConectado = activity.getUsuarioConectado();
        anuncios = activity.getAnuncios();
        fbc = activity.getFbc();
        ah = activity.getAnunciosHandler();
        uh = activity.getUsersHandler();
        item= inflater.inflate(R.layout.fragment__crear__anuncios, container, false);

        //Enlazamos los elementos gráficos del fragment
        et_titulo = item.findViewById(R.id.editText_titulo_anuncio);
        et_descripcion = item.findViewById(R.id.editText_descripcion_anuncio);
        et_fecha = item.findViewById(R.id.editText_fecha);
        et_hora = item.findViewById(R.id.editText_hora);
        boton_basico=item.findViewById(R.id.button_basico_ca);
        boton_profesional=item.findViewById(R.id.button_profesional_ca);
        boton_entretenimiento=item.findViewById(R.id.button_entretenimiento_ca);
        boton_cargar_foto = item.findViewById(R.id.button_subir_foto_anuncio);
        boton_publicar = item.findViewById(R.id.button_publicar_anuncio);
        boton_hora=item.findViewById(R.id.button_hora);
        boton_fecha=item.findViewById(R.id.button_fecha);

        //Si el usuario que está conectado es profesional le dejamos seleccionar tipo de anuncio profesional
        if(usuarioConectado.isProfesional()){
            l.log("Fragment_Crear_Anuncios:onCreateView:usuarioConectado.isProfesional()");
            boton_profesional.setVisibility(View.VISIBLE);
        }
        //Onclick botones tipo de anuncio
        boton_basico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_basico=true;
                b_entretenimiento=false;
                b_profesional=false;
                boton_basico.setBackgroundResource(R.drawable.ok_peque);
                boton_entretenimiento.setBackgroundResource(R.drawable.x_peque);
                boton_profesional.setBackgroundResource(R.drawable.x_peque);
                et_fecha.setVisibility(View.INVISIBLE);
                et_hora.setVisibility(View.INVISIBLE);
                boton_hora.setVisibility(View.INVISIBLE);
                boton_fecha.setVisibility(View.INVISIBLE);
            }
        });
        boton_entretenimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_entretenimiento=true;
                b_profesional=false;
                b_basico=false;
                boton_basico.setBackgroundResource(R.drawable.x_peque);
                boton_entretenimiento.setBackgroundResource(R.drawable.ok_peque);
                boton_profesional.setBackgroundResource(R.drawable.x_peque);
                et_fecha.setVisibility(View.VISIBLE);
                et_hora.setVisibility(View.VISIBLE);
                boton_hora.setVisibility(View.VISIBLE);
                boton_fecha.setVisibility(View.VISIBLE);
            }
        });
        boton_profesional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_profesional=true;
                b_entretenimiento=false;
                b_basico=false;
                boton_basico.setBackgroundResource(R.drawable.x_peque);
                boton_entretenimiento.setBackgroundResource(R.drawable.x_peque);
                boton_profesional.setBackgroundResource(R.drawable.ok_peque);
                et_fecha.setVisibility(View.INVISIBLE);
                et_hora.setVisibility(View.INVISIBLE);
                boton_hora.setVisibility(View.INVISIBLE);
                boton_fecha.setVisibility(View.INVISIBLE);
            }
        });
        //Onclick boton subida de imagen
        boton_cargar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Fragment_Crear_Anuncios:boton_cargar_foto.setOnClickListener");
                requestRead();
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
        //Onclick boton publicar anuncio
        boton_publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Fragment_Crear_Anuncios:boton_publicar.setOnClickListener");
                Anuncio a = new Anuncio();

                //Si hay algún campo sin marcar
                if(et_titulo.getText().toString().isEmpty()||et_descripcion.getText().toString().isEmpty()){
                    l.log("Fragment_Crear_Anuncios:boton_publicar.setOnClickListener:et_titulo.getText().toString().isEmpty()||et_descripcion.getText().toString().isEmpty()");
                    Toast toast= Toast.makeText(getContext(),"Los campos 'Titulo' y 'Descripción' son obligatorios.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                //Si todos los campos están marcados
                else{
                    l.log("Fragment_Crear_Anuncios:boton_publicar.setOnClickListener:else");
                    if(b_basico){
                        l.log("Fragment_Crear_Anuncios:rb_basico.isChecked()");
                        a.setTipo("Basico");
                    }
                    if(b_profesional){
                        l.log("Fragment_Crear_Anuncios:rb_profesional.isChecked()");
                        a.setTipo("Profesional");
                    }
                    if(b_entretenimiento){
                        l.log("Fragment_Crear_Anuncios:rb_entretenimiento.isChecked()");
                        entretenimiento=true;
                        a.setTipo("Entretenimiento");
                    }
                    //Recogemos los datos y los guardamos en el objeto
                    String titulo = et_titulo.getText().toString();
                    a.setTitulo(titulo);
                    a.setDireccion(usuarioConectado.getDireccion());
                    String descripcion = et_descripcion.getText().toString();
                    a.setDescripcion(descripcion);
                    a.setAutorNick(usuarioConectado.getNick());
                    //Si el anuncio es de entretenimiento hay que guardar más datos
                    if(entretenimiento){
                        l.log("Fragment_Crear_Anuncios:boton_publicar.setOnClickListener:else:entretenimiento");
                        String fecha = et_fecha.getText().toString();
                        a.setFecha(fecha);
                        String hora=et_hora.getText().toString();
                        a.setHora(hora);
                    }
                    //Si ya hay un anuncio con ese título
                    if(existeAnuncio(a)){
                        l.log("Fragment_Crear_Anuncios:boton_publicar.setOnClickListener:else:existeAnuncio(a) TRUE");
                        Toast toast= Toast.makeText(getContext(),"Ya has publicado un anuncio con ese titulo.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    //Si el título del anuncio es correcto
                    else{
                        l.log("Fragment_Crear_Anuncios:boton_publicar.setOnClickListener:else:existeAnuncio(a) FALSE");
                        //Si ha subido una imagen la guardamos
                        if(imagenAnuncio != null){
                            l.log("Fragment_Crear_Anuncios:boton_publicar.setOnClickListener:else:imagenAnuncio != null");
                            fbc.uploadFile(usuarioConectado, a, imagenAnuncio);
                            a.setTieneImagen(true);
                        }else{
                            a.setTieneImagen(false);
                        }
                        fbc.addAnuncio(a);
                        anuncios.add(a);
                        Toast toast= Toast.makeText(getContext(),"Publicando Anuncio.... ", Toast.LENGTH_SHORT);
                        toast.show();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast toast2= Toast.makeText(getContext(),"Anuncio Publicado.", Toast.LENGTH_SHORT);
                        toast.show();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Una vez publicado el anuncio llevamos al usuario al detalle de este
                        Intent i=new Intent(getContext(),Actividad_detalle_anuncio.class);
                        i.putExtra(Constantes.ANUNCIO, a);
                        i.putExtra(Constantes.USUARIO_CONECTADO, usuarioConectado);
                        i.putExtra(Constantes.ANUNCIOS_HANDLER, ah);
                        i.putExtra(Constantes.USERS_HANDLER, uh);
                        i.putExtra("llamante", "Fragment_Crear_Anuncios");
                        startActivityForResult(i,0);
                    }
                }
            }

        });
        //OnClick botón seleccionar hora
        boton_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c= Calendar.getInstance();
                hora=c.get(Calendar.HOUR_OF_DAY);
                min=c.get(Calendar.MINUTE);
                TimePickerDialog tpd=new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        et_hora.setText(hourOfDay+":"+minute);
                    }
                },hora,min,true);
                tpd.show();

            }
        });
        //OnClick botón seleccionar fecha
        boton_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c= Calendar.getInstance();
                dia=c.get(Calendar.DAY_OF_MONTH);
                mes=c.get(Calendar.MONTH + 1);
                ano=c.get(Calendar.YEAR);

                DatePickerDialog dpd= new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et_fecha.setText(dayOfMonth+"/"+(month + 1)+"/"+year);
                    }
                },dia,mes,ano);
                dpd.show();
            }
        });
        activity.getSupportActionBar().setTitle("Publicar anuncio");
        return item;
    }

    //Obtenemos la foto que hemos seleccionado
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        l.log("Fragment_Crear_Anuncios:onActivityResult");
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            l.log("Fragment_Crear_Anuncios:onActivityResult:requestCode == GALLERY_INTENT && resultCode == RESULT_OK");
            final Uri imageUri = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = item.getContext().getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                l.log("Fragment_Crear_Anuncios:onActivityResult:e.printStackTrace(): " + e.getMessage());
            }
            imagenAnuncio = BitmapFactory.decodeStream(imageStream);
            Toast toast= Toast.makeText(item.getContext().getApplicationContext(),"Imagen subida correctamente.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Solicita permisos de Lectura
    public void requestRead(){
        if (ContextCompat.checkSelfPermission(item.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //readFile();
            } else {
                // Permission Denied
                Toast.makeText(item.getContext().getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private boolean existeAnuncio(Anuncio aTmp){
        l.log("Fragment_Crear_Anuncios:existeAnuncio" + aTmp.getTitulo() + " " + aTmp.isTieneImagen());
        l.log("Fragment_Crear_Anuncios:existeAnuncio" + anuncios.toString());
        for(Anuncio aLoop: anuncios){
            if(aLoop.getTitulo().equals(aTmp.getTitulo()) && aLoop.getAutorNick().equals(aTmp.getAutorNick())){
                l.log("Fragment_Crear_Anuncios:existeAnuncio" + aTmp.getTitulo() + " " + aLoop.getTitulo() + " " +  aTmp.getAutorNick() + " " + aLoop.getAutorNick());
                return true;
            }
        }
        return false;
    }
}

