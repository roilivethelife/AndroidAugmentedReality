package com.example.roi.climaar.modelo.JsonRest;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.example.roi.climaar.modelo.mapa.MapElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Float.NaN;

/**
 * Created by roi on 20/06/17.
 */

public class WebRestData implements WebRestDataInterface{
    private static final String LOGTAG = "WebRestData";

    //Variables Despacho
    private static final String URL_DESPACHO= "http://demos.citius.usc.es/hvac/CITIUS_SR/";
    private String numDespacho; //http://demos.citius.usc.es/hvac/CITIUS_SR/XX

    private boolean bSRvalvulaAbierta; //CITIUS_SR_P2_Actuadores_209
    private float fSRtempConsignaFrio; //CITIUS_SR_P2_Consigna_Frio_209
    private float fSRtempConsignaCalor; //CITIUS_SR_P2_Consigna_Calor_209


    //Variables URL general suelo radiante
    private static final String URL_SUELO_RADIANTE_GENERAL= "http://demos.citius.usc.es/hvac/CITIUS_BC/SR";
    private boolean bSRmodoFrio;//CITIUS_BC_Sel_Calor_Frio_SR"
    private float fSRtempImpulsion; //CITIUS_BC_Temp_Impulsion_SR
    private float fSRtempRetorno; //CITIUS_BC_Temp_Retorno_SR


    //Variables URL general aire acondicionado
    private static final String URL_AC_GENERAL= "http://demos.citius.usc.es/hvac/CITIUS_CL01_Temp";
    private float fACtempExterior; //CITIUS_CL01_Temp_Exterior
    private float fACtempImpulsion; //CITIUS_CL01_Temp_Impulsion
    private float fACtempRetorno; //CITIUS_CL01_Temp_Retorno


    private static final long DEFAULT_DELAY = 20000;//20seg
    private static final long MIN_DELAY = 5000;//5seg
    private long delay;
    private Handler mHandler;
    private Runnable mRunnable;

    private ArrayList<DynamicMapElement> dynMapElements;
    private boolean onPause;


    public WebRestData(String despacho, ArrayList<MapElement> dynMapElements) {
        this(despacho,DEFAULT_DELAY, dynMapElements);
    }

    public WebRestData(String despacho, long delayMillis, ArrayList<MapElement> mapElements){
        this.numDespacho = despacho;
        onPause = true;
        setDelay(delayMillis);

        dynMapElements= new ArrayList<>();

        //Recorrer mapElements, los que son dinamicos comprobar que sean instancia
        //de DynamicMapElement, y agregarlos a la lista
        for (MapElement mE :mapElements) {
            if(mE.isDynamic()){
                DynamicMapElement dynMapElement = null;
                if(mE instanceof DynamicMapElement){
                    dynMapElement = (DynamicMapElement) mE;
                    dynMapElements.add(dynMapElement);
                }
            }
        }
        mHandler = new Handler();
        createRunnable();
        initVars();
    }

    private void initVars() {
        bSRvalvulaAbierta=false;
        fSRtempConsignaFrio=NaN;
        fSRtempConsignaCalor=NaN;
        bSRmodoFrio=false;
        fSRtempImpulsion=NaN;
        fSRtempRetorno=NaN;
        fACtempExterior=NaN;
        fACtempImpulsion=NaN;
        fACtempRetorno=NaN;
    }


