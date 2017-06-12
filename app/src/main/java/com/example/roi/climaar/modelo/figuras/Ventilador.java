package com.example.roi.climaar.modelo.figuras;

import android.content.Context;
import android.opengl.Matrix;

import com.example.roi.climaar.R;
import com.example.roi.climaar.vista.Shader;

import java.io.Serializable;

/**
 * Created by roi on 24/04/17.
 */

public class Ventilador extends Figura implements Serializable{


    private transient Obj caja;
    private transient Obj fan;


    private static int resourceIDCaja = R.raw.fan_box;
    private static int resourceIDFan = R.raw.fan_fan;

    private long lastTime;
    private float ang = 0f;
    private static float ROT_PER_SEC = 1f;
    private boolean fan_on;

    public Ventilador(boolean funcionando) {
        super(FigureType.VENTILADOR);
        fan_on = funcionando;
    }

    @Override
    public void loadFigura(Context context) {
        fan = new Obj(resourceIDFan);
        caja = new Obj(resourceIDCaja);
        fan.loadFigura(context);
        caja.loadFigura(context);
        isLoaded = true;
    }

    public void setFan_on(boolean fan_on) {
        this.fan_on = fan_on;
    }

    public boolean isFan_on() {
        return fan_on;
    }

    @Override
    public void dibujar(Shader shader, float[] modelViewMatrix) {
        caja.dibujar(shader,modelViewMatrix);
        if(fan_on) {
            long actTime = System.currentTimeMillis();
            long diffTime = (lastTime - actTime) % 1000;
            lastTime = actTime;
            ang += diffTime / 1000f * ROT_PER_SEC * 360f;
            ang %= 360f;
            Matrix.rotateM(modelViewMatrix, 0, ang, 0, 1, 0);
        }
        fan.dibujar(shader,modelViewMatrix);
    }
}
