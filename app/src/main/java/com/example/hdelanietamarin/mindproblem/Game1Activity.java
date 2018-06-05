package com.example.hdelanietamarin.mindproblem;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
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

public class Game1Activity extends AppCompatActivity implements DialogInterface.OnDismissListener,DialogInterface.OnShowListener {
    Button btn_pink; Button btn_red;Button btn_blue;Button btn_green; Button btn_start;
    int actual;
    private static final String  FILENAME_CODE = "user.txt";
    TextView text_level;

    //TODO: AVERIGUAR PORQUÉ TRAS EL GIRO LOS QUE QUEDEN POR MOSTRAR NO SE GUARDAN BIEN

    int nivel;
    int secuencia[];
    int secuen_answer[];
    int index =0;
    int index_answ=0;
    int cont =0;
    int index_prev =-1;
    String code;
    boolean ended=false;
    FirebaseDatabase database;
    boolean visible = false;


    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if(index==index_prev){
                index++;
            }
            jugar();
            if(index==index_prev){
                index++;
            }
            timerHandler.postDelayed(this, 1000);
            if(index==index_prev){
                index++;
            }

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

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            nivel = savedInstanceState.getInt("Level");
            secuencia = savedInstanceState.getIntArray("Secuencia");
            secuen_answer = savedInstanceState.getIntArray("Respuesta");


            ended=savedInstanceState.getBoolean("Ended");
            index= savedInstanceState.getInt("Index");
            cont=savedInstanceState.getInt("Cont");
            index_answ=savedInstanceState.getInt("Index_answer");
            visible = savedInstanceState.getBoolean("Visible");
            index_prev = savedInstanceState.getInt("Index_prev");


