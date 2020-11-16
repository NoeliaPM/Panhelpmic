package com.example.panhelpmic;

import android.graphics.Bitmap;

import com.example.panhelpmic.modelo.Anuncio;

import java.io.Serializable;
import java.util.ArrayList;

public class AnunciosHandler implements Serializable {

    private ArrayList<Anuncio> anuncios = new ArrayList<Anuncio>();


    public AnunciosHandler(){

    }

    public AnunciosHandler(ArrayList<Anuncio> anuncios) {
        this.anuncios = anuncios;
    }

    public ArrayList<Anuncio> getAnuncios() {
        return anuncios;
    }

    public void setAnuncios(ArrayList<Anuncio> anuncios) {
        this.anuncios = anuncios;
    }




}
