package com.example.hdelanietamarin.mindproblem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Game4Activity extends AppCompatActivity {

    Button btn_check;
    TextView text_view, text_level;
    EditText editText;
    ImageAdapterBig imageAdapterBig;
    ImageAdapter imageAdapter;
    GridView gridview;
    FirebaseDatabase database;
    String code;
    int codigo =0;
    int level=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4);
        btn_check =(Button) findViewById(R.id.btn_check);
        text_view =(TextView) findViewById(R.id.text_view);
        text_level = (TextView) findViewById(R.id.text_level);
        editText = (EditText) findViewById(R.id.editText);
        gridview = (GridView) findViewById(R.id.gridview);
        code = getIntent().getStringExtra("code");

        database = FirebaseDatabase.getInstance();
        if(code==null){
            code = getString(R.string.dibujos);
        }
        final DatabaseReference myRef = database.getReference(code);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            level = savedInstanceState.getInt("Level");
            Log.i("Hugo", "Saved instance");

        }
        Log.i("Hugo", "Level: " + level);





        myRef.child("Dibujos");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("Dibujos")){
                    myRef.child("Dibujos").setValue("1");
                    text_level.setText(getString(R.string.Record)+ 1);
                }else{
                    text_level.setText(getString(R.string.Record)+ dataSnapshot.child("Dibujos").getValue().toString());
                }
                text_level.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {//detecta si se pulsa fuera del Edittext. también hace falta añadir focusable y clickable en el xml
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        jugar(gridview);




        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
                hideKeyboard(v);
            }
        });

    }

    private void jugar(GridView gridview) {
        if(level>5) {
            gridview.setNumColumns(4);
            imageAdapterBig = new ImageAdapterBig(this);
            text_view.setText(generateText());
            gridview.setAdapter(imageAdapterBig);
        }else{
            imageAdapter = new ImageAdapter(this);
            text_view.setText(generateText());
            gridview.setAdapter(imageAdapter);
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(Game4Activity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void check() {
        //Context context = this.getApplicationContext();
        if(!editText.getText().toString().equals("")) {
            int total = 0;

            if(level<=5) {
                for (int i = 0; i < imageAdapter.getCount(); i++) {
                    long id_actual = imageAdapter.getItemId(i);
                    if (id_actual == codigo) {
                        total = total + 1;
                    }
                }
            }else{
                for (int i = 0; i < imageAdapterBig.getCount(); i++) {
                    long id_actual = imageAdapterBig.getItemId(i);
                    if (id_actual == codigo) {
                        total = total + 1;
                    }
                }
            }
            if(editText.getText().toString().equals(String.valueOf(total))){
                editText.setText("");
                level = level +1;


                final DatabaseReference myRef = database.getReference(code).child("Dibujos");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String current_s = dataSnapshot.getValue().toString();
                        int record_actual = Integer.parseInt(current_s);
                        if(level>record_actual){
                            myRef.setValue(String.valueOf(level-1));
                            text_level.setText(getString(R.string.Record)+ (level-1));
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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
                        jugar(gridview);
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

            }else{
                editText.setText("");

                final AlertDialog newDialog = new AlertDialog.Builder(this).create();
                LayoutInflater factory = LayoutInflater.from(this);
                final View view_ = factory.inflate(R.layout.dialog_wrong, null);
                newDialog.setView(view_);

                Button button = view_.findViewById(R.id.button2);
                Button button2 = view_.findViewById(R.id.button3);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newDialog.dismiss();
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_f = new Intent(Game4Activity.this, InitActivity.class);
                        startActivity(intent_f);
                        finish();
                        newDialog.dismiss();
                    }
                });

                newDialog.setTitle(getString(R.string.fallado));


                newDialog.setMessage(getString(R.string.action_question));
                newDialog.show();


            }

        }else{
            Toast.makeText(this, R.string.valid_number, Toast.LENGTH_LONG).show();
        }
    }
    public String generateText(){
        String text = "";
        Random r = new Random();
        int i1 = r.nextInt(4 - 0);//

        switch (i1){
            case 0:
                text = getString(R.string.baby_number);
                codigo = R.drawable.baby;
                break;
            case 1:
                text = getString(R.string.ball_number);
                codigo = R.drawable.football;
                break;
            case 2:
                text = getString(R.string.house_number);
                codigo = R.drawable.house;
                break;
            case 3:
                text = getString(R.string.plane_number);
                codigo = R.drawable.plane;
                break;
        }
        return text;
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt("Level", level);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
