package com.example.lukie.criptoim;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.stefano.android.NewRSA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Connection extends AppCompatActivity {


    Button connect;
    TextView ip_serv;
    TextView port_serv;
    String TAG="Cripto Connection";
    NewRSA algRSAServ=new NewRSA();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        connect=(Button) findViewById(R.id.connect);
        ip_serv=(TextView) findViewById(R.id.ip_server);
        port_serv=(TextView)findViewById(R.id.port);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        connect.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {

                                            if(!ip_serv.getText().toString().equals("") && !port_serv.getText().toString().equals("")) {
                                                String ip = ip_serv.getText().toString();
                                                int port = Integer.parseInt(port_serv.getText().toString());

                                                try {
                                                    SocketHandler.setSocket(new Socket(InetAddress.getByName(ip), port));
                                                    connect.setText("OK");

                                                } catch (IOException e) {
                                                    Log.d(TAG, "Connessione Socket non riuscita");
                                                }
                                            }


                                           try {
                                               if(SocketHandler.getSocket()!=null){
                                                   SocketHandler.setInput(new ObjectInputStream(SocketHandler.getSocket().getInputStream()));
                                               algRSAServ.setKPu((PublicKey) SocketHandler.getInput().readObject());
                                               // algRSA=new RSA(PuKeyServ[0],PuKeyServ[1]);
                                               Log.d(TAG, "Chiave Pubblica del Server: " + Base64.encodeToString(algRSAServ.getPu().getEncoded(),Base64.DEFAULT));



                                               SocketHandler.setOutput( new ObjectOutputStream(SocketHandler.getSocket().getOutputStream()));
                                                   //Crittografia ack chiave pubblica Server
                                                   byte[] s=algRSAServ.rsaEncrypt("ACK".getBytes(),algRSAServ.getPu());

                                                   SocketHandler.getOutput().writeObject(s);
                                                   SocketHandler.getOutput().flush();

                                                   if(SocketHandler.getSocket().isConnected())
                                                   {
                                                       Intent intent=new Intent(getApplicationContext(),LoginReg.class);
                                                       intent.putExtra(TAG,algRSAServ);
                                                       startActivity(intent);
                                                   }

                                               }



                                           } catch (IOException e) {
                                               e.printStackTrace();
                                           } catch (ClassNotFoundException e) {
                                               e.printStackTrace();
                                           } catch (NoSuchPaddingException e) {
                                               e.printStackTrace();
                                           } catch (NoSuchAlgorithmException e) {
                                               e.printStackTrace();
                                           } catch (InvalidKeyException e) {
                                               e.printStackTrace();
                                           } catch (IllegalBlockSizeException e) {
                                               e.printStackTrace();
                                           } catch (BadPaddingException e) {
                                               e.printStackTrace();
                                           }

                                       }
                                   }
                           );

}
}
