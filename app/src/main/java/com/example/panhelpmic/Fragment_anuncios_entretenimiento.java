package com.example.panhelpmic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_anuncios_entretenimiento extends Fragment {

    private ListView listaEntretenimiento;
    private TextView titulo, direccion, fecha, hora;
    private ImageView imagen;
    private Logger l = new Logger();
    private FireBaseConnector fbc;
    private User usuarioConectado;
    private MCalendarView calendario;
    private TextView seleccion;
    private View item;
    private DatabaseReference FBDB;
    private ArrayList<Anuncio> arTmp;
    private ArrayList<Anuncio> anunciostmp;
    private UsersHandler uh;

    public Fragment_anuncios_entretenimiento() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        l.log("Fragment_anuncios_entretenimiento:onCreateView");
        item = inflater.inflate(R.layout.fragment_anuncios_entretenimiento, container, false);
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        activity.getSupportActionBar().setTitle(R.string.fae);
        seleccion = item.findViewById(R.id.textView_SeleccionCalendario);
        calendario = (MCalendarView) item.findViewById(R.id.calendarView);
        //calendario.markDate(2020, 8, 1);
        uh = activity.getUsersHandler();
        /*
        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                seleccion.setText(dayOfMonth+"/"+ (month + 1) +"/"+year);
                l.log("Fragment_anuncios_entretenimiento:onCreateView:calendario.setOnDateChangeListener");
                changeDay();

            }
        });

         */



        arTmp = new ArrayList<Anuncio>();
        FBDB = FirebaseDatabase.getInstance().getReference();
        Query query = FBDB.child("Anuncios").orderByChild("tipo").equalTo("Entretenimiento");
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
                    try {
                        updateUI();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                l.log("FireBaseConnector:getAnunciosByQuery:onCancelled" + databaseError.toString());
            }
        });
        calendario.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                //Toast.makeText(getApplicationContext(), "Today is : " + date.getDayString(), Toast.LENGTH_LONG).show();
                seleccion.setText(date.getDay()+"/"+ (date.getMonth()) +"/"+date.getYear());
                l.log("Fragment_anuncios_entretenimiento:onCreateView:calendario.setOnDateChangeListener");
                changeDay();
            }
        });
        return item;
    }
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        l.log("Fragment_anuncios_entretenimiento:onActivityCreated");
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        usuarioConectado = activity.getUsuarioConectado();
        fbc = activity.getFbc();
        listaEntretenimiento = getView().findViewById(R.id.listView_entretenimiento);

    }

    class AdaptadorEntretenimiento extends ArrayAdapter<Anuncio> {
        Activity context;
        //Constructor
        public AdaptadorEntretenimiento(Fragment context){
            super(context.getActivity(), R.layout.listitem_entretenimientos, anunciostmp);
            l.log("Fragment_anuncios_entretenimiento:AdaptadorEntretenimiento:AdaptadorEntretenimiento");
            this.context = context.getActivity();
        }

        //Rellenamos los datos de la lsta
        public View getView(int position, View convertView, ViewGroup parent){
            l.log("Fragment_anuncios_entretenimiento:AdaptadorEntretenimiento:getView");
            LayoutInflater inflater=context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitem_entretenimientos,null);

            //Enlazamos con la parte gráfica del listitem
            titulo = item.findViewById(R.id.titulo);
            direccion = item.findViewById(R.id.direccion);
            fecha = item.findViewById(R.id.fecha);
            hora = item.findViewById(R.id.hora);
            imagen = item.findViewById(R.id.imagen);
            l.log("Seleccion" + seleccion + "anuncio" + anunciostmp.get(position).getFecha());


            //Establecemos los valores
            titulo.setText(anunciostmp.get(position).getTitulo());
            direccion.setText(anunciostmp.get(position).getDireccion());
            fecha.setText(anunciostmp.get(position).getFecha());
            hora.setText(anunciostmp.get(position).getHora());

            if(anunciostmp.get(position).isTieneImagen()){
                StorageReference storageReference = fbc.getmStorageRef().child("images/anuncios/" + anunciostmp.get(position).getAutorNick() + "-" + anunciostmp.get(position).getTitulo() + ".jpg");
                ImageView imageView = item.findViewById(R.id.imagen);
                Glide.with(item.getContext())
                        .load(storageReference)
                        .into(imageView);
            }else{
                StorageReference storageReference = fbc.getmStorageRef().child("images/anuncios/" + "imagen_por_defecto" + ".jpg");
                ImageView imageView = item.findViewById(R.id.imagen);
                Glide.with(item.getContext())
                        .load(storageReference)
                        .into(imageView);
            }

            return item;
        }

    }

    public void changeDay(){
        l.log("Fragment_anuncios_entretenimiento:changeDay");
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        fbc = activity.getFbc();
        usuarioConectado = activity.getUsuarioConectado();

        anunciostmp = new ArrayList<Anuncio>();
        for(Anuncio a: arTmp){
            if("Entretenimiento".equals(a.getTipo())){
                l.log("Fragment_anuncios_entretenimiento:changeDay:getFecha " + a.getFecha());
                l.log("Fragment_anuncios_entretenimiento:changeDay:seleccion " + seleccion.getText());
                if(a.getFecha().equals(seleccion.getText())) {
                    anunciostmp.add(a);
                }
            }
        }


        l.log("Fragment_anuncios_entretenimiento:onActivityCreated:listaBasicos.setOnItemClickListener:onItemClick");
        listaEntretenimiento.setAdapter(new AdaptadorEntretenimiento(this));
        listaEntretenimiento.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                l.log("Fragment_anuncios_entretenimiento:onActivityCreated:listaBasicos.setOnItemClickListener:onItemClick");
                Anuncio a = (Anuncio) listaEntretenimiento.getAdapter().getItem(position);
                Intent i=new Intent(getContext(),Actividad_detalle_anuncio.class);
                i.putExtra(Constantes.ANUNCIO, a);
                i.putExtra(Constantes.USUARIO_CONECTADO, usuarioConectado);
                i.putExtra(Constantes.USERS_HANDLER, uh);
                startActivityForResult(i,0);

            }
        });
    }

    void updateUI() throws ParseException {
        l.log("Fragment_anuncios_entretenimiento:updateUI");
        for(Anuncio a: arTmp){
            l.log("Fragment_anuncios_entretenimiento:updateUI: " + a.getFecha());
            StringTokenizer st = new StringTokenizer(a.getFecha(),"/");
            ArrayList<String> tmp = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                tmp.add(st.nextToken());
            }
            l.log("Fragment_anuncios_entretenimiento:updateUI" + tmp.toString());
            calendario.markDate(Integer.parseInt(tmp.get(2)), Integer.parseInt(tmp.get(1)), Integer.parseInt(tmp.get(0)));

        }
    }

}
