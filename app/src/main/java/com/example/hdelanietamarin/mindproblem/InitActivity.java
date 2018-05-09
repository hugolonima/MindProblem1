package com.example.hdelanietamarin.mindproblem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InitActivity extends AppCompatActivity {

    //TODO: CAMBIAR TODOS LOS BUBBLE_DIALOG POR EL DE LA CHICA QUE ES EL ÚNICO QUE SE VE BIEN
Button btn_game1; Button btn_game2; Button btn_game3; Button btn_game4;
String code;
    String r_colores="";
    String r_numeros="";
    String r_dibujos="";

    FirebaseDatabase database;
    DatabaseReference dref;
    private static final String  FILENAME_CODE = "user.txt";
    private static final int MAX_BYTES = 80000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);


        database = FirebaseDatabase.getInstance();

        code = getIntent().getStringExtra("user");


        if(code==null){
            readCode();
        }


        dref = database.getReference().child(String.valueOf(code));

        if(code!=null){
            writeCode();
        }

        if(code==null){
            Intent intent = new Intent(InitActivity.this, LoginActivity.class);
            startActivity(intent);
        }


        btn_game1 = (Button) findViewById(R.id.btn_game1);
        btn_game2 = (Button) findViewById(R.id.btn_game2);
        btn_game3 = (Button) findViewById(R.id.btn_game3);
        btn_game4 = (Button) findViewById(R.id.btn_game4);

        btn_game1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitActivity.this, InstrActi1Activity.class);
                intent.putExtra("code", code);
                startActivity(intent);
            }
        });

        btn_game2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitActivity.this, InstrActi2Activity.class);
                intent.putExtra("code", code);
                startActivity(intent);
            }
        });

        btn_game3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitActivity.this, InstructionsActivity.class);
                intent.putExtra("code", code);
                startActivity(intent);
            }
        });

        btn_game4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitActivity.this, InstrActi4Activity.class);
                intent.putExtra("code", code);
                startActivity(intent);
            }
        });



        if(code!=null) {
            actualize();
        }




    }

    private void actualize() {
        dref.child(getString(R.string.Colores)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    r_colores = getString(R.string.color_record) + dataSnapshot.getValue().toString() + "\n";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dref.child(getString(R.string.numeros)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    r_numeros = getString(R.string.number_record) + dataSnapshot.getValue().toString() + "\n";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dref.child(getString(R.string.dibujos)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    r_dibujos = getString(R.string.draw_record) + dataSnapshot.getValue().toString() + "\n";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
    private void readCode(){
        try {
            FileInputStream fis = openFileInput(FILENAME_CODE);
            byte[] buffer_i = new byte[MAX_BYTES];
            int nread = fis.read(buffer_i);
            if (nread>0) {
                String content = new String(buffer_i, 0, nread);
                code = content;
                fis.close();
            }

        } catch (FileNotFoundException e) {


        } catch (IOException e) {

            Toast.makeText(this, R.string.couldnt_read, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        actualize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        readCode();
        writeCode();
    }
    @Override
    protected void onStop() {
        super.onStop();

        writeCode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }







    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cerrar:
                final AlertDialog builder2 = new AlertDialog.Builder(this).create();
                builder2.setTitle(getString(R.string.close_question));
                builder2.setMessage(getString(R.string.delete));

                builder2.setCancelable(false);
                LayoutInflater factory = LayoutInflater.from(this);
                final View view = factory.inflate(R.layout.close_dialog, null);
                builder2.setView(view);

                Button button = view.findViewById(R.id.button2);
                Button button2 = view.findViewById(R.id.button3);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFile(FILENAME_CODE);
                        Intent intent = new Intent(InitActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        builder2.dismiss();
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder2.dismiss();
                    }
                });
                builder2.show();

                return true;

                case R.id.action_compartir:
                    final AlertDialog builder1 = new AlertDialog.Builder(this).create();
                    builder1.setTitle(getString(R.string.share_question));
                    builder1.setMessage(getString(R.string.share_records));

                    builder1.setCancelable(false);
                    LayoutInflater factory1 = LayoutInflater.from(this);
                    final View view1 = factory1.inflate(R.layout.close_dialog, null);
                    builder1.setView(view1);

                    Button button_ = view1.findViewById(R.id.button2);
                    Button button_2 = view1.findViewById(R.id.button3);

                    button_.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            String text = r_colores.concat(r_dibujos).concat(r_numeros).
                                    concat(getString(R.string.app_publi));

                                sendRecords(text);


                            //finish();
                            builder1.dismiss();
                        }
                    });

                    button_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder1.dismiss();
                        }
                    });
                    builder1.show();
                    return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendRecords(String text) {
        if (text.equals("")) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.no_records_yet));
            alertDialog
                    .setMessage(getString(R.string.play));
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

            alertDialog.show();
        }else{
            PackageManager pm = getPackageManager();
            try {

                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                //String text = "YOUR TEXT HERE";

                PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                //Check if package exists or not. If not then code
                //in catch block will be called
                waIntent.setPackage("com.whatsapp");

                waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(waIntent, getString(R.string.share_with)));

            } catch (PackageManager.NameNotFoundException e) {
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(getString(R.string.something_went_wrong));
                alertDialog
                        .setMessage(getString(R.string.no_wa) +
                                getString(R.string.install_wa));
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

                alertDialog.show();

            }
        }
    }


}
