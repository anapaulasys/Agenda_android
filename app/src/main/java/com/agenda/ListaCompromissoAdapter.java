package com.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListaCompromissoAdapter extends ArrayAdapter<Compromisso> {

    private LayoutInflater mInflater;
    private TextView hora;
    private TextView descricao;

    public ListaCompromissoAdapter(Context context, int resource, List<Compromisso> objects) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        v = mInflater.from(this.getContext()).inflate(R.layout.adapter_item_compromisso, null);

        hora = (TextView) v.findViewById(R.id.comp_horario);
        descricao = (TextView) v.findViewById(R.id.comp_descricao);

        final Compromisso compromisso = getItem(position);

        if (compromisso != null) {

            if (compromisso.getHorario() != null) {
                hora.setText(compromisso.getHorario());
            }

            if (compromisso.getDescricao() != null) {
                descricao.setText(compromisso.getDescricao());
            }
        }

        return v;
    }
}
