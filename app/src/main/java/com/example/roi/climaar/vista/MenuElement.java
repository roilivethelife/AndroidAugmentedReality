package com.example.roi.climaar.vista;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.roi.climaar.R;

/**
 * Created by roi on 18/06/17.
 */

public class MenuElement extends RelativeLayout implements CompoundButton.OnCheckedChangeListener{
    private TextView txtNombre;
    private TextView txtEstado;
    private CheckBox chkVisible;
    OnCheckedChangedMenuElementListener listener;

    public MenuElement(Context context, OnCheckedChangedMenuElementListener listener){
        this(context);
        this.listener = listener;
    }

    public MenuElement(Context context) {
        super(context);
        inicializar();
    }

    public MenuElement(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar();
    }

    private void inicializar() {
        //Utilizamos el layout 'control_login' como interfaz del control
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.list_item_menu_elemento_virtual, this, true);

        //Obtenemoslas referencias a los distintos control
        txtNombre = (TextView) findViewById(R.id.textViewNombreElementoVirtual);
        txtEstado = (TextView) findViewById(R.id.textViewElementoStatus);
        chkVisible = (CheckBox) findViewById(R.id.checkBoxElementVisible);

        //Asociamos los eventos necesarios
        asignarEventos();
    }

    private void asignarEventos() {
        chkVisible.setOnCheckedChangeListener(this);
    }

    public void setNombre(String nombre) {
        txtNombre.setText(nombre);
    }

    public void setEstado(String estado) {
        txtEstado.setText(estado);
    }

    public void setChkVisible(boolean isVisible) {
        chkVisible.setChecked(isVisible);
    }

    public void setListener(OnCheckedChangedMenuElementListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(listener!=null)
            listener.onCheckedChangedElement(this, isChecked);
    }

    public interface OnCheckedChangedMenuElementListener{
        void onCheckedChangedElement(MenuElement elemento, boolean isChecked);
    }
}
