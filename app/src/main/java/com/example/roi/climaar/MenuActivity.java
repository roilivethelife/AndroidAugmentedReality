package com.example.roi.climaar;

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
import android.widget.Toast;

import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.mapa.Mapa;
import com.example.roi.climaar.vista.ARActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuActivity extends Activity {
    private static final String LOGTAG = "MenuActivity";

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

    private static final int RESULT_PICK_MAP = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //comprueba que permisos están activos y pide al usuario
        comprobarPedirPermisos();


        btnArActivity = (Button) findViewById(R.id.btn_aractivity);
        btnArActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permisosOk()) {
                    //TODO: AUTOLOADMAP: Eliminar lineas y descomentar
                    Intent intent = new Intent(view.getContext(), ARActivity.class);
                    intent.putExtra("MAPA", Modelo.getInstance().getMapas().get(0));
                    Log.d(LOGTAG, "StartActivity");
                    startActivity(intent);
                    /*
                    AppInstance.getInstance().leerObjetos();
                    Intent intent = new Intent(view.getContext(), SelectMapActivity.class);
                    intent.putExtra("TITULO","Seleccione el mapa a utilizar:");
                    startActivityForResult(intent, RESULT_PICK_MAP);*/
                } else {
                    Log.d(LOGTAG, "Error boton click: no estan todos los permisos");
                    //TODO: mensaje al usuario
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Modelo.getInstance().setContext(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            String[] sPermissions = aPermissions.toArray(new String[aPermissions.size()]);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_CANCELED){
            Toast.makeText(this, "No se ha seleccionado ningún mapa", Toast.LENGTH_SHORT).show();
        }else {
            switch (requestCode) {
                case RESULT_PICK_MAP:
                    Mapa map=null;
                    if(data!=null && data.getExtras()!=null) {
                        map = (Mapa) data.getExtras().getSerializable("MAPA");
                    }
                    if(map!=null) {
                        Toast.makeText(this, "Mapa seleciconado:" + map.getNombre(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, ARActivity.class);
                        intent.putExtra("MAPA",map);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
