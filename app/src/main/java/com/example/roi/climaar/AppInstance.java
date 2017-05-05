package com.example.roi.climaar;

import android.content.Context;

import com.example.roi.climaar.graficos.Mapa.Mapa;

import java.util.ArrayList;

/**
 * Created by roi on 19/04/17.
 */

public class AppInstance {
    private static final AppInstance ourInstance = new AppInstance();

    private static final String FILE_MAPAS = "mapas.dat";
    private ArrayList<Mapa> mapas;
    private Context context;

    public static AppInstance getInstance() {
        return ourInstance;
    }

    private AppInstance() {
        mapas = new ArrayList<>();
    }

    public void setContext(Context context) {
        if(context!=null)
            this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void guardarObjetos(){
        //TODO: descomentar funciones
        /*

        try
        {
            //Guardar mapas
            ObjectOutputStream oos = new ObjectOutputStream(
                    context.openFileOutput(FILE_MAPAS,Context.MODE_PRIVATE));
            for (Mapa map : mapas) {
                oos.writeObject(map);
            }
            Object end = null;
            oos.writeObject(end);
            oos.close();
            Toast.makeText(context,"Mapas guardados, num="+mapas.size(),Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e("AppInstance", "guardarObjetos(): Error al guardar el fichero "+
                    FILE_MAPAS+"en memoria interna");
            ex.printStackTrace();
        }*/
    }

    public void leerObjetos(){
        //TODO: descomentar funciones
        /*try
        {
            mapas = new ArrayList<>();
            //Leer mapas
            ObjectInputStream ois = new ObjectInputStream(
                    context.openFileInput(FILE_MAPAS));

            Object obj = ois.readObject();//leer primer objeto
            while (obj!=null){
                if(obj instanceof Mapa){
                    Mapa map = (Mapa)obj;
                    for (MapaElement mElement :
                            map.mapaElements) {
                        
                    }
                    mapas.add(map);
                }
                obj = ois.readObject();//leer siguiente objeto
                ois.close();
            }
            if(mapas.size()==0){
                mapas.add(MapaControler.createLoadDefaultMap());
            }
            Toast.makeText(context,"Mapas leidos: num="+mapas.size(),Toast.LENGTH_SHORT).show();

        }
        catch (EOFException ex)
        {
            Log.e("AppInstance", "leer(): Error al leer el fichero "+
                    FILE_MAPAS+"en memoria interna");
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        //mapas = new ArrayList<>();
        //mapas.add(MapaControler.createLoadDefaultMap());
    }

    public ArrayList<Mapa> getMapas() {
        return mapas;
    }
}
