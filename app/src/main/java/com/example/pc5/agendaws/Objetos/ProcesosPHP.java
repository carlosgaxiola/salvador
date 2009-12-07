package com.example.pc5.agendaws.Objetos;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class ProcesosPHP implements Response.Listener<JSONObject>, Response.ErrorListener {

    private RequestQueue request;
    private JsonObjectRequest json;
    private ArrayList<Contactos> contactos = new ArrayList<>();
    private String serverip = "https://2016030023.000webhostapp.com/WebService/";

    public void setContext (Context context) {
        this.request = Volley.newRequestQueue(context);
    }

    public void insertarContacto (Contactos contacto) {
        String url = serverip + "weRegistro.php?nombre=" + contacto.getNombre() +
                "&telefono1=" + contacto.getTelefono1() +
                "&telefono2=" + contacto.getTelefono2() +
                "&direccion=" + contacto.getDireccion() +
                "&notas=" + contacto.getNotas() +
                "&favorite=" + contacto.getFavorite() +
                "&idMovil=" + contacto.getIdMovil();
        url = url.replace(" ", "%20");
        json = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(json);
    }

    public void actualizarContacto (Contactos contacto, int id) {
        String url = serverip = "wsActualizar.php?_ID=" + id +
                "&nombre=" + contacto.getNombre() +
                "&telefono1=" + contacto.getTelefono1() +
                "&telefono2=" + contacto.getTelefono2() +
                "&direccion=" + contacto.getDireccion() +
                "&favorite=" + contacto.getFavorite() +
                "&notas=" + contacto.getNotas();
        url = url.replace(" ", "%20");
        json = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(json);
    }

    public void borrarContacto (int id) {
        String url = serverip + "wsEliminar.php?_ID=" + id;
        json = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(json);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.i("ERROR", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {

    }
}
