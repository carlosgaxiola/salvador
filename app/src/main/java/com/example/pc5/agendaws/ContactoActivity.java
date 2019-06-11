package com.example.pc5.agendaws;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.pc5.agendaws.Objetos.Contactos;
import com.example.pc5.agendaws.Objetos.Divice;
import com.example.pc5.agendaws.Objetos.ProcesosPHP;

public class ContactoActivity extends AppCompatActivity {

    private TextView txtNombre, txtTel1, txtTel2, txtNotas, txtDir;
    private CheckBox cbxFav;
    private Contactos contacto;
    ProcesosPHP php;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);
        this.initComponents();
        this.getContacto();
    }

    private void getContacto () {
        try {
          Intent intent = getIntent();
          Bundle data = intent.getExtras();
          Contactos contacto = (Contactos) data.getSerializable("contacto");
          if (contacto != null) {
              this.txtNombre.setText(contacto.getNombre());
              this.txtDir.setText(contacto.getDireccion());
              this.txtTel1.setText(contacto.getTelefono1());
              this.txtTel2.setText(contacto.getTelefono2());
              this.txtNotas.setText(contacto.getNotas());
              this.cbxFav.setChecked(contacto.getFavorite() == 1);
              this.contacto = contacto;
              getSupportActionBar().setTitle("Nuevo Contacto");
          }
        } catch (Exception ex) {

        }
    }

    private void initComponents () {
        try {
            this.php = new ProcesosPHP();
            php.setContext(this);
            this.txtNombre = findViewById(R.id.txtNombre);
            this.txtDir = findViewById(R.id.txtDir);
            this.txtTel1 = findViewById(R.id.txtTel1);
            this.txtTel2 = findViewById(R.id.txtTel2);
            this.txtNotas = findViewById(R.id.txtNotas);
            this.cbxFav = findViewById(R.id.cbxFav);
            this.contacto = null;
        }
        catch (Exception ex) {

        }
    }

    public void guardar (View view) {
        try {
            boolean valido = false;
            if (valido |= this.txtNombre.getText().toString().equals("")) {
                mensajeCorto("Ingresa un nombre");
            }
            if (valido |= this.txtTel1.getText().toString().equals("")) {
                mensajeCorto("Ingresa el teléfono 1");
            }
            if (valido |= this.txtDir.getText().toString().equals("")) {
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
                else if (this.contacto == null) {
                    php.insertarContacto(nContacto, ContactoActivity.this);
                    limpiar(null);
                }
                else if (noHayCambios()) {
                    finish();
                }
                else {
                    nContacto.set_ID(contacto.get_ID());
                    nContacto.setIdMovil(contacto.getIdMovil());
                    this.contacto = nContacto;
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Modificar contacto");
                    dialog.setMessage("¿Modificar los datos del contacto");
                    dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            php.actualizarContacto(contacto, contacto.get_ID(), ContactoActivity.this);
                            limpiar(null);
                        }
                    });
                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        }
        catch (Exception ex) {
            mensajeCorto("Error: " + ex.getMessage());
        }
    }

    private boolean noHayCambios () {
        return this.contacto.getNombre().equals(this.txtNombre.getText().toString()) &&
                this.contacto.getDireccion().equals(this.txtDir.getText().toString()) &&
                this.contacto.getNotas().equals(this.txtNotas.getText().toString()) &&
                this.contacto.getTelefono1().equals(this.txtTel1.getText().toString()) &&
                this.contacto.getTelefono2().equals(this.txtTel2.getText().toString()) &&
                (this.contacto.getFavorite() == 1) == this.cbxFav.isChecked();
    }

    private void mensajeCorto (String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    public void limpiar (View view) {
        this.txtNombre.setText("");
        this.txtTel2.setText("");
        this.txtTel1.setText("");
        this.txtNotas.setText("");
        this.txtDir.setText("");
        this.cbxFav.setChecked(false);
        this.contacto = null;
    }

    public boolean isNetworkingAvailable () {
        ConnectivityManager connmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connmgr.getActiveNetworkInfo();
        return netinfo != null && netinfo.isConnected();
    }

    public void exito (Contactos contacto) {
        Intent intent = new Intent(ContactoActivity.this, ListarActivity.class);
        intent.putExtra("contacto", contacto);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mensajeCorto("ActivityResult" +  resultCode);
        if (resultCode == RESULT_OK) {
            Bundle datos = intent.getExtras();
            contacto = (Contactos) datos.getSerializable("contacto");
            if (contacto !=  null) {
                txtNombre.setText(contacto.getNombre());
                txtTel2.setText(contacto.getTelefono2());
                txtTel1.setText(contacto.getTelefono1());
                txtDir.setText(contacto.getDireccion());
                txtNotas.setText(contacto.getNotas());
                cbxFav.setChecked(contacto.getFavorite() == 1);
                getSupportActionBar().setTitle("Editar Contacto");
            }
            else {
                limpiar(null);
                getSupportActionBar().setTitle("Nuevo Contacto");
            }
        }
        else {
            limpiar(null);
            getSupportActionBar().setTitle("Nuevo Contacto");
        }
    }

    public void listar (View view) {
        Intent intent = new Intent(ContactoActivity.this, ListarActivity.class);
        intent.putExtra("contactos", new Contactos());
        finish();
        startActivity(intent);
    }
}
