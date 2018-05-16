package com.example.hdelanietamarin.mindproblem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Year;
import java.util.Calendar;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    EditText user_text, day_text, month_text, year_text;
    TextView text_date;
    Button btn_enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_text = (EditText) findViewById(R.id.user_text);
        btn_enter = (Button) findViewById(R.id.btn_enter);
        text_date= (TextView) findViewById(R.id.textView3);
        day_text = (EditText) findViewById(R.id.day_text);
        month_text = (EditText) findViewById(R.id.month_text);
        year_text = (EditText) findViewById(R.id.year_text);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                hideKeyboard(v);
                final String user = user_text.getText().toString();
                //final String password = password_text.getText().toString();
                final DatabaseReference dref = database.getReference();

                dref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (text_date.getVisibility() == View.INVISIBLE){
                            if ((!user.equals("")) && (!dataSnapshot.hasChild(String.valueOf(user)))) {

                                year_text.setVisibility(View.VISIBLE);
                                day_text.setVisibility(View.VISIBLE);
                                month_text.setVisibility(View.VISIBLE);


                                final DatabaseReference myRef = database.getReference(user);
                                //myRef.child("Password").setValue(password);
                                if ((!year_text.getText().toString().equals("")) &&
                                        (!day_text.getText().toString().equals("")) &&
                                        (!month_text.getText().toString().equals(""))) {




                                    //TODO: METER AQUÍ LA COMPROBACIÓN DE LA FECHA


                                    Boolean fecha_correcta = false;
                                    String dia = day_text.getText().toString();
                                    String mes = month_text.getText().toString();
                                    String any = year_text.getText().toString();


                                    //FUNCIONA--> HAY QUE AVERIGUAR CON ESTO SI ES O NO UN NÚMERO
                                    Calendar calendar = Calendar.getInstance();
                                    int year = calendar.get(Calendar.YEAR);
                                    int month_calendar = calendar.get(Calendar.MONTH);
                                    int day_calendar = calendar.get(Calendar.DAY_OF_MONTH);


                                    try {
                                        int num = Integer.parseInt(dia);
                                        int num_1 = Integer.parseInt(mes);
                                        int num_2 = Integer.parseInt(any);


                                        Log.i("Hugo", "num: " + num);
                                        Log.i("Hugo", "num1: " + num_1);
                                        Log.i("Hugo", "num2: " + num_2);

                                        Log.i("Hugo", "year: " + year);
                                        Log.i("Hugo", "month: " + month_calendar);
                                        Log.i("Hugo", "day: " + day_calendar);
                                        if((num>31)||(num_1>12)){
                                            fecha_correcta=false;
                                        }else{
                                        if(Integer.parseInt(any)>year){
                                            fecha_correcta = false;
                                        }else {
                                            if (Integer.parseInt(any) == year) {
                                                if (Integer.parseInt(mes) > month_calendar) {
                                                    fecha_correcta = false;
                                                } else {
                                                    if (Integer.parseInt(mes) == month_calendar) {
                                                        if (Integer.parseInt(dia) > day_calendar) {
                                                            fecha_correcta = false;
                                                        } else {
                                                            if (Integer.parseInt(dia) == day_calendar) {
                                                                fecha_correcta = false;
                                                            } else {
                                                                fecha_correcta = true;
                                                            }
                                                        }
                                                    } else {
                                                        fecha_correcta = true;
                                                    }
                                                }
                                            } else {
                                                fecha_correcta = true;
                                            }
                                            }
                                        }

                                    } catch (NumberFormatException e) {

                                    }


                                    if(fecha_correcta) {
                                        myRef.child("Fecha").setValue(getDate());
                                        Intent intent = new Intent(LoginActivity.this, InitActivity.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);
                                    }else {
                                        final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                                        alertDialog.setTitle(getString(R.string.entry_error));
                                        alertDialog
                                                .setMessage("Parece que la fecha introducida no es correcta, vuelve a probar sin caracteres especiales y con una fecha real");
                                        alertDialog.setCancelable(false);
                                        LayoutInflater factory = LayoutInflater.from(v.getContext());
                                        final View view_ = factory.inflate(R.layout.login_dialog, null);
                                        alertDialog.setView(view_);

                                        Button button2 = view_.findViewById(R.id.button3);
                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialog.dismiss();
                                                year_text.setText("");
                                                month_text.setText("");
                                                day_text.setText("");
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                } else {
                                    final AlertDialog falertDialog = new AlertDialog.Builder(v.getContext()).create();
                                    falertDialog.setTitle(getString(R.string.fill_all));
                                    falertDialog
                                            .setMessage(getString(R.string.introduce_fecha));
                                    falertDialog.setCancelable(false);
                                    LayoutInflater factory = LayoutInflater.from(v.getContext());
                                    final View view_ = factory.inflate(R.layout.login_dialog, null);
                                    falertDialog.setView(view_);

                                    Button button2 = view_.findViewById(R.id.button3);
                                    button2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            falertDialog.dismiss();
                                        }
                                    });

                                    falertDialog.show();
                                }
                            } else if (!user.equals("")) {

                                final AlertDialog alertDialog_ = new AlertDialog.Builder(v.getContext()).create();
                                alertDialog_.setTitle(getString(R.string.exist));
                                alertDialog_
                                        .setMessage(getString(R.string.entry_question));
                                alertDialog_.setCancelable(false);
                                LayoutInflater factory_ = LayoutInflater.from(v.getContext());
                                final View view_1 = factory_.inflate(R.layout.start_new_dialog, null);
                                alertDialog_.setView(view_1);

                                Button button_yes = view_1.findViewById(R.id.button2);
                                button_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        text_date.setVisibility(View.VISIBLE);
                                        year_text.setVisibility(View.VISIBLE);
                                        day_text.setVisibility(View.VISIBLE);
                                        month_text.setVisibility(View.VISIBLE);
                                        alertDialog_.dismiss();
                                    }
                                });
                                Button button_no = view_1.findViewById(R.id.button3);
                                button_no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                                        alertDialog.setTitle(getString(R.string.entry_error));
                                        alertDialog
                                                .setMessage(getString(R.string.choose_another));
                                        alertDialog.setCancelable(false);
                                        LayoutInflater factory = LayoutInflater.from(v.getContext());
                                        final View view_ = factory.inflate(R.layout.login_dialog, null);
                                        alertDialog.setView(view_);

                                        Button button2 = view_.findViewById(R.id.button3);
                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialog.dismiss();
                                                alertDialog_.dismiss();
                                                year_text.setText("");
                                                month_text.setText("");
                                                day_text.setText("");
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                });
                                alertDialog_.show();


                            } else {
                                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                                alertDialog.setTitle(getString(R.string.entry_error));
                                alertDialog
                                        .setMessage(getString(R.string.valid_user));
                                alertDialog.setCancelable(false);
                                LayoutInflater factory = LayoutInflater.from(v.getContext());
                                final View view_ = factory.inflate(R.layout.login_dialog, null);
                                alertDialog.setView(view_);

                                Button button2 = view_.findViewById(R.id.button3);
                                button2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        year_text.setText("");
                                        month_text.setText("");
                                        day_text.setText("");
                                    }
                                });

                                alertDialog.show();
                            }
                    }else{//Aquí el código que comprueba si la fecha es igual a la que hay o no
                            //Se hace el string con la fecha, se comprueba en firebase y si es se da acceso
                            //si no se hace invisible y salta el mensaje de error
                            if((!day_text.getText().toString().equals(""))&&(!month_text.getText().toString().equals(""))
                                    &&(!year_text.getText().toString().equals(""))){

                                Boolean fecha_correcta = false;
                                String dia = day_text.getText().toString();
                                String mes = month_text.getText().toString();
                                String any = year_text.getText().toString();


                                //FUNCIONA--> HAY QUE AVERIGUAR CON ESTO SI ES O NO UN NÚMERO
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month_calendar = calendar.get(Calendar.MONTH);
                                int day_calendar = calendar.get(Calendar.DAY_OF_MONTH);


                                try {
                                    int num = Integer.parseInt(dia);
                                    int num_1 = Integer.parseInt(mes);
                                    int num_2 = Integer.parseInt(any);


                                    Log.i("Hugo", "num: " + num);
                                    Log.i("Hugo", "num1: " + num_1);
                                    Log.i("Hugo", "num2: " + num_2);

                                    Log.i("Hugo", "year: " + year);
                                    Log.i("Hugo", "month: " + month_calendar);
                                    Log.i("Hugo", "day: " + day_calendar);
                                    if(Integer.parseInt(any)>year){
                                        fecha_correcta = false;
                                    }else{
                                        if(Integer.parseInt(any)==year){
                                            if(Integer.parseInt(mes)>month_calendar){
                                                fecha_correcta = false;
                                            }else{
                                                if(Integer.parseInt(mes)==month_calendar) {
                                                    if (Integer.parseInt(dia) > day_calendar) {
                                                        fecha_correcta = false;
                                                    } else {
                                                        if(Integer.parseInt(dia) == day_calendar){
                                                            fecha_correcta=false;
                                                        }else {
                                                            fecha_correcta = true;
                                                        }
                                                    }
                                                }else{
                                                    fecha_correcta=true;
                                                }
                                            }
                                        }else{
                                           fecha_correcta=true;
                                        }
                                    }
                                       /* if(Integer.parseInt(any)==year){
                                            if(Integer.parseInt(mes)>month_calendar){
                                            fecha_correcta = false;
                                            }else if(Integer.parseInt(dia)>day_calendar){
                                            fecha_correcta = false;

                                            }else{
                                            fecha_correcta = true;
                                        }
                                    }*/

                                    //fecha_correcta= true;
                                } catch (NumberFormatException e) {

                                }
                                //dia.matches()

                                final String date = dia.concat("/").concat(mes).concat("/").concat(any);


                                if(fecha_correcta) {
                                    dref.child(user).child("Fecha").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (date.equals(dataSnapshot.getValue().toString())) {
                                                Intent intent = new Intent(LoginActivity.this, InitActivity.class);
                                                intent.putExtra("user", user);
                                                startActivity(intent);
                                            } else {
                                                text_date.setVisibility(View.INVISIBLE);
                                                year_text.setVisibility(View.INVISIBLE);
                                                day_text.setVisibility(View.INVISIBLE);
                                                month_text.setVisibility(View.INVISIBLE);
                                                user_text.setText("");
                                                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                                                alertDialog.setTitle(getString(R.string.entry_error));
                                                alertDialog
                                                        .setMessage(getString(R.string.choose_another));
                                                alertDialog.setCancelable(false);
                                                LayoutInflater factory = LayoutInflater.from(v.getContext());
                                                final View view_ = factory.inflate(R.layout.login_dialog, null);
                                                alertDialog.setView(view_);

                                                Button button2 = view_.findViewById(R.id.button3);
                                                button2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        alertDialog.dismiss();
                                                        year_text.setText("");
                                                        month_text.setText("");
                                                        day_text.setText("");
                                                    }
                                                });
                                                alertDialog.show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }else{
                                    final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                                    alertDialog.setTitle(getString(R.string.entry_error));
                                    alertDialog
                                            .setMessage("Parece que la fecha introducida no es correcta, vuelve a probar sin caracteres especiales y con una fecha real");
                                    alertDialog.setCancelable(false);
                                    LayoutInflater factory = LayoutInflater.from(v.getContext());
                                    final View view_ = factory.inflate(R.layout.login_dialog, null);
                                    alertDialog.setView(view_);

                                    Button button2 = view_.findViewById(R.id.button3);
                                    button2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                            year_text.setText("");
                                            month_text.setText("");
                                            day_text.setText("");
                                        }
                                    });
                                    alertDialog.show();
                                }
                            }else{

                                final AlertDialog falertDialog = new AlertDialog.Builder(v.getContext()).create();
                                falertDialog.setTitle(getString(R.string.fill_all));
                                falertDialog
                                        .setMessage(getString(R.string.introduce_fecha));
                                falertDialog.setCancelable(false);
                                LayoutInflater factory = LayoutInflater.from(v.getContext());
                                final View view_ = factory.inflate(R.layout.login_dialog, null);
                                falertDialog.setView(view_);

                                Button button2 = view_.findViewById(R.id.button3);
                                button2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        falertDialog.dismiss();
                                    }
                                });

                                falertDialog.show();
                            }

                        }
                }

                        @Override
                        public void onCancelled (DatabaseError databaseError){

                        }
                });

            }
        });


    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getDate(){
        String date = "";
        date = day_text.getText().toString().concat("/").concat(month_text.getText().toString()).
                concat("/").concat(year_text.getText().toString());
        return date;
    }
}
