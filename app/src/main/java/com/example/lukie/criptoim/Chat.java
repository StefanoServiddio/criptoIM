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
import java.util.Random;
import com.stefano.android.Envelop;





import android.util.Log;
enum Mode{NO,AES,DES3,Blow};
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
    RSA algRSA=null;
    ObjectInputStream inputStream = null;
    ObjectOutputStream outputStream = null;

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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Intent i=getIntent();
        final RSASend algRSAServ=(RSASend)i.getSerializableExtra(TAG);
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
                 sendMess(task);



               }


        });



    }



    private void sendMess(Receiver task){

        sent=et.getText().toString();
        if(!sent.equals("")) {

            try {
                Envelop mess=new Envelop();
                mess.setFrom(userName);
                mess.setText(sent);
                mess.setCripto(Envelop.Mode.NO);
                Log.d(TAG,"ho scritto: "+sent);
                SocketHandler.getOutput().writeObject(mess);
                SocketHandler.getOutput().flush();
                Log.d(TAG,"ho scritto: "+sent);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    private class Receiver extends AsyncTask<Void,Envelop,Void>{


        @Override
        protected Void doInBackground(Void... params) {
            try{

                while(true) {
                    //deve decrittografare il tutto
                   if( (received = (Envelop)SocketHandler.getInput().readObject())!=null) {
                        publishProgress(received);
                   }
                }
            }catch(IOException ioe)
            {
                ioe.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(Envelop... msg){
            tx.append(msg[0].getFrom()+" scrive: ");
            tx.append(msg[0].getText());

       }

    };



}
