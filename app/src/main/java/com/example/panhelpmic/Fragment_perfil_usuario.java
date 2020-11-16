package com.example.panhelpmic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.panhelpmic.modelo.User;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_perfil_usuario extends Fragment {


    private User usuarioConectado;
    private Logger l = new Logger();
    private TextView nombre,nick, direccion, correo,telefono;
    private View view;
    private ImageView imageView;
    private FireBaseConnector fbc;

    public Fragment_perfil_usuario() {
        // Required empty public constructor
        l.log("Fragment_perfil_usuario:Fragment_perfil_usuario");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        l.log("Fragment_perfil_usuario:onCreateView");
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        usuarioConectado = activity.getUsuarioConectado();
        fbc = activity.getFbc();
        l.log("Fragment_perfil_usuario:onCreateView" + " usuarioConectado: " + usuarioConectado.toString());

        activity.getSupportActionBar().setTitle(R.string.fpu);
        view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        nombre = view.findViewById(R.id.textView_NombreU);
        nick = view.findViewById(R.id.textView_NickU);
        direccion = view.findViewById(R.id.textView_DireccionU);
        correo = view.findViewById(R.id.textView_CorreoU);
        telefono = view.findViewById(R.id.textView_TelefonoU);

        nombre.setText(usuarioConectado.getNombre());
        nick.setText(usuarioConectado.getNick());
        direccion.setText(usuarioConectado.getDireccion());
        correo.setText(usuarioConectado.getEmail());
        telefono.setText("" + String.valueOf(usuarioConectado.getTelefono()));

        //imageView  = view.findViewById(R.id.imageView_perfilU);
        //imageView.setImageBitmap(activity.getProfileImage());
        imageView  = view.findViewById(R.id.imageView_perfilU);

        if(usuarioConectado.isTieneImagen()){
            l.log("Fragment_perfil_usuario:onCreateView" + " usuarioConectado: TRY" );
            StorageReference storageReference = fbc.getmStorageRef().child("images/profiles/" + usuarioConectado.getNick() + ".jpg");
            Glide.with(getContext() /* context */)
                    .load(storageReference)
                    .apply(new RequestOptions().override(1000, 1000))
                    .into(imageView);
        }else{
            l.log("Fragment_perfil_usuario:onCreateView" + " usuarioConectado: Catch" );
            StorageReference storageReference = fbc.getmStorageRef().child("images/profiles/" + "imagen_defecto" + ".png");
            Glide.with(getContext())
                    .load(storageReference)
                    .apply(new RequestOptions().override(1000, 1000))
                    .into(imageView);
        }
        return view;
    }




}
