package com.example.roi.climaar.vista;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.despacho.DespachoElement;

import java.util.HashMap;

/**
 * Created by roi on 18/06/17.
 */

public class MenuOpciones extends LinearLayout implements MenuElement.OnCheckedChangedMenuElementListener{


    private static final int USE_BAROMETER = 1;
    private static final int EXIT_BUTTON = 2;
    private static final int RESET_TRACKING = 3;
    private static final int EXT_TRACKING = 4;
    private LinearLayout menuLayout;
    private LinearLayout scrollLayout;
    private ViewGroup.LayoutParams layoutParams;

    private MenuOpcionesListener listener;

    private HashMap<MenuElement, DespachoElement> elementos;


    public MenuOpciones(Context context) {
        super(context);
        inicializar();
    }

    public MenuOpciones(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inicializar();
    }

    private void inicializar(){
        //Utilizamos el layout menu opciones layout
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.menu_opciones_layout, this, true);

        //Obtenemoslas referencias a los distintos control
        menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
        scrollLayout = (LinearLayout) findViewById(R.id.layoutScrollView);

        this.layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        elementos = new HashMap<>();
    }


    public void setListener(MenuOpcionesListener listener) {
        this.listener = listener;
    }

    public void addElemento(Context context, DespachoElement despachoElement, String estado, boolean isVisible){
        MenuOpcionesListener tmplistener = listener;
        listener = null;//Desactivar listener

        MenuElement elemento = new MenuElement(context, this);
        elemento.setNombre(despachoElement.getName());
        elemento.setEstado(estado);
        elemento.setChkVisible(isVisible);
        elemento.setListener(this);
        elementos.put(elemento, despachoElement);

        //Reactivar listener
        listener = tmplistener;
    }

    public void makeMenu(Context context,String mapName, boolean hasBarometer, boolean useBarometer, boolean extendedTracking){
        MenuOpcionesListener tmplistener = listener;
        listener = null;//Desactivar listener

        //Añadir Nombre despacho
        TextView txtNombreMapa = createHeaderTextView(context,"Ubicación: "+mapName);
        scrollLayout.addView(txtNombreMapa,layoutParams);

        //Añadir elementos
        for (MenuElement menuElement : elementos.keySet()) {
            scrollLayout.addView(menuElement,layoutParams);
        }

        //Añadir "Configuracoin AR"
        TextView txtConfig = createHeaderTextView(context,"Configuracion AR");
        scrollLayout.addView(txtConfig,layoutParams);

        CheckBox chkBoxUseBarometer = createCheckBox(context,"Usar baromero",useBarometer,USE_BAROMETER);
        if(!hasBarometer) chkBoxUseBarometer.setEnabled(false);
        scrollLayout.addView(chkBoxUseBarometer,layoutParams);

        CheckBox chkBoxExtTracking = createCheckBox(context,"ExtendedTracking",extendedTracking,EXT_TRACKING);
        scrollLayout.addView(chkBoxExtTracking,layoutParams);

        Button btnResetT = createButton(context,"Resetear seguimiento",RESET_TRACKING);
        scrollLayout.addView(btnResetT,layoutParams);

        Button btnExit = createButton(context,"Salir",EXIT_BUTTON);
        scrollLayout.addView(btnExit,layoutParams);

        //Reactivar listener
        listener = tmplistener;
    }


    @Override
    public void onCheckedChangedElement(MenuElement elemento, boolean isChecked) {
        if(listener!=null)
            listener.setVisibleElementPressed(elementos.get(elemento), isChecked);
    }

    private TextView createHeaderTextView(Context context, String texto){
        TextView txtHeader = new TextView(context);
        txtHeader.setText(texto);
        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (10 * scale + 0.5f);
        txtHeader.setPadding(dpAsPixels,dpAsPixels,dpAsPixels,dpAsPixels);
        txtHeader.setTextColor(Color.BLACK);
        txtHeader.setTypeface(null, Typeface.BOLD);
        return txtHeader;
    }

    private CheckBox createCheckBox(Context context, String name, boolean isChecked, int command){
        CheckBox chkBox = new CheckBox(context);
        chkBox.setText(name);
        chkBox.setChecked(isChecked);
        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (10 * scale + 0.5f);
        chkBox.setPadding(dpAsPixels,dpAsPixels,dpAsPixels,dpAsPixels);
        switch (command){
            case USE_BAROMETER:
                chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        listener.setUseBarometerPressed(isChecked);
                    }
                });
                break;
            case EXT_TRACKING:
                chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        listener.setExtTrackingPressed(isChecked);
                    }
                });
                break;
            default:
                break;
        }
        return chkBox;

    }

    private Button createButton(Context context, String text, int command){
        Button btn = new Button(context);
        btn.setText(text);
        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (10 * scale + 0.5f);
        btn.setPadding(dpAsPixels,dpAsPixels,dpAsPixels,dpAsPixels);
        switch (command){
            case EXIT_BUTTON:
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.exitButtonPressed();
                    }
                });
                break;
            case RESET_TRACKING:
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.resetTrackingPressed();
                    }
                });
            default:
                break;
        }
        return btn;
    }

    public interface MenuOpcionesListener{
        void setUseBarometerPressed(boolean useBarometer);
        void setExtTrackingPressed(boolean extTrackinActive);
        void setVisibleElementPressed(DespachoElement element, boolean isVisible);
        void resetTrackingPressed();
        void exitButtonPressed();
    }


    public void mostrarMenu(){
        menuLayout.setVisibility(View.VISIBLE);
    }

    public void ocultarMenu(){
        menuLayout.setVisibility(View.GONE);
    }
}

