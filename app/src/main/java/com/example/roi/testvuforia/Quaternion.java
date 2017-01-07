package com.example.roi.testvuforia;

import java.util.Locale;

import static java.lang.Math.PI;

/**
 * Created by roi on 25/12/16.
 */

public class Quaternion {

    static public final float degreesToRadians = (float) (PI / 180);
    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion(){
        x=0;
        y=0;
        z=0;
        w=1.0f;
    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Quaternion q){
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    /**
     * Devuelve el quaternion en un vector float[4] componentes: x,y,z,w
     * @return quaternion en float(x,y,z,w)
     */
    public float[] toFloat(){
        float[] f = new float[4];
        f[0]=x;
        f[1]=y;
        f[2]=z;
        f[3]=w;
        return f;
    }

    /**
     * Invierte el quaternion actual
     */
    public void invertQuaternion(){
        x=-x;
        y=-y;
        z=-z;
    }

    /**
     * Invierte el quaternion actual y devuelve el resultado en una copia aparte
     * @return
     */
    public Quaternion invertQuaternionCopy(){
        return new Quaternion(-x,-y,-z,w);
    }

    /** Multiplies this quaternion with another one in the form of this = this * other
     *
     * @param other Quaternion to multiply with
     * @return This quaternion for chaining */
    public Quaternion mul (final Quaternion other) {
        final float newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
        final float newY = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z;
        final float newZ = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x;
        final float newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    /** Multiplies this quaternion with another one in the form of this = this * other
     *
     * @param x the x component of the other quaternion to multiply with
     * @param y the y component of the other quaternion to multiply with
     * @param z the z component of the other quaternion to multiply with
     * @param w the w component of the other quaternion to multiply with
     * @return This quaternion for chaining */
    public Quaternion mul (final float x, final float y, final float z, final float w) {
        final float newX = this.w * x + this.x * w + this.y * z - this.z * y;
        final float newY = this.w * y + this.y * w + this.z * x - this.x * z;
        final float newZ = this.w * z + this.z * w + this.x * y - this.y * x;
        final float newW = this.w * w - this.x * x - this.y * y - this.z * z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    /** Multiplies this quaternion with another one in the form of this = other * this
     *
     * @param other Quaternion to multiply with
     * @return This quaternion for chaining */
    public Quaternion mulLeft (Quaternion other) {
        final float newX = other.w * this.x + other.x * this.w + other.y * this.z - other.z * y;
        final float newY = other.w * this.y + other.y * this.w + other.z * this.x - other.x * z;
        final float newZ = other.w * this.z + other.z * this.w + other.x * this.y - other.y * x;
        final float newW = other.w * this.w - other.x * this.x - other.y * this.y - other.z * z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    /** Multiplies this quaternion with another one in the form of this = other * this
     *
     * @param x the x component of the other quaternion to multiply with
     * @param y the y component of the other quaternion to multiply with
     * @param z the z component of the other quaternion to multiply with
     * @param w the w component of the other quaternion to multiply with
     * @return This quaternion for chaining */
    public Quaternion mulLeft (final float x, final float y, final float z, final float w) {
        final float newX = w * this.x + x * this.w + y * this.z - z * this.y;
        final float newY = w * this.y + y * this.w + z * this.x - x * this.z;
        final float newZ = w * this.z + z * this.w + x * this.y - y * this.x;
        final float newW = w * this.w - x * this.x - y * this.y - z * this.z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    public void set(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Importa vector de la forma x,y,z,w
     * @param vec
     */
    public void set(float []vec){
        this.x = vec[0];
        this.y = vec[1];
        this.z = vec[2];
        this.w = vec[3];
    }


    /** Sets the quaternion to the given euler angles in degrees.
     * @param yaw the rotation around the y axis in degrees
     * @param pitch the rotation around the x axis in degrees
     * @param roll the rotation around the z axis degrees
     * @return this quaternion */
    public Quaternion setEulerAngles (float yaw, float pitch, float roll) {
        return setEulerAnglesRad(yaw * degreesToRadians, pitch * degreesToRadians, roll
                * degreesToRadians);
    }

    /** Sets the quaternion to the given euler angles in radians.
     * @param yaw the rotation around the y axis in radians
     * @param pitch the rotation around the x axis in radians
     * @param roll the rotation around the z axis in radians
     * @return this quaternion */
    public Quaternion setEulerAnglesRad (float yaw, float pitch, float roll) {
        final float hr = roll * 0.5f;
        final float shr = (float)Math.sin(hr);
        final float chr = (float)Math.cos(hr);
        final float hp = pitch * 0.5f;
        final float shp = (float)Math.sin(hp);
        final float chp = (float)Math.cos(hp);
        final float hy = yaw * 0.5f;
        final float shy = (float)Math.sin(hy);
        final float chy = (float)Math.cos(hy);
        final float chy_shp = chy * shp;
        final float shy_chp = shy * chp;
        final float chy_chp = chy * chp;
        final float shy_shp = shy * shp;

        x = (chy_shp * chr) + (shy_chp * shr); // cos(yaw/2) * sin(pitch/2) * cos(roll/2) + sin(yaw/2) * cos(pitch/2) * sin(roll/2)
        y = (shy_chp * chr) - (chy_shp * shr); // sin(yaw/2) * cos(pitch/2) * cos(roll/2) - cos(yaw/2) * sin(pitch/2) * sin(roll/2)
        z = (chy_chp * shr) - (shy_shp * chr); // cos(yaw/2) * cos(pitch/2) * sin(roll/2) - sin(yaw/2) * sin(pitch/2) * cos(roll/2)
        w = (chy_chp * chr) + (shy_shp * shr); // cos(yaw/2) * cos(pitch/2) * cos(roll/2) + sin(yaw/2) * sin(pitch/2) * sin(roll/2)
        return this;
    }

    @Override
    public Quaternion clone() {
        return new Quaternion(this);
    }

    @Override
    public String toString() {
        return x+", "+y+", "+z+", "+w+", ";
    }

    public String toString(int decimals){
        String text = "%."+decimals+"f, %."+decimals+"f, %."+decimals+"f, %."+decimals+"f";
        return String.format(Locale.ROOT,text,x,y,z,w);
    }
}
