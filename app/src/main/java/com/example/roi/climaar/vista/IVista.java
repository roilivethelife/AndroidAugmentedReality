package com.example.roi.climaar.vista;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

/**
 * 
 */
public interface IVista {


    void addGLView(View v);

    void mostrarTextoDebug(String text);

    void mostrarBarraCargando(boolean visible);

    void mostrarToast(String text);

    MenuOpciones getMenuOpciones();
    FloatingActionButton getFAB();

}