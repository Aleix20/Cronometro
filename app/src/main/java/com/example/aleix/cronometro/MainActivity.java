package com.example.aleix.cronometro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Array;
import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnContinue, btnReset, btnPost, btnHandler, btnAsync, btnPause;
    TextView txtCrono;
    Boolean sigue = false, pausa = false;
    int contador;
    Integer[] tiempo = new Integer[3];
    public static final String EXTRA_MESSAGE = "com.example.aleix.cronometro.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contador = 0;

        btnStart = findViewById(R.id.btn_start);
        btnContinue = findViewById(R.id.btn_continue);
        btnReset = findViewById(R.id.btn_reset);

        btnHandler = findViewById(R.id.btn_handler);
        btnAsync = findViewById(R.id.btn_async);
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

        btnHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                intent= new Intent(btnHandler.getContext(), handler.class);
                startActivity(intent);

            }
        });
        btnAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2;
                intent2= new Intent(btnAsync.getContext(), async.class);
                startActivity(intent2);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                btnReset.post(new Runnable() {
                    @Override
                    public void run() {
                        btnReset.setEnabled(false);
                    }
                });
                btnStart.post(new Runnable() {
                    @Override
                    public void run() {
                        btnStart.setEnabled(false);
                    }
                });
                sigue = true;
                pausa = false;
                inciarTiempo();
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        while (sigue) {

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
                            espera(10);

                            if (tiempo[2] == 100) {
                                tiempo[1]++;
                                tiempo[2] = 00;
                                contador = 0;
                            } else if (tiempo[1] == 60) {
                                tiempo[0]++;
                                tiempo[1] = 00;
                            }

                            txtCrono.post(new Runnable() {
                                @Override
                                public void run() {

                                    txtCrono.setText(String.format("%02d", tiempo[0]) + ":" + String.format("%02d", tiempo[1])
                                            + ":" + String.format("%02d", tiempo[2]));


                                }
                            });


                        }

                        txtCrono.post(new Runnable() {
                            @Override
                            public void run() {
                                txtCrono.setText("00:00:00");
                            }
                        });

                    }
                });
                th.start();
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
}
