package com.example.pc5.agendaws.Objetos;

import android.content.Context;
import android.widget.Toast;

import com.example.pc5.agendaws.ListActivity;

public class CallbackHelper {
    private Context context;

    public CallbackHelper (Context context) {
        this.context = context;
    }

    public void consultarTodosWebService () {
        if (this.context instanceof ListActivity) {
            ((ListActivity) this.context).consultarTodosWebService();
        }
    }
}
