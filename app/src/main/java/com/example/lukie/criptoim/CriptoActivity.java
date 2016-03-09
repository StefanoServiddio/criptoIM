package com.example.lukie.criptoim;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stefano.android.Envelop;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;


public class CriptoActivity extends AppCompatActivity {
    ImageView image;
    String TAG = "Cripto";
    EditText ed;
    Button save;
    TextView et;
    Button aesBut;
    Button desBut;
    Button blowBut;
    Button noButt;
    Envelop.Mode mode=Envelop.Mode.NO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cripto);
        ed = (EditText) findViewById(R.id.destination);
        Button to = (Button) findViewById(R.id.to);
        et=(TextView)findViewById(R.id.state);
        save=(Button)findViewById(R.id.save);
        aesBut=(Button)findViewById(R.id.aes);
        desBut=(Button)findViewById(R.id.des);
        blowBut=(Button)findViewById(R.id.blow);
        noButt=(Button)findViewById(R.id.no);


        Intent i=getIntent();
        mode=(Envelop.Mode)i.getSerializableExtra("mode");

        switch (mode){
            case AES:
                aesBut.setText("AES-256 Active");
                break;
            case DES3:
                desBut.setText("DES3-198 Active");
                break;
            case Blow:
                blowBut.setText("Blowfish-128 Active");
                break;
            default:
                break;


        }
       noButt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mode= Envelop.Mode.NO;
               aesBut.setText("AES-256 off");
               desBut.setText("DES3-192 Off");
               blowBut.setText("Blowfish-128 off");
           }
       });
        aesBut.setOnClickListener(new View.OnClickListener() {
       @Override
        public void onClick(View v) {
           mode= Envelop.Mode.AES;
           aesBut.setText("AES-256 Active");
           desBut.setText("DES3-192 Off");
           blowBut.setText("Blowfish-128 off");


        }
   });
        desBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode= Envelop.Mode.DES3;
                desBut.setText("DES3-192 Active");
                blowBut.setText("Blowfish-128 off");
                aesBut.setText("AES-256 off");

            }
        });
        blowBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode= Envelop.Mode.Blow;
                blowBut.setText("Blowfish-128 Active");
                aesBut.setText("AES-256 off");
                desBut.setText("DES3-192 Off");

            }
        });

     save.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent();
             intent.putExtra("to",ed.getText().toString());
             intent.putExtra("mode",mode);
             setResult(RESULT_OK, intent);
             finish();
             et.setText("Saved Complete");
         }
     });

    }

}


