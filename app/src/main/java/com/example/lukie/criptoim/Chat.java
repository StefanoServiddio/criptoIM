package com.example.lukie.criptoim;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.content.Intent;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import com.stefano.android.*;





import android.util.Log;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

//enum Mode{NO,AES,DES3,Blow};
public class Chat extends AppCompatActivity {

    Button bt; // connect button
    Button bt2; //sender button

    TextView tx;
    ScrollView mScrollView;
    EditText et;
    Envelop received=null;
    String sent="";
    String userName;
    BigInteger[]PuKeyServ;
    String TAG="CriptoIM";
    String crittoState="NO";
    RSA algRSA=null;
    RSASend algRSAServ=null;
    ObjectInputStream inputStream = null;
    ObjectOutputStream outputStream = null;
    AES algAES;
    TripleDES algDes;
    Blowfish algBlow;
    HmacSha1 algHMAC;


    int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt=(Button)findViewById(R.id.button2);
        bt2=(Button)findViewById(R.id.button);

        tx=(TextView)findViewById(R.id.textView);
        et=(EditText)findViewById(R.id.editText);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        tx.setMovementMethod(new ScrollingMovementMethod());

       final Receiver task=new Receiver();
        task.execute();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Intent i=getIntent();
        algRSAServ=(RSASend)i.getSerializableExtra(TAG);
        Bundle bundle=getIntent().getExtras();
        userName=bundle.getString("userName");
        Log.d(TAG,"il mio nome è "+userName);




        bt.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),CriptoActivity.class);
                startActivity(intent);
            }
        });

        bt2.setOnClickListener(new View.OnClickListener(){
               public void onClick(View v)
               {
                 sendMess();



               }


        });



    }



    private void sendMess(){

        sent=et.getText().toString();
        crittoState="NO";
        if(!sent.equals("")) {

            try {
                Envelop mess=new Envelop();
                mess.setFrom(userName);
                mess.setText(sent);

                //modalità di criptazione da acquisire dall'activity crypto
                mess.setCripto(Envelop.Mode.NO);
                Log.d(TAG,"ho scritto: "+sent);
                //conversione in bye
                byte[] data=mess.convEnvByte(mess);
                //Scegli il tipo di Criptazione
                Log.d("TAG", "Modalità di crittazione inviata"+crittoState);
                SocketHandler.getOutput().writeObject(algRSAServ.encryptPu(crittoState));
                SocketHandler.getOutput().flush();

                //invio criptato
                SocketHandler.getOutput().writeObject(data);
                SocketHandler.getOutput().flush();
                Log.d(TAG,"ho scritto: "+sent);
                et.getText().clear();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    private class Receiver extends AsyncTask<Void,Envelop,Void>{


        @Override
        protected Void doInBackground(Void... params) {
            try{
                byte[] dataRec=null;
                Envelop e=new Envelop();

                while(true) {



                   if( (dataRec = (byte[])SocketHandler.getInput().readObject())!=null){
                       //Decripta e converti i byte in envelop


                       e=e.convByteEnv(dataRec);
                       Log.d(TAG,"ho ricevuto: "+e.getText());
                       publishProgress(e);
                   }
                }
            }catch(IOException ioe)
            {
                ioe.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(Envelop... msg){
            tx.append(msg[0].getFrom()+" scrive: ");
            tx.append(msg[0].getText());
            tx.append("\n");

       }

    };



}
