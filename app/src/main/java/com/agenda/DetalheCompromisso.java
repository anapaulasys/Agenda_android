package com.agenda;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.Serializable;

public class DetalheCompromisso extends AppCompatActivity {

    private Context mContext;
    Compromisso compromisso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_compromisso);

        mContext = getBaseContext();

        Bundle extras = getIntent().getExtras();
        compromisso = new Compromisso();

        if (extras != null) {
            compromisso = (Compromisso) extras.getSerializable("compromisso");
        }

        TextView data = (TextView) findViewById(R.id.detalhe_data);
        TextView hora = (TextView) findViewById(R.id.detalhe_hora);
        TextView descricao = (TextView) findViewById(R.id.detalhe_descricao);

        data.setText(compromisso.getData());
        hora.setText(compromisso.getHorario());
        descricao.setText(compromisso.getDescricao());

        Button criarAlarme = (Button) findViewById(R.id.btn_criar_alarme);
        criarAlarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent incluirAlarme = new Intent(mContext, IncluirAlarmeActivity.class);
                incluirAlarme.putExtra("compromisso", (Serializable) compromisso);
                startActivity(incluirAlarme);
            }
        });

        Button removerCompromisso = (Button) findViewById(R.id.btn_remover_compromisso);
        removerCompromisso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExcluirCompromissoTask().execute(compromisso.getId());
            }
        });

    }

    private String excluirCompromisso(int id){
        InputStream inputStream = null;
        String result = "-1";

        try{

            String st = AgendaUtil.URL_SERVICOS + AgendaUtil.URL_COMPROMISSOS + AgendaUtil.URL_EXCLUIR + "?id=" + id;

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(st);

            HttpResponse httpResponse = httpclient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null){
                result = AgendaUtil.inputStreamToString(inputStream);
            } else {
                result = "-1";
            }

        } catch (Exception e){

            Log.e("DetalheCompromisso", e.getMessage(), e);
        }

        return result;
    }

        private class ExcluirCompromissoTask extends AsyncTask<Integer, Integer, String> {
            @Override
            protected String doInBackground(Integer... id) {

                return excluirCompromisso(id[0]);
            }

            @Override
            protected void onPostExecute(String resultado) {

                if(Integer.parseInt(resultado) == -1){

                    AlertDialog alertDialog = new AlertDialog.Builder(DetalheCompromisso.this).create();
                    alertDialog.setTitle("Atenção");
                    alertDialog.setMessage("Não foi possível excluir o compromisso.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(DetalheCompromisso.this).create();
                    alertDialog.setTitle("Confirmação");
                    alertDialog.setMessage("Compromisso excluido com sucesso.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent agenda = new Intent(mContext, AgendaCompromissosActivity.class);
                                    agenda.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(agenda);
                                }
                            });
                    alertDialog.show();

                }

            }

        }
}
