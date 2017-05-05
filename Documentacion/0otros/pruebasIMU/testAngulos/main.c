/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: roi
 *
 * Created on 21 de marzo de 2016, 19:23
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

float r[16];

void calculateMatrixFromVector(float *giro) {
    //INVERTIR UN EJE
    float q0;
    float q1 = giro[0];
    float q2 = giro[1];
    float q3 = giro[2];

    /*if (rotationVector.length == 4) {
        q0 = rotationVector[3];
    } else {*/
    q0 = 1 - q1 * q1 - q2 * q2 - q3*q3;
    q0 = (q0 > 0) ? (float) sqrt(q0) : 0;
    //}

    float sq_q1 = 2 * q1 * q1;
    float sq_q2 = 2 * q2 * q2;
    float sq_q3 = 2 * q3 * q3;
    float q1_q2 = 2 * q1 * q2;
    float q3_q0 = 2 * q3 * q0;
    float q1_q3 = 2 * q1 * q3;
    float q2_q0 = 2 * q2 * q0;
    float q2_q3 = 2 * q2 * q3;
    float q1_q0 = 2 * q1 * q0;


    r[0] = 1 - sq_q2 - sq_q3;
    r[4] = q1_q2 - q3_q0;
    r[8] = q1_q3 + q2_q0;
    r[12] = 0.0f;

    r[1] = q1_q2 + q3_q0;
    r[5] = 1 - sq_q1 - sq_q3;
    r[9] = q2_q3 - q1_q0;
    r[13] = 0.0f;

    r[2] = q1_q3 - q2_q0;
    r[6] = q2_q3 + q1_q0;
    r[10] = 1 - sq_q1 - sq_q2;
    r[14] = 0.0f;

    r[3] = r[7] = r[11] = 0.0f;
    r[15] = 1.0f;
}

float angleFromQuaternion(float q1, float q2, float q3) {
    float q0 = 1 - q1 * q1 - q2 * q2 - q3*q3;
    q0 = (q0 > 0) ? (float) sqrt(q0) : 0;
    float sq_q2 = 2 * q2 * q2;
    float sq_q3 = 2 * q3 * q3;
    //float q1_q2 = 2 * q1 * q2;
    float q3_q0 = 2 * q3 * q0;
    float sq_q1 = 2 * q1 * q1;
    return atan2(-2 * q3_q0, 2 - 2 * sq_q3 - sq_q1 - sq_q2) / M_PI * 180;
}

/*
 * 
 */
int main(int argc, char** argv) {
    float vector[3] = {0, 0, 0.65};
    calculateMatrixFromVector(vector);
    /*
     * EEE0
     * NNN0
     * GGG0
     * 0001
     */
    float angle = atan2(r[4] - r[1], r[0] + r[5]) / M_PI * 180;    
    printf("Angle=%f %f\n", angle, angleFromQuaternion(vector[0],vector[1],vector[2]));
    return (EXIT_SUCCESS);
}

