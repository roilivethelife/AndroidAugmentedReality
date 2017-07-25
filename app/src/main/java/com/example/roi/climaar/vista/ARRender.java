package com.example.roi.climaar.vista;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.roi.climaar.modelo.despacho.Despacho;
import com.example.roi.climaar.modelo.despacho.DespachoElement;
import com.example.roi.climaar.vista.vuforia.SampleAppRenderer;
import com.example.roi.climaar.vista.vuforia.SampleAppRendererControl;
import com.vuforia.Device;
import com.vuforia.State;
import com.vuforia.Vuforia;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 
 */
public class ARRender extends GLSurfaceView implements GLSurfaceView.Renderer , SampleAppRendererControl{

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
    private Despacho despachoActual;

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
    public ARRender(Activity activity, IARActivity iARActivity, Despacho despachoActual) {
        super(activity);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        this.activity = activity;
        this.iARActivity = iARActivity;

        mSampleAppRenderer = new SampleAppRenderer(this, activity, Device.MODE.MODE_AR, false,
                1f , 1000f);
        this.despachoActual = despachoActual;
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
        iARActivity.onSurfaceCreated();
        iARActivity.loadFiguras();
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
        if(matrix[15]>0.99f && despachoActual !=null) {//vuforia track ok
            Matrix.translateM(matrix, 0, -despachoActual.markerPos[0], -despachoActual.markerPos[1], -despachoActual.markerPos[2]);
            for (DespachoElement despachoElement : despachoActual.despachoElements) {
                if(despachoElement.visible) {
                    float[] tempModelViewMatrix = matrix.clone();
                    Matrix.translateM(tempModelViewMatrix, 0, despachoElement.pos[0], despachoElement.pos[1], despachoElement.pos[2]);
                    if(!despachoElement.alignCamera) {
                        Matrix.scaleM(tempModelViewMatrix, 0, despachoElement.scale[0], despachoElement.scale[1], despachoElement.scale[2]);
                    }else{
                        double ang = -Math.toDegrees(Math.atan2(tempModelViewMatrix[4],tempModelViewMatrix[0]));
                        tempModelViewMatrix[0]= despachoElement.scale[0];
                        tempModelViewMatrix[1]=0f;
                        tempModelViewMatrix[2]=0f;
                        tempModelViewMatrix[4]=0f;
                        tempModelViewMatrix[5]=-despachoElement.scale[1];
                        tempModelViewMatrix[6]=0f;
                        tempModelViewMatrix[8]=0f;
                        tempModelViewMatrix[9]=0f;
                        tempModelViewMatrix[10]=-despachoElement.scale[2];
                        Matrix.rotateM(tempModelViewMatrix,0,(float)ang,0,0,1);
                    }
                    despachoElement.dibujar(shader, tempModelViewMatrix);
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