package com.example.roi.climaar.vista;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.example.roi.climaar.modelo.mapa.MapElement;

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