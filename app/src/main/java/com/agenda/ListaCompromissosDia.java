package com.agenda;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

public class ListaCompromissosDia extends AppCompatActivity {

    Context mContext;
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compromissos_dia);

        mContext = getBaseContext();

        Bundle extras = getIntent().getExtras();
        String dia = "";

        if (extras != null) {
            dia = extras.getString("data");
        }

        ArrayList<Compromisso> compromissosDia = new ArrayList<Compromisso>();

        for(int i = 0; i < AgendaUtil.compromissos.size(); i++){

            if(!dia.equals("") && dia.equals(AgendaUtil.compromissos.get(i).getData())){

                compromissosDia.add(AgendaUtil.compromissos.get(i));
            }
        }

        lista = (ListView) findViewById(R.id.compromissos_dia);
        ListaCompromissoAdapter adapter = new ListaCompromissoAdapter(this, R.layout.adapter_item_compromisso, compromissosDia);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent detalhe = new Intent(mContext, DetalheCompromisso.class);
                detalhe.putExtra("compromisso", (Serializable) lista.getAdapter().getItem(position));
                startActivity(detalhe);
            }
        });

    }
}
