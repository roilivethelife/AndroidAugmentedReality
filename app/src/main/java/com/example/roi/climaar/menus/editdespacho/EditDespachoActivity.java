package com.example.roi.climaar.menus.editdespacho;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roi.climaar.R;
import com.example.roi.climaar.menus.AttributeListItem;
import com.example.roi.climaar.menus.editelemento.EditElementoActivity;
import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.figuras.PanelExterior;
import com.example.roi.climaar.modelo.figuras.PanelTermostato;
import com.example.roi.climaar.modelo.figuras.SueloRadiante;
import com.example.roi.climaar.modelo.figuras.Ventilador;
import com.example.roi.climaar.modelo.despacho.Despacho;
import com.example.roi.climaar.modelo.despacho.DespachoElement;

public class EditDespachoActivity extends AppCompatActivity implements View.OnClickListener, ElementosListView.ElementosListViewInterface {

    private static final String LOGTAG = "EditDespachoActivity";
    private AttributeListItem nombreDespachoConfig;
    private AttributeListItem descDespachoConfig;
    private AttributeListItem numDespachoConfig;
    private AttributeListItem tamXDespachoConfig;
    private AttributeListItem tamYDespachoConfig;
    private AttributeListItem tamZDespachoConfig;
    private AttributeListItem markerXDespachoConfig;
    private AttributeListItem markerYDespachoConfig;
    private AttributeListItem markerZDespachoConfig;
    private ElementosListView editElementosConfig;
    private EditText editText;


    private Despacho despachoOriginal;
    private Despacho despachoEditar;
    private DespachoElement mSR;
    private DespachoElement mPanelTermostato;
    private DespachoElement mVentilador;
    private DespachoElement mPanelExterior;


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


        despachoOriginal = Modelo.getInstance().getDespachoOriginal();
        if(despachoOriginal !=null){
            editMap = true;
            despachoEditar = new Despacho(despachoOriginal);
            myToolbar.setTitle("Editar despacho");

            //Buscar elementos predeterminados
            for (DespachoElement me : despachoEditar.despachoElements) {
                switch (me.getFigureType()) {
                    case PANEL_TERMOSTATO:
                        mPanelTermostato = me;
                        break;
                    case SUELO_RADIANTE:
                        mSR = me;
                        break;
                    case VENTILADOR:
                        mVentilador = me;
                        break;
                    case PANEL_EXTERIOR:
                        mPanelExterior = me;
                        break;
                }
            }
        }else{
            editMap = false;
            despachoOriginal = new Despacho("nuevo despacho");
            despachoEditar = despachoOriginal;//despacho nuevo
            myToolbar.setTitle("Nuevo despacho");
        }
        addDespachoElements();//añadir elementos predeterminados
        Modelo.getInstance().setDespachoEditar(despachoEditar);

