package com.example.pc5.agendaws;

import android.app.Activity;
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
import java.util.Locale;

public class ContactosListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List contactosList;
    private ArrayList<Contactos> arrayList;
    private LayoutInflater inflater;

    public ContactosListViewAdapter(Context mContext, List<Contactos> contactosList) {
        this.mContext = mContext;
        this.contactosList = contactosList;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(contactosList);
        this.inflater = LayoutInflater.from(this.mContext);
    }

    public class ViewHolder {
        TextView nombre, tel1;
        ImageButton btnDel;
    }

    @Override
    public int getCount() { return this.contactosList.size(); }

    @Override
    public Object getItem(int position) { return this.arrayList.get(position); }

    @Override
    public long getItemId(int position) { return this.arrayList.get(position).get_ID(); }

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
        holder.nombre.setText(arrayList.get(position).getNombre());
        if (arrayList.get(position).getFavorite() == 1)
            holder.nombre.setTextColor(Color.BLUE);
        holder.tel1.setText(arrayList.get(position).getTelefono1());
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Borrar contacto");
                String nombre = arrayList.get(position).getNombre();
                dialog.setMessage("¿Se borrara el contacto " + nombre + "?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ListarActivity) mContext).borrar(arrayList.get(position).get_ID());
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
                    data.putSerializable("contacto", arrayList.get(position));
                    Intent intent = new Intent(mContext, ContactoActivity.class);
                    intent.putExtras(data);
                    Toast.makeText(mContext, "click", Toast.LENGTH_SHORT).show();
                    ((ListarActivity) mContext).setResult(Activity.RESULT_OK, intent);
                    ((ListarActivity) mContext).finish();
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
        contactosList.clear();
        if (critter.length() == 0) {
            contactosList.addAll(arrayList);
        }
        else {
            for (Contactos contacto : arrayList) {
                boolean match = contacto.getNombre()
                        .toLowerCase()
                        .contains(critter);
                if (match) {
                    contactosList.add(contacto);
                }
            }
        }
        notifyDataSetChanged();
    }
}
