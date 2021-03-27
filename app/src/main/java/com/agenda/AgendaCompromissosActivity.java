package com.agenda;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AgendaCompromissosActivity extends AppCompatActivity {

    private CalendarioAdapter adapter;
    private Handler handler;
    private Calendar month;
    private ArrayList<String> itens;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_compromissos);

        new ConsultarCompromissosTask().execute();

        mContext = getBaseContext();
        month = Calendar.getInstance();

        itens = new ArrayList<String>();

        adapter = new CalendarioAdapter(this, month);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        handler = new Handler();
        handler.post(calendarUpdater);

        TextView title  = (TextView) findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        TextView previous  = (TextView) findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
                }
                refreshCalendar();
            }
        });

        TextView next  = (TextView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
                }
                refreshCalendar();

            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = (TextView)v.findViewById(R.id.date);
                if(date instanceof TextView && !date.getText().equals("")) {

                    String day = date.getText().toString();
                    String diaSelecionado = day + "/" + android.text.format.DateFormat.format("MM/yyyy", month);

                    String[] dia = diaSelecionado.split("/");

                    diaSelecionado = Integer.parseInt(dia[0]) + "/" + Integer.parseInt(dia[1]) + "/" + Integer.parseInt(dia[2]);

                    boolean possuiCompromissos = false;

                    for(int i = 0; i < AgendaUtil.compromissos.size(); i++){

                        if(AgendaUtil.compromissos.get(i).getData().equals(diaSelecionado)){
                            possuiCompromissos = true;
                            break;
                        }
                    }

                    if(possuiCompromissos){

                        Intent compromissosDia = new Intent(mContext, ListaCompromissosDia.class);
                        if(day.length() == 1) {
                            day = "0" + day;
                        }

                        compromissosDia.putExtra("data", diaSelecionado);
                        startActivity(compromissosDia);
                    } else {

                        AlertDialog alertDialog = new AlertDialog.Builder(AgendaCompromissosActivity.this).create();
                        alertDialog.setTitle("Atenção");
                        alertDialog.setMessage("Não há compromissos para a data selecionada.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }


                }

            }
        });

        Button btnNovoCompromisso = (Button) findViewById(R.id.btn_novo_compromisso);
        btnNovoCompromisso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent novoCompromisso = new Intent(mContext, NovoCompromissoActivity.class);
                startActivity(novoCompromisso);
            }
        });
    }

    public void refreshCalendar() {
        TextView title  = (TextView) findViewById(R.id.title);

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater);

        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            itens.clear();

            Calendar c = Calendar.getInstance();
            int mes = c.get(Calendar.MONTH);
            ArrayList<String> compromissosMes = new ArrayList<>();

            for(int i = 0; i < AgendaUtil.compromissos.size(); i++){

                String[] diaCompromisso = AgendaUtil.compromissos.get(i).getData().split("/");

                if((mes + 1) == Integer.parseInt(diaCompromisso[1])){
                    compromissosMes.add(diaCompromisso[0]);
                }
            }

            Set<String> hs = new HashSet<>();
            hs.addAll(compromissosMes);
            compromissosMes.clear();
            compromissosMes.addAll(hs);

            adapter.setItens(compromissosMes);
            adapter.notifyDataSetChanged();
        }
    };

    private ArrayList<Compromisso> consultarCompromissos() {

        InputStream inputStream = null;
        String result = "";

        try{

            String st = AgendaUtil.URL_SERVICOS + AgendaUtil.URL_COMPROMISSOS + "?idUsuario=" + AgendaUtil.idUsuario;

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(st);

            HttpResponse httpResponse = httpclient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null){
                result = AgendaUtil.inputStreamToString(inputStream);
            } else {
                result = "erro";
            }

        } catch (Exception e){

            Log.e("LoginActivity", e.getMessage(), e);
        }

        JSONObject json = null;

        JSONArray jsonArray = new JSONArray();

        try {

            json = new JSONObject("{'json':" + result + "}");

            jsonArray = json.getJSONArray("json");

        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Compromisso> compromissos = new ArrayList<Compromisso>();

        for (int i = 0 ; i < jsonArray.length(); i++) {

            Compromisso compromisso = new Compromisso();
            try {
                JSONObject compromissoJson = jsonArray.getJSONObject(i);
                compromisso.setId(compromissoJson.getInt("id"));
                compromisso.setIdUsuario(compromissoJson.getInt("idUsuario"));
                compromisso.setData(compromissoJson.getString("data"));
                compromisso.setHorario(compromissoJson.getString("horario"));
                compromisso.setDescricao(compromissoJson.getString("descricao"));
                compromissos.add(compromisso);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return compromissos;
    }

    private class ConsultarCompromissosTask extends AsyncTask<Compromisso, Compromisso, ArrayList<Compromisso>> {
        @Override
        protected ArrayList<Compromisso> doInBackground(Compromisso... usuario) {

            return consultarCompromissos();
        }

        @Override
        protected void onPostExecute(ArrayList<Compromisso> compromissos) {

            AgendaUtil.compromissos = compromissos;
            handler.post(calendarUpdater);
        }

    }
}
