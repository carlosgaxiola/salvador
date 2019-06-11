package com.example.pc5.agendaws.Objetos;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pc5.agendaws.ListarActivity;
import com.example.pc5.agendaws.ContactoActivity;

import java.util.ArrayList;

public class ProcesosPHP {

    private RequestQueue request;
    private JsonObjectRequest json;
    private ArrayList<Contactos> contactos = new ArrayList<>();
    private String serverip = "http://2016030023.000webhostapp.com/WebService/";
    private Contactos contactoHolder;

    public void setContext (Context context) {
        this.request = Volley.newRequestQueue(context);
    }

    public void insertarContacto (Contactos contacto, final ContactoActivity context) {
        String url = serverip + "wsRegistro.php?nombre=" + contacto.getNombre() +
                "&telefono1=" + contacto.getTelefono1() +
                "&telefono2=" + contacto.getTelefono2() +
                "&direccion=" + contacto.getDireccion() +
                "&notas=" + contacto.getNotas() +
                "&favorite=" + contacto.getFavorite() +
                "&idMovil=" + contacto.getIdMovil();
        url = url.replace(" ", "%20");
        this.contactoHolder = contacto;
        json = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject data) {
                    try {
                        int code = data.getInt("code");
                        if (code == 1) {
                            mensajeCorto("Contacto agregado!", context);
                            int _ID = 0;
                            try {
                                _ID = data.getInt("_ID");
                            } catch (Exception ex) {
                                JSONArray contactos = data.getJSONArray("contactos");
                                JSONObject con = contactos.getJSONObject(0);
                                _ID = con.getInt("_ID");
                            }
                            contactoHolder.set_ID(_ID);
                            context.exito(contactoHolder);
                        }
                        else {
                            mensajeCorto("No se pudo agregar el contacto", context);
                            String mySQLError = data.getString("mysql_error");
                            if (mySQLError != null && !mySQLError.equals("")) {
                                mensajeCorto("MySQL Error: " + data.getString("mysql_error"), context);
                            }
                        }
                    }
                    catch (Exception ex) {
                        mensajeCorto("Error: " +  ex.getMessage(), context);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mensajeCorto("Ocurrio un error: " + volleyError.toString(), context);
                }
            });
        request.add(json);
    }

    public void actualizarContacto (Contactos contacto, int id, final ContactoActivity context) {
        String url = serverip + "wsActualizar.php?_ID=" + id +
                "&nombre=" + contacto.getNombre() +
                "&telefono1=" + contacto.getTelefono1() +
                "&telefono2=" + contacto.getTelefono2() +
                "&direccion=" + contacto.getDireccion() +
                "&favorite=" + contacto.getFavorite() +
                "&notas=" + contacto.getNotas();
        url = url.replace(" ", "%20");
        this.contactoHolder = contacto;
        json = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject data) {
                    try {
                        int code = data.getInt("code");
                        switch (code) {
                            case -1:
                                mensajeCorto("Error al actualizar", context);
                                String mySQLError = data.getString("mysql_error");
                                if (mySQLError != null && !mySQLError.equals("")) {
                                    mensajeCorto("MySQL Error: " + data.getString("mysql_error"), context);
                                }
                                break;
                            case 0:
                            case 1:
                                mensajeCorto("Contacto actualizado", context);
                                context.exito(contactoHolder);
                                break;
                        }
                    }
                    catch (Exception ex) {
                        mensajeCorto("Error: " +  ex.getMessage(), context);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mensajeCorto("Ocurrio un error: " + volleyError.toString(), context);
                }
            });
        request.add(json);
    }

    public void borrarContacto (int id, final ListarActivity context) {
        String url = serverip + "wsEliminar.php?_ID=" + id;
        json = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject data) {
                    try {
                        int code = data.getInt("code");
                        switch (code) {
                            case 0:
                                mensajeCorto("No fue posible borrar el contacto", context);
                                String mySQLError = data.getString("mysql_error");
                                if (mySQLError != null && !mySQLError.equals("")) {
                                    mensajeCorto("MySQL Error: " + data.getString("mysql_error"), context);
                                }
                                break;
                            case 1:
                                mensajeCorto("Contacto borrado", context);
                                context.consultarTodosWebService();
                                break;
                        }
                    }
                    catch (Exception ex) {
                        mensajeCorto("Error: " +  ex.getMessage(), context);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mensajeCorto("Ocurrio un error: " + volleyError.toString(), context);
                }
            });
        request.add(json);
    }

    private void mensajeCorto (String mensaje, Context context) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }
}
