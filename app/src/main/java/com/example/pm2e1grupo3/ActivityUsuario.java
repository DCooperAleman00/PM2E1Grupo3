package com.example.pm2e1grupo3;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm2e1grupo3.Models.RestApiMethods;//
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityUsuario extends AppCompatActivity {



    Button btnSave,btnListarContactos,btnGaleria;
    FloatingActionButton btnTomarfoto;
    ImageView Foto;
    EditText txtNombre,txtTelefono,txtLat,txtLon;

    Bitmap imagen;

    static final int RESULT_GALLERY_IMG = 200;
    static final int PETICION_ACCESO_CAM = 100;
    static final int TAKE_PIC_REQUEST = 101;

    public static String latitud = "";
    public static String longitud = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {

        }
        Foto = (ImageView) findViewById(R.id.imageView);
        btnSave = (Button) findViewById(R.id.btnGuardar);
        btnListarContactos = (Button) findViewById(R.id.btnContactos);
        btnTomarfoto = (FloatingActionButton) findViewById(R.id.fbtnTomarFoto);
        btnGaleria = (Button) findViewById(R.id.btnGaleria);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtTelefono = (EditText) findViewById(R.id.txtTelefono);
        txtLat = (EditText) findViewById(R.id.txtLat);
        txtLon = (EditText) findViewById(R.id.txtLon);


        btnListarContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ActivityListarContactos.class);
                startActivity(intent);
            }
        });


        btnTomarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();

            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaleriaImagenes();

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarDatos();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Debe de tomarse una foto ",Toast.LENGTH_LONG).show();
                }
            }
        });

        //valida si tiene los permisos de ser asi manda a llamar el metodo locationStart()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

    }

    private void validarDatos() {
        if(txtNombre.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Debe de escribir un nombre" ,Toast.LENGTH_LONG).show();
        }else if (txtTelefono.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un telefono" ,Toast.LENGTH_LONG).show();
        }else if (txtLat.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un latitud" ,Toast.LENGTH_LONG).show();
        }else if (txtLon.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un longitud" ,Toast.LENGTH_LONG).show();
        }
        else {
            crearUsuario();
        }
    }

    private void permisos() {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PETICION_ACCESO_CAM);
        }else{
            tomarFoto();
        }
    }

    private void tomarFoto() {
        Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takepic.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takepic,TAKE_PIC_REQUEST);
        }
    }

    //**Entrar a la carpeta de fotos del telefono**//
    private void GaleriaImagenes() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, RESULT_GALLERY_IMG);
    }

    //***Metodo para convertir imagen***//
    private String GetStringImage(Bitmap photo) {

        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 70, ba);
            byte[] imagebyte = ba.toByteArray();
            String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);

            return encode;
        }catch (Exception ex)
        {
            ex.toString();
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PETICION_ACCESO_CAM)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                tomarFoto();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Se necesitan permisos",Toast.LENGTH_LONG).show();
        }
        //permiso para GPS
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri;
        //obtener la iamgen por el almacenamiento interno
        if(resultCode==RESULT_OK && requestCode==RESULT_GALLERY_IMG)
        {

            imageUri = data.getData();
            Foto.setImageURI(imageUri);
            try {
                imagen=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);

            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
            }
        }
        //obtener la iamgen por la camara
        if(requestCode == TAKE_PIC_REQUEST && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imagen = (Bitmap) extras.get("data");
            Foto.setImageBitmap(imagen);
        }



    }




    private void crearUsuario() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        HashMap<String, String> parametros = new HashMap<>();

        String fotoString = GetStringImage(imagen);


        //setear los parametros mediante put
        parametros.put("nombre", txtNombre.getText().toString());
        parametros.put("telefono", txtTelefono.getText().toString());
        parametros.put("latitud", txtLat.getText().toString());
        parametros.put("longitud", txtLon.getText().toString());
        parametros.put("foto", fotoString);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, RestApiMethods.EndPointCreateUsuario,
                new JSONObject(parametros), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(getApplicationContext(), "String Response " + response.getString("mensaje").toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

        limpiar();
    }

    private void limpiar() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtLon.setText("");
        txtLat.setText("");
    }


    //-----------------------------LATITUD Y LONGITUD----------------------------------------
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            //SE VA A LA CONFIGURACION DEL SISTEMA PARA QUE ACTIVE EL GPS UNA VEZ QUE INICIA LA APLICACION
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);


    }


    public class Localizacion implements LocationListener {
        ActivityUsuario mainActivity;

        public void setMainActivity(ActivityUsuario mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            loc.getLatitude();
            loc.getLongitude();

            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();


            ActivityUsuario.setLatitud(loc.getLatitude()+"");
            ActivityUsuario.setLongitud(loc.getLongitude()+"");
            txtLat.setText(loc.getLatitude()+"");
            txtLon.setText(loc.getLongitude()+"");
            this.mainActivity.setLocation(loc);
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    ///

    public static void setLatitud(String latitud) {
        ActivityUsuario.latitud = latitud;
    }

    public static void setLongitud(String longitud) {
        ActivityUsuario.longitud = longitud;
    }



}