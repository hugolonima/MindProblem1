package com.example.hdelanietamarin.mindproblem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Random;

public class Game2Activity extends AppCompatActivity {
    TextView text_number, text_level; EditText edit_answer;
    int cant;
    Button btn_ready;  Button btn_check;
    private static final String  FILENAME_CODE = "user.txt";
    String code;
    FirebaseDatabase database;
    String number;
    Boolean visible = true;
    Boolean responder_visible = false;


    int contador;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            if (contador == 0) {
                jugar();
                if(((number!=null))&&(visible==false)) {
                    text_number.setText(String.valueOf(number));
                    number = null;
                }
                btn_ready.setVisibility(View.INVISIBLE);
                timerHandler.postDelayed(this, 3000);
                contador++;
            }else{
                text_number.setVisibility(View.INVISIBLE);
                edit_answer.setVisibility(View.VISIBLE);
                btn_check.setVisibility(View.VISIBLE);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);
        text_number = (TextView) findViewById(R.id.text_number);
        edit_answer = (EditText) findViewById(R.id.edit_answer);
        btn_ready = (Button) findViewById(R.id.btn_ready);
        btn_check = (Button) findViewById(R.id.btn_check);
        text_level = (TextView) findViewById(R.id.text_level);
        database = FirebaseDatabase.getInstance();
        code = getIntent().getStringExtra("code");