        bindListItemMapaConfig();
        crearDialogAyuda();
    }


    private void addDespachoElements(){
        if(mSR==null) {
            mSR = new DespachoElement("Suelo Radiante", new SueloRadiante(0, 0));
            despachoEditar.despachoElements.add(mSR);
        }
        if(mPanelTermostato ==null) {
            mPanelTermostato = new DespachoElement("Panel información", new PanelTermostato(0));
            despachoEditar.despachoElements.add(mPanelTermostato);
        }
        if(mVentilador==null) {
            mVentilador = new DespachoElement("Ventilador", new Ventilador(true));
            despachoEditar.despachoElements.add(mVentilador);
        }
        if(mPanelExterior==null){
            mPanelExterior = new DespachoElement("Panel Exterior",new PanelExterior());
            mPanelExterior.alignCamera=true;
            despachoEditar.despachoElements.add(mPanelExterior);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        editElementosConfig.delAllElements();
        despachoOriginal = Modelo.getInstance().getDespachoOriginal();
        despachoEditar = Modelo.getInstance().getDespachoEditar();
        for (DespachoElement despachoElement :
                despachoEditar.despachoElements) {
            editElementosConfig.addElemento(despachoElement);
        }
    }

    private void bindListItemMapaConfig() {
        nombreDespachoConfig = (AttributeListItem) findViewById(R.id.despachoAttributeNombre);
        descDespachoConfig = (AttributeListItem) findViewById(R.id.despachoAttributeDesc);
        numDespachoConfig = (AttributeListItem) findViewById(R.id.despachoAttributeNumDespacho);
        tamXDespachoConfig = (AttributeListItem) findViewById(R.id.despachoAttributeTamX);
        tamYDespachoConfig = (AttributeListItem) findViewById(R.id.despachoAttributeTamY);
        tamZDespachoConfig = (AttributeListItem) findViewById(R.id.despachoAttributeTamZ);
        markerXDespachoConfig = (AttributeListItem) findViewById(R.id.despachoAttributeMarkerX);
        markerYDespachoConfig = (AttributeListItem) findViewById(R.id.depachoAttributeMarkerY);
        markerZDespachoConfig = (AttributeListItem) findViewById(R.id.depachoAttributeMarkerZ);
        editElementosConfig = (ElementosListView) findViewById(R.id.despachoAttributeConfigElementosView);
        nombreDespachoConfig.setOnClickListener(this);
        descDespachoConfig.setOnClickListener(this);
        numDespachoConfig.setOnClickListener(this);
        tamXDespachoConfig.setOnClickListener(this);
        tamYDespachoConfig.setOnClickListener(this);
        tamZDespachoConfig.setOnClickListener(this);
        markerXDespachoConfig.setOnClickListener(this);
        markerYDespachoConfig.setOnClickListener(this);
        markerZDespachoConfig.setOnClickListener(this);
        editElementosConfig.setListener(this);
        nombreDespachoConfig.setSummaryText(despachoEditar.getNombre());
        descDespachoConfig.setSummaryText(despachoEditar.getDescripcion());
        numDespachoConfig.setSummaryText(despachoEditar.getNumDespacho()+"");
        tamXDespachoConfig.setSummaryText(""+ despachoEditar.tam[0]);
        tamYDespachoConfig.setSummaryText(""+ despachoEditar.tam[1]);
        tamZDespachoConfig.setSummaryText(""+ despachoEditar.tam[2]);
        markerXDespachoConfig.setSummaryText(""+ despachoEditar.markerPos[0]);
        markerYDespachoConfig.setSummaryText(""+ despachoEditar.markerPos[1]);
        markerZDespachoConfig.setSummaryText(""+ despachoEditar.markerPos[2]);
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

                updateLinkedMapAttibutes();
                if(editMap) {
                    Modelo.getInstance().deleteMapa(despachoOriginal);
                }
                Modelo.getInstance().addMapa(despachoEditar);
                Modelo.getInstance().setDespachoEditar(null);
                Modelo.getInstance().setDespachoOriginal(null);
                Toast.makeText(this,"Despacho guardado",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.toolbarHelpEditMap:
                dialogAyuda.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void crearDialogAyuda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ayuda")
                .setMessage("Aquí podrá añadir o editar un despacho.\n" +
                        "Permite configurar un nombre y una descripción.\n" +
                        "Deberá introducir el número de sala a la que se corresponda esta ubicación, de forma que" +
                        "la aplicación pueda obtener los datos del sistema de climatización." +
                        "También deberá configurar el tamaño del despacho, midiendo de la siguiente forma siempre mirando de frente a la pared donde se encuentre el marcador:\n" +
                        "Ancho: de izquierda a derecha.\n" +
                        "Alto: altura de la sala.\n" +
                        "Largo: longitud de la sala desde la pared del marcador hasta la pared opuesta\n" +
                        "Para configurar la posición del marcador, el eje X se corresponde con el ancho de izquierda a derecha, el eje Y con la altura y el eje Z con la separación a la pared del marcador.\n" +
                        "Por último puede configurar los elementos de climatización disponibles para este despacho.");
        dialogAyuda = builder.create();
    }

    private void updateLinkedMapAttibutes(){
        ((PanelTermostato) mPanelTermostato.getFigura()).setNumDespacho(despachoEditar.getNumDespacho());
        mPanelTermostato.pos[0]= despachoEditar.markerPos[0];
        mPanelTermostato.pos[1]= despachoEditar.markerPos[1];
        mPanelTermostato.pos[2]= despachoEditar.markerPos[2];
        ((SueloRadiante)mSR.getFigura()).setDimensions(despachoEditar.tam[0], despachoEditar.tam[2]);
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
            case R.id.despachoAttributeNombre:
                editText.setText(despachoEditar.getNombre());
                builder.setTitle("Nombre")
                        .setMessage("Introduzca nombre del despacho")
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                despachoEditar.setNombre(editText.getText().toString());
                                nombreDespachoConfig.setSummaryText(despachoEditar.getNombre());
                            }
                        });
                hasDialog=true;
                break;
            case R.id.despachoAttributeDesc:
                editText.setText(despachoEditar.getDescripcion());
                builder.setTitle("Descripción")
                        .setMessage("Introduzca descripción del despacho")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                despachoEditar.setDescripcion(editText.getText().toString());
                                descDespachoConfig.setSummaryText(despachoEditar.getDescripcion());
                            }
                        });
                hasDialog=true;
                break;
            case R.id.despachoAttributeNumDespacho:
                editText.setText(""+ despachoEditar.getNumDespacho());
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                builder.setTitle("Numero despacho")
                        .setMessage("Introduzca número de sala correspondiente al despacho")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                despachoEditar.setNumDespacho(Integer.parseInt(editText.getText().toString()));
                                numDespachoConfig.setSummaryText(""+ despachoEditar.getNumDespacho());
                                updateLinkedMapAttibutes();
                            }
                        });
                hasDialog=true;
                break;
            case R.id.despachoAttributeTamX:
                editText.setText(""+ despachoEditar.tam[0]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Ancho despacho")
                        .setMessage("Introduzca el ancho del despacho (tamaño en el eje X) en cm. Se corresponde con el ancho de la pared donde está situado el marcador.")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                despachoEditar.tam[0]=(Float.parseFloat(editText.getText().toString()));
                                tamXDespachoConfig.setSummaryText(""+ despachoEditar.tam[0]);
                                updateLinkedMapAttibutes();
                            }
                        });
                hasDialog=true;
                break;
            case R.id.despachoAttributeTamY:
                editText.setText(""+ despachoEditar.tam[1]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Alto despacho")
                        .setMessage("Introduzca el alto del despacho (tamaño en el eje Y) en cm.")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                despachoEditar.tam[1]=(Float.parseFloat(editText.getText().toString()));
                                tamYDespachoConfig.setSummaryText(""+ despachoEditar.tam[1]);
                            }
                        });
                builder.create().show();
                break;
            case R.id.despachoAttributeTamZ:
                editText.setText(""+ despachoEditar.tam[2]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Largo despacho")
                        .setMessage("Introduzca el largo del despacho (tamaño en el eje Z) en cm")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                despachoEditar.tam[2]=(Float.parseFloat(editText.getText().toString()));
                                tamZDespachoConfig.setSummaryText(""+ despachoEditar.tam[2]);
                                updateLinkedMapAttibutes();
                            }
                        });
                hasDialog=true;
                break;
            case R.id.despachoAttributeMarkerX:
                editText.setText(""+ despachoEditar.markerPos[0]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Posición X Marcador")
                        .setMessage("Introduzca la posición en el eje X del marcador en el despacho en cm, midiendo desde la pared izquierda hasta el centro del marcador.")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                despachoEditar.markerPos[0]=(Float.parseFloat(editText.getText().toString()));
                                markerXDespachoConfig.setSummaryText(""+ despachoEditar.markerPos[0]);
                                updateLinkedMapAttibutes();
                            }
                        });
                hasDialog=true;
                break;
            case R.id.depachoAttributeMarkerY:
                editText.setText(""+ despachoEditar.markerPos[1]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Posición Y Marcador")
                        .setMessage("Introduzca la posición en el eje Y del marcador en el despacho en cm, midiendo desde el suelo hasta el centro del marcador.")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                despachoEditar.markerPos[1]=(Float.parseFloat(editText.getText().toString()));
                                markerYDespachoConfig.setSummaryText(""+ despachoEditar.markerPos[1]);
                                updateLinkedMapAttibutes();
                            }
                        });
                hasDialog=true;
                break;
            case R.id.depachoAttributeMarkerZ:
                editText.setText(""+ despachoEditar.markerPos[2]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Posición Z Marcador")
                        .setMessage("Introduzca la posición en el eje Z del marcador en el depacho en cm, midiendo la separación entre la pared de fondo y el marcador.")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                despachoEditar.markerPos[2]=(Float.parseFloat(editText.getText().toString()));
                                markerZDespachoConfig.setSummaryText(""+ despachoEditar.markerPos[2]);
                                updateLinkedMapAttibutes();
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

    /*
    @Override
    public void onAddElementoPushed() {
        Intent intent = new Intent(this,EditElementoActivity.class);
        startActivity(intent);
    }*/

    @Override
    public void onEditElementoPushed(DespachoElement element) {
        Intent intent = new Intent(this,EditElementoActivity.class);
        Modelo.getInstance().setEditDespachoElement(element);
        startActivity(intent);
    }

    /*
    @Override
    public void onDelElementoPushed(DespachoElement element) {
        despachoOriginal.depachoElements.remove(element);
        editElementosConfig.delElemento(element);
    }*/
}