    private void createRunnable() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                updateData();
            }
        };
    }


    /**
     * Activar peticiones al servidor cada X tiempo
     */
    public void onResume(){
        mHandler.post(mRunnable);
        onPause=false;
    }

    /**
     * llamado cada X tiempo por runnable
     */
    private void updateData(){
        new LoadDataTask().execute();
    }

    /**
     * LLamado por task cuando se han recibido los datos
     */
    protected void onUpdateDataReceived(){
        Log.d(LOGTAG,"onUpdateDataReceived()");
        for (DynamicMapElement dynMapE : dynMapElements) {
            dynMapE.recargarInformacion(this);
        }
        //Llamar al runnable X segundos despues
        if(!onPause)
            mHandler.postDelayed(mRunnable,delay);
    }



    public void onPause(){
        onPause = true;
        mHandler.removeCallbacks(mRunnable);
    }

    public void setDelay(long delayMillis) {
        if(delayMillis>MIN_DELAY) {
            this.delay = delayMillis;
        }else {
            this.delay = DEFAULT_DELAY;
        }
    }

    public boolean isbSRvalvulaAbierta() {
        return bSRvalvulaAbierta;
    }

    public float getfSRtempConsignaFrio() {
        return fSRtempConsignaFrio;
    }

    public float getfSRtempConsignaCalor() {
        return fSRtempConsignaCalor;
    }

    public boolean isbSRmodoFrio() {
        return bSRmodoFrio;
    }

    public float getfSRtempImpulsion() {
        return fSRtempImpulsion;
    }

    public float getfSRtempRetorno() {
        return fSRtempRetorno;
    }

    public float getfACtempExterior() {
        return fACtempExterior;
    }

    public float getfACtempImpulsion() {
        return fACtempImpulsion;
    }

    public float getfACtempRetorno() {
        return fACtempRetorno;
    }

    public class LoadDataTask extends AsyncTask<Void,Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String urlDespacho = URL_DESPACHO+numDespacho;
                String jsonStr=  HttpRequest.get(urlDespacho).
                        accept("application/json").body();
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray observations = jsonObj.getJSONArray("observations");
                for (int i = 0; i < observations.length(); i++) {
                    JSONObject observation = observations.getJSONObject(i);
                    String deviceName = observation.optString("deviceName");
                    if(deviceName.equals("CITIUS_SR_P2_Actuadores_"+numDespacho)){
                        bSRvalvulaAbierta = observation.optBoolean("value");
                    }else if(deviceName.equals("CITIUS_SR_P2_Consigna_Frio_"+numDespacho)){
                        fSRtempConsignaFrio = (float)observation.optDouble("value");
                    }else if(deviceName.equals("CITIUS_SR_P2_Consigna_Calor_"+numDespacho)){
                        fSRtempConsignaCalor = (float)observation.optDouble("value");
                    }
                }

                jsonStr =  HttpRequest.get(URL_SUELO_RADIANTE_GENERAL).
                        accept("application/json").body();
                jsonObj = new JSONObject(jsonStr);
                observations = jsonObj.getJSONArray("observations");
                for (int i = 0; i < observations.length(); i++) {
                    JSONObject observation = observations.getJSONObject(i);
                    String deviceName = observation.optString("deviceName");
                    if(deviceName.equals("CITIUS_BC_Sel_Calor_Frio_SR")){
                        bSRmodoFrio = observation.optBoolean("value");
                    }else if(deviceName.equals("CITIUS_BC_Temp_Impulsion_SR")){
                        fSRtempImpulsion = (float)observation.optDouble("value");
                    }else if(deviceName.equals("CITIUS_BC_Temp_Retorno_SR")){
                        fSRtempRetorno = (float)observation.optDouble("value");
                    }
                }
                jsonStr =  HttpRequest.get(URL_AC_GENERAL).accept("application/json").body();
                jsonObj = new JSONObject(jsonStr);
                observations = jsonObj.getJSONArray("observations");
                for (int i = 0; i < observations.length(); i++) {
                    JSONObject observation = observations.getJSONObject(i);
                    String deviceName = observation.optString("deviceName");
                    if(deviceName.equals("CITIUS_CL01_Temp_Exterior")){
                        fACtempExterior = (float)observation.optDouble("value");
                    }else if(deviceName.equals("CITIUS_CL01_Temp_Impulsion")){
                        fACtempImpulsion = (float)observation.optDouble("value");
                    }else if(deviceName.equals("CITIUS_CL01_Temp_Retorno")){
                        fACtempRetorno = (float)observation.optDouble("value");
                    }
                }



                //String measureTime = observation.getString("deviceName");
                //SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
                //Date parsedTimeStamp = dateFormat.parse(measureTime);
            } catch (HttpRequest.HttpRequestException exception) {
                exception.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void p) {
            onUpdateDataReceived();
        }
    }

}
