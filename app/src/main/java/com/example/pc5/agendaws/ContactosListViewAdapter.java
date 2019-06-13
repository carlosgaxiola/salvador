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
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc5.agendaws.Objetos.Contactos;

import java.util.ArrayList;
import java.util.List;

public class ContactosListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Contactos> contactos;
    private ArrayList<Contactos> copyContactos = new ArrayList<>();;
    private LayoutInflater inflater;

    public ContactosListViewAdapter(Context context, ArrayList<Contactos> objects) {
        this.mContext = context;
        this.contactos = objects;
        this.copyContactos.addAll(contactos);
        this.inflater = LayoutInflater.from(this.mContext);
    }

    public class ViewHolder {
        TextView nombre, tel1;
        ImageButton btnDel;
    }

    @Override
    public int getCount() { return this.contactos.size(); }

    @Override
    public Contactos getItem(int position) { return this.copyContactos.get(position); }

    @Override
    public long getItemId(int position) { return this.copyContactos.get(position).get_ID(); }

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
        holder.nombre.setText(contactos.get(position).getNombre());
        if (contactos.get(position).getFavorite() == 1)
            holder.nombre.setTextColor(Color.BLUE);
        holder.tel1.setText(contactos.get(position).getTelefono1());
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListarActivity) mContext).borrar(contactos.get(position).get_ID());
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Borrar contacto");
                String nombre = contactos.get(position).getNombre();
                dialog.setMessage("¿Se borrara el contacto " + nombre + "?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                    data.putSerializable("contacto", contactos.get(position));
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

    public void filtrar (String critter) {
        this.contactos.clear();
        if (critter.length() == 0) {
            this.contactos.addAll(copyContactos);
        }
        else {
            for (Contactos con : copyContactos) {
                if (con.getNombre().toLowerCase().contains(critter)) {
                    this.contactos.add(con);
                }
            }
        }
        notifyDataSetChanged();
    }
}