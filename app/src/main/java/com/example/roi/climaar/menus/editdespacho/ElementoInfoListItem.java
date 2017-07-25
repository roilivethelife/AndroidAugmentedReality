package com.example.roi.climaar.menus.editdespacho;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.roi.climaar.R;

/**
 * Created by roi on 25/06/17.
 */

class ElementoInfoListItem extends LinearLayout {
    TextView nombre;
    TextView desc;

    public ElementoInfoListItem(Context context) {
        super(context);
        inicializar();
    }

    public ElementoInfoListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inicializar();
    }

    void inicializar(){
        //Utilizamos el layout menu opciones layout
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.list_item_name_desc, this, true);
        nombre = (TextView) findViewById(R.id.textViewNombre);
        desc = (TextView) findViewById(R.id.textViewDescripcion);
    }

    void  setNombreText(String nombre){
        this.nombre.setText(nombre);
    }

    void setDescText(String desc){
        this.desc.setText(desc);
    }
}
