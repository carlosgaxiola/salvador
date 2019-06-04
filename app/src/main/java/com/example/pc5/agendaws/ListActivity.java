package com.example.pc5.agendaws;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import java.util.List;

public class ListActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private Button btnNuevo;
    private final Context context = this;
    private ProcesosPHP php = new ProcesosPHP();
    private RequestQueue queue;
    private JsonObjectRequest request;
    private ArrayList<Contactos> contactos;
    private String server = "https://2016030023.000webhostapp.com/WebService/";
    private TableLayout listaContactos;
    private View helpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        contactos = new ArrayList<>();
        queue = Volley.newRequestQueue(context);
        consultarTodosWebService();
        listaContactos = findViewById(R.id.listContactos);
        btnNuevo = findViewById(R.id.btnNuevo);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    public void consultarTodosWebService () {
        String url = server + "wsConsultarTodos.php?idMovil=" + Divice.getSecureId(this);
        request = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        queue.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {
        Contactos contacto = null;
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
            cargarContactos();
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensajeCorto("Error con la petición");
        }
    }

    private void mensajeCorto (String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void cargarContactos () {
        try {
            listaContactos.removeAllViews();
            for (Contactos contacto : contactos) {
                TableRow nRow = new TableRow(ListActivity.this);
                nRow.setOrientation(TableRow.VERTICAL);

                LinearLayout fila1 = new LinearLayout(this);
                fila1.setOrientation(LinearLayout.HORIZONTAL);

                TextView lblNombre = new TextView(this);
                lblNombre.setText(contacto.getNombre());
                lblNombre.setTextColor( contacto.getFavorite() == 1 ? Color.BLUE : Color.BLACK);

                Button btnModificar = new Button(this);
                btnModificar.setText("Modificar");
                btnModificar.setTag(R.string.pos, contactos.indexOf(contacto));
                btnModificar.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle datos = new Bundle();
                        Contactos con = contactos.get((int)v.getTag(R.string.pos));
                        datos.putSerializable("contacto", con);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtras(datos);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
                fila1.addView(lblNombre);
                fila1.addView(btnModificar);

                LinearLayout fila2 = new LinearLayout(this);
                fila2.setOrientation(LinearLayout.HORIZONTAL);

                TextView lblTel1 = new TextView(this);
                lblTel1.setText(contacto.getTelefono1());
                lblTel1.setTextColor( contacto.getFavorite() == 1 ? Color.BLUE : Color.BLACK);

                Button btnDel = new Button(this);
                btnDel.setText("Borrar");
                btnDel.setTag(R.string.pos, contactos.indexOf(contacto));
                this.helpView = btnDel;
                btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ListActivity.this);
                            dialog.setTitle("Borrar contacto");
                            dialog.setMessage("¿Borra el contacto?");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int pos = (int) helpView.getTag(R.string.pos);
                                    int id = contactos.get(pos).get_ID();
                                    php.borrarContacto(id, ListActivity.this);
                                    cargarContactos();
                                }
                            });
                            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dialog.show();
                        }
                        catch (Exception ex) {
                            mensajeCorto("Error: " + ex.getMessage());
                        }
                    }
                });

                fila2.addView(lblTel1);
                fila2.addView(btnDel);

                LinearLayout filaPadre = new LinearLayout(this);
                filaPadre.setOrientation(LinearLayout.VERTICAL);

                filaPadre.addView(fila1);
                filaPadre.addView(fila2);

                nRow.addView(filaPadre);
                listaContactos.addView(nRow);
            }
        }
        catch (Exception ex) {
            mensajeCorto("Error: " + ex.getMessage());
        }
    }
}
