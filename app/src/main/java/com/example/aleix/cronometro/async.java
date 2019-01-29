package com.example.aleix.cronometro;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.sql.Time;

public class async extends AppCompatActivity {

    Button btnStart, btnContinue, btnReset, btnPost, btnHandler, btnAsync, btnPause;
    TextView txtCrono;
    Boolean sigue = false, pausa = false;
    int contador;
    MiTareaAsincrona tareaAsincrona;
    Integer[] tiempo = new Integer[3];
    public static final String EXTRA_MESSAGE = "com.example.aleix.cronometro.MESSAGE";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        contador = 0;

        btnStart = findViewById(R.id.btn_start);
        btnContinue = findViewById(R.id.btn_continue);
        btnReset = findViewById(R.id.btn_reset);

        btnHandler = findViewById(R.id.btn_handler);

        txtCrono = findViewById(R.id.txt_crono);
        btnPause = findViewById(R.id.btn_pause);


        inciarTiempo();

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausa = true;
                btnReset.setEnabled(true);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (pausa) {
                    pausa.notifyAll();
                }
                contador = 0;
                sigue = false;
                pausa = false;
                btnStart.setEnabled(true);

                //SignalAll
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SignalAll
                synchronized (pausa) {

                    pausa.notifyAll();
                }
                if (sigue) {
                    btnReset.setEnabled(false);
                }

                pausa = false;


            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tareaAsincrona = new MiTareaAsincrona();
                tareaAsincrona.execute(100);
                btnReset.setEnabled(false);
                btnStart.setEnabled(false);
            }
        });

    }

    public void inciarTiempo() {
        for (int i = 0; i < tiempo.length; i++) {
            tiempo[i] = 0;
        }
    }


    static void espera(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private class MiTareaAsincrona extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... integers) {
//se ejecuta en otro hilo
            boolean retorno = true;

            while (sigue){

                if (pausa) {
                    synchronized (pausa) {
                        try {
                            pausa.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                contador++;
                espera(10);
                tiempo[2] = contador;
                espera(10);

                if (tiempo[2] == 100) {
                    tiempo[1]++;
                    tiempo[2] = 00;
                    contador = 0;
                } else if (tiempo[1] == 60) {
                    tiempo[0]++;
                    tiempo[1] = 00;
                }
                publishProgress(tiempo );
                if( isCancelled()){
                    retorno = false;
                    break;
                }
            }

            return retorno;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//se ejecuta en el hilo principal del usuario
            super.onProgressUpdate(values);
            txtCrono.setText(String.format("%02d", tiempo[0]) + ":" + String.format("%02d", tiempo[1])
                    + ":" + String.format("%02d", tiempo[2]));
        }

        @Override
        protected void onPreExecute() {
//se ejecuta en hilo principal antes de iniciar el doInBackGround
            super.onPreExecute();
            txtCrono.setText("00:00:00");
            sigue = true;
            pausa = false;
            inciarTiempo();
            btnStart.setEnabled(false); //que no se pueda pulsar otra vez
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
//se ejecuta en hilo principal cuando acaba el doInBackground
// aBoolean es el valor que devuelve el doInBackground
            super.onPostExecute(aBoolean);
            txtCrono.setText("00:00:00");
            btnStart.setEnabled(true);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText( async.this, "Tarea cancelada", Toast.LENGTH_LONG).show();
            btnStart.setEnabled(true);

        }
    }


}


