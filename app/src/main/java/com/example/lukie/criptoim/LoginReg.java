package com.example.lukie.criptoim;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stefano.android.AES;
import com.stefano.android.Blowfish;

import com.stefano.android.Envelop;
import com.stefano.android.HmacSha1;
import com.stefano.android.NewRSA;
import com.stefano.android.TripleDES;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class LoginReg extends AppCompatActivity {

    EditText userClient;
    EditText passwClient;
    EditText nameClient;
    EditText mailClient;
    Button reg;
    Button log;
    String TAG="Cripto Connection";
    NewRSA myRSA;

    SecretKey keyAes;
    SecretKey keyDes;
    SecretKey keyBlow;
    SecretKey keyHmac;


    AES nuovo;
    TripleDES nuovo2;
    Blowfish nuovo3;
    NewRSA algRSAServ;



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
        myRSA=new NewRSA();
        myRSA.generateRsaKeyPair(1024,BigInteger.probablePrime(15,new Random()));
        Intent i=getIntent();
        algRSAServ=(NewRSA) i.getSerializableExtra(TAG);




            //genera tutte le chiavi necessarie per gli agloritmi
        try {
            createKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        //genero algoritmi che userà anche il server



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
                    byte[] dataKey;
                    //fase di registrazione
                    //recupero dell'algoritmo

                    //invia oggetto registrazione User password Nome Mail
                    try {

                        if(!(userClient.getText().toString().equals("")) && !(passwClient.getText().toString().equals(""))
                               && !(nameClient.getText().toString().equals("")) && !(mailClient.getText().toString().equals(""))) {
                            SocketHandler.getOutput().reset();



                            dataKey= algRSAServ.rsaEncrypt("reg".getBytes(),algRSAServ.getPu());
                            SocketHandler.getOutput().writeObject(dataKey);
                            SocketHandler.getOutput().flush();


                            dataKey = algRSAServ.rsaEncrypt(userClient.getText().toString().getBytes(),algRSAServ.getPu());
                            SocketHandler.getOutput().writeObject(dataKey);
                            Log.d(TAG, "invio: " + userClient.getText());
                            SocketHandler.getOutput().flush();

                            dataKey = algRSAServ.rsaEncrypt(passwClient.getText().toString().getBytes(),algRSAServ.getPu());
                            SocketHandler.getOutput().writeObject(dataKey);
                            Log.d(TAG, "invio: " + passwClient.getText());
                            SocketHandler.getOutput().flush();



                            dataKey = algRSAServ.rsaEncrypt(nameClient.getText().toString().getBytes(),algRSAServ.getPu());
                            SocketHandler.getOutput().writeObject(dataKey);
                            Log.d(TAG, "invio: " + nameClient.getText());
                            SocketHandler.getOutput().flush();


                            dataKey = algRSAServ.rsaEncrypt(mailClient.getText().toString().getBytes(),algRSAServ.getPu());
                            SocketHandler.getOutput().writeObject(dataKey);
                            Log.d(TAG, "invio: " + mailClient.getText());
                            SocketHandler.getOutput().flush();




                          //possibilità di ricevere con chiave privata un messaggio di conferma
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
                    } //catch (ClassNotFoundException e) {
                    catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    // e.printStackTrace();     }


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
                   byte[] dataKey;




                    if(!(userClient.getText().toString().equals("")) && !(passwClient.getText().toString().equals(""))) {
                        Log.d(TAG, "invio log");
                        SocketHandler.getOutput().reset();

                        dataKey= algRSAServ.rsaEncrypt("log".getBytes(),algRSAServ.getPu());
                        SocketHandler.getOutput().writeObject(dataKey);
                        SocketHandler.getOutput().flush();


                        dataKey = algRSAServ.rsaEncrypt(userClient.getText().toString().getBytes(),algRSAServ.getPu());
                         String userName=userClient.getText().toString();

                        SocketHandler.getOutput().writeObject(dataKey);
                        Log.d(TAG, "invio: " + userClient.getText());
                        SocketHandler.getOutput().flush();

                        dataKey = algRSAServ.rsaEncrypt(passwClient.getText().toString().getBytes(),algRSAServ.getPu());
                        SocketHandler.getOutput().writeObject(dataKey);
                        Log.d(TAG, "invio: " + passwClient.getText());
                        SocketHandler.getOutput().flush();

                        Log.d(TAG,"Chiave Pubblica Client "+Base64.encodeToString(myRSA.getKPair().getPublic().getEncoded(),Base64.DEFAULT));

                         dataKey=myRSA.getKPair().getPublic().getEncoded();
                        SocketHandler.getOutput().writeObject(dataKey);
                        SocketHandler.getOutput().flush();

                        //Trasmetto tutte le chiavi segrete

                        dataKey = algRSAServ.rsaEncrypt(keyAes.getEncoded(),algRSAServ.getPu());
                        SocketHandler.getOutput().writeObject(dataKey);
                        Log.d(TAG, "invio AES: "+Base64.encodeToString(keyAes.getEncoded(),Base64.DEFAULT));
                        SocketHandler.getOutput().flush();

                        dataKey = algRSAServ.rsaEncrypt(nuovo.getIv(),algRSAServ.getPu());
                        SocketHandler.getOutput().writeObject(dataKey);
                        Log.d(TAG, "invio Iv AES: "+Base64.encodeToString(nuovo.getIv(),Base64.DEFAULT));
                        SocketHandler.getOutput().flush();

                        dataKey = algRSAServ.rsaEncrypt(keyDes.getEncoded(),algRSAServ.getPu());
                        SocketHandler.getOutput().writeObject(dataKey);
                        Log.d(TAG, "invio DES: "+Base64.encodeToString(keyDes.getEncoded(),Base64.DEFAULT));
                        SocketHandler.getOutput().flush();

                        dataKey = algRSAServ.rsaEncrypt(nuovo2.getIv(),algRSAServ.getPu());
                        SocketHandler.getOutput().writeObject(dataKey);
                        Log.d(TAG, "invio Iv DES: "+Base64.encodeToString(nuovo2.getIv(),Base64.DEFAULT));
                        SocketHandler.getOutput().flush();

                        dataKey = algRSAServ.rsaEncrypt(keyBlow.getEncoded(),algRSAServ.getPu());
                        SocketHandler.getOutput().writeObject(dataKey);
                        Log.d(TAG, "invio Blowfish: "+Base64.encodeToString(keyBlow.getEncoded(),Base64.DEFAULT));
                        SocketHandler.getOutput().flush();

                        dataKey = algRSAServ.rsaEncrypt(nuovo3.getIv(),algRSAServ.getPu());
                        SocketHandler.getOutput().writeObject(dataKey);
                        Log.d(TAG, "invio Iv Blow: "+Base64.encodeToString(nuovo3.getIv(),Base64.DEFAULT));
                        SocketHandler.getOutput().flush();

                        dataKey = algRSAServ.rsaEncrypt(keyHmac.getEncoded(),algRSAServ.getPu());
                        SocketHandler.getOutput().writeObject(dataKey);
                        Log.d(TAG, "invio HmacSha1: "+Base64.encodeToString(keyHmac.getEncoded(),Base64.DEFAULT));
                        SocketHandler.getOutput().flush();






                        dataKey = (byte[]) SocketHandler.getInput().readObject();
                        String check=new String(myRSA.rsaDecrypt(dataKey,myRSA.getKPair().getPrivate()));
                        Log.d(TAG, "ricevo: " + check );
                        if(check.equals("OK"))
                        {
                            CharSequence text = "LOGIN Completed";
                            toast = Toast.makeText(context, text, duration);
                            toast.show();
                            Intent i=new Intent(getApplicationContext(),Chat.class);
                            i.putExtra("PuServer",algRSAServ.getPu());
                            i.putExtra("PrKclient",myRSA.getKPair().getPrivate());
                            i.putExtra("AES",keyAes);
                            i.putExtra("AesIv",nuovo.getIv());
                            i.putExtra("DES3",keyDes);
                            i.putExtra("DesIv",nuovo2.getIv());
                            i.putExtra("Blowfish",keyBlow);
                            i.putExtra("BlowIv",nuovo3.getIv());
                            i.putExtra("Hmac",keyHmac);

                            i.putExtra("userName",userName);
                            startActivity(i);

                        }
                        else if (check.equals("NoUser")){
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
        });

    }
    private void createKey() throws NoSuchAlgorithmException, NoSuchPaddingException {



        KeyGenerator kg=KeyGenerator.getInstance("AES");
        kg.init(256);
        keyAes=kg.generateKey();
        nuovo=new AES(keyAes);


        kg=KeyGenerator.getInstance("DESede");
        kg.init(168);
        keyDes=kg.generateKey();
        nuovo2=new TripleDES(keyDes);

        kg=KeyGenerator.getInstance("Blowfish");
        kg.init(128);
        keyBlow=kg.generateKey();
        nuovo3=new Blowfish(keyBlow);
        kg=KeyGenerator.getInstance("HmacSHA1");
        kg.init(160);
        keyHmac=kg.generateKey();


    }
}
