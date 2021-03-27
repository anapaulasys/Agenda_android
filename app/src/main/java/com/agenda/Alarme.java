package com.agenda;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Alarme extends BroadcastReceiver{

    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Você tem um compromisso marcado.", Toast.LENGTH_LONG).show();

    }



}
