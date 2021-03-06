package com.example.hdelanietamarin.mindproblem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
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

public class Game4Activity extends AppCompatActivity implements DialogInterface.OnDismissListener,DialogInterface.OnShowListener{

    //TODO: un get all y un set all para que pasando, y devolviendo un array del
    //1 al 4 podamos pillar todos los dibujos y volver a ponerlos
    Button btn_check;
    TextView text_view, text_level;
    EditText editText;
    ImageAdapterBig imageAdapterBig;
    ImageAdapter imageAdapter;
    GridView gridview;
    FirebaseDatabase database;
    String code; String text;
    int[] plantilla;
    Boolean iniciado = false;
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
            codigo = savedInstanceState.getInt("Codigo");
            text = savedInstanceState.getString("Text");
            iniciado = savedInstanceState.getBoolean("Iniciado");
            plantilla = savedInstanceState.getIntArray("Plantilla");


        }





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
            if((plantilla!=null)&&(iniciado)){
                imageAdapterBig.setAll(plantilla);
            }
            if(!iniciado) {
                text_view.setText(generateText());
                iniciado = true;
            }else {
                text_view.setText(text);
            }
            gridview.setAdapter(imageAdapterBig);
        }else{
            imageAdapter = new ImageAdapter(this);
            if((plantilla!=null)&&(iniciado)){
                imageAdapter.setAll(plantilla);
            }
            if (!iniciado) {
                text_view.setText(generateText());
                iniciado = true;
            }else {
                text_view.setText(text);
            }
            gridview.setAdapter(imageAdapter);
        }

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
                        iniciado=false;
                        jugar(gridview);
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setOnShowListener(this);
                alertDialog.setOnDismissListener(this);
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
                newDialog.setOnShowListener(this);
                newDialog.setOnDismissListener(this);
                newDialog.show();


            }

        }else{
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.insert_valid));

            alertDialog.setCancelable(false);
            LayoutInflater factory = LayoutInflater.from(this);
            final View view_ = factory.inflate(R.layout.login_dialog, null);
            alertDialog.setView(view_);

            Button button2 = view_.findViewById(R.id.button3);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setOnShowListener(this);
            alertDialog.setOnDismissListener(this);
            alertDialog.show();
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
        savedInstanceState.putInt("Codigo", codigo);
        savedInstanceState.putString("Text", text_view.getText().toString());
        savedInstanceState.putBoolean("Iniciado", iniciado);
        savedInstanceState.putIntArray("Plantilla", imageAdapter.getAll());
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
}
