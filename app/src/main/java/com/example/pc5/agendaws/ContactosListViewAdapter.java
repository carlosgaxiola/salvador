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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc5.agendaws.Objetos.Contactos;

import java.util.ArrayList;
import java.util.List;

public class ContactosListViewAdapter extends ArrayAdapter<Contactos> {

    private Context mContext;
    private List contactosList;
    private ArrayList<Contactos> contactos;
    private LayoutInflater inflater;

    public ContactosListViewAdapter(Context context, int resource, List<Contactos> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.contactosList = objects;
        this.contactos = new ArrayList<>();
        this.contactos.addAll(contactosList);
        this.inflater = LayoutInflater.from(this.mContext);
    }

    public class ViewHolder {
        TextView nombre, tel1;
        ImageButton btnDel;
    }

    @Override
    public int getCount() { return this.contactosList.size(); }

    @Override
    public Contactos getItem(int position) { return this.contactos.get(position); }

    @Override
    public long getItemId(int position) { return this.contactos.get(position).get_ID(); }

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Borrar contacto");
                String nombre = contactos.get(position).getNombre();
                dialog.setMessage("¿Se borrara el contacto " + nombre + "?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ListarActivity) mContext).borrar(contactos.get(position).get_ID());
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

    @Override
    public Filter getFilter(final ContactosListViewAdapter adapter) {
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                List<Contactos> arrayListContactos = (List<Contactos>) filterResults.values;
                adapter.notifyDataSetChanged();
            }
        };
        return filter;
    }

    /*public void filter (String critter) {
        critter = critter.toLowerCase();
        this.contactosList.clear();
        if (critter.length() == 0) {
            this.contactosList.addAll(contactos);
        }
        else {
            for (Contactos con : contactos) {
                Toast.makeText(mContext, con.getNombre().toLowerCase(),
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "Contiene " + con.getNombre().toLowerCase()
                                .contains(critter),
                        Toast.LENGTH_SHORT).show();
                if (con.getNombre().toLowerCase().contains(critter)) {
                    boolean added = this.contactosList.add(con);
                    Toast.makeText(mContext, "contacto " + con.getNombre() +
                                    " agregado " + added,
                            Toast.LENGTH_SHORT).show();
                }
            }
            for (int i = 0; i < this.contactosList.size(); i++) {
                Toast.makeText(mContext, "contacto " +
                        ((Contactos)this.contactosList.get(i)).getNombre(), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        notifyDataSetChanged();
    }*/
}