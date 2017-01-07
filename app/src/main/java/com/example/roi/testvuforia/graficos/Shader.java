package com.example.roi.testvuforia.graficos;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.roi.testvuforia.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by roi on 13/11/16.
 */

public class Shader {

    private final int mProgram;
    private final int mColorHandle;
    private final int mCoordHandle;
    private final int mModelMatrixHandle;
    private final int mPVMatrixHandle;
    private final int mTexCoordinateHandle;
    private final int mTexUniformHandle;



    public Shader(Context context) {

        String vertexShaderCode = readTextFileFromRawResource(context, R.raw.vertex_shader);
        String fragmentShaderCode = readTextFileFromRawResource(context,R.raw.fragment_shader);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

        // Get the link status.
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        // If the link failed, delete the program.
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(mProgram);
            Log.e("SHADER", "Shader constructor, glLinkPRogram error");
        }


        GLES20.glUseProgram(mProgram);
        mCoordHandle = GLES20.glGetAttribLocation(mProgram, "aCoordenate");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        mModelMatrixHandle = GLES20.glGetUniformLocation(mProgram, "modelMatrix");
        mPVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uPVMatrix");
        mTexCoordinateHandle = GLES20.glGetAttribLocation(mProgram,"a_TexCoordinate");
        mTexUniformHandle = GLES20.glGetUniformLocation(mProgram,"u_Texture");
    }

    public int getmProgram() {
        return mProgram;
    }

    public int getmColorHandle() {
        return mColorHandle;
    }

    public int getmModelMatrixHandle() {
        return mModelMatrixHandle;
    }

    public int getmCoordHandle() {
        return mCoordHandle;
    }

    public int getmPVMatrixHandle() {
        return mPVMatrixHandle;
    }

    public int getmTexCoordinateHandle() {
        return mTexCoordinateHandle;
    }

    public int getmTexUniformHandle() {
        return mTexUniformHandle;
    }

    private static String readTextFileFromRawResource(final Context context, final int resourceId) {
        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return body.toString();
    }

    private static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        if(shader!=0) {
            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0){
                GLES20.glDeleteShader(shader);
                shader = 0;
                Log.e("LOAD_Shader","glCompileShader error");
            }
        }else{
            Log.e("LOAD_Shader","glCreateShader error, return 0");
        }
        return shader;
    }
}
