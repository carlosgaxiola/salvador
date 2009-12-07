package com.example.pc5.agendaws;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.pc5.agendaws.Objetos.Contactos;
import com.example.pc5.agendaws.Objetos.Divice;
import com.example.pc5.agendaws.Objetos.ProcesosPHP;

public class MainActivity extends AppCompatActivity {

    private Button btnGuardar, btnLimpiar, btnListar;
    private TextView txtNombre, txtTel1, txtTel2, txtNotas, txtDir;
    private CheckBox cbxFav;
    private Contactos saveContacto;
    ProcesosPHP php;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initComponents();
    }

    private void initComponents () {
        this.php = new ProcesosPHP();
        php.setContext(this);
        this.txtNombre = findViewById(R.id.txtNombre);
        this.txtDir = findViewById(R.id.txtDir);
        this.txtTel1 = findViewById(R.id.txtTel1);
        this.txtTel2 = findViewById(R.id.txtTel2);
        this.txtNotas = findViewById(R.id.txtNotas);
        this.cbxFav = findViewById(R.id.cbxFav);
        this.saveContacto = null;
    }

    public void guardar (View view) {
        try {
            if (isNetworkingAvailable()) {
                boolean valido = false;
                if (valido = this.txtNombre.getText().toString().equals("")) {
                    mensajeCorto("Ingresa un nombre");
                }
                if (valido = this.txtTel1.getText().toString().equals("")) {
                    mensajeCorto("Ingresa el teléfono 1");
                }
                if (valido = this.txtDir.getText().toString().equals("")) {
                    mensajeCorto("Ingresa la dirección");
                }
                if (!valido) {
                    Contactos nContacto = new Contactos();
                    nContacto.setNombre(this.txtNombre.getText().toString());
                    nContacto.setTelefono1(this.txtTel1.getText().toString());
                    nContacto.setTelefono2(this.txtTel2.getText().toString());
                    nContacto.setDireccion(this.txtDir.getText().toString());
                    nContacto.setNotas(this.txtNotas.getText().toString());
                    nContacto.setFavorite(this.cbxFav.isChecked() ? 1 : 0);
                    nContacto.setIdMovil(Divice.getSecureId(this));
                    if (this.saveContacto == null) {
                        php.insertarContacto(nContacto);
                        mensajeCorto(R.string.mensaje);
                        limpiar(null);
                    }
                    else {
                        php.actualizarContacto(nContacto, id);
                        mensajeCorto(R.string.mensaje);
                        limpiar(null);
                    }
                }
            }
            else {
                mensajeCorto("La conexión a internet es requerida");
            }
        }
        catch (Exception ex) {
            mensajeCorto("Error: " + ex.getMessage());
        }

    }

    private void mensajeCorto (String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void mensajeCorto (int string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    public void limpiar (View view) {
        this.txtNombre.setText("");
        this.txtTel2.setText("");
        this.txtTel1.setText("");
        this.txtNotas.setText("");
        this.txtDir.setText("");
        this.cbxFav.setChecked(false);
        this.saveContacto = null;
    }

    public void listar (View view) {
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        limpiar(null);
        startActivityForResult(intent, 0);
    }

    public boolean isNetworkingAvailable () {
        ConnectivityManager connmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connmgr.getActiveNetworkInfo();
        return netinfo != null && netinfo.isConnected();
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null) {
            Bundle datos = intent.getExtras();
            if (Activity.RESULT_OK == resultCode) {
                Contactos contacto = (Contactos) datos.getSerializable("contacto");
                saveContacto = contacto;
                id = contacto.get_ID();
                txtNombre.setText(contacto.getNombre());
                txtTel2.setText(contacto.getTelefono2());
                txtTel1.setText(contacto.getTelefono1());
                txtDir.setText(contacto.getDireccion());
                txtNombre.setText(contacto.getNotas());
                cbxFav.setChecked(contacto.getFavorite() == 1);
            }
            else {
                limpiar(null);
            }
        }
    }
}
