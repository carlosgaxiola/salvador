package com.example.pc5.agendaws;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc5.agendaws.Objetos.Contactos;

import java.util.ArrayList;
import java.util.List;

public class ContactosListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List contactosList;
    private ArrayList<Contactos> contactosTemp;
    private ArrayList<Contactos> contactos;
    private LayoutInflater inflater;

    public ContactosListViewAdapter(Context mContext, List<Contactos> contactosList) {
        this.mContext = mContext;
        this.contactosList = contactosList;
        this.contactos = new ArrayList<>();
        this.contactos.addAll(contactosList);
        this.contactosTemp = new ArrayList<>();
        this.contactosTemp.addAll(contactosList);
        this.inflater = LayoutInflater.from(this.mContext);
    }

    public class ViewHolder {
        TextView nombre, tel1;
        ImageButton btnDel;
    }

    @Override
    public int getCount() { return this.contactosList.size(); }

    @Override
    public Object getItem(int position) { return this.contactosTemp.get(position); }

    @Override
    public long getItemId(int position) { return this.contactosTemp.get(position).get_ID(); }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row, null);
            holder.nombre = view.findViewById(R.id.lblNombre);
            holder.tel1 = view.findViewById(R.id.lblTel1);
            holder.btnDel = view.findViewById(R.id.btnDel);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        holder.nombre.setText(contactosTemp.get(position).getNombre());
        if (contactosTemp.get(position).getFavorite() == 1)
            holder.nombre.setTextColor(Color.BLUE);
        holder.tel1.setText(contactosTemp.get(position).getTelefono1());
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Borrar contacto");
                String nombre = contactosTemp.get(position).getNombre();
                dialog.setMessage("¿Se borrara el contacto " + nombre + "?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ListarActivity) mContext).borrar(contactosTemp.get(position).get_ID());
                    }
                });
                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { } });
                dialog.show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bundle data = new Bundle();
                    data.putSerializable("contacto", contactosTemp.get(position));
                    Intent intent = new Intent(mContext, ContactoActivity.class);
                    intent.putExtras(data);
                    ((ListarActivity) mContext).finish();
                    mContext.startActivity(intent);
                }
                catch (Exception ex) {
                    Toast.makeText(mContext, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void filter (String critter) {
        critter = critter.toLowerCase();
        contactosTemp = new ArrayList<>();
        if (critter.length() == 0) {
            contactosTemp.addAll(contactos);
        }
        else {
            for (Contactos con : contactos) {
                Toast.makeText(mContext, con.getNombre().toLowerCase(),
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "Contiene " + con.getNombre().toLowerCase()
                                .contains(critter),
                        Toast.LENGTH_SHORT).show();
                if (con.getNombre().toLowerCase().contains(critter)) {
                    boolean added = contactosTemp.add(con);
                    Toast.makeText(mContext, "contacto " + con.getNombre() +
                                    " agregado " + added,
                            Toast.LENGTH_SHORT).show();
                }
            }
            for (int i = 0; i < contactosTemp.size(); i++) {
                Toast.makeText(mContext, "contacto " +
                        contactosTemp.get(i).getNombre(), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        notifyDataSetChanged();
    }

    public void updateData (ArrayList<Contactos> contactos) {
        this.contactosTemp = contactos;
        this.contactosList.clear();
        this.contactosList.addAll(this.contactosTemp);
    }
}