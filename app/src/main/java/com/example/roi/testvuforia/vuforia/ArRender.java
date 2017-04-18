package com.example.roi.testvuforia.vuforia;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import com.example.roi.testvuforia.LocationControler;
import com.example.roi.testvuforia.graficos.Mapa.MapaControler;
import com.example.roi.testvuforia.graficos.OnTouchInterface;
import com.example.roi.testvuforia.graficos.Shader;
import com.vuforia.COORDINATE_SYSTEM_TYPE;
import com.vuforia.CameraCalibration;
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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by roi on 21/11/16.
 */

public class ArRender implements GLSurfaceView.Renderer, OnTouchInterface {

    private static final String LOGTAG = "ARRender";


    private Context context;

    private Renderer mRenderer;
    private int currentView = VIEW.VIEW_SINGULAR;
    private RenderingPrimitives mRenderingPrimitives;
    private GLTextureUnit videoBackgroundTex;


    private LocationControler locControl;
    private MapaControler mapaControler;
    private Shader shader;



    public ArRender(Context context,LocationControler locControl,MapaControler mapaControler) {
        this.context = context;

        //Obtenemos render de vuforia
        mRenderer = Renderer.getInstance();
        this.locControl = locControl;
        this.mapaControler = mapaControler;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        videoBackgroundTex = new GLTextureUnit();

        this.shader= new Shader(context);
        mapaControler.cargarNuevoMapa();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mRenderingPrimitives = Device.getInstance().getRenderingPrimitives();
        Vuforia.onSurfaceChanged(width, height);
    }

    private float[] setupProjectionMatrix(int width, int height, float nearPlane, float farPlane){
        CameraCalibration camCalib = CameraDevice.getInstance().getCameraCalibration();
        // The following code reproduces the projectionMatrix above using the camera parameters
        Vec2F size = camCalib.getSize();
        Vec2F focalLength = camCalib.getFocalLength();
        Vec2F principalPoint = camCalib.getPrincipalPoint();
        float fovRadians = 2 * (float)Math.atan(0.5f * size.getData()[1] / focalLength.getData()[1]);
        float fovDegrees = fovRadians * 180.0f / M_PI;
        float[] matrix = new float[16];
        float aspect= (float)width/height;
        Matrix.perspectiveM(matrix,0,fovDegrees,aspect,nearPlane,farPlane);
        return matrix;
    }

    private float[] vuforiaOnDrawFramePre() {
        // We must detect if background reflection is active and adjust the
        // culling direction.
        // If the reflection is active, this means the post matrix has been
        // reflected as well,
        // therefore standard counter clockwise face culling will result in
        // "inside out" models.
        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            GLES20.glFrontFace(GLES20.GL_CW);  // Front camera
        else
            GLES20.glFrontFace(GLES20.GL_CCW);   // Back camera

        // We get a list of views which depend on the mode we are working on, for mono we have
        // only one view, in stereo we have three: left, right and postprocess
        //TODO: voy a omitir parte de este codigo, ya que está pensado de forma universal para implementar vision en estereo
        //TODO: y solo me interesa el modo simple
        ViewList viewList = mRenderingPrimitives.getRenderingViews();
        // Get the view id
        //Log.d(LOGTAG,"Numero de views(tiene que ser 1)="+viewList.getNumViews());
        int viewID = viewList.getView(0);//solo hay un view
        //TODO: comprobar si hace falta el glViewport y glScissor
        Vec4I viewport;
        viewport = mRenderingPrimitives.getViewport(viewID);// Get the viewport for that specific view
        // Set viewport for current view
        GLES20.glViewport(viewport.getData()[0], viewport.getData()[1], viewport.getData()[2], viewport.getData()[3]);
        // Set scissor
        GLES20.glScissor(viewport.getData()[0], viewport.getData()[1], viewport.getData()[2], viewport.getData()[3]);


        // Get projection matrix for the current view. COORDINATE_SYSTEM_CAMERA used for AR and
        // COORDINATE_SYSTEM_WORLD for VR
        Matrix34F projMatrix = mRenderingPrimitives.getProjectionMatrix(viewID, COORDINATE_SYSTEM_TYPE.COORDINATE_SYSTEM_CAMERA);

        // Create GL matrix setting up the near and far planes
        float rawProjectionMatrixGL[] = Tool.convertPerspectiveProjection2GLMatrix(
                projMatrix,
                1f,//nearPlane
                1000f)//far plane
                .getData();

        // Apply the appropriate eye adjustment to the raw projection matrix, and assign to the global variable
        float eyeAdjustmentGL[] = Tool.convert2GLMatrix(mRenderingPrimitives
                .getEyeDisplayAdjustmentMatrix(viewID)).getData();

        float projectionMatrix[] = new float[16];
        // Apply the adjustment to the projection matrix
        Matrix.multiplyMM(projectionMatrix, 0, rawProjectionMatrixGL, 0, eyeAdjustmentGL, 0);
        currentView = viewID;
        return projectionMatrix;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        //Limiamos depth y color buffer
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//limpiamos de color negro


        //Iniciamos valores para dibujar
        State state;
        state = TrackerManager.getInstance().getStateUpdater().updateState();
        mRenderer.begin(state);
        float[] projectionMatrix = vuforiaOnDrawFramePre();
        //dibujamos video en el fondo
        renderVideoBackground();


        //VIDEO dibujado: activamos depth test
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniformMatrix4fv(shader.getmPVMatrixHandle(), 1, false, projectionMatrix, 0);
        float[] modelViewMatrix = locControl.updateLocation(state);
        mapaControler.dibujar(shader,modelViewMatrix);
        /*GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(),1,false,modelViewMatrix,0);
        cubo.dibujar();*/

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        checkGLError("Fin dibujar");

    }

