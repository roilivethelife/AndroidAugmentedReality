package com.example.roi.climaar.modelo;

import android.content.Context;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.figuras.Obj;
import com.example.roi.climaar.modelo.figuras.PanelTermostato;
import com.example.roi.climaar.modelo.figuras.SueloRadiante;
import com.example.roi.climaar.modelo.figuras.Ventilador;
import com.example.roi.climaar.modelo.despacho.Despacho;
import com.example.roi.climaar.modelo.despacho.DespachoElement;

import java.util.ArrayList;

/**
 * Created by roi on 11/06/17.
 */

public class Modelo{

    private static Modelo instance = new Modelo();
    private Context context;
    private ArrayList<Despacho> despachos;

    private Despacho despachoEditar;
    private Despacho despachoOriginal;
    private DespachoElement editDespachoElement;

    private Modelo(){
        despachos = new ArrayList<>();
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
        return despachos.size();
    }

    public Despacho getMapa(int index){
        if(index<0||index>= despachos.size()){
            return null;
        }
        return despachos.get(index);
    }

    public boolean deleteMapa(int index){
        if(index<0||index>= despachos.size()){
            return false;
        }
        despachos.remove(index);
        return true;
    }

    public boolean deleteMapa(Despacho despacho){
        return despachos.remove(despacho);
    }


    /*
    public ArrayList<Despacho> getMapas() {
        if(despachos.size()==0){
            despachos.add(createLoadDefaultMap());
        }
        return despachos;
    }*/

    public Despacho createLoadDefaultMap(){
            float[] tamMap = new float[3];
            tamMap[0] = 421.6f;
            tamMap[1] = 227.6f;
            tamMap[2] = 337.8f;
            float[] markerPos = new float[3];
            markerPos[0]=114f;//Distancia desde pared izq
            markerPos[1]=89.3f;//Altura desde suelo
            markerPos[2]=40f;//Distancia desde pared
            Despacho map = new Despacho("CuartoFondo",tamMap,markerPos);
            map.setDescripcion("DefaultMap: cuartoFondo");
            DespachoElement cuboCentro= null;
            DespachoElement habitacionElement = null;
            DespachoElement sueloRadiante = null;
            cuboCentro = new DespachoElement("CuboCentro",new Obj(R.raw.cubo));
            cuboCentro.pos[0]=markerPos[0];
            cuboCentro.pos[1]=markerPos[1];
            cuboCentro.pos[2]=markerPos[2];
            //cuboCentro.alignCamera=true;
            map.despachoElements.add(cuboCentro);

            sueloRadiante = new DespachoElement("Suelo",new SueloRadiante(421,337));
            sueloRadiante.pos[1]=-20f;
            map.despachoElements.add(sueloRadiante);
            DespachoElement fan = new DespachoElement("Ventilador",new Ventilador(true));
            //Posicion = posicion lampara
            fan.pos[0] = 197f;
            fan.pos[1] = 227.6f;
            fan.pos[2] = 166.8f;
            map.despachoElements.add(fan);
            //map.despachoElements.add(habitacionElement);
            DespachoElement panel = new DespachoElement("Panel",new PanelTermostato(209));
            //texto.alignCamera=true;
            panel.pos[0]=markerPos[0];
            panel.pos[1]=markerPos[1];
            panel.pos[2]=markerPos[2];
            map.despachoElements.add(panel);
            return map;
        }

    public Despacho getDespachoEditar() {
        return despachoEditar;
    }

    public void setDespachoEditar(Despacho despachoEditar) {
        this.despachoEditar = despachoEditar;
    }

    public Despacho getDespachoOriginal() {
        return despachoOriginal;
    }

    public void setDespachoOriginal(Despacho despachoOriginal) {
        this.despachoOriginal = despachoOriginal;
    }

    public void setEditDespachoElement(DespachoElement editDespachoElement) {
        this.editDespachoElement = editDespachoElement;
    }

    public DespachoElement getEditDespachoElement() {
        return editDespachoElement;
    }

    public void addMapa(Despacho despachoEditar) {
        if(!despachos.contains(despachoEditar))
            despachos.add(despachoEditar);
    }


    public void guardarObjetos(){
        //TODO: descomentar funciones
        /*
        try
        {
            //Guardar despachos
            ObjectOutputStream oos = new ObjectOutputStream(
                    context.openFileOutput(FILE_MAPAS,Context.MODE_PRIVATE));
            for (Despacho map : despachos) {
                oos.writeObject(map);
            }
            Object end = null;
            oos.writeObject(end);
            oos.close();
            Toast.makeText(context,"Mapas guardados, num="+despachos.size(),Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e("AppInstance", "guardarObjetos(): Error al guardar el fichero "+
                    FILE_MAPAS+"en memoria interna");
            ex.printStackTrace();
        }*/
    }

    public void leerObjetos(){
        //TODO: descomentar funciones
        /*try
        {
            despachos = new ArrayList<>();
            //Leer despachos
            ObjectInputStream ois = new ObjectInputStream(
                    context.openFileInput(FILE_MAPAS));

            Object obj = ois.readObject();//leer primer objeto
            while (obj!=null){
                if(obj instanceof Despacho){
                    Despacho map = (Despacho)obj;
                    for (MapaElement mElement :
                            map.despachoElements) {

                    }
                    despachos.add(map);
                }
                obj = ois.readObject();//leer siguiente objeto
                ois.close();
            }
            if(despachos.size()==0){
                despachos.add(MapControler.createLoadDefaultMap());
            }
            Toast.makeText(context,"Mapas leidos: num="+despachos.size(),Toast.LENGTH_SHORT).show();

        }
        catch (EOFException ex)
        {
            Log.e("AppInstance", "leer(): Error al leer el fichero "+
                    FILE_MAPAS+"en memoria interna");
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        //despachos = new ArrayList<>();
        //despachos.add(MapControler.createLoadDefaultMap());
    }
}
