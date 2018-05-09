package com.example.hdelanietamarin.mindproblem;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class InstrActi4Activity extends AppCompatActivity {
    String code;
    Button btn_to_game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instr_acti4);
        btn_to_game = (Button) findViewById(R.id.btn_to_game);
        code = getIntent().getStringExtra("code");
        btn_to_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog newDialog = new AlertDialog.Builder(v.getContext()).create();
                LayoutInflater factory = LayoutInflater.from(v.getContext());
                final View view_ = factory.inflate(R.layout.start_new_dialog, null);
                newDialog.setView(view_);

                Button button = view_.findViewById(R.id.button2);
                Button button2 = view_.findViewById(R.id.button3);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(InstrActi4Activity.this, Game4Activity.class);
                        intent.putExtra("code", code);
                        startActivity(intent);
                        newDialog.dismiss();
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newDialog.dismiss();
                    }
                });

                newDialog.setTitle(getString(R.string.read));


                newDialog.setMessage(getString(R.string.read_question));
                newDialog.show();



            }
        });


    }
}
