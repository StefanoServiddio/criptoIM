package com.example.lukie.criptoim;

import android.util.Base64;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

/**
 * Created by lukie on 17/02/2016.
 */
public class RSASend implements Serializable{



        //scegli due primi p e q
        private BigInteger n;

        private BigInteger e;


        public RSASend(){

            this.n=null;

            this.e=null;


        }
    public void setPuKey(BigInteger[] v){
        this.e=v[0];
        this.n=v[1];


    }

    public BigInteger getE(){

        return this.e;
    }
    public BigInteger getN(){

        return this.n;
    }


    public String encryptPu(String s) throws UnsupportedEncodingException
    {
        //si cripta trovando c:=plaintext^e mod n;
        BigInteger c=new BigInteger(s.getBytes());
        c=c.modPow(this.e, this.n);
        String encr = Base64.encodeToString(c.toByteArray(), Base64.DEFAULT);
        return encr;
    }



    public String decryptPu(String s) throws UnsupportedEncodingException, IOException
    {
        byte[] decod = Base64.decode(s.getBytes(), Base64.DEFAULT);
        BigInteger crypto=new BigInteger(decod);
        crypto=crypto.modPow(this.e,this.n);
        String plaintext=new String(crypto.toByteArray());
        return plaintext;
    }
    public byte[] encryptPuByte(byte[] s) throws UnsupportedEncodingException
    {
        //si cripta trovando c:=plaintext^e mod n;
        BigInteger c=new BigInteger(s);
        c=c.modPow(this.e, this.n);
        return c.toByteArray();
    }

    public byte[] decryptPuByte(byte[] decod) throws UnsupportedEncodingException, IOException
    {


        BigInteger crypto=new BigInteger(decod);
        crypto=crypto.modPow(this.e,this.n);

        return crypto.toByteArray();
    }

}