        final DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child(String.valueOf(code));
        edit_answer.setOnFocusChangeListener(new View.OnFocusChangeListener() {//detecta si se pulsa fuera del textview. también hace falta añadir focusable y clickable en el xml
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        if (savedInstanceState != null) {
            // Restore value of members from saved state
            cant = savedInstanceState.getInt("Level");
            number = savedInstanceState.getString("Number");
            visible = savedInstanceState.getBoolean("Play");
            responder_visible = savedInstanceState.getBoolean("Visible");
            if(!visible) {
                if ((number != null)&&(!responder_visible)) {
                    if (!number.equals("")) {
                        text_number.setVisibility(View.VISIBLE);
                        edit_answer.setVisibility(View.INVISIBLE);
                        btn_ready.setVisibility(View.INVISIBLE);
                        btn_check.setVisibility(View.INVISIBLE);
                        jugar();
                        timerHandler.postDelayed(timerRunnable, 0);
                    }

                }else if(number!=null){
                    text_number.setVisibility(View.INVISIBLE);
                    edit_answer.setVisibility(View.VISIBLE);
                    btn_ready.setVisibility(View.INVISIBLE);
                    btn_check.setVisibility(View.VISIBLE);
                    text_number.setText(String.valueOf(number));
                    number=null;
                }
            }
        } else {




            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.start_question));

            alertDialog.setCancelable(false);
            LayoutInflater factory = LayoutInflater.from(this);
            final View view = factory.inflate(R.layout.level_dialog, null);
            alertDialog.setView(view);


            Button button = view.findViewById(R.id.button2);
            Button button2 = view.findViewById(R.id.button3);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cant =1;
                    alertDialog.dismiss();
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dref.child("Números").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String record_actual_s = dataSnapshot.getValue().toString();
                            int record_actual = Integer.parseInt(record_actual_s);
                            if(record_actual>cant){
                                cant = record_actual;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    alertDialog.dismiss();
                }
            });


            alertDialog.show();



            // Probably initialize members with default values for a new instance
        }


        dref.child("Números");//.setValue("2");
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("Números")){
                    dref.child("Números").setValue("1");
                    text_level.setText(getString(R.string.Record)+ 1);
                }else{
                    text_level.setText(getString(R.string.Record)+ dataSnapshot.child("Números").getValue().toString());
                }
                text_level.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jugar();
                text_number.setVisibility(View.VISIBLE);
                timerHandler.postDelayed(timerRunnable, 0);

            }
        });

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(v);
            }
        });

    }

    private void check(View v) {
        String num_1 = edit_answer.getText().toString();
        if (!num_1.equals("")) {
            String num_2 = text_number.getText().toString();
            contador = 0;

            int num1s = Integer.parseInt(num_1);
            int num2s = Integer.parseInt(num_2);

            int res = num1s - num2s;

            edit_answer.setText("");

            if (res == 0) {
                cant = cant + 1;
                if (cant > 15) {
                    timerHandler.postDelayed(timerRunnable, 1500);
                } else if (cant > 10) {
                    timerHandler.postDelayed(timerRunnable, 2000);
                } else if (cant > 5) {
                    timerHandler.postDelayed(timerRunnable, 2500);
                }
                text_number.setVisibility(View.VISIBLE);
                edit_answer.setVisibility(View.INVISIBLE);
                btn_ready.setVisibility(View.VISIBLE);
                btn_check.setVisibility(View.INVISIBLE);
                text_number.setText("");



                final DatabaseReference myRef = database.getReference(code).child("Números");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String current_s = dataSnapshot.getValue().toString();
                        int record_actual = Integer.parseInt(current_s);
                        if(cant>record_actual){
                            myRef.setValue(String.valueOf(cant-1));
                            text_level.setText(getString(R.string.Record)+ (cant-1));
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle(getString(R.string.felicidades));
                alertDialog
                        .setMessage(getString(R.string.acierto));
                alertDialog.setCancelable(false);
                LayoutInflater factory = LayoutInflater.from(v.getContext());
                final View view_ = factory.inflate(R.layout.login_dialog, null);
                alertDialog.setView(view_);

                Button button2 = view_.findViewById(R.id.button3);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                //Toast.makeText(this, "¡Has acertado!", Toast.LENGTH_LONG).show();
            } else {

                final AlertDialog newDialog = new AlertDialog.Builder(this).create();
                LayoutInflater factory = LayoutInflater.from(this);
                final View view_ = factory.inflate(R.layout.dialog_wrong, null);
                newDialog.setView(view_);

                Button button = view_.findViewById(R.id.button2);
                Button button2 = view_.findViewById(R.id.button3);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        text_number.setVisibility(View.VISIBLE);
                        edit_answer.setVisibility(View.INVISIBLE);
                        btn_ready.setVisibility(View.VISIBLE);
                        btn_check.setVisibility(View.INVISIBLE);
                        text_number.setText("");
                        newDialog.dismiss();
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_f = new Intent(Game2Activity.this, InitActivity.class);
                        startActivity(intent_f);
                        finish();
                        newDialog.dismiss();
                    }
                });

                newDialog.setTitle(getString(R.string.fallado));


                newDialog.setMessage(getString(R.string.action_question));
                newDialog.show();

            }
        } else{
            Toast.makeText(this, R.string.insert_valid, Toast.LENGTH_LONG).show();
        }
    }

    private void jugar() {
        String new_number= "";
        new_number=generarNumber(cant);
        text_number.setText(new_number);
    }

    private String generarNumber(int cant){
        int n = 0;
        String num = "";
        for(int i=1; i<=cant; i++){
            Random r = new Random();
            int i1 = r.nextInt(10 - 0);
            if((i==1)&&(i1==0)){
                while(i1==0){
                    i1 = r.nextInt(10 - 0);
                }
            }
            num = num.concat(String.valueOf(i1));
        }
        return num;
    }


    private void writeCode(){

        try {
            FileOutputStream fos = openFileOutput(FILENAME_CODE, Context.MODE_PRIVATE);

            String line = String.valueOf(code);
            fos.write(line.getBytes());

            fos.close();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.couldnt_write), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.couldnt_write), Toast.LENGTH_SHORT).show();
        }

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
        savedInstanceState.putBoolean("Play", btn_ready.getVisibility()==View.VISIBLE);
        savedInstanceState.putInt("Level", cant);
        savedInstanceState.putString("Number", text_number.getText().toString());
        //Con esto mandamos si el botón de responder está visible
        Log.i("Hugo", String.valueOf(edit_answer.getVisibility()==View.VISIBLE));
        savedInstanceState.putBoolean("Visible", edit_answer.getVisibility()==View.VISIBLE);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
