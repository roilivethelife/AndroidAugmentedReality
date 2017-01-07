package com.example.roi.testvuforia.graficos;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.figuras.Obj;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by roi on 19/12/16.
 */

public class MapaControler {
    private String mapName;

    private ArrayList<MapElement> mapElements;
    private MapElement colisionElement;
    private MapElement habitacionElement;

    private float[] tamHabitacion = {360,300,450};//cm
    private float[] posicionReferencia = {180,120,0};//cm


    private AABB aabb;

    Context context;

    public MapaControler(String mapName, Context context){
        this.mapName=mapName;
        mapElements = new ArrayList<>();
        AABB.Vec3D min = new AABB.Vec3D(-posicionReferencia[0],-posicionReferencia[1],-posicionReferencia[2]);
        AABB.Vec3D max = new AABB.Vec3D(tamHabitacion[0]-posicionReferencia[0],tamHabitacion[1]-posicionReferencia[1],tamHabitacion[2]-posicionReferencia[2]);
        //Todo: "deberia funcionar"
        aabb = new AABB(min, max);
        this.context=context;
    }

    public void cargar(){
        MapElement cuboCentro=new MapElement("Centro",context, R.raw.cubo);
        mapElements.add(cuboCentro);

        colisionElement= new MapElement("Colision",context, R.raw.cubo);
        colisionElement.visible=false;
        mapElements.add(colisionElement);


        habitacionElement = new MapElement("Wireframe",context,R.raw.paredes);
        habitacionElement.scale=Arrays.copyOf(tamHabitacion,tamHabitacion.length);
        //habitacionElement.pos=Arrays.copyOf(posicionReferencia,posicionReferencia.length);
        //habitacionElement.pos[0]=posicionReferencia[0];
        //habitacionElement.pos[1]=-posicionReferencia[1];
        habitacionElement.obj.setModoDibujado(GLES20.GL_LINE_LOOP);
        mapElements.add(habitacionElement);
    }

    public boolean isInside(float[] camPos){
        //x=ancho, y=alto, z=largo
        return camPos[0]>0.0f && camPos[0]<tamHabitacion[0] &&
                camPos[1]>0.0f && camPos[1]<tamHabitacion[1] &&
                camPos[2]>0.0f && camPos[2]<tamHabitacion[2];
    }

    public void addObject(MapElement mapElement){
        mapElements.add(mapElement);
    }

    public void dibujar(Shader shader, float[] modelViewMatrix){
        for (MapElement mapElement : mapElements) {
            float[] tempModelViewMatrix = modelViewMatrix.clone();
            Matrix.translateM(tempModelViewMatrix,0,mapElement.pos[0],mapElement.pos[1],mapElement.pos[2]);
            Matrix.scaleM(tempModelViewMatrix,0,mapElement.scale[0],mapElement.scale[1],mapElement.scale[2]);
            GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(),1,false,tempModelViewMatrix,0);
            mapElement.dibujar(shader);
        }
    }

    /**
     *
     * @param camPos [IN]posicion de la camara
     * @param camDir [IN]direcion de la camara
     * @param colisionPoint [OUT] float[3] punto de colision
     * @return boolean
     */
    public boolean colisionRayoPared(float[] camPos, float[] camDir, float[] colisionPoint){
        if(!isInside(camPos)){
            return false;
        }
        AABB.Ray3D camara = new AABB.Ray3D(AABB.Vec3D.fromVector(camPos), AABB.Vec3D.fromVector(camDir));
        AABB.Vec3D colision = aabb.intersectsRay(camara);
        if(colision!=null){
            colisionPoint[0]=colision.x;
            colisionPoint[1]=colision.y;
            colisionPoint[2]=colision.z;
            return true;
        }
        return false;
    }

    public MapElement getColisionElement() {
        return colisionElement;
    }
}



