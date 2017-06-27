package com.example.roi.climaar.modelo;

import android.content.Context;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.figuras.Obj;
import com.example.roi.climaar.modelo.figuras.PanelTermostato;
import com.example.roi.climaar.modelo.figuras.SueloRadiante;
import com.example.roi.climaar.modelo.figuras.Texto.GLText;
import com.example.roi.climaar.modelo.figuras.Ventilador;
import com.example.roi.climaar.modelo.mapa.MapElement;
import com.example.roi.climaar.modelo.mapa.Mapa;

import java.util.ArrayList;

/**
 * Created by roi on 11/06/17.
 */

public class Modelo{

    private static Modelo instance = new Modelo();
    private Context context;
    private ArrayList<Mapa> mapas;

    private Mapa mapaEditar;
    private Mapa mapaOriginal;
    private MapElement editMapElement;

    private Modelo(){
        mapas = new ArrayList<>();
    }

    public static Modelo getInstance(){
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public int getMapasSize(){
        return mapas.size();
    }

    public Mapa getMapa(int index){
        if(index<0||index>=mapas.size()){
            return null;
        }
        return mapas.get(index);
    }

    public boolean deleteMapa(int index){
        if(index<0||index>=mapas.size()){
            return false;
        }
        mapas.remove(index);
        return true;
    }

    public boolean deleteMapa(Mapa mapa){
        return mapas.remove(mapa);
    }


    /*
    public ArrayList<Mapa> getMapas() {
        if(mapas.size()==0){
            mapas.add(createLoadDefaultMap());
        }
        return mapas;
    }*/

    public Mapa createLoadDefaultMap(){
            float[] tamMap = new float[3];
            tamMap[0] = 421.6f;
            tamMap[1] = 227.6f;
            tamMap[2] = 337.8f;
            float[] markerPos = new float[3];
            markerPos[0]=114f;//Distancia desde pared izq
            markerPos[1]=89.3f;//Altura desde suelo
            markerPos[2]=40f;//Distancia desde pared
            Mapa map = new Mapa("CuartoFondo",tamMap,markerPos);
            map.setDescripcion("DefaultMap: cuartoFondo");
            MapElement cuboCentro= null;
            MapElement habitacionElement = null;
            MapElement sueloRadiante = null;
            cuboCentro = new MapElement("CuboCentro",new Obj(R.raw.cubo));
            cuboCentro.pos[0]=markerPos[0];
            cuboCentro.pos[1]=markerPos[1];
            cuboCentro.pos[2]=markerPos[2];
            //cuboCentro.alignCamera=true;
            map.mapaElements.add(cuboCentro);

            sueloRadiante = new MapElement("Suelo",new SueloRadiante(421,337));
            sueloRadiante.pos[0]=-20f;
            map.mapaElements.add(sueloRadiante);
            MapElement fan = new MapElement("Ventilador",new Ventilador(true));
            //Posicion = posicion lampara
            fan.pos[0] = 197f;
            fan.pos[1] = 227.6f;
            fan.pos[2] = 166.8f;
            map.mapaElements.add(fan);
            //map.mapaElements.add(habitacionElement);
            MapElement panel = new MapElement("Panel",new PanelTermostato(209));
            //texto.alignCamera=true;
            panel.pos[0]=markerPos[0];
            panel.pos[1]=markerPos[1];
            panel.pos[2]=markerPos[2];
            map.mapaElements.add(panel);
            return map;
        }

    public Mapa getMapaEditar() {
        return mapaEditar;
    }

    public void setMapaEditar(Mapa mapaEditar) {
        this.mapaEditar = mapaEditar;
    }

    public Mapa getMapaOriginal() {
        return mapaOriginal;
    }

    public void setMapaOriginal(Mapa mapaOriginal) {
        this.mapaOriginal = mapaOriginal;
    }

    public void setEditMapElement(MapElement editMapElement) {
        this.editMapElement = editMapElement;
    }

    public MapElement getEditMapElement() {
        return editMapElement;
    }

    public void addMapa(Mapa mapaEditar) {
        if(!mapas.contains(mapaEditar))
            mapas.add(mapaEditar);
    }
}
