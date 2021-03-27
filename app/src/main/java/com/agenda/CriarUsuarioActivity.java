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
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

public class CriarUsuarioActivity extends AppCompatActivity {

    private EditText inputUsuario, inputSenha;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_usuario);

        context = getBaseContext();

        inputUsuario = (EditText) findViewById(R.id.input_usuario);
        inputSenha = (EditText) findViewById(R.id.input_senha);

        Button btnCriarUsuario = (Button) findViewById(R.id.btn_criar_usuario);
        btnCriarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputUsuario.getText() == null || inputUsuario.getText().toString().equals("") || inputSenha.getText() == null || inputSenha.getText().equals("")){

                    AlertDialog alertDialog = new AlertDialog.Builder(CriarUsuarioActivity.this).create();
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

                    Usuario novoUsuario = new Usuario();
                    novoUsuario.setId(0);
                    novoUsuario.setUsuario(inputUsuario.getText().toString());
                    novoUsuario.setSenha(inputSenha.getText().toString());
                    new CriarUsuarioTask().execute(novoUsuario);
                }
            }
        });
    }


    private String enviarUsuario(Usuario usuario){

        InputStream inputStream = null;
        String result = "";

        try{

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(AgendaUtil.URL_SERVICOS + AgendaUtil.URL_USUARIOS);

            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", usuario.getId());
            jsonObject.accumulate("usuario", usuario.getUsuario());
            jsonObject.accumulate("senha", AgendaUtil.senha(usuario.getSenha()));
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

            Log.e("CriarUsuarioActivity", e.getMessage(), e);
        }

        return result;
    }

    private class CriarUsuarioTask extends AsyncTask<Usuario, Usuario, String> {
        @Override
        protected String doInBackground(Usuario... usuario) {

            return enviarUsuario(usuario[0]);
        }

        @Override
        protected void onPostExecute(String resultado) {

            if(resultado != null && resultado.equals("1")){

                AlertDialog alertDialog = new AlertDialog.Builder(CriarUsuarioActivity.this).create();
                alertDialog.setTitle("Confirmação");
                alertDialog.setMessage("Usuário criado com sucesso.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent login = new Intent(context, LoginActivity.class);
                                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(login);
                            }
                        });
                alertDialog.show();

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(CriarUsuarioActivity.this).create();
                alertDialog.setTitle("Atenção");
                alertDialog.setMessage("Não foi possível cadastrar usuário.");
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