            //Log.i("Hugo","Cont: " + String.valueOf(cont));
           // Log.i("Hugo","Index " +index);
           /* if(secuencia!=null) {
                for (int i = 0; i < secuencia.length; i++) {
                    Log.i("Hugo", "Secuencia en " + i + " " + secuencia[i]);
                }

            }*/
            if(!visible) {
                comenzar();
                timerHandler.postDelayed(timerRunnable, 0);

            }

        } else {




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
                    myRef.child("Colores").addListenerForSingleValueEvent(new ValueEventListener() {
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
            alertDialog.setOnShowListener(this);
            alertDialog.setOnDismissListener(this);
            alertDialog.show();
            // Probably initialize members with default values for a new instance
        }


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("Colores")){
                    myRef.child("Colores").setValue("1");
                    text_level.setText(getString(R.string.Record)+1);
                }else{
                    text_level.setText(getString(R.string.Record)+ dataSnapshot.child("Colores").getValue().toString());
                }
                text_level.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerHandler.postDelayed(timerRunnable, 0);
                if(index==index_prev){
                    index++;
                }
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
        btn_blue.setEnabled(false);
        btn_pink.setEnabled(false);
        btn_green.setEnabled(false);
        btn_red.setEnabled(false);
        int rand =0;
       if(cont<secuencia.length){

           do {
               rand = (int) Math.floor(Math.random() * 100);
           } while(actual==getActual(rand));
           changeColor(rand);
           cont ++;
       }else{
           btn_blue.setEnabled(true);
           btn_pink.setEnabled(true);
           btn_green.setEnabled(true);
           btn_red.setEnabled(true);

           /*if(secuencia!=null) {
               for (int i = 0; i < secuencia.length; i++) {
                   //Log.i("Hugo", "Secuencia en " + i + " " + secuencia[i]);
               }

           }*/

          /* for(int i =0; i<secuencia.length;i++){
               Log.i("Hugo", "Secuencia guardada en " +i + " " + secuencia[i]);
           }*/
       }

        if(cont==secuencia.length){
            ended=true;
            cont++;
        }else if(cont>secuencia.length){
            toBlack();
        }
    }

    private void pintarRespuesta(int bot){
        //Log.i("Hugo","Indice respuesta: " + index_answ);
        if(bot!=secuencia[index_answ]){

            final AlertDialog newDialog = new AlertDialog.Builder(this).create();
            LayoutInflater factory = LayoutInflater.from(this);
            final View view_ = factory.inflate(R.layout.dialog_wrong, null);
            newDialog.setView(view_);
            newDialog.setCancelable(false);

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
            newDialog.setOnShowListener(this);
            newDialog.setOnDismissListener(this);
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
                                if(dataSnapshot.getValue()!=null) {
                                    String current_s = dataSnapshot.getValue().toString();
                                    int record_actual = Integer.parseInt(current_s);
                                    if (nivel > record_actual) {
                                        myRef.setValue(String.valueOf(nivel - 1));
                                        text_level.setText(getString(R.string.Record) + (nivel - 1));
                                    }
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
                alertDialog.setOnShowListener(this);
                alertDialog.setOnDismissListener(this);
                alertDialog.show();


                index_answ++;
            }
        }
    }

    private void toBlack() {
        //btn_blue.setBackgroundColor(0xf0000000);
        btn_blue.setBackground(getDrawable(R.drawable.btn_black_b));

        btn_red.setBackground(getDrawable(R.drawable.btn_black_r));
        btn_pink.setBackground(getDrawable(R.drawable.btn_black_p));
        btn_green.setBackground(getDrawable(R.drawable.btn_black_g));
    }

    private void changeColor(int rand) {

        if(rand<=25){
            btn_blue.setBackground(getDrawable(R.drawable.btn_blue));
            btn_red.setBackground(getDrawable(R.drawable.btn_black_r));
            btn_pink.setBackground(getDrawable(R.drawable.btn_black_p));
            btn_green.setBackground(getDrawable(R.drawable.btn_black_g));

            if(!ended) {
                actual = 1;
                /*if(index==index_prev){
                    index++;
                }*/
                secuencia[index] = actual;
              //  index_prev=index;
               // Log.i("Hugo", String.valueOf(index));
                index++;
                //Log.i("Hugo", String.valueOf(index));
            }



        }else if(rand<=50){
            btn_green.setBackground(getDrawable(R.drawable.btn_green));
            btn_red.setBackground(getDrawable(R.drawable.btn_black_r));
            btn_pink.setBackground(getDrawable(R.drawable.btn_black_p));
            btn_blue.setBackground(getDrawable(R.drawable.btn_black_b));

            if(!ended) {
                actual = 2;
                /*if(index==index_prev){
                    index++;
                }*/
                secuencia[index] = actual;
                //index_prev=index;
                //Log.i("Hugo", String.valueOf(index));
                index++;
                //Log.i("Hugo", String.valueOf(index));
            }

        }else if(rand<=75){
            btn_pink.setBackground(getDrawable(R.drawable.btn_pink));
            btn_red.setBackground(getDrawable(R.drawable.btn_black_r));
            btn_blue.setBackground(getDrawable(R.drawable.btn_black_b));
            btn_green.setBackground(getDrawable(R.drawable.btn_black_g));

            if(!ended) {
                actual = 3;
                /*if(index==index_prev){
                    index++;
                }*/
                secuencia[index] = actual;
               // index_prev=index;
               // Log.i("Hugo", String.valueOf(index));
                index++;
                //Log.i("Hugo", String.valueOf(index));
            }

        }else{
            btn_red.setBackground(getDrawable(R.drawable.btn_red));
            btn_blue.setBackground(getDrawable(R.drawable.btn_black_b));
            btn_pink.setBackground(getDrawable(R.drawable.btn_black_p));
            btn_green.setBackground(getDrawable(R.drawable.btn_black_g));

            if(!ended) {
                actual = 4;

               /* if(index==index_prev){
                    index++;
                }*/
                secuencia[index] = actual;
                //index_prev=index;
                //Log.i("Hugo", String.valueOf(index));
                index++;
               // Log.i("Hugo", String.valueOf(index));
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
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt("Level", nivel);
        savedInstanceState.putIntArray("Secuencia", secuencia);
        savedInstanceState.putIntArray("Respuesta", secuen_answer);
        savedInstanceState.putBoolean("Ended", ended);
        savedInstanceState.putInt("Index", index);
        savedInstanceState.putInt("Cont", cont);
        savedInstanceState.putInt("Index_answer", index_answ);
        savedInstanceState.putInt("Index_prev", index_prev);
        savedInstanceState.putBoolean("Visible", btn_start.getVisibility()==View.VISIBLE);

        /*try{
            timerRunnable.wait();
        } catch (Exception e){

        }*/


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        final int screenOrientation = ((WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (screenOrientation){
            case Surface.ROTATION_180:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_270:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case Surface.ROTATION_0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Surface.ROTATION_90:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }



    public int findCero(int[] secuencia){
        int c = 0;
        for(int i=0; i<secuencia.length; i++){
            if(secuencia[i]==0){
                c =i;
                i = secuencia.length;
            }
        }
        return c;
    }
}
