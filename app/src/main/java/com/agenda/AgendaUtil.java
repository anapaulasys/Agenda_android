package com.agenda;


import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AgendaUtil {

    public static int idUsuario;
    public static ArrayList<Compromisso> compromissos = new ArrayList<Compromisso>();
    public static final String URL_SERVICOS = "http://10.0.2.2:8081/agenda-services";
    public static final String URL_USUARIOS = "/usuarios";
    public static final String URL_COMPROMISSOS = "/compromissos";
    public static final String URL_EXCLUIR = "/excluir";

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static String senha(String plainText) throws UnsupportedEncodingException {
        byte[] utf8 = plainText.getBytes("UTF-8");
        byte[] test = DigestUtils.sha(DigestUtils.sha(utf8));
        return "*" + getString(test).toUpperCase();
    }

    private static String getString( byte[] bytes ) {

        StringBuffer sb = new StringBuffer();
        for( int i=0; i<bytes.length; i++ )
        {
            byte b = bytes[ i ];
            String hex = Integer.toHexString((int) 0x00FF & b);
            if (hex.length() == 1)
            {
                sb.append("0");
            }
            sb.append( hex );
        }
        return sb.toString();
    }

    public static String todayString() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
}
