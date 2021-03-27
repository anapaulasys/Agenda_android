package com.agenda;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IncluirAlarmeActivity extends AppCompatActivity {

    private Context mContext;
    Compromisso compromisso;
    Spinner antecedencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incluir_alarme);

        mContext = getBaseContext();

        Bundle extras = getIntent().getExtras();
        compromisso = new Compromisso();

        if (extras != null) {
            compromisso = (Compromisso) extras.getSerializable("compromisso");
        }


        antecedencia = (Spinner) findViewById(R.id.antecedencia);
        List<String> list = new ArrayList<String>();
        list.add("5 min");
        list.add("10 min");
        list.add("15 min");
        list.add("20 min");
        list.add("30 min");
        list.add("45 min");
        list.add("1 h");
        list.add("2 h");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        antecedencia.setAdapter(dataAdapter);

        TextView dataAlarme = (TextView) findViewById(R.id.alarme_data);
        TextView horaAlarme = (TextView) findViewById(R.id.alarme_hora);

        dataAlarme.setText(compromisso.getData());
        horaAlarme.setText(compromisso.getHorario());

        Button btnIncluirAlarme = (Button) findViewById(R.id.btn_incluir_alarme);
        btnIncluirAlarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int antecedenciaSelecionada = 0;

                switch (antecedencia.getSelectedItem().toString()) {
                    case "5 min":
                        antecedenciaSelecionada = 5;
                        break;
                    case "10 min":
                        antecedenciaSelecionada = 10;
                        break;
                    case "15 min":
                        antecedenciaSelecionada = 15;
                        break;
                    case "20 min":
                        antecedenciaSelecionada = 20;
                        break;
                    case "30 min":
                        antecedenciaSelecionada = 30;
                        break;
                    case "45 min":
                        antecedenciaSelecionada = 45;
                        break;
                    case "1 h":
                        antecedenciaSelecionada = 60;
                        break;
                    case "2 h":
                        antecedenciaSelecionada = 120;
                        break;

                    default:
                        break;
                }



                String[] diaCompromisso = compromisso.getData().split("/");
                String[] horaCompromisso = compromisso.getHorario().split(":");

/*                try {

                    String myTime = "14:10";
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                    Date d = df.parse(myTime);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    cal.add(Calendar.MINUTE, 10);
                    String newTime = df.format(cal.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }*/

                int minutoAlarme = 0, horaAlarme = 0, diaAlarme = 0, mesAlarme = 0, anoAlarme = 0;

                minutoAlarme = Integer.parseInt(horaCompromisso[1]);
                horaAlarme = Integer.parseInt(horaCompromisso[0]);
                diaAlarme = Integer.parseInt(diaCompromisso[0]);
                mesAlarme = Integer.parseInt(diaCompromisso[1]);
                anoAlarme = Integer.parseInt(diaCompromisso[2]);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH,(mesAlarme - 1));
                cal.set(Calendar.YEAR,anoAlarme);
                cal.set(Calendar.DAY_OF_MONTH,diaAlarme);
                cal.set(Calendar.HOUR_OF_DAY,horaAlarme);
                cal.set(Calendar.MINUTE,minutoAlarme);


                Intent intent = new Intent(mContext, Alarme.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1253, intent, PendingIntent.FLAG_UPDATE_CURRENT|  Intent.FILL_IN_DATA);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent );
                Toast.makeText(mContext, "Alarme configurado.", Toast.LENGTH_LONG).show();
            }
        });

    }
}
