package com.agenda;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;


public class LoginActivity extends AppCompatActivity {

    private EditText inputUsuario, inputSenha;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getBaseContext();

        inputUsuario = (EditText) findViewById(R.id.usuario);
        inputSenha = (EditText) findViewById(R.id.senha);

        Button btnEntrar = (Button) findViewById(R.id.btn_entrar);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Usuario usuario = new Usuario();
                usuario.setId(0);
                usuario.setUsuario(inputUsuario.getText().toString());
                usuario.setSenha(inputSenha.getText().toString());
                new AutenticarUsuarioTask().execute(usuario);
            }
        });

        Button btnCriarUsuario = (Button) findViewById(R.id.btn_novo_usuario);
        btnCriarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent criarUsuario = new Intent(getBaseContext(), CriarUsuarioActivity.class);
                startActivity(criarUsuario);
            }
        });

    }

    private String autenticarUsuario(Usuario usuario){

        InputStream inputStream = null;
        String result = "";

        try{

            String senha = AgendaUtil.senha(usuario.getSenha());

            String st2 = AgendaUtil.URL_SERVICOS + AgendaUtil.URL_USUARIOS + "/usuario/?usuario='id':0,'usuario':'" + usuario.getUsuario() + "','senha':'" + senha + "'";

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(st2);

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

        return result;
    }

    private class AutenticarUsuarioTask extends AsyncTask<Usuario, Usuario, String> {
        @Override
        protected String doInBackground(Usuario... usuario) {

            return autenticarUsuario(usuario[0]);
        }

        @Override
        protected void onPostExecute(String resultado) {

            if(resultado != null && Integer.parseInt(resultado) > 0){

                AgendaUtil.idUsuario = Integer.parseInt(resultado);
                Intent agenda = new Intent(context, AgendaCompromissosActivity.class);
                startActivity(agenda);

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Atenção");
                alertDialog.setMessage("Não foi possível encontrar o usuário.");
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
