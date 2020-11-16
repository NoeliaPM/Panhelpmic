package com.example.panhelpmic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.example.panhelpmic.modelo.User;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class Actividad_Registro extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =0 ;
    private Button boton_profesional, boton_siguiente, boton_foto, boton_ubicacion;
    private CheckBox checkBox;
    private EditText et_user, et_contra, et_nombre, et_apellidos, et_direccion,  et_email, et_telefono;
    private boolean existe = true, contra_correcta = false, numero=false;
    private static final int GALLERY_INTENT = 1;
    private Bitmap profileImage = null;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private FireBaseConnector fbc = new FireBaseConnector();
    private AnunciosHandler ah;
    private double longitud, latitud;
    private Logger l = new Logger();
    private UsersHandler uh;
    private User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad__registro);
        l.log("Actividad_Registro:onCreate");
        boton_profesional = findViewById(R.id.button_profesiona);
        boton_siguiente = findViewById(R.id.button_siguiente);
        boton_foto = findViewById(R.id.button_foto_usuario);
        boton_ubicacion=findViewById(R.id.button_permitir_ubica);
        checkBox = findViewById(R.id.checkBox);
        et_user = findViewById(R.id.editText_usuarioC);
        et_contra = findViewById(R.id.editText_contraC);
        et_nombre = findViewById(R.id.editText_nombre);
        et_apellidos = findViewById(R.id.editText_apellidos);
        et_direccion = findViewById(R.id.editText_dirección);
        et_email = findViewById(R.id.editText_email);
        et_telefono = findViewById(R.id.editText_telefono);

        fbc.initFireBase(getApplicationContext());
        fbc.loadUsers();
        ah = (AnunciosHandler) getIntent().getExtras().get(Constantes.ANUNCIOS_HANDLER);
        uh = (UsersHandler) getIntent().getExtras().get(Constantes.USERS_HANDLER);

        //Onclick boton información
        boton_profesional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Registro:botonProfesional.setOnClickListener");
                AlertDialog.Builder builder = new AlertDialog.Builder(Actividad_Registro.this);
                LayoutInflater li = getLayoutInflater();
                View view = li.inflate(R.layout.alertdialog_profesionales, null);
                builder.setView(view);
                AlertDialog alertDialog = builder.create();
                builder.setNegativeButton(R.string.cerrar, null);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
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

        //OnClik botón subir foto
        boton_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Registro:botonFoto.setOnClickListener");
                requestRead();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        //OnClick botón ubicación
        boton_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Registro:botonUbicación.setOnClickListener");
                obtenerCoordenadas();
            }
        });

        //Onclick boton continuar
        boton_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.log("Actividad_Registro:botonContinuar.setOnClickListener");
                //Si el usuario ha dejado algún editText sin rellentar
                if (et_user.getText().toString().isEmpty() || et_contra.getText().toString().isEmpty() || et_nombre.getText().toString().isEmpty() || et_apellidos.getText().toString().isEmpty() || et_direccion.getText().toString().isEmpty() || et_email.getText().toString().isEmpty() || et_telefono.getText().toString().isEmpty()) {
                    l.log("Actividad_Registro:botonContinuar.setOnClickListener:not_valid_isEmpty");
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.tlcso, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    l.log("Actividad_Registro:botonContinuar.setOnClickListener:valid_isNotEmpty");
                    String nick = et_user.getText().toString();
                    String password = et_contra.getText().toString();
                    String nombre = et_nombre.getText().toString();
                    String apellidos = et_apellidos.getText().toString();
                    String direccion = et_direccion.getText().toString();
                    String email = et_email.getText().toString();
                    String telefonoStr = et_telefono.getText().toString();
                    int telefono=0;
                    try{
                        telefono = Integer.parseInt(telefonoStr);
                        numero=true;
                    }
                    catch (NumberFormatException nfe){
                        numero=false;
                    }
                    u = new User(nick, password, nombre, apellidos, direccion, email, telefono, false, "");
                    boolean existe = false;
                    existe = fbc.chkUser(u);

                    if(!validateSpecialChars(nick)){
                        l.log("Actividad_Registro:botonContinuar.validateSpecialChars");
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.nickError, Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        //Si el usuario ya existe en la bbdd
                        if (existe) {
                            l.log("Actividad_Registro:botonContinuar.setOnClickListener:not_valid:userExist");
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.enduye, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        //Si el usuario no existe ttodo ok.
                        else {
                            l.log("Actividad_Registro:botonContinuar.setOnClickListener:valid:userNotExist");
                            //Comprobamos que la contraseña cumpla los requisitos
                            int c = comprobarContra(password);
                            if (c == 1) {
                                l.log("Actividad_Registro:botonContinuar.setOnClickListener:not_valid:password1");
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.contra1, Toast.LENGTH_SHORT);
                                toast.show();
                            } else if (c == 2) {
                                l.log("Actividad_Registro:botonContinuar.setOnClickListener:not_valid:password2");
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.contra2, Toast.LENGTH_SHORT);
                                toast.show();
                            } else if (c == 3) {
                                l.log("Actividad_Registro:botonContinuar.setOnClickListener:not_valid:password3");
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.contra3, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            //Si todook con la contraseña
                            else {
                                l.log("Actividad_Registro:botonContinuar.setOnClickListener:valid:password");
                                //Si ha añadido un número de teléfono correcto
                                if(numero){
                                    l.log("Actividad_Registro:botonContinuar.setOnClickListener:valid:numberPhone");
                                    if (checkBox.isChecked()) {
                                        l.log("Actividad_Registro:botonContinuar.setOnClickListener:valid:esProfesional");
                                        u.setProfesional(true);
                                        if (profileImage != null) {
                                            u.setTieneImagen(true);
                                            fbc.uploadFile(u.getNick(), profileImage);
                                        }else{
                                            u.setTieneImagen(false);
                                        }
                                        fbc.addUser(u);
                                        Intent i = new Intent(v.getContext(), Actividad_Registro_Profesional.class);
                                        i.putExtra("usuarioConectado", u);
                                        i.putExtra(Constantes.ANUNCIOS_HANDLER, ah);
                                        fbc.loadUsersToHandler(uh);
                                        i.putExtra(Constantes.USERS_HANDLER, uh);
                                        startActivityForResult(i, 0);
                                        finish();
                                    }
                                    //Si no es profesional vamos a la actividad principal
                                    else {
                                        l.log("Actividad_Registro:botonContinuar.setOnClickListener:valid:noEsProfesional");
                                        u.setProfesional(false);
                                        if (profileImage != null) {
                                            fbc.uploadFile(u.getNick(), profileImage);
                                            u.setTieneImagen(true);
                                        }else{
                                            u.setTieneImagen(false);
                                        }
                                        fbc.addUser(u);
                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.ucc, Toast.LENGTH_SHORT);
                                        toast.show();
                                        Intent i = new Intent(v.getContext(), Actividad_Principal.class);
                                        i.putExtra(Constantes.ANUNCIOS_HANDLER, ah);
                                        fbc.loadUsersToHandler(uh);
                                        i.putExtra(Constantes.USERS_HANDLER, uh);
                                        i.putExtra("usuarioConectado", u);
                                        startActivityForResult(i, 0);
                                        finish();
                                    }
                                }
                                //Si el número de teléfono no es númerico
                                else{
                                    l.log("Actividad_Registro:botonContinuar.setOnClickListener:not_valid:numberPhone");
                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.endtdsr, Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }
                    }
                    }


            }
        });
    }
    //Obtenemos la foto que hemos seleccionado
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        l.log("Actividad_Registro:ActivityResult");
        System.out.println("Traza 1 RESULT");
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            System.out.println("onActivityResult RESULT_OK");
            final Uri imageUri = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            profileImage = BitmapFactory.decodeStream(imageStream);
            Toast toast = Toast.makeText(getApplicationContext(), R.string.isc, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public int comprobarContra(String contra) {
        l.log("Actividad_Registro:comprobarContra");
        int i = 0;
        int num = 0;
        int mayus = 0;
        int salida = 0;
        //Recorremos la contraseña para contar mayusculas y numeros
        for (int j = 0; j < contra.length(); j++) {
            if ((contra.charAt(j) > 47 && contra.charAt(j) < 58)) {
                num++;
            }
            if ((contra.charAt(j) > 64 && contra.charAt(j) < 91)) {
                mayus++;
            }
        }
        //Si no tiene números
        if (num < 1) {
            salida = 1;
        }
        //Si no tiene mayúscula
        if (mayus < 1) {
            salida = 2;
        }
        //Si no tiene 7 caracteres
        if (contra.length() < 7) {
            salida = 3;
        }
        return salida;
    }
    //Solicita permisos de Lectura
    public void requestRead() {
        l.log("Actividad_Registro:requestRead");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        l.log("Actividad_Registro:onRequestPermissionsResult");
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //readFile();
            } else {
                // Permission Denied
                Toast.makeText(getApplicationContext(), R.string.pd, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void obtenerCoordenadas() {
        l.log("Actividad_Registro:obtenerCoordenadas");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        else {
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            u.setLatitud(latitud);
            u.setLongitud(longitud);
            Toast.makeText(getApplicationContext(), latitud+longitud+"", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateSpecialChars(String nick){
        if(nick.contains(".") || nick.contains("#") || nick.contains("$") || nick.contains("[") || nick.contains("]")){
            return false;
        }else{
            return true;
        }
    }
}
