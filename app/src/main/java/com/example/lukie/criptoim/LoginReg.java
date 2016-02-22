package com.example.lukie.criptoim;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;

public class LoginReg extends AppCompatActivity {

    EditText userClient;
    EditText passwClient;
    EditText nameClient;
    EditText mailClient;
    Button reg;
    Button log;
    String TAG="Cripto Connection";
    RSA myRSA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_reg);
        nameClient=(EditText)findViewById(R.id.name);
        mailClient=(EditText)findViewById(R.id.mail);
        userClient=(EditText)findViewById(R.id.user_name);
        passwClient=(EditText)findViewById(R.id.password);

        log=(Button)findViewById(R.id.login);
        reg=(Button)findViewById(R.id.sign_up);
        myRSA=new RSA();
        Intent i=getIntent();
        final RSASend algRSAServ=(RSASend)i.getSerializableExtra(TAG);
        Log.d(TAG,algRSAServ.getE().toString());


        nameClient.setVisibility(View.INVISIBLE);
        mailClient.setVisibility(View.INVISIBLE);



        reg.setOnClickListener(new View.OnClickListener() {
            boolean gone=true;
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                 int duration = Toast.LENGTH_SHORT;
                Toast toast;


                if (gone) {
                    nameClient.setVisibility(View.VISIBLE);
                    mailClient.setVisibility(View.VISIBLE);
                    gone=false;
                } else {
                     String s;

                    //fase di registrazione
                    //recupero dell'algoritmo

                    //invia oggetto registrazione User password Nome Mail
                    try {

                        if(!(userClient.getText().toString().equals("")) && !(passwClient.getText().toString().equals(""))
                               && !(nameClient.getText().toString().equals("")) && !(mailClient.getText().toString().equals(""))) {
                            SocketHandler.getOutput().reset();

                            s = algRSAServ.encryptPu("reg");
                            SocketHandler.getOutput().writeObject(s);


                            s = algRSAServ.encryptPu(userClient.getText().toString());
                            SocketHandler.getOutput().flush();
                            SocketHandler.getOutput().writeObject(s);
                            Log.d(TAG, "invio: " + userClient.getText());
                            SocketHandler.getOutput().flush();
                            s = algRSAServ.encryptPu(userClient.getText().toString());
                            SocketHandler.getOutput().writeObject(s);
                            Log.d(TAG, "invio: " + passwClient.getText());
                            SocketHandler.getOutput().flush();
                            s = algRSAServ.encryptPu(nameClient.getText().toString());
                            SocketHandler.getOutput().writeObject(s);
                            Log.d(TAG, "invio: " + nameClient.getText());
                            SocketHandler.getOutput().flush();
                            s = algRSAServ.encryptPu(mailClient.getText().toString());
                            SocketHandler.getOutput().writeObject(s);
                            Log.d(TAG, "invio: " + mailClient.getText());
                            SocketHandler.getOutput().flush();


                            //inputStream = new ObjectInputStream(SocketHandler.getSocket().getInputStream());
                            String check = (String) SocketHandler.getInput().readObject();
                            Log.d(TAG, "ricevo: " + check);
                            if(check.equals("ERROR")){
                                Log.d(TAG,"Registrazione non riuscita");

                                CharSequence text = "Registration ERROR";
                                toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                            else{
                                Log.d(TAG,"Registrato con successo");

                                CharSequence text = "Successfully Registered!";
                                toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }

                            //sei appena stato registrato
                        }
                        else{
                            if(userClient.getText().toString().equals("")){
                                CharSequence text = "Insert User Name!";
                                toast = Toast.makeText(context, text, duration);
                                toast.show();

                            }
                            if(passwClient.getText().toString().equals("")){
                                CharSequence text = "Insert Password";
                                toast = Toast.makeText(context, text, duration);
                                toast.show();

                            }
                            if(nameClient.getText().toString().equals("")){
                                CharSequence text = "Insert Name";
                                toast = Toast.makeText(context, text, duration);
                                toast.show();

                            }
                            if(mailClient.getText().toString().equals("")){
                                CharSequence text = "Insert Mail";
                                toast = Toast.makeText(context, text, duration);
                                toast.show();

                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Invio un Oggetto Login User e Passwrod il server controlla
                //avvia Intent
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast;


                try {
                    String s;

                    if(!(userClient.getText().toString().equals("")) && !(passwClient.getText().toString().equals(""))) {
                        Log.d(TAG, "invio log");
                        SocketHandler.getOutput().reset();

                        s = algRSAServ.encryptPu("log");
                        SocketHandler.getOutput().writeObject(s);
                        SocketHandler.getOutput().flush();
                        s = algRSAServ.encryptPu(userClient.getText().toString());
                         String userName=userClient.getText().toString();

                        SocketHandler.getOutput().writeObject(s);
                        Log.d(TAG, "invio: " + userClient.getText());
                        SocketHandler.getOutput().flush();
                        s = algRSAServ.encryptPu(userClient.getText().toString());
                        SocketHandler.getOutput().writeObject(s);
                        Log.d(TAG, "invio: " + passwClient.getText());
                        SocketHandler.getOutput().flush();

                        Log.d(TAG,"chiave mia E "+myRSA.getPuKey()[0].toString());
                        Log.d(TAG,"chiave mia N"+myRSA.getPuKey()[1].toString());
                        BigInteger[] Pukey=myRSA.getPuKey();
                        SocketHandler.getOutput().writeObject(Pukey);
                        SocketHandler.getOutput().flush();
                        String check = (String) SocketHandler.getInput().readObject();
                        check=algRSAServ.decryptPu(check);
                        Log.d(TAG, "ricevo: " + check );
                        if(check.equals("OK"))
                        {
                            CharSequence text = "LOGIN Completed";
                            toast = Toast.makeText(context, text, duration);
                            toast.show();
                            Intent i=new Intent(getApplicationContext(),Chat.class);
                            i.putExtra(TAG,algRSAServ);
                            i.putExtra("userName",userName);
                            startActivity(i);

                        }
                        else {
                            CharSequence text = "LOGIN ERROR!";
                            toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }

                    }
                    else
                    {
                        if(userClient.getText().toString().equals("")){
                            CharSequence text = "Insert User Name!";
                            toast = Toast.makeText(context, text, duration);
                            toast.show();

                        }
                        if(passwClient.getText().toString().equals("")){
                            CharSequence text = "Insert Password";
                            toast = Toast.makeText(context, text, duration);
                            toast.show();

                        }

                    }

                    //manda chiave pubblica
                    //passa in chat

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });


    }
}
