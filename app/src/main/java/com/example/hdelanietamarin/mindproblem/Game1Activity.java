package com.example.hdelanietamarin.mindproblem;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.datatype.Duration;

public class Game1Activity extends AppCompatActivity {
    Button btn_pink; Button btn_red;Button btn_blue;Button btn_green; Button btn_start;
    int actual;
    private static final String  FILENAME_CODE = "user.txt";
    TextView text_level;


    int nivel;
    int secuencia[];
    int secuen_answer[];
    int index =0;
    int index_answ=0;
    int cont =0;
    String code;
    boolean ended=false;
    FirebaseDatabase database;


    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            jugar();
            timerHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

        btn_pink = (Button) findViewById(R.id.btn_pink);
        btn_red = (Button) findViewById(R.id.btn_red);
        btn_blue = (Button) findViewById(R.id.btn_blue);
        btn_green = (Button) findViewById(R.id.btn_green);
        btn_start = (Button) findViewById(R.id.btn_start);
        text_level = (TextView) findViewById(R.id.text_level);


        code = getIntent().getStringExtra("code");

        database = FirebaseDatabase.getInstance();

        final DatabaseReference myRef = database.getReference(code);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(getString(R.string.Colores))){
                    myRef.child(getString(R.string.Colores)).setValue("1");
                    text_level.setText(getString(R.string.Record)+1);
                }else{
                    text_level.setText(getString(R.string.Record)+ dataSnapshot.child(getString(R.string.Colores)).getValue().toString());
                }
                text_level.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        //NEW
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.level_dialog, null);
        alertDialog.setView(view);

        alertDialog.setCancelable(false);

        Button button = view.findViewById(R.id.button2);
        Button button2 = view.findViewById(R.id.button3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nivel =1;
                secuencia = new int[nivel];
                secuen_answer = new int[nivel];
                alertDialog.dismiss();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(getString(R.string.Colores)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String record_actual_s = dataSnapshot.getValue().toString();
                        int record_actual = Integer.parseInt(record_actual_s);
                        if(record_actual>nivel){
                            nivel = record_actual;
                            secuencia= new int[nivel];
                            secuen_answer = new int[nivel];
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                alertDialog.dismiss();
            }
        });


        alertDialog.setTitle(getString(R.string.start_question));

        alertDialog.show();


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerHandler.postDelayed(timerRunnable, 0);
                comenzar();
            }
        });


    }

    private void comenzar() {
        btn_start.setVisibility(View.INVISIBLE);
        btn_blue.setVisibility(View.VISIBLE);
        btn_red.setVisibility(View.VISIBLE);
        btn_green.setVisibility(View.VISIBLE);
        btn_pink.setVisibility(View.VISIBLE);
    }

    public void btnClick(View view){
        Button btn = (Button) view;
        int id = btn.getId();
        switch (id){
            case R.id.btn_blue:
                pintarRespuesta(1);
               break;
            case R.id.btn_green:
                pintarRespuesta(2);
                break;
            case R.id.btn_pink:
                pintarRespuesta(3);
                break;
            case R.id.btn_red:
                pintarRespuesta(4);
                break;
        }
    }

    private void jugar() {
        int rand =0;
       if(cont<secuencia.length){
           do {
               rand = (int) Math.floor(Math.random() * 100);
           } while(actual==getActual(rand));
           changeColor(rand);
           cont ++;
       }else{
       }

        if(cont==secuencia.length){
            ended=true;
            cont++;
        }else if(cont>secuencia.length){
            toBlack();
        }
    }

    private void pintarRespuesta(int bot){
        if(bot!=secuencia[index_answ]){

            final AlertDialog newDialog = new AlertDialog.Builder(this).create();
            LayoutInflater factory = LayoutInflater.from(this);
            final View view_ = factory.inflate(R.layout.dialog_wrong, null);
            newDialog.setView(view_);

            Button button = view_.findViewById(R.id.button2);
            Button button2 = view_.findViewById(R.id.button3);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ended=false;
                    index=0;
                    cont=0;
                    index_answ=0;
                    comenzar();
                    newDialog.dismiss();
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_f = new Intent(Game1Activity.this, InitActivity.class);
                    intent_f.putExtra("user", code);
                    startActivity(intent_f);
                    finish();
                    newDialog.dismiss();
                }
            });

            newDialog.setTitle(getString(R.string.fallado));


            newDialog.setMessage(getString(R.string.action_question));
            newDialog.show();



        }else {
            if (index_answ < secuen_answer.length) {
                secuen_answer[index_answ] = bot;
                changeColor(bot * 25);
                index_answ++;
            }
            if (index_answ == secuen_answer.length) {
                for (int q = 0; q < secuen_answer.length; q++) {
                }

                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(getString(R.string.felicidades));
                alertDialog
                        .setMessage(getString(R.string.acierto));
                alertDialog.setCancelable(false);
                LayoutInflater factory = LayoutInflater.from(this);
                final View view_ = factory.inflate(R.layout.login_dialog, null);
                alertDialog.setView(view_);

                Button button2 = view_.findViewById(R.id.button3);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ended=false;
                        index=0;
                        cont=0;
                        index_answ=0;
                        nivel = nivel +1;
                        secuencia = new int[nivel];
                        secuen_answer = new int[nivel];


                        final DatabaseReference myRef =database.getReference(code).child("Colores");
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String current_s = dataSnapshot.getValue().toString();
                                int record_actual = Integer.parseInt(current_s);
                                if(nivel>record_actual){
                                    myRef.setValue(String.valueOf(nivel-1));
                                    text_level.setText(getString(R.string.Record)+ (nivel-1));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                        comenzar();
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                index_answ++;
            }
        }
    }

    private void toBlack() {
        //btn_blue.setBackgroundColor(0xf0000000);
        btn_blue.setBackground(getDrawable(R.drawable.btn_black));

        btn_red.setBackground(getDrawable(R.drawable.btn_black));
        btn_pink.setBackground(getDrawable(R.drawable.btn_black));
        btn_green.setBackground(getDrawable(R.drawable.btn_black));
    }

    private void changeColor(int rand) {

        if(rand<=25){
            btn_blue.setBackground(getDrawable(R.drawable.btn_blue));
            btn_red.setBackground(getDrawable(R.drawable.btn_black));
            btn_pink.setBackground(getDrawable(R.drawable.btn_black));
            btn_green.setBackground(getDrawable(R.drawable.btn_black));

            if(!ended) {
                actual = 1;

                secuencia[index] = actual;
                index++;
            }



        }else if(rand<=50){
            btn_green.setBackground(getDrawable(R.drawable.btn_green));
            btn_red.setBackground(getDrawable(R.drawable.btn_black));
            btn_pink.setBackground(getDrawable(R.drawable.btn_black));
            btn_blue.setBackground(getDrawable(R.drawable.btn_black));

            if(!ended) {
                actual = 2;

                secuencia[index] = actual;
                index++;
            }

        }else if(rand<=75){
            btn_pink.setBackground(getDrawable(R.drawable.btn_pink));
            btn_red.setBackground(getDrawable(R.drawable.btn_black));
            btn_blue.setBackground(getDrawable(R.drawable.btn_black));
            btn_green.setBackground(getDrawable(R.drawable.btn_black));

            if(!ended) {
                actual = 3;

                secuencia[index] = actual;
                index++;
            }

        }else{
            btn_red.setBackground(getDrawable(R.drawable.btn_red));
            btn_blue.setBackground(getDrawable(R.drawable.btn_black));
            btn_pink.setBackground(getDrawable(R.drawable.btn_black));
            btn_green.setBackground(getDrawable(R.drawable.btn_black));

            if(!ended) {
                actual = 4;

                secuencia[index] = actual;
                index++;
            }

        }
    }

    private int getActual(int rand){
        if(rand<=25){
            return 1;
        }else if(rand<=50){
            return 2;
        }else if(rand <=75){
            return 3;
        }else{
            return 4;
        }
    }

    private void writeCode(){

        try {
            FileOutputStream fos = openFileOutput(FILENAME_CODE, Context.MODE_PRIVATE);

            String line = String.valueOf(code);
            fos.write(line.getBytes());

            fos.close();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, R.string.couldnt_write, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.couldnt_write), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //readCode();
        writeCode();
    }
    @Override
    protected void onStop() {
        super.onStop();
        writeCode();
    }


}
