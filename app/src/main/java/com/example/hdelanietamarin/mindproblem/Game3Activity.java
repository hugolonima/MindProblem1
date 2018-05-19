package com.example.hdelanietamarin.mindproblem;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;



public class Game3Activity extends AppCompatActivity implements View.OnClickListener{
    //TODO: SI ME DA TIEMPO PODRÍA PONER UN INTENT PARA MANDAR LOS DIBUJOS
    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
    private Canvas canvas;
    public Bitmap bm;
    private float smallBrush, mediumBrush, largeBrush;
    final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3);

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        drawView = (DrawingView)findViewById(R.id.drawing);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        saveBtn = (ImageButton)findViewById(R.id.save_btn);

        saveBtn.setOnClickListener(this);
        newBtn.setOnClickListener(this);
        eraseBtn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);
        drawBtn.setOnClickListener(this);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            //cant = savedInstanceState.getInt("Level");
            Bundle b = new Bundle();
            b = savedInstanceState.getBundle("Bitmap");
            byte[] byteArray = b.getByteArray("image");
            //byte[] byteArray = getArgument().getByteArrayExtra("image");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            drawView.setBm(bmp);

           // drawView.getCanvas().restore();

            //canvas = new Canvas(bmp);
            //drawView.setBitmap(bmp);

        }

    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.draw_btn){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle(R.string.brush_size);
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }else if(view.getId()==R.id.erase_btn){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle(R.string.eraser_size);
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }else if(view.getId()==R.id.new_btn){
            final AlertDialog newDialog = new AlertDialog.Builder(this).create();
            LayoutInflater factory = LayoutInflater.from(this);
            final View view_ = factory.inflate(R.layout.start_new_dialog, null);
            newDialog.setView(view_);

            Button button = view_.findViewById(R.id.button2);
            Button button2 = view_.findViewById(R.id.button3);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.startNew();
                    newDialog.dismiss();
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newDialog.dismiss();
                }
            });

            newDialog.setTitle(getString(R.string.start_again));


            newDialog.setMessage(getString(R.string.draw_question));
            newDialog.show();

        }else if(view.getId()==R.id.save_btn){

            final AlertDialog saveDialog = new AlertDialog.Builder(this).create();
            LayoutInflater factory = LayoutInflater.from(this);
            final View view_2 = factory.inflate(R.layout.start_new_dialog, null);
            saveDialog.setView(view_2);

            Button button = view_2.findViewById(R.id.button2);
            Button button2 = view_2.findViewById(R.id.button3);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download();
                    drawView.destroyDrawingCache();
                    saveDialog.dismiss();
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDialog.dismiss();
                }
            });

            saveDialog.setTitle(getString(R.string.save_draw));


            saveDialog.setMessage(getString(R.string.save_draw_question));
            saveDialog.show();
        }
    }

    public void paintClicked(View view){
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = imgView.getTag().toString();


            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }



private void download() {
    try {

        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {

                int month = Calendar.getInstance().get(Calendar.MONTH)+1;
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                drawView.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), drawView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png", getString(R.string.draw) +day+"/"+month+"/"+year);
                if(imgSaved!=null) {

                    final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
                    alertDialog.setTitle(getString(R.string.save_ok));
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
                   /* Toast savedToast = Toast.makeText(getApplicationContext(),
                            R.string.save_ok, Toast.LENGTH_SHORT);
                    savedToast.show();*/
                }else{
                    final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
                    alertDialog.setTitle(getString(R.string.save_wrong));
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

                    /*Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            R.string.save_wrong, Toast.LENGTH_SHORT);
                    unsavedToast.show();*/
                }


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

            }

        }

        } catch (Exception e) {

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    int month = Calendar.getInstance().get(Calendar.MONTH)+1;
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", getString(R.string.draw) +day+"/"+month+"/"+year);
                    if(imgSaved!=null) {
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                R.string.save_ok, Toast.LENGTH_SHORT);
                        savedToast.show();
                    }else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                R.string.save_wrong, Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        drawView.getCanvas().save();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        drawView.getCanvasBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bundle b = new Bundle();
        b.putByteArray("image",byteArray);
        savedInstanceState.putBundle("Bitmap",b);
        //drawView.setBm(drawView.getCanvasBitmap());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
