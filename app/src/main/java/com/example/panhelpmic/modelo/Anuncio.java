package com.example.panhelpmic.modelo;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Date;

public class Anuncio implements Serializable {

    private String autorNick;
    private String Titulo = "";
    private String tipo = "";
    private String descripcion = "";
    private String fecha;
    private String hora;
    private String direccion;
    private Drawable imagen;
    private boolean tieneImagen;

    public Anuncio() {
    }

    public boolean isTieneImagen() {
        return tieneImagen;
    }

    public void setTieneImagen(boolean tieneImagen) {
        this.tieneImagen = tieneImagen;
    }

    public String getAutorNick() {
        return autorNick;
    }

    public void setAutorNick(String autorNick) {
        this.autorNick = autorNick;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String contenido) {
        this.descripcion = contenido;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Drawable getImagen() { return imagen; }
    public void setImagen(Drawable imagen) { this.imagen = imagen; }

    @Override
    public String toString() {
        return "Anuncio{" +
                "autorNick='" + autorNick + '\'' +
                ", Titulo='" + Titulo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", direccion='" + direccion + '\'' +
                ", imagen=" + imagen +
                ", tieneImagen=" + tieneImagen +
                '}';
    }
}
