package com.example.hdelanietamarin.mindproblem;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class InstructionsActivity extends AppCompatActivity {
Button btn_to_game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        btn_to_game = (Button) findViewById(R.id.btn_to_game);

        btn_to_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Poner aquí el dialog preguntando si se ha leído las instrucciones, si dice que
                //sí --> Intent. Si dice que no --> dialog.dismiss()




                final AlertDialog newDialog = new AlertDialog.Builder(view.getContext()).create();
                LayoutInflater factory = LayoutInflater.from(view.getContext());
                final View view_ = factory.inflate(R.layout.start_new_dialog, null);
                newDialog.setView(view_);

                Button button = view_.findViewById(R.id.button2);
                Button button2 = view_.findViewById(R.id.button3);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(InstructionsActivity.this, Game3Activity.class);
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
