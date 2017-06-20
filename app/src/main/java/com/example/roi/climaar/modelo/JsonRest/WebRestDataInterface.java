package com.example.roi.climaar.modelo.JsonRest;

/**
 * Created by roi on 20/06/17.
 */

public interface WebRestDataInterface {
    boolean isbSRvalvulaAbierta();
    float getfSRtempConsignaFrio();
    float getfSRtempConsignaCalor();
    boolean isbSRmodoFrio();
    float getfSRtempImpulsion();
    float getfSRtempRetorno();
    float getfACtempExterior();
    float getfACtempImpulsion();
    float getfACtempRetorno();
}
