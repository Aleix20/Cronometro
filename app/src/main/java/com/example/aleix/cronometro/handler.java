package com.example.aleix.cronometro;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class handler extends AppCompatActivity {
    Button btnStart, btnContinue, btnReset, btnPost, btnHandler, btnAsync, btnPause;
    TextView txtCrono;
    Boolean sigue = false, pausa = false;
    int contador;
    Integer[] tiempo = new Integer[3];
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);

        contador = 0;

        btnStart = findViewById(R.id.btn_start);
        btnContinue = findViewById(R.id.btn_continue);
        btnReset = findViewById(R.id.btn_reset);



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

                        btnReset.setEnabled(false);
                        btnStart.setEnabled(false);


                sigue = true;
                pausa = false;
                inciarTiempo();
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        while (sigue) {
                            Message msg = new Message();
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
                            tiempo[2] = contador;
                            espera(1000);

                            if (tiempo[2] == 60) {
                                tiempo[1]++;
                                tiempo[2] = 00;
                                contador = 0;
                            } else if (tiempo[1] == 60) {
                                tiempo[0]++;
                                tiempo[1] = 00;
                            }
                            msg.what=1;
                            handler.sendMessage(msg);
                            espera(100);


                    }
                        Message msg = new Message();
                        msg.what=2;
                        handler.sendMessage(msg);

                    }
                });
                th.start();
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage( Message msg){
                switch( msg.what){
                    case 1:
                        txtCrono.setText(String.format("%02d", tiempo[0]) + ":" + String.format("%02d", tiempo[1])
                                + ":" + String.format("%02d", tiempo[2]));
                        break;
                    case 2:
                        txtCrono.setText("00:00:00");
                        break;

                }
            }
        };





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
}
