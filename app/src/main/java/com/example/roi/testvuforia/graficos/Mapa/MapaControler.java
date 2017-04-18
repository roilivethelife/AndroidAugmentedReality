package com.example.roi.testvuforia.graficos.Mapa;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.AABB;
import com.example.roi.testvuforia.graficos.ObjLoader.ObjReader;
import com.example.roi.testvuforia.graficos.Shader;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by roi on 19/12/16.
 */

public class MapaControler {
    private String mapName;

    private ArrayList<MapaElement> mapaElements;
    private MapaElement colisionElement;
    private MapaElement habitacionElement;

    private float[] tamHabitacion = {18,15 ,15};//cm
    private float[] posicionReferencia = {180,120,0};//cm


    private AABB aabb;

    Context context;

    public MapaControler(String mapName, Context context){
        this.mapName=mapName;
        mapaElements = new ArrayList<>();
        AABB.Vec3D min = new AABB.Vec3D(-posicionReferencia[0],-posicionReferencia[1],-posicionReferencia[2]);
        AABB.Vec3D max = new AABB.Vec3D(tamHabitacion[0]-posicionReferencia[0],tamHabitacion[1]-posicionReferencia[1],tamHabitacion[2]-posicionReferencia[2]);
        //Todo: "deberia funcionar"
        aabb = new AABB(min, max);

        this.context=context;
    }

    public void cargarNuevoMapa(){
        MapaElement cuboCentro=new MapaElement("Centro", new ObjReader(context, R.raw.cubo).getObjeto());
        mapaElements.add(cuboCentro);

        colisionElement= new MapaElement("Colision",new ObjReader(context, R.raw.cubo).getObjeto());
        colisionElement.visible=false;
        mapaElements.add(colisionElement);


        habitacionElement = new MapaElement("Wireframe",new ObjReader(context, R.raw.pared).getObjeto());
        habitacionElement.scale=Arrays.copyOf(tamHabitacion,tamHabitacion.length);
        //habitacionElement.obj.setModoDibujado(GLES20.);
        mapaElements.add(habitacionElement);
    }



    public void addObject(MapaElement mapaElement){
        mapaElements.add(mapaElement);
    }

    public void dibujar(Shader shader, float[] modelViewMatrix){
        for (MapaElement mapaElement : mapaElements) {
            float[] tempModelViewMatrix = modelViewMatrix.clone();
            Matrix.translateM(tempModelViewMatrix,0, mapaElement.pos[0], mapaElement.pos[1], mapaElement.pos[2]);
            Matrix.scaleM(tempModelViewMatrix,0, mapaElement.scale[0], mapaElement.scale[1], mapaElement.scale[2]);
            GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(),1,false,tempModelViewMatrix,0);
            mapaElement.dibujar(shader);
        }
    }



    public MapaElement getColisionElement() {
        return colisionElement;
    }
}



