package com.example.lukie.criptoim;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.content.Intent;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import com.stefano.android.*;


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
    Envelop.Mode crittoState=Envelop.Mode.NO;
    NewRSA myRSA=null;
    NewRSA algRSAServ=null;

    PublicKey keyPuServer;
    SecretKey keyAes;
    byte[] aesIv;
    SecretKey keyDes;
    SecretKey keyBlow;
    SecretKey keyHmac;

    AES algAES;
    TripleDES algDes;
    Blowfish algBlow;
    HmacSha1 algHMAC;



    ObjectInputStream inputStream = null;
    ObjectOutputStream outputStream = null;

    String to;


    int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = (Button) findViewById(R.id.button2);
        bt2 = (Button) findViewById(R.id.button);

        tx = (TextView) findViewById(R.id.textView);
        et = (EditText) findViewById(R.id.editText);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        tx.setMovementMethod(new ScrollingMovementMethod());

        final Receiver task = new Receiver();
        task.execute();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Intent i = getIntent();

        keyPuServer = (PublicKey) i.getSerializableExtra("PuServer");
        Log.d(TAG, "Chiave pubblica del Server" + Base64.encodeToString(keyPuServer.getEncoded(), Base64.DEFAULT));
        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("userName");
        keyAes = (SecretKey) i.getSerializableExtra("AES");
        aesIv = i.getByteArrayExtra("AesIv");
        keyDes = (SecretKey) i.getSerializableExtra("DES3");
        keyBlow = (SecretKey) i.getSerializableExtra("Blowfish");
        keyHmac = (SecretKey) i.getSerializableExtra("Hmac");
        Log.d(TAG, "il mio nome è " + userName);
        Log.d(TAG, "chiave AES:  " + Base64.encodeToString(keyAes.getEncoded(), Base64.DEFAULT));


        try {


            //genero algoritmi che userà anche il server
            algRSAServ = new NewRSA();
            algRSAServ.setKPu(keyPuServer);
            //controllo chiave Pubblica del Server


            algAES = new AES(keyAes,aesIv);
            algDes = new TripleDES(keyDes);
            algBlow = new Blowfish(keyBlow);
            algHMAC = new HmacSha1(keyHmac);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }


        bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent cripto = new Intent(Chat.this, CriptoActivity.class);
                cripto.putExtra("mode",crittoState);
                startActivityForResult(cripto, 1);
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMess();


            }


        });
    }
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub

            if (requestCode == 1) {
                if(resultCode == Chat.RESULT_OK){
                    String result=data.getStringExtra("to");
                    Envelop.Mode newcritto=(Envelop.Mode)data.getSerializableExtra("mode");
                    Log.d(TAG,"ho ricevuto da cripto "+result);
                    Log.d(TAG,"Cripto Mode "+newcritto);
                    if(newcritto!=crittoState)
                    {

                        sendMessChangeCrypto(newcritto);
                    }

                }
                if (resultCode == Chat.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }



    }


    private void sendMessChangeCrypto(Envelop.Mode newcritto){
        //devi mandare un messaggio di modifica sulla crittazione
        Envelop mess = new Envelop();
        mess.setFrom(userName);
        mess.setTo("");
        mess.setText("Change Crypto");
        Log.d(TAG, "ho scritto: "+mess.getText()+" in "+newcritto);

        try {

            switch (crittoState) {

                case AES:


                   // mess.setMac(algHMAC.hashing(mess.getText().getBytes()));
                    //modalità di criptazione da acquisire dall'activity crypto
                    mess.setCripto(newcritto);

                    //conversione in byte
                    byte[] data = mess.convEnvByte(mess);

                    //Scegli il tipo di Criptazione
                    Log.d("TAG", "Modalità di crittazione inviata: " + crittoState);
                    data = algAES.encrypt(data);
                    SocketHandler.getOutput().writeLong(data.length);
                    SocketHandler.getOutput().writeObject(data);
                    SocketHandler.getOutput().flush();
                    crittoState=newcritto;

                    break;

                case DES3:

                   // mess.setMac(algHMAC.hashing(mess.getText().getBytes()));
                    //modalità di criptazione da acquisire dall'activity crypto
                    mess.setCripto(newcritto);

                    //conversione in byte

                    data = mess.convEnvByte(mess);

                    //Scegli il tipo di Criptazione
                    Log.d("TAG", "Modalità di crittazione inviata: " + crittoState);
                    data = algDes.encrypt(data);
                    SocketHandler.getOutput().writeLong(data.length);
                    SocketHandler.getOutput().writeObject(data);
                    SocketHandler.getOutput().flush();
                    crittoState=newcritto;
                    break;

                case Blow:

                   // mess.setMac(algHMAC.hashing(mess.getText().getBytes()));
                    //modalità di criptazione da acquisire dall'activity crypto
                    mess.setCripto(newcritto);

                    //conversione in byte

                    data = mess.convEnvByte(mess);

                    //Scegli il tipo di Criptazione
                    Log.d("TAG", "Modalità di crittazione inviata: " + crittoState);
                    data = algBlow.encrypt(data);
                    SocketHandler.getOutput().writeLong(data.length);
                    SocketHandler.getOutput().writeObject(data);
                    SocketHandler.getOutput().flush();
                    crittoState=newcritto;
                    break;

                case NO:

                    //mess.setMac(algHMAC.hashing(mess.getText().getBytes()));
                    //modalità di criptazione da acquisire dall'activity crypto
                    mess.setCripto(newcritto);
                    data = mess.convEnvByte(mess);
                    Log.d(TAG, "ho scritto: " + sent);
                    SocketHandler.getOutput().writeLong(data.length);
                    SocketHandler.getOutput().writeObject(data);
                    SocketHandler.getOutput().flush();
                    crittoState=newcritto;


                    break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void sendMess(){

        sent=et.getText().toString();
        Envelop mess = new Envelop();

        if(!sent.equals("")) {

            try {

                switch (crittoState) {

                    case AES:

                        mess.setFrom(userName);
                        mess.setText(sent);


                        byte[] digest=algHMAC.hashing(mess.getText().getBytes());
                     //   controllo sul digest
                        Log.d(TAG,"lunghezza Dati Digest: "+digest.length+" byte"+"\n Digest inviato: "
                                +Base64.encodeToString(digest,Base64.DEFAULT));


                       mess.setMac(algRSAServ.rsaEncrypt(digest,algRSAServ.getPu()));
                        //modalità di criptazione da acquisire dall'activity crypto
                        mess.setMac(mess.getMac());
                        mess.setCripto(crittoState);

                        //conversione in byte

                        byte[] data = mess.convEnvByte(mess);

                        //Scegli il tipo di Criptazione
                        Log.d("TAG", "Modalità di crittazione inviata: " + crittoState);

                        Log.d(TAG,"lunghezza Dati"+data.length+"\n Dati inviati: "+Base64.encodeToString(data,Base64.DEFAULT));

                        data=algAES.encrypt(data);
                        Log.d(TAG,"lunghezza Dati Criptati: "+data.length);
                        Log.d(TAG,"Dati AES Criptati: "+Base64.encodeToString(data,Base64.DEFAULT));
                        SocketHandler.getOutput().writeLong(data.length);
                        SocketHandler.getOutput().writeObject(data);
                        SocketHandler.getOutput().flush();
                        Log.d(TAG, "ho scritto AES: " + sent);
                        break;

                    case DES3:

                        mess.setFrom(userName);
                        mess.setText(sent);
                        //mess.setMac(algHMAC.hashing(mess.getText().getBytes()));
                        //modalità di criptazione da acquisire dall'activity crypto
                        mess.setCripto(crittoState);
                        Log.d(TAG, "ho scritto DES: " + sent);
                        //conversione in byte

                         data = mess.convEnvByte(mess);

                        //Scegli il tipo di Criptazione
                        Log.d("TAG", "Modalità di crittazione inviata: " + crittoState);
                        data=algDes.encrypt(data);
                        SocketHandler.getOutput().writeLong(data.length);
                        SocketHandler.getOutput().writeObject(data);
                        SocketHandler.getOutput().flush();
                        break;

                    case Blow:
                        mess.setFrom(userName);
                        mess.setText(sent);
                       // mess.setMac(algHMAC.hashing(mess.getText().getBytes()));
                        //modalità di criptazione da acquisire dall'activity crypto
                        mess.setCripto(crittoState);
                        Log.d(TAG, "ho scritto Blow: " + sent);
                        //conversione in byte

                        data = mess.convEnvByte(mess);

                        //Scegli il tipo di Criptazione
                        Log.d("TAG", "Modalità di crittazione inviata: " + crittoState);
                        data=algBlow.encrypt(data);
                        SocketHandler.getOutput().writeLong(data.length);
                        SocketHandler.getOutput().writeObject(data);
                        SocketHandler.getOutput().flush();


                        break;



                    default:

                    mess.setFrom(userName);
                    mess.setText(sent);
                    //mess.setMac(algHMAC.hashing(mess.getText().getBytes()));
                    //modalità di criptazione da acquisire dall'activity crypto

                    mess.setCripto(crittoState);
                    Log.d(TAG, "ho scritto NO Crypting: " + sent);
                    //conversione in byte

                    data = mess.convEnvByte(mess);

                    //Scegli il tipo di Criptazione
                    Log.d("TAG", "Modalità di crittazione inviata: " + crittoState);
                    SocketHandler.getOutput().writeLong(data.length);
                    SocketHandler.getOutput().writeObject(data);
                    SocketHandler.getOutput().flush();
                    break;
                }





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

                       switch (crittoState)
                       {
                           case AES:

                               Log.d(TAG,"Dati AES Criptati: "+Base64.encodeToString(dataRec,Base64.DEFAULT)+"\nlunghezza: "
                               +dataRec.length);
                               dataRec=algAES.decrypt(dataRec);
                               Log.d(TAG,"decriptati AES");
                               break;
                           case DES3:
                               dataRec=algDes.decrypt(dataRec);
                               break;
                           case Blow:
                               dataRec=algBlow.decrypt(dataRec);
                               break;
                           default:
                               break;

                       }



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
