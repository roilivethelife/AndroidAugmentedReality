package com.example.roi.climaar.vista;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import com.example.roi.climaar.modelo.mapa.MapElement;
import com.example.roi.climaar.modelo.mapa.Mapa;
import com.example.roi.climaar.vista.vuforia.SampleAppRenderer;
import com.example.roi.climaar.vista.vuforia.SampleAppRendererControl;
import com.vuforia.COORDINATE_SYSTEM_TYPE;
import com.vuforia.CameraDevice;
import com.vuforia.Device;
import com.vuforia.GLTextureUnit;
import com.vuforia.Matrix34F;
import com.vuforia.Mesh;
import com.vuforia.Renderer;
import com.vuforia.RenderingPrimitives;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackerManager;
import com.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.vuforia.VIEW;
import com.vuforia.Vec2F;
import com.vuforia.Vec2I;
import com.vuforia.Vec4I;
import com.vuforia.VideoBackgroundConfig;
import com.vuforia.VideoMode;
import com.vuforia.ViewList;
import com.vuforia.Vuforia;

import java.util.ArrayList;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 
 */
public class ARRender implements GLSurfaceView.Renderer , SampleAppRendererControl{

    private static final String LOGTAG = "ARRender";

    /**
     * Contexto aplicación
     */
    private Activity activity;

    /**
     * Shader empleado para dibujar
     */
    private Shader shader;

    /**
     * Interfaz para comunicarse con ARActivity, se piden los datos a dibujar desde aqui
     * También se envían los toques brutos en pantalla.
     */
    private IARActivity iARActivity;

    private boolean mIsActive = false;
    private SampleAppRenderer mSampleAppRenderer;

    /**
     * Elementos a dibujar en pantalla
     */
    private Mapa mapaActual;

    public static float[] identityMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f};
    public static float[] nullMatrix = {
            0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] whiteColor = {1.0f, 1.0f, 1.0f, 1.0f};
    private static final float VIRTUAL_FOV_Y_DEGS = 85.0f;
    private static final float M_PI = 3.14159f;

    /**
     * Constructor ARRender
     * @param activity activity actual
     * @param iARActivity Interfaz IARActivity
     */
    public ARRender(Activity activity, IARActivity iARActivity, Mapa mapaActual) {
        this.activity = activity;
        this.iARActivity = iARActivity;

        mSampleAppRenderer = new SampleAppRenderer(this, activity, Device.MODE.MODE_AR, false,
                1f , 1000f);
        this.mapaActual = mapaActual;
    }

    /**
     * Funcion llamada cuando se crea la superficie de dibujado
     * @param gl10 objero GL10
     * @param eglConfig configuracion EGL
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d(LOGTAG,"onSurfaceCreated");
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        this.shader= new Shader(activity);
        iARActivity.loadFiguras();
        iARActivity.onSurfaceCreated();
        mSampleAppRenderer.onSurfaceCreated();
    }

    /**
     * Funcion llamada cuando se modifica la disposición o tamaño de la superficie de dibujado
     * @param gl objetoGL10
     * @param w anchira
     * @param h altura
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        Log.d(LOGTAG,"onSurfaceChanged");
        iARActivity.onSurfaceChanged(w,h);
        mSampleAppRenderer.onConfigurationChanged(mIsActive);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f: 1.0f);
    }



    /**
     * Función dibujado en pantalla
     * @param gl objeto GL10
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsActive)
            return;
        // Call our function to render content from SampleAppRenderer class
        mSampleAppRenderer.render();
    }

    public void setActive(boolean active){
        mIsActive = active;

        if(mIsActive)
            mSampleAppRenderer.configureVideoBackground();
    }



    /**
     * Comprueba si hay algun error de opengl por la ejecucion de algun comando
     * @param s Mensaje a mostrar por terminal con el check
     */
    private void checkGLError(String s) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(LOGTAG, s + ": glError " + error);
        }
    }



    @Override
    public void renderFrame(State state, float[] projectionMatrix) {

        mSampleAppRenderer.renderVideoBackground();

        GLES20.glUseProgram(shader.getmProgram());
        //VIDEO dibujado: activamos depth test
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniformMatrix4fv(shader.getmPVMatrixHandle(), 1, false, projectionMatrix, 0);

        float[] matrix = iARActivity.updateVuforiaState(state);
        if(matrix[15]>0.99f && mapaActual!=null) {//vuforia track ok
            Matrix.translateM(matrix, 0, -mapaActual.markerPos[0], -mapaActual.markerPos[1], -mapaActual.markerPos[2]);
            for (MapElement mapElement :mapaActual.mapaElements) {
                if(mapElement.visible) {
                    float[] tempModelViewMatrix = matrix.clone();
                    Matrix.translateM(tempModelViewMatrix, 0, mapElement.pos[0], mapElement.pos[1], mapElement.pos[2]);
                    Matrix.scaleM(tempModelViewMatrix, 0, mapElement.scale[0], mapElement.scale[1], mapElement.scale[2]);
                    mapElement.dibujar(shader, tempModelViewMatrix);
                }
            }
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        checkGLError("Fin dibujar");
    }

    public void updateConfiguration(){
        mSampleAppRenderer.onConfigurationChanged(mIsActive);
    }
}