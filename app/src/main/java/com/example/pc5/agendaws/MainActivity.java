package com.example.pc5.agendaws;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private View helpView;
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
                if (!isNetworkingAvailable()) {
                    mensajeCorto("La conexión a internet es requerida");
                }
                else if (this.saveContacto == null) {
                    php.insertarContacto(nContacto, MainActivity.this);
                }
                else if (hayCambios()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Modificar contacto");
                    dialog.setMessage("¿Modificar los datos del contacto");
                    dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int pos = (int) helpView.getTag(R.string.pos);
                            int id = contactos.get(pos).get_ID();
                            php.borrarContacto(id);
                            cargarContactos();
                        }
                    });
                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    php.actualizar(nCointacto, MainActivity.this);
                }
            }
        }
        catch (Exception ex) {
            mensajeCorto("Error: " + ex.getMessage());
        }
    }

    private boolean hayCambios () {
        return this.saveContacto.getNombre().compareToIgnoreCase(this.txtNombre.getText().toString()) != 0 ||
                this.saveContacto.getDireccion().compareToIgnoreCase(this.txtDir.getText().toString()) != 0 ||
                this.saveContacto.getNotas().compareToIgnoreCase(this.txtNotas.getText().toString()) != 0 ||
                this.saveContacto.getTelefono1().compareToIgnoreCase(this.txtTel1.getText().toString()) != 0 ||
                this.saveContacto.getTelefono2().compareToIgnoreCase(this.txtTel2.getText().toString()) != 0 ||
                (this.saveContacto.getFavorite() == 1) != this.cbxFav.isChecked();
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
                txtNotas.setText(contacto.getNotas());
                cbxFav.setChecked(contacto.getFavorite() == 1);
            }
            else {
                limpiar(null);
            }
        }
    }
}
