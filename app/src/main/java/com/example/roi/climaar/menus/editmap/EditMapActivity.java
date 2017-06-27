package com.example.roi.climaar.menus.editmap;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.mapa.MapElement;
import com.example.roi.climaar.modelo.mapa.Mapa;

import java.util.ArrayList;

public class EditMapActivity extends AppCompatActivity implements View.OnClickListener, ElementosListView.ElementosListViewInterface {

    private static final String LOGTAG = "EditMapActivity";
    private AttributeListItem nombreMapaConfig;
    private AttributeListItem descMapaConfig;
    private AttributeListItem numDespachoMapaConfig;
    private AttributeListItem tamXMapaConfig;
    private AttributeListItem tamYMapaConfig;
    private AttributeListItem tamZMapaConfig;
    private AttributeListItem markerXMapaConfig;
    private AttributeListItem markerYMapaConfig;
    private AttributeListItem markerZMapaConfig;
    private ElementosListView editElementosMapaConfig;
    private EditText editText;


    private Mapa mapaOriginal;
    private Mapa mapaEditar;
    private boolean editMap;
    private AlertDialog dialogAyuda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_map);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_new_map);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        if(myToolbar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        mapaOriginal = Modelo.getInstance().getMapaOriginal();
        if(mapaOriginal!=null){
            editMap = true;
            mapaEditar = new Mapa(mapaOriginal);
            myToolbar.setTitle("Editar mapa");

        }else{
            editMap = false;
            mapaOriginal = new Mapa("nuevo mapa");
            mapaEditar = mapaOriginal;//mapa nuevo
            myToolbar.setTitle("Nuevo mapa");
        }
        Modelo.getInstance().setMapaEditar(mapaEditar);

        bindListItemMapaConfig();
        crearDialogAyuda();
    }

    @Override
    protected void onResume() {
        super.onResume();
        editElementosMapaConfig.delAllElements();
        mapaOriginal = Modelo.getInstance().getMapaOriginal();
        mapaEditar = Modelo.getInstance().getMapaEditar();
        for (MapElement mapElement :
                mapaEditar.mapaElements) {
            editElementosMapaConfig.addElemento(mapElement);
            Log.d(LOGTAG,mapElement.getName()+" added");
        }
    }

    private void bindListItemMapaConfig() {
        nombreMapaConfig = (AttributeListItem) findViewById(R.id.mapaAttributeNombre);
        descMapaConfig = (AttributeListItem) findViewById(R.id.mapaAttributeDesc);
        numDespachoMapaConfig = (AttributeListItem) findViewById(R.id.mapaAttributeNumHabita);
        tamXMapaConfig = (AttributeListItem) findViewById(R.id.mapaAttributeTamX);
        tamYMapaConfig = (AttributeListItem) findViewById(R.id.mapaAttributeTamY);
        tamZMapaConfig = (AttributeListItem) findViewById(R.id.mapaAttributeTamZ);
        markerXMapaConfig = (AttributeListItem) findViewById(R.id.mapaAttributeMarkerX);
        markerYMapaConfig = (AttributeListItem) findViewById(R.id.mapaAttributeMarkerY);
        markerZMapaConfig = (AttributeListItem) findViewById(R.id.mapaAttributeMarkerZ);
        editElementosMapaConfig = (ElementosListView) findViewById(R.id.mapaAttributeConfigElementosView);
        nombreMapaConfig.setOnClickListener(this);
        descMapaConfig.setOnClickListener(this);
        numDespachoMapaConfig.setOnClickListener(this);
        tamXMapaConfig.setOnClickListener(this);
        tamYMapaConfig.setOnClickListener(this);
        tamZMapaConfig.setOnClickListener(this);
        markerXMapaConfig.setOnClickListener(this);
        markerYMapaConfig.setOnClickListener(this);
        markerZMapaConfig.setOnClickListener(this);
        editElementosMapaConfig.setListener(this);
        nombreMapaConfig.setSummaryText(mapaEditar.getNombre());
        descMapaConfig.setSummaryText(mapaEditar.getDescripcion());
        numDespachoMapaConfig.setSummaryText(mapaEditar.getNumDespacho()+"");
        tamXMapaConfig.setSummaryText(""+mapaEditar.tam[0]);
        tamYMapaConfig.setSummaryText(""+mapaEditar.tam[1]);
        tamZMapaConfig.setSummaryText(""+mapaEditar.tam[2]);
        markerXMapaConfig.setSummaryText(""+mapaEditar.markerPos[0]);
        markerYMapaConfig.setSummaryText(""+mapaEditar.markerPos[1]);
        markerZMapaConfig.setSummaryText(""+mapaEditar.markerPos[2]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_edit_map,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbarSaveEditMap:
                //TODO: checkValues
                if(editMap) {
                    Modelo.getInstance().deleteMapa(mapaOriginal);
                }
                Modelo.getInstance().addMapa(mapaEditar);
                Modelo.getInstance().setMapaEditar(null);
                Modelo.getInstance().setMapaOriginal(null);
                Toast.makeText(this,"Mapa guardado",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.toolbarHelpEditMap:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void crearDialogAyuda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ayuda")
                .setMessage("Aquí podrá crear o editar una nueva ubicación.\n" +
                        "Permite configurar un nombre y una descripción.\n" +
                        "Deberá introducir el número de sala a la que se corresponda esta ubicación, de forma que" +
                        "la aplicación pueda obtener los datos del sistema de climatización." +
                        "También deberá configurar el tamaño de la sala, midiendo de la siguiente forma siempre mirando de frente a la pared donde se encuentre el marcador:\n" +
                        "Ancho: de izquierda a derecha.\n" +
                        "Alto: altura de la sala.\n" +
                        "Largo: longitud de la sala desde la pared del marcador hasta la pared opuesta\n" +
                        "Para configurar la posición del marcador, el eje X se corresponde con el ancho de izquierda a derecha, el eje Y con la altura y el eje Z con la separación a la pared del marcador.\n" +
                        "Por último puede configurar los elementos de climatización disponibles para esta ubicación.");
        dialogAyuda = builder.create();
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        editText = new EditText(this);
        builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        })
                .setView(editText);
        boolean hasDialog=false;
        switch (v.getId()){
            case R.id.mapaAttributeNombre:
                editText.setText(mapaEditar.getNombre());
                builder.setTitle("Nombre")
                        .setMessage("Introduzca nombre del mapa")
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mapaEditar.setNombre(editText.getText().toString());
                                nombreMapaConfig.setSummaryText(mapaEditar.getNombre());
                            }
                        });
                hasDialog=true;
                break;
            case R.id.mapaAttributeDesc:
                editText.setText(mapaEditar.getDescripcion());
                builder.setTitle("Descripción")
                        .setMessage("Introduzca descripción del mapa")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mapaEditar.setDescripcion(editText.getText().toString());
                                descMapaConfig.setSummaryText(mapaEditar.getDescripcion());
                            }
                        });
                hasDialog=true;
                break;
            case R.id.mapaAttributeNumHabita:
                editText.setText(""+mapaEditar.getNumDespacho());
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                builder.setTitle("Numero sala")
                        .setMessage("Introduzca número de sala correspondiente al mapa")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mapaEditar.setNumDespacho(Integer.parseInt(editText.getText().toString()));
                                numDespachoMapaConfig.setSummaryText(""+mapaEditar.getNumDespacho());
                            }
                        });
                hasDialog=true;
                break;
            case R.id.mapaAttributeTamX:
                editText.setText(""+mapaEditar.tam[0]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Ancho mapa")
                        .setMessage("Introduzca el ancho del mapa (tamaño en el eje X) en cm. Se corresponde con el ancho de la pared donde está situado el marcador.")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mapaEditar.tam[0]=(Float.parseFloat(editText.getText().toString()));
                                tamXMapaConfig.setSummaryText(""+mapaEditar.tam[0]);
                            }
                        });
                hasDialog=true;
                break;
            case R.id.mapaAttributeTamY:
                editText.setText(""+mapaEditar.tam[1]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Alto mapa")
                        .setMessage("Introduzca el alto del mapa (tamaño en el eje Y) en cm. L")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mapaEditar.tam[1]=(Float.parseFloat(editText.getText().toString()));
                                tamYMapaConfig.setSummaryText(""+mapaEditar.tam[1]);
                            }
                        });
                builder.create().show();
                break;
            case R.id.mapaAttributeTamZ:
                editText.setText(""+mapaEditar.tam[2]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Largo mapa")
                        .setMessage("Introduzca el largo del mapa (tamaño en el eje Z) en cm")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mapaEditar.tam[2]=(Float.parseFloat(editText.getText().toString()));
                                tamZMapaConfig.setSummaryText(""+mapaEditar.tam[2]);
                            }
                        });
                hasDialog=true;
                break;
            case R.id.mapaAttributeMarkerX:
                editText.setText(""+mapaEditar.markerPos[0]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Posición X Marcador")
                        .setMessage("Introduzca la posición en el eje X del marcador en el mapa en cm, midiendo desde la pared izquierda hasta el centrod del marcador.")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mapaEditar.markerPos[0]=(Float.parseFloat(editText.getText().toString()));
                                markerXMapaConfig.setSummaryText(""+mapaEditar.markerPos[0]);
                            }
                        });
                hasDialog=true;
                break;
            case R.id.mapaAttributeMarkerY:
                editText.setText(""+mapaEditar.markerPos[1]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Posición Y Marcador")
                        .setMessage("Introduzca la posición en el eje Y del marcador en el mapa en cm, midiendo desde el suelo hasta el centro del marcador.")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mapaEditar.markerPos[1]=(Float.parseFloat(editText.getText().toString()));
                                markerYMapaConfig.setSummaryText(""+mapaEditar.markerPos[1]);
                            }
                        });
                hasDialog=true;
                break;
            case R.id.mapaAttributeMarkerZ:
                editText.setText(""+mapaEditar.markerPos[2]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Posición Z Marcador")
                        .setMessage("Introduzca la posición en el eje Z del marcador en el mapa en cm, midiendo la separación entre la pared de fondo y el marcador.")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mapaEditar.markerPos[2]=(Float.parseFloat(editText.getText().toString()));
                                markerZMapaConfig.setSummaryText(""+mapaEditar.markerPos[2]);
                            }
                        });
                hasDialog=true;
                break;
        }
        if(hasDialog){
            Dialog dialog = builder.create();
            dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            dialog.show();
            editText.requestFocus();
        }
    }

    @Override
    public void onSelectedElementoChanged() {
        Log.d(LOGTAG,"ElementChanged");
    }

    @Override
    public void onAddElementoPushed() {
        Intent intent = new Intent(this,EditElementoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onEditElementoPushed(MapElement element) {
        Intent intent = new Intent(this,EditElementoActivity.class);
        Modelo.getInstance().setEditMapElement(element);
        startActivity(intent);
    }

    @Override
    public void onDelElementoPushed(MapElement element) {
        //TODO: alertdialog?
        mapaOriginal.mapaElements.remove(element);
        editElementosMapaConfig.delElemento(element);
    }
}
