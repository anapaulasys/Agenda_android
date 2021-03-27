package com.agenda;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;
public class NovoCompromissoActivity extends AppCompatActivity {


    private TextView data;
    private TextView horario;
    private EditText descricao;
    private Button btnData;
    private Button btnHora;
    private Button btnIncluirCompromisso;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    static final int DATE_DIALOG_ID = 998;
    static final int TIME_DIALOG_ID = 999;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_compromisso);

        mContext = getBaseContext();

        descricao = (EditText) findViewById(R.id.descricao);
        data = (TextView) findViewById(R.id.data);
        horario = (TextView) findViewById(R.id.hora);
        exibirData();
        exibirHorario();
        btnData = (Button) findViewById(R.id.btn_data);
        btnData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });

        btnHora = (Button) findViewById(R.id.btn_hora);
        btnHora.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(TIME_DIALOG_ID);

            }

        });

        btnIncluirCompromisso= (Button) findViewById(R.id.btn_incluir_compromisso);
        btnIncluirCompromisso.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (data.getText() == null || data.getText().toString().equals("") || horario.getText() == null || horario.getText().equals("") || descricao.getText().toString() == null || descricao.getText().toString().equals("")){

                    AlertDialog alertDialog = new AlertDialog.Builder(NovoCompromissoActivity.this).create();
                    alertDialog.setTitle("Atenção");
                    alertDialog.setMessage("Todos os campos devem ser preenchidos.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                } else {

                    Compromisso novoCompromisso = new Compromisso();
                    novoCompromisso.setId(0);
                    novoCompromisso.setIdUsuario(AgendaUtil.idUsuario);
                    String x = data.getText().toString();
                    novoCompromisso.setData(data.getText().toString());
                    novoCompromisso.setHorario(horario.getText().toString());
                    novoCompromisso.setDescricao(descricao.getText().toString());
                    new IncluirCompromissoTask().execute(novoCompromisso);
                }

            }

        });

    }

    public void exibirData() {

        data = (TextView) findViewById(R.id.data);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        data.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
    }

    public void exibirHorario() {

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        horario.setText(new StringBuilder().append(pad(hour)).append(":").append(pad(minute)));

    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:

                return new DatePickerDialog(this, datePickerListener, year, month, day);
            case TIME_DIALOG_ID:

                return new TimePickerDialog(this, timePickerListener, hour, minute,false);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            data.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));

        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    horario.setText(new StringBuilder().append(pad(hour)).append(":").append(pad(minute)));


                }
            };

    private String enviarCompromisso(Compromisso compromisso){

        InputStream inputStream = null;
        String result = "";

        try{

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(AgendaUtil.URL_SERVICOS + AgendaUtil.URL_COMPROMISSOS);

            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", compromisso.getId());
            jsonObject.accumulate("idUsuario", compromisso.getIdUsuario());
            jsonObject.accumulate("data", compromisso.getData());
            jsonObject.accumulate("horario", compromisso.getHorario());
            jsonObject.accumulate("descricao", compromisso.getDescricao());
            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null){
                result = AgendaUtil.inputStreamToString(inputStream);
            } else {
                result = "erro";
            }

        } catch (Exception e){

            Log.e("NovoCompromissoActivity", e.getMessage(), e);
        }

        return result;
    }

    private class IncluirCompromissoTask extends AsyncTask<Compromisso, Compromisso, String> {
        @Override
        protected String doInBackground(Compromisso... compromisso) {

            return enviarCompromisso(compromisso[0]);
        }

        @Override
        protected void onPostExecute(String resultado) {

            if(resultado != null && resultado.equals("1")){

                AlertDialog alertDialog = new AlertDialog.Builder(NovoCompromissoActivity.this).create();
                alertDialog.setTitle("Confirmação");
                alertDialog.setMessage("Compromisso agendado com sucesso.");
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

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(NovoCompromissoActivity.this).create();
                alertDialog.setTitle("Atenção");
                alertDialog.setMessage("Não foi possível cadastrar o compromisso.");
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
}
