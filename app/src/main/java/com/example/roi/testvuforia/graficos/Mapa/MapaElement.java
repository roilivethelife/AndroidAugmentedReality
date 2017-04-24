package com.example.roi.testvuforia.graficos.Mapa;

import android.content.Context;

import com.example.roi.testvuforia.AppInstance;
import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.ObjLoader.ObjReader;
import com.example.roi.testvuforia.graficos.Shader;
import com.example.roi.testvuforia.graficos.figuras.Obj;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by roi on 22/12/16.
 */

public class MapaElement implements Serializable{
    String name;
    transient Obj obj;
    public boolean visible=true;
    public float[] scale={1.0f,1.0f,1.0f};
    public float[] pos= {0.0f,0.0f,0.0f};


    private int objResourceId;


    /**
     * Constructor
     * @param name Nombre del elemento del mapa
     * @param objResourceId Resouce id del obj que contiene el elemento a dibujar
     */
    public MapaElement(String name, int objResourceId) {
        this.name = name;
        this.objResourceId = objResourceId;
        try {
            cargarObj();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarObj() throws IOException {
        obj = new ObjReader(AppInstance.getInstance().getContext(),objResourceId).getObjeto();
    }

    void dibujar(Shader shader){
        if(visible){
            try {
                if(obj==null){
                    cargarObj();
                }
                obj.dibujar(shader);
            }catch (IOException e){
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}