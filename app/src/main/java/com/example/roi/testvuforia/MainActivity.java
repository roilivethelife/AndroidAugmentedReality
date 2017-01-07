package com.example.roi.testvuforia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.roi.testvuforia.graficos.OpenGLActivity;
import com.example.roi.testvuforia.vuforia.ArActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {
    private static final String LOGTAG = "MainActivity";


    private Button btnOpenGLActivity;
    private Button btnArActivity;


    private boolean permissionCamera = false;
    private boolean permissionInternet = false;
    private boolean permissionAccessNetworkState = false;
    private boolean permissionReadExternalStorage = false;
    private static final int MY_PERMISSIONS_MULTIPLE_REQUEST = 0;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 3;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //comprueba que permisos están activos y pide al usuario
        comprobarPedirPermisos();


        btnOpenGLActivity = (Button) findViewById(R.id.btn_openglactivity);
        btnOpenGLActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permisosOk()) {
                    Intent intent = new Intent(view.getContext(), OpenGLActivity.class);
                    view.getContext().startActivity(intent);
                } else {
                    Log.d(LOGTAG, "Error boton click: no estan todos los permisos");
                    //TODO: mensaje al usuario
                }
            }
        });

        btnArActivity = (Button) findViewById(R.id.btn_aractivity);
        btnArActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permisosOk()) {
                    Intent intent = new Intent(view.getContext(), ArActivity.class);
                    view.getContext().startActivity(intent);
                } else {
                    Log.d(LOGTAG, "Error boton click: no estan todos los permisos");
                    //TODO: mensaje al usuario
                }
            }
        });
    }

    private void comprobarPedirPermisos() {
        ArrayList<String> aPermissions = new ArrayList<>();

        if (!permissionCamera && ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            aPermissions.add(Manifest.permission.CAMERA);
            permissionCamera = false;
        } else {
            permissionCamera = true;
        }
        if (!permissionInternet && ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            aPermissions.add(Manifest.permission.INTERNET);
            permissionInternet = false;
        } else {
            permissionInternet = true;
        }
        if (!permissionAccessNetworkState && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            aPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            permissionAccessNetworkState = false;
        } else {
            permissionAccessNetworkState = true;
        }
        if (!permissionReadExternalStorage && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            aPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionReadExternalStorage = false;
        } else {
            permissionReadExternalStorage = true;
        }
        if(!aPermissions.isEmpty()) {
            String[] sPermissions = (String[]) aPermissions.toArray(new String[aPermissions.size()]);
            //pedir todos los permisos del tiron
            ActivityCompat.requestPermissions(this, sPermissions,
                    MY_PERMISSIONS_MULTIPLE_REQUEST);
        }
    }

    private boolean permisosOk() {
        return permissionCamera && permissionInternet && permissionReadExternalStorage && permissionAccessNetworkState;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionAccessNetworkState = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCamera = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionInternet = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionReadExternalStorage = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_MULTIPLE_REQUEST: {
                HashMap<String, Integer> hashMap = new HashMap<>();
                hashMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                hashMap.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                hashMap.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                hashMap.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++) {
                    hashMap.put(permissions[i],grantResults[i]);
                }
                if(!permissionInternet) permissionInternet=(hashMap.get(Manifest.permission.INTERNET)==PackageManager.PERMISSION_GRANTED);
                if(!permissionCamera) permissionCamera=(hashMap.get(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED);
                if(!permissionReadExternalStorage) permissionReadExternalStorage=(hashMap.get(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED);
                if(!permissionAccessNetworkState) permissionAccessNetworkState=(hashMap.get(Manifest.permission.ACCESS_NETWORK_STATE)==PackageManager.PERMISSION_GRANTED);
            }
        }
    }
}
