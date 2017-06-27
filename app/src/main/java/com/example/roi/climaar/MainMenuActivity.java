package com.example.roi.climaar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roi.climaar.menus.editmap.EditMapActivity;
import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.mapa.Mapa;
import com.example.roi.climaar.vista.ARActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener, MapaListView.MapaListViewInterface{
    private static final String LOGTAG = "MainMenuActivity";

    private MapaListView mapaListView;
    private TextView textViewUbicacionSeleccionada;
    private AlertDialog dialogAyuda;
    private Mapa mapaSeleccionado;
    private Button buttonMainIniciarAR;


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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        buttonMainIniciarAR = (Button) findViewById(R.id.buttonMainIniciarAR);
        buttonMainIniciarAR.setOnClickListener(this);
        mapaListView = (MapaListView)findViewById(R.id.mapa_list_view);
        mapaListView.setListener(this);
        textViewUbicacionSeleccionada = (TextView) findViewById(R.id.textViewUbicacionSeleccionada);
        crearDialogAyuda();

        if(Modelo.getInstance().getMapasSize()==0)
            Modelo.getInstance().addMapa(Modelo.getInstance().createLoadDefaultMap());
    }

    private void crearDialogAyuda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ayuda")
                .setMessage("Este es el mensaje de ayuda de la aplicación.");
        dialogAyuda = builder.create();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Modelo.getInstance().setContext(this);
        //comprueba que permisos están activos y pide al usuario
        comprobarPedirPermisos();
        mapaListView.notifyNewData();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbarHelp:
                dialogAyuda.show();
                break;
            case R.id.toolbarSettings:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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


    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonMainIniciarAR:
                if (permisosOk()) {
                    if(mapaSeleccionado!=null) {
                        Intent intent = new Intent(this, ARActivity.class);
                        intent.putExtra("MAPA",mapaSeleccionado);
                        startActivity(intent);
                    }else{
                        Toast.makeText(this, "No se ha seleccionado ningún mapa", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(LOGTAG, "Error boton click: no estan todos los permisos");
                    Toast.makeText(this, "Error, hay permisos no concedidos", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSelectedMapaChanged() {
        mapaSeleccionado = mapaListView.getMapaSeleccionado();
        if(mapaSeleccionado==null){
            textViewUbicacionSeleccionada.setText("Ningun mapa seleccionado");
        }else{
            textViewUbicacionSeleccionada.setText(mapaSeleccionado.getNombre());
        }
    }

    @Override
    public void onAddNewMapPushed() {
        Intent intent = new Intent(this, EditMapActivity.class);
        Modelo.getInstance().setMapaOriginal(null);
        startActivity(intent);
    }

    @Override
    public void onEditMapPushed(Mapa mapaSeleccionado) {
        Intent intent = new Intent(this, EditMapActivity.class);
        Modelo.getInstance().setMapaOriginal(mapaSeleccionado);
        startActivity(intent);
    }

    @Override
    public void onDelMapPushed(Mapa mapaSeleccionado) {
        AlertDialog.Builder dialogConfirmDelete = new AlertDialog.Builder(this);

        // set title
        dialogConfirmDelete.setTitle("¿Eliminar el mapa?");

        // set dialog inputMessage
        dialogConfirmDelete
                .setMessage("¿Seguro que quiere eliminar el mapa seleccionado?\n" +
                        "No podrá volver a recuperarlo")
                .setCancelable(false)
                .setPositiveButton("Eliminar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        mapaListView.deleteSelectedItem();
                    }
                })
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = dialogConfirmDelete.create();

        // show it
        alertDialog.show();
    }
}
