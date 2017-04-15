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

    private float[] tamHabitacion = {360,300,450};//cm
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

    public void cargar(){
        MapaElement cuboCentro=new MapaElement("Centro", new ObjReader(context, R.raw.cubo).getObjeto());
        mapaElements.add(cuboCentro);

        colisionElement= new MapaElement("Colision",new ObjReader(context, R.raw.cubo).getObjeto());
        colisionElement.visible=false;
        mapaElements.add(colisionElement);


        habitacionElement = new MapaElement("Wireframe",new ObjReader(context, R.raw.paredes).getObjeto());
        habitacionElement.scale=Arrays.copyOf(tamHabitacion,tamHabitacion.length);
        habitacionElement.obj.setModoDibujado(GLES20.GL_LINE_LOOP);
        mapaElements.add(habitacionElement);
    }

    public boolean isInside(float[] camPos){
        //x=ancho, y=alto, z=largo
        return camPos[0]>0.0f && camPos[0]<tamHabitacion[0] &&
                camPos[1]>0.0f && camPos[1]<tamHabitacion[1] &&
                camPos[2]>0.0f && camPos[2]<tamHabitacion[2];
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

    public MapaElement getColisionElement() {
        return colisionElement;
    }
}



