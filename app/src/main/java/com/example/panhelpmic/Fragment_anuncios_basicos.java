package com.example.panhelpmic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class Fragment_anuncios_basicos extends Fragment {
    private ListView listaBasicos;
    private TextView tv_li_titulo, tv_li_direccion;
    private static Anuncio[] basicos;
    private ArrayList<Anuncio> anuncios;
    private Logger l = new Logger();
    private FireBaseConnector fbc;
    private AnunciosHandler ah;
    private User usuarioConectado;
    private ArrayList<Anuncio> arTmp;
    private DatabaseReference FBDB;
    private UsersHandler uh;

    public Fragment_anuncios_basicos() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        l.log("Fragment_anuncios_basicos:onCreateView");
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        fbc = activity.getFbc();
        //arTmp = fbc.getAnunciosByQuery("Basico");
        arTmp = new ArrayList<Anuncio>();
        activity.getSupportActionBar().setTitle(R.string.fab);
        uh = activity.getUsersHandler();
        FBDB = FirebaseDatabase.getInstance().getReference();
        Query query = FBDB.child("Anuncios").orderByChild("tipo").equalTo("Basico");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    l.log("FireBaseConnector:getAnunciosByQuery:onDataChange");
                    for (DataSnapshot objS : dataSnapshot.getChildren()) {
                        Anuncio a = objS.getValue(Anuncio.class);
                        l.log("FireBaseConnector:getAnunciosByQuery:onDataChange:FOR " + a.toString());
                        arTmp.add(a);
                    }
                    updateUI();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                l.log("FireBaseConnector:getAnunciosByQuery:onCancelled" + databaseError.toString());
            }
        });
        return inflater.inflate(R.layout.fragment_anuncios_basicos, container, false);
    }
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        l.log("Fragment_anuncios_basicos:onActivityCreated");
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        usuarioConectado = activity.getUsuarioConectado();
        listaBasicos=getView().findViewById(R.id.listview_basicos);

    }
    void updateUI(){
        listaBasicos.setAdapter(new AdaptadorBasicos(this));
        listaBasicos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                l.log("Fragment_anuncios_basicos:onActivityCreated:listaBasicos.setOnItemClickListener:onItemClick");
                Anuncio a = (Anuncio) listaBasicos.getAdapter().getItem(position);

                Intent i=new Intent(getContext(),Actividad_detalle_anuncio.class);
                i.putExtra(Constantes.ANUNCIO, a);
                i.putExtra(Constantes.USUARIO_CONECTADO, usuarioConectado);
                i.putExtra(Constantes.USERS_HANDLER, uh);
                startActivityForResult(i,0);

            }
        });
    }

    class AdaptadorBasicos extends ArrayAdapter<Anuncio>{
        Activity context;
        public AdaptadorBasicos(Fragment context){
            super(context.getActivity(),R.layout.listitem_basicos, arTmp);
            l.log("Fragment_anuncios_basicos:AdaptadorBasicos:AdaptadorBasicos");
            this.context=context.getActivity();
        }
        //Rellenamos los datos de la lsta
        public View getView(int position, View convertView, ViewGroup parent){
            l.log("Fragment_anuncios_basicos:AdaptadorBasicos:getView");
            LayoutInflater inflater=context.getLayoutInflater();
            View item=inflater.inflate(R.layout.listitem_basicos,null);
            tv_li_titulo=item.findViewById(R.id.TextView_item_titulo_basicos);
            tv_li_direccion=item.findViewById(R.id.TextView_item_direccion_basicos);
            tv_li_titulo.setText(arTmp.get(position).getTitulo());

            tv_li_direccion.setText(arTmp.get(position).getDireccion());

            l.log("Fragment_anuncios_basicos:Fragment_anuncios_basicos:getView:a" + arTmp.get(position).toString());
            if(arTmp.get(position).isTieneImagen()){
                l.log("Fragment_anuncios_basicos:Fragment_anuncios_basicos:getView:a.isTieneImagen():IF");
                StorageReference storageReference = fbc.getmStorageRef().child("images/anuncios/" + arTmp.get(position).getAutorNick() + "-" + arTmp.get(position).getTitulo() + ".jpg");
                ImageView imageView = item.findViewById(R.id.imagen);
                Glide.with(item.getContext() /* context */)
                        .load(storageReference)
                        .apply(new RequestOptions().override(600, 600))
                        .into(imageView);
            }else{
                l.log("Fragment_anuncios_basicos:Fragment_anuncios_basicos:getView:a.isTieneImagen():ELSE");
                StorageReference storageReference = fbc.getmStorageRef().child("images/anuncios/" + "imagen_por_defecto" + ".jpg");
                ImageView imageView = item.findViewById(R.id.imagen);
                Glide.with(item.getContext() /* context */)
                        .load(storageReference)
                        .apply(new RequestOptions().override(600, 600))
                        .into(imageView);
            }
            return item;
        }
    }
}


