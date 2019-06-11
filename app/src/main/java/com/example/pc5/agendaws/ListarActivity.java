package com.example.pc5.agendaws;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pc5.agendaws.Objetos.Contactos;
import com.example.pc5.agendaws.Objetos.Divice;
import com.example.pc5.agendaws.Objetos.ProcesosPHP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListarActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private Button btnNuevo;
    private final Context context = this;
    private ProcesosPHP php = new ProcesosPHP();
    private RequestQueue queue;
    private JsonObjectRequest request;
    private ArrayList<Contactos> contactos;
    private String server = "http://2016030023.000webhostapp.com/WebService/";
    private ListView listaContactos;
    private ContactosListViewAdapter adapter = null;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);
        try {
            queue = Volley.newRequestQueue(context);
            php.setContext(ListarActivity.this);
            listaContactos = findViewById(R.id.listaContactos);
            contactos = new ArrayList<>();
            consultarTodosWebService();
            btnNuevo = findViewById(R.id.btnNuevo);
            btnNuevo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ListarActivity.this, ContactoActivity.class);
                    finish();
                    startActivity(intent);
                }
            });
        }
        catch (Exception ex) {
            mensajeCorto("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void consultarTodosWebService () {
        String url = server + "wsConsultarTodos.php?idMovil=" + Divice.getSecureId(this);
        request = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        queue.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        mensajeCorto("Error al consultar los datos");
    }

    @Override
    public void onResponse(JSONObject response) {
        Contactos contacto = null;
        int code = 0;
        try {
            code = response.getInt("code");
        } catch (Exception ex) {
            mensajeCorto("Error: " + ex.getMessage());
        }
        if (code == 1) {
            JSONArray data = response.optJSONArray("contactos");
            try {
                contactos = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    contacto = new Contactos();
                    JSONObject json = data.getJSONObject(i);
                    contacto.set_ID(json.optInt("_ID"));
                    contacto.setNombre(json.optString("nombre"));
                    contacto.setDireccion(json.optString("direccion"));
                    contacto.setTelefono2(json.optString("telefono2"));
                    contacto.setTelefono1(json.optString("telefono1"));
                    contacto.setNotas(json.optString("notas"));
                    contacto.setFavorite(json.optInt("favorite"));
                    contacto.setIdMovil(json.optString("idMovil"));
                    contactos.add(contacto);
                }
                adapter = new ContactosListViewAdapter(ListarActivity.this, contactos);
                listaContactos.setAdapter(adapter);
            }
            catch (Exception ex){
                ex.printStackTrace();
                mensajeCorto("Error con la petición");
            }
        }
        else {
            mensajeCorto("No hay contactos");
        }
    }

    private void mensajeCorto (String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_action_activity, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mensajeCorto(s);
                if (TextUtils.isEmpty(s)) {
                  adapter.filter("");
                  listaContactos.clearTextFilter();
                }
                else {
                    adapter.filter(s);
                }
                return true;
            }
        });
        return true;
    }

    public void borrar (int id) {
        php.borrarContacto(id, this);
    }
}
