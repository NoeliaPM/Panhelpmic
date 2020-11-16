package com.example.panhelpmic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.panhelpmic.modelo.Anuncio;
import com.example.panhelpmic.modelo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_anuncios_profesionales extends Fragment {

    private ListView listaProfesionales;
    private TextView titulo, direccion;
    private ArrayList<Anuncio> profesionales;
    private ArrayList<Anuncio> anuncios;
    private Logger l = new Logger();
    private FireBaseConnector fbc;
    private AnunciosHandler ah;
    private User usuarioConectado;
    private DatabaseReference FBDB;
    private UsersHandler uh;


    public Fragment_anuncios_profesionales() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        l.log("Fragment_anuncios_profesionales:onCreateView");
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        activity.getSupportActionBar().setTitle(R.string.fap);
        profesionales = new ArrayList<Anuncio>();
        FBDB = FirebaseDatabase.getInstance().getReference();
        uh = activity.getUsersHandler();
        Query query = FBDB.child("Anuncios").orderByChild("tipo").equalTo("Profesional");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    l.log("FireBaseConnector:getAnunciosByQuery:onDataChange");
                    for (DataSnapshot objS : dataSnapshot.getChildren()) {
                        Anuncio a = objS.getValue(Anuncio.class);
                        l.log("FireBaseConnector:getAnunciosByQuery:onDataChange:FOR " + a.toString());
                        profesionales.add(a);
                    }
                    updateUi();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                l.log("FireBaseConnector:getAnunciosByQuery:onCancelled" + databaseError.toString());
            }
        });

        return inflater.inflate(R.layout.fragment_anuncios_profesionales, container, false);
    }

    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        l.log("Fragment_anuncios_profesionales:onActivityCreated");
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        //anuncios = activity.getAnuncios();
        fbc = activity.getFbc();
        ah = activity.getAnunciosHandler();
        usuarioConectado = activity.getUsuarioConectado();
        listaProfesionales=getView().findViewById(R.id.listProfesionales);

    }
    void updateUi(){
        listaProfesionales.setAdapter(new Fragment_anuncios_profesionales.AdaptadorProfesionales(this));
        listaProfesionales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                l.log("Fragment_anuncios_basicos:onActivityCreated:listaBasicos.setOnItemClickListener:onItemClick");
                Anuncio a = (Anuncio) listaProfesionales.getAdapter().getItem(position);
                Intent i=new Intent(getContext(),Actividad_detalle_anuncio.class);
                i.putExtra(Constantes.ANUNCIO, a);
                i.putExtra(Constantes.USUARIO_CONECTADO, usuarioConectado);
                i.putExtra(Constantes.USERS_HANDLER, uh);
                startActivityForResult(i,0);
            }
        });
    }


    class AdaptadorProfesionales extends ArrayAdapter<Anuncio> {
        Activity context;

        public AdaptadorProfesionales(Fragment context){
            super(context.getActivity(),R.layout.listitem_profesionales, profesionales);
            l.log("Fragment_anuncios_profesionales:AdaptadorProfesionales:AdaptadorProfesionales");
            this.context=context.getActivity();
        }

        public View getView(int position, View convertView, ViewGroup parent){
            l.log("Fragment_anuncios_profesionales:AdaptadorProfesionales:getView");
            LayoutInflater inflater=context.getLayoutInflater();
            View item=inflater.inflate(R.layout.listitem_profesionales,null);

            titulo = item.findViewById(R.id.titulo);
            direccion = item.findViewById(R.id.direccion);

            titulo.setText(profesionales.get(position).getTitulo());
            direccion.setText(profesionales.get(position).getDireccion());

            if(profesionales.get(position).isTieneImagen()){
                StorageReference storageReference = fbc.getmStorageRef().child("images/anuncios/" + profesionales.get(position).getAutorNick() + "-" + profesionales.get(position).getTitulo() + ".jpg");
                ImageView imageView = item.findViewById(R.id.imagen);
                Glide.with(item.getContext() /* context */)
                        .load(storageReference)
                        .into(imageView);
            }else{
                StorageReference storageReference = fbc.getmStorageRef().child("images/anuncios/" + "imagen_por_defecto" + ".jpg");
                ImageView imageView = item.findViewById(R.id.imagen);
                Glide.with(item.getContext() /* context */)
                        .load(storageReference)
                        .into(imageView);
            }

            return item;
        }
    }
}
