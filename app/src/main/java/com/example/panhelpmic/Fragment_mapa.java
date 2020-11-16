package com.example.panhelpmic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.panhelpmic.modelo.Anuncio;
import com.example.panhelpmic.modelo.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_mapa extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =0 ;
    private View item;
    private GoogleMap migoogleMap;
    private MapView mapView;
    private Button boton_tipo;
    private boolean satelite;
    private double lng, lat;
    private FireBaseConnector fbc;
    private Logger l = new Logger();
    private ArrayList<Anuncio> arTmp;
    private DatabaseReference FBDB;
    private UsersHandler uh;
    private User usuarioConectado;
    public Fragment_mapa() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        item= inflater.inflate(R.layout.fragment_mapa, container, false);
        Actividad_Principal activity = (Actividad_Principal) getActivity();
        activity.getSupportActionBar().setTitle(R.string.fm);
        fbc = activity.getFbc();
        fbc = activity.getFbc();
        //arTmp = fbc.getAnunciosByQuery("Basico");
        arTmp = new ArrayList<Anuncio>();
        uh = activity.getUsersHandler();
        FBDB = FirebaseDatabase.getInstance().getReference();
        usuarioConectado = activity.getUsuarioConectado();
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
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                l.log("FireBaseConnector:getAnunciosByQuery:onCancelled" + databaseError.toString());
            }
        });
        return item;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Enlazamos con la parte gráfica
        mapView = item.findViewById(R.id.mapView);
        boton_tipo = item.findViewById(R.id.button_tipoMapa);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        satelite = false;
    }

    public void onMapReady(final GoogleMap googleMap) {
        //Iniciamos el mapa
        MapsInitializer.initialize(getContext());
        migoogleMap = googleMap;

        //Añadimos el botón que nos centra a la ubicación del dispositivo
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        else {
            migoogleMap.setMyLocationEnabled(true);
        }

        //Establecemos el tipo de mapa por defecto
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //OnClick boton tipo mapa
        boton_tipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si está en modo satelite
                if (satelite) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    boton_tipo.setText(R.string.satelite);
                    satelite = false;
                }
                //Si está en modo normal
                else {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    boton_tipo.setText(R.string.normal);
                    satelite = true;
                }
            }
        });

        l.log("onMapReady: put mark");
        l.log("onMapReady: put mark: AUTOR (0):  "+ arTmp.get(0).getAutorNick());


        for(Anuncio a :arTmp){
            User uTmp = uh.getUser(a.getAutorNick());
            if(uTmp != null){
                if(uTmp.getLatitud() != 0 && uTmp.getLongitud() != 0) {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(uTmp.getLatitud(), uTmp.getLongitud())).title(a.getTitulo()).snippet(a.getDescripcion().substring(0, 10) + "...").icon(BitmapDescriptorFactory.fromResource(R.drawable.ubica_peque)));
                }
            }
        }
        //Establecemos la posición de la cámara
        CameraPosition cam = CameraPosition.builder().target(new LatLng(40.4146500, -3.7004000)).zoom(14).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam));

        migoogleMap.setOnInfoWindowClickListener(this);
    }

    //Eventos con los markers
    public void onInfoWindowClick(Marker marker) {
        String titulo=marker.getTitle();
        l.log("Fragment_mapa:onInfoWindowClick: "+titulo);
        for(Anuncio a :arTmp){
            if(arTmp != null){
                if(a.getTitulo().equals(titulo) ){
                    l.log("Fragment_mapa:onInfoWindowClick: "+a);
                    Intent i=new Intent(getContext(),Actividad_detalle_anuncio.class);
                    i.putExtra(Constantes.ANUNCIO, a);
                    i.putExtra(Constantes.USUARIO_CONECTADO, usuarioConectado);
                    i.putExtra(Constantes.USERS_HANDLER, uh);
                    startActivityForResult(i,0);
                }
            }
        }
    }
}