    public static void checkGLError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("MyApp", op + ": glError " + error);
        }
    }

    public void configureVideoBackground(int mScreenWidth, int mScreenHeight) {
        CameraDevice cameraDevice = CameraDevice.getInstance();
        VideoMode vm = cameraDevice.getVideoMode(CameraDevice.MODE.MODE_DEFAULT);

        VideoBackgroundConfig config = new VideoBackgroundConfig();
        config.setEnabled(true);
        config.setPosition(new Vec2I(0, 0));

        int xSize = 0, ySize = 0;
        // We keep the aspect ratio to keep the video correctly rendered. If it is portrait we
        // preserve the height and scale width and vice versa if it is landscape, we preserve
        // the width and we check if the selected values fill the screen, otherwise we invert
        // the selection
        //Modo actual: landscape
        xSize = mScreenWidth;
        ySize = (int) (vm.getHeight() * (mScreenWidth / (float) vm
                .getWidth()));

        if (ySize < mScreenHeight) {
            xSize = (int) (mScreenHeight * (vm.getWidth() / (float) vm
                    .getHeight()));
            ySize = mScreenHeight;
        }

        config.setSize(new Vec2I(xSize, ySize));

        //RRender: Configure Video Background : Video (0 , 0), Screen (1080 , 1920), mSize (0 , 1920)
        Log.i(LOGTAG, "Configure Video Background : Video (" + vm.getWidth()
                + " , " + vm.getHeight() + "), Screen (" + mScreenWidth + " , "
                + mScreenHeight + "), mSize (" + xSize + " , " + ySize + ")");

        Renderer.getInstance().setVideoBackgroundConfig(config);
    }


    public static float[] identityMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f};
    private static final float[] whiteColor = {1.0f, 1.0f, 1.0f, 1.0f};

    private void renderVideoBackground() {
        int vbVideoTextureUnit = 0;
        // Bind the video bg texture and get the Texture ID from Vuforia
        videoBackgroundTex.setTextureUnit(vbVideoTextureUnit);
        if (!mRenderer.updateVideoBackgroundTexture(videoBackgroundTex)) {
            Log.e(LOGTAG, "Unable to update video background texture");
            return;
        }

        float[] vbProjectionMatrix = Tool.convert2GLMatrix(
                mRenderingPrimitives.getVideoBackgroundProjectionMatrix(currentView, COORDINATE_SYSTEM_TYPE.COORDINATE_SYSTEM_CAMERA)).getData();

        // Apply the scene scale on video see-through eyewear, to scale the video background and augmentation
        // so that the display lines up with the real world
        // This should not be applied on optical see-through devices, as there is no video background,
        // and the calibration ensures that the augmentation matches the real world
        if (Device.getInstance().isViewerActive()) {
            float sceneScaleFactor = (float) getSceneScaleFactor();
            Matrix.scaleM(vbProjectionMatrix, 0, sceneScaleFactor, sceneScaleFactor, 1.0f);
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);

        Mesh vbMesh = mRenderingPrimitives.getVideoBackgroundMesh(currentView);
        // Load the shader and upload the vertex/texcoord/index data
        GLES20.glUseProgram(shader.getmProgram());
        GLES20.glVertexAttribPointer(shader.getmCoordHandle(), 3, GLES20.GL_FLOAT, false, 0, vbMesh.getPositions().asFloatBuffer());
        GLES20.glVertexAttribPointer(shader.getmTexCoordinateHandle(), 2, GLES20.GL_FLOAT, false, 0, vbMesh.getUVs().asFloatBuffer());

        GLES20.glUniform1i(shader.getmTexUniformHandle(), vbVideoTextureUnit);

        // Render the video background with the custom shader
        // First, we enable the vertex arrays
        GLES20.glEnableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glEnableVertexAttribArray(shader.getmTexCoordinateHandle());

        // Pass the projection matrix to OpenGL
        GLES20.glUniformMatrix4fv(shader.getmPVMatrixHandle(), 1, false, vbProjectionMatrix, 0);

        //Enviamos matriz de modelo(no se usa pero hay que setearla a la identidad)
        Matrix.setIdentityM(identityMatrix, 0);
        GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(), 1, false, identityMatrix, 0);
        //Enviamos color blanco(no se usa pero hace falta para el shader)
        GLES20.glUniform4fv(shader.getmColorHandle(), 1, whiteColor, 0);

        // Then, we issue the render call
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vbMesh.getNumTriangles() * 3, GLES20.GL_UNSIGNED_SHORT,
                vbMesh.getTriangles().asShortBuffer());

        // Finally, we disable the vertex arrays
        GLES20.glDisableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glDisableVertexAttribArray(shader.getmTexCoordinateHandle());
        checkGLError("Rendering of the video background failed");

    }

    static final float VIRTUAL_FOV_Y_DEGS = 85.0f;
    static final float M_PI = 3.14159f;

    double getSceneScaleFactor() {
        // Get the y-dimension of the physical camera field of view
        Vec2F fovVector = CameraDevice.getInstance().getCameraCalibration().getFieldOfViewRads();
        float cameraFovYRads = fovVector.getData()[1];

        // Get the y-dimension of the virtual camera field of view
        float virtualFovYRads = VIRTUAL_FOV_Y_DEGS * M_PI / 180;

        // The scene-scale factor represents the proportion of the viewport that is filled by
        // the video background when projected onto the same plane.
        // In order to calculate this, let 'd' be the distance between the cameras and the plane.
        // The height of the projected image 'h' on this plane can then be calculated:
        //   tan(fov/2) = h/2d
        // which rearranges to:
        //   2d = h/tan(fov/2)
        // Since 'd' is the same for both cameras, we can combine the equations for the two cameras:
        //   hPhysical/tan(fovPhysical/2) = hVirtual/tan(fovVirtual/2)
        // Which rearranges to:
        //   hPhysical/hVirtual = tan(fovPhysical/2)/tan(fovVirtual/2)
        // ... which is the scene-scale factor
        return Math.tan(cameraFovYRads / 2) / Math.tan(virtualFovYRads / 2);
    }


    private float mPreviousX;
    private float mPreviousY;
    private final static float sensibility = 30.0f;
    @Override
    public boolean onCustomTouchEvent(MotionEvent event) {
        if (event != null)
        {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                if (mRenderer != null)
                {
                    float deltaX = (x - mPreviousX) / sensibility;
                    float deltaY = (y - mPreviousY) / sensibility;

                    //posicionX +=deltaX;
                    ///posicionZ +=deltaY;
                }
            }

            mPreviousX = x;
            mPreviousY = y;

            return true;
        }else{
            return false;
        }
    }
}
