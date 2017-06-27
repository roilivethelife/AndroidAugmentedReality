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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.mapa.MapElement;
import com.example.roi.climaar.modelo.mapa.Mapa;

import org.w3c.dom.Attr;

import java.util.ArrayList;

public class EditElementoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOGTAG = "EditMapActivity";
    private AttributeListItem attributeNombre;
    private AttributeListItem attributeTipo;
    private AttributeListItem attributeTamX;
    private AttributeListItem attributeTamY;
    private AttributeListItem attributeTamZ;
    private AttributeListItem attributePosX;
    private AttributeListItem attributePosY;
    private AttributeListItem attributePosZ;

    private Dialog dialogAyuda;

    private EditText editText;

    private Mapa mapaEditar;
    private MapElement elementOriginal;
    private MapElement elementEditar;
    private boolean editElement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_element);
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


        elementOriginal = null;
        mapaEditar  = Modelo.getInstance().getMapaEditar();
        elementOriginal = Modelo.getInstance().getEditMapElement();
        if(elementOriginal!=null){
            editElement = true;
            elementEditar = new MapElement(elementOriginal);
            myToolbar.setTitle("Editar elemento");
        }else{
            editElement = false;
            elementEditar = elementOriginal;//mapa nuevo
            elementEditar = new MapElement("NuevoElemento",null);
            myToolbar.setTitle("Nuevo mapa");
        }
        bindListItemElementoConfig();
        crearDialogAyuda();
    }

    private void crearDialogAyuda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ayuda")
                .setMessage("Aquí podrá crear o editar un elemento de climatización virtual.\n" +
                        "Permite configurar un nombre, el tipo de elemento de climatización, su posición, y su tamaño.");
        dialogAyuda = builder.create();
    }

    private void bindListItemElementoConfig() {
        attributeNombre = (AttributeListItem) findViewById(R.id.elementoAttributeName);
        attributeTipo = (AttributeListItem) findViewById(R.id.elementoAttributeType);
        attributeTamX = (AttributeListItem) findViewById(R.id.elementoAttributeTamX);
        attributeTamY = (AttributeListItem) findViewById(R.id.elementoAttributeTamY);
        attributeTamZ = (AttributeListItem) findViewById(R.id.elementoAttributeTamZ);
        attributePosX = (AttributeListItem) findViewById(R.id.elementoAttributePosX);
        attributePosY = (AttributeListItem) findViewById(R.id.elementoAttributePosY);
        attributePosZ = (AttributeListItem) findViewById(R.id.elementoAttributePosZ);
        attributeNombre.setOnClickListener(this);
        attributeTipo.setOnClickListener(this);
        attributeTamX.setOnClickListener(this);
        attributeTamY.setOnClickListener(this);
        attributeTamZ.setOnClickListener(this);
        attributePosX.setOnClickListener(this);
        attributePosY.setOnClickListener(this);
        attributePosZ.setOnClickListener(this);
        attributeNombre.setSummaryText(elementEditar.getName());
        attributeTipo.setSummaryText(elementEditar.getFigureType());
        attributeTamX.setSummaryText(""+elementEditar.scale[0]);
        attributeTamY.setSummaryText(""+elementEditar.scale[1]);
        attributeTamZ.setSummaryText(""+elementEditar.scale[2]);
        attributePosX.setSummaryText(""+elementEditar.pos[0]);
        attributePosY.setSummaryText(""+elementEditar.pos[1]);
        attributePosZ.setSummaryText(""+elementEditar.pos[2]);
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
                guardarSalir();
                break;
            case R.id.toolbarHelpEditMap:
                dialogAyuda.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    void guardarSalir(){
        //TODO: checkValues
        //TODO: guardar
        if (editElement) {
            mapaEditar.mapaElements.remove(elementOriginal);
        }
        mapaEditar.mapaElements.add(elementEditar);
        Modelo.getInstance().setMapaEditar(mapaEditar);

        Toast.makeText(this,"Elemento guardado",Toast.LENGTH_SHORT).show();
        finish();
    }

    void salir(){
        finish();
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
            case R.id.elementoAttributeName:
                editText.setText(elementEditar.getName());
                builder.setTitle("Nombre")
                        .setMessage("Introduzca nombre del elemento")
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                elementEditar.setName(editText.getText().toString());
                                attributeNombre.setSummaryText(elementEditar.getName());
                            }
                        });
                hasDialog=true;
                break;
            case R.id.elementoAttributeType:
                //TODO: tipo de elemento
                break;
            case R.id.elementoAttributePosX:
                editText.setText(""+elementEditar.pos[0]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Posición X")
                        .setMessage("Introduzca la posición en el eje X del elemento")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                elementEditar.pos[0]=(Float.parseFloat(editText.getText().toString()));
                                attributePosX.setSummaryText(""+elementEditar.pos[0]);
                            }
                        });
                hasDialog=true;
                break;
            case R.id.elementoAttributePosY:
                editText.setText(""+elementEditar.pos[1]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Posición Y")
                        .setMessage("Introduzca la posición en el eje Y del elemento")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                elementEditar.pos[1]=(Float.parseFloat(editText.getText().toString()));
                                attributePosY.setSummaryText(""+elementEditar.pos[1]);
                            }
                        });
                hasDialog=true;
                break;
            case R.id.elementoAttributePosZ:
                editText.setText(""+elementEditar.pos[2]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Posición Z")
                        .setMessage("Introduzca la posición en el eje Z del elemento")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                elementEditar.pos[2]=(Float.parseFloat(editText.getText().toString()));
                                attributePosZ.setSummaryText(""+elementEditar.pos[2]);
                            }
                        });
                hasDialog=true;
                break;
            case R.id.elementoAttributeTamX:
                editText.setText(""+elementEditar.scale[0]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Tamaño X")
                        .setMessage("Introduzca tamaño en el eje X del elemento")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                elementEditar.scale[0]=(Float.parseFloat(editText.getText().toString()));
                                attributePosZ.setSummaryText(""+elementEditar.scale[0]);
                            }
                        });
                hasDialog=true;
                break;
            case R.id.elementoAttributeTamY:
                editText.setText(""+elementEditar.scale[1]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Tamaño Y")
                        .setMessage("Introduzca tamaño en el eje Y del elemento")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                elementEditar.scale[1]=(Float.parseFloat(editText.getText().toString()));
                                attributePosZ.setSummaryText(""+elementEditar.scale[1]);
                            }
                        });
                hasDialog=true;
                break;
            case R.id.elementoAttributeTamZ:
                editText.setText(""+elementEditar.scale[2]);
                editText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("Tamaño Z")
                        .setMessage("Introduzca tamaño en el eje Z del elemento")
                        .setView(editText)
                        .setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                elementEditar.scale[2]=(Float.parseFloat(editText.getText().toString()));
                                attributePosZ.setSummaryText(""+elementEditar.scale[2]);
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
    public void onBackPressed() {
        super.onBackPressed();
        //TODO: AlertDialog preguntando si salir() o guardarSalir(), eliminar super
    }
}
