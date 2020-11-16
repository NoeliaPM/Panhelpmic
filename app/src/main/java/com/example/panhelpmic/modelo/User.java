package com.example.panhelpmic.modelo;


import java.io.Serializable;

public class User implements Serializable {
    private String nick = "";
    private String password = "";
    private String nombre = "";
    private String apellidos = "";
    private String direccion = "";
    private String email = "";
    private int telefono = 0;
    private boolean profesional = false;
    private String tipoProfesional = "";
    private String especialidad = "";
    private double longitud=0;
    private double latitud=0;
    private boolean tieneImagen = false;

    public User(){

    }

    public User(String nick, String password) {
        this.nick = nick;
        this.password = password;
    }

    public User(String nick, String password, String nombre, String apellidos, String direccion, String email, int telefono, boolean profesional, String tipoProfesional, String especialidad, double longitud, double latitud, boolean tieneImagen) {
        this.nick = nick;
        this.password = password;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
        this.profesional = profesional;
        this.tipoProfesional = tipoProfesional;
        this.especialidad = especialidad;
        this.longitud = longitud;
        this.latitud = latitud;
        this.tieneImagen = tieneImagen;
    }

    public User(String nick, String password, String nombre, String apellidos, String direccion, String email, int telefono, boolean b, String s) {
        this.nick = nick;
        this.password = password;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
        this.tipoProfesional = tipoProfesional;
        this.especialidad = especialidad;
    }


    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public boolean isProfesional() {
        return profesional;
    }

    public void setProfesional(boolean profesional) {
        this.profesional = profesional;
    }

    public String getTipoProfesional() {
        return tipoProfesional;
    }

    public void setTipoProfesional(String tipoProfesional) {
        this.tipoProfesional = tipoProfesional;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public boolean isTieneImagen() {
        return tieneImagen;
    }

    public void setTieneImagen(boolean tieneImagen) {
        this.tieneImagen = tieneImagen;
    }

    @Override
    public String toString() {
        return "User{" +
                "nick='" + nick + '\'' +
                ", password='" + password + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", direccion='" + direccion + '\'' +
                ", email='" + email + '\'' +
                ", telefono=" + telefono +
                ", profesional=" + profesional +
                ", tipoProfesional='" + tipoProfesional + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", longitud=" + longitud +
                ", latitud=" + latitud +
                ", tieneImagen=" + tieneImagen +
                '}';
    }
}
