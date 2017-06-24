package com.example.roi.climaar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.roi.climaar.modelo.mapa.Mapa;

import java.util.ArrayList;

/**
 * Created by roi on 23/06/17.
 */

public class AdaptadorEditMap extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, ListItemMapaConfig.ListItemMapaConfigInterface{
    private View.OnClickListener listener;
    ArrayList<EditMapContainer> datos;
    Mapa mapaActual;



    public AdaptadorEditMap(Mapa mapaActual) {
        this.mapaActual=mapaActual;
        datos = new ArrayList<>();
        datos.add(new HeaderData("Datos mapa"));
        datos.add(new AttributeData(EditMapActivity2.ID_NAME,"Nombre",
                mapaActual.getNombre(),"Nombre del mapa",ListItemMapaConfig.TYPE_EDITTEXT));
        datos.add(new AttributeData(EditMapActivity2.ID_DESC,"Descripcion",
                mapaActual.getDescripcion(),"Descripción del mapa",ListItemMapaConfig.TYPE_EDITTEXT));
        datos.add(new AttributeData(EditMapActivity2.ID_NUM_DESP,"NºDespacho",
                ""+mapaActual.getNumDespacho(),"Número de despacho del mapa",ListItemMapaConfig.TYPE_NUMBER));
        datos.add(new HeaderData("Tamaño de la sala"));
        datos.add(new AttributeData(EditMapActivity2.ID_MAPA_X,"Ancho",
                ""+mapaActual.tam[0],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new AttributeData(EditMapActivity2.ID_MAPA_Y,"Alto",
                ""+mapaActual.tam[1],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new AttributeData(EditMapActivity2.ID_MAPA_Z,"Largo",
                ""+mapaActual.tam[2],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new HeaderData("Posición Marcador"));
        datos.add(new AttributeData(EditMapActivity2.ID_MRK_X,"Posición X",
                ""+mapaActual.markerPos[0],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new AttributeData(EditMapActivity2.ID_MRK_Y,"Posición Y",
                ""+mapaActual.markerPos[1],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new AttributeData(EditMapActivity2.ID_MRK_Z,"Posición Z",
                ""+mapaActual.markerPos[2],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new HeaderData("Elementos de climatización"));
        datos.add(new AttributeData(EditMapActivity2.ID_EDIT_ELEMENTS,"Editar elementos",
                "Editar los elementos de climatización del mapa","",ListItemMapaConfig.TYPE_NONE));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==1){//Header
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_edit_map_header, parent, false);
            return new MapHeaderViewHolder(itemView);
        }else if(viewType==2){//Atributos
            View itemView = new ListItemMapaConfig(parent.getContext());
            itemView.setOnClickListener(this);
            return new MapAttributeViewHolder(itemView);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EditMapContainer container = datos.get(position);
        if(container.getViewType()==1){
            HeaderData headerData = (HeaderData)container;
            MapHeaderViewHolder viewHolder = (MapHeaderViewHolder)holder;
            viewHolder.bindText(headerData.headerText);
        }else{
            AttributeData attributeData = (AttributeData)container;
            MapAttributeViewHolder viewHolder = (MapAttributeViewHolder)holder;
            viewHolder.bindData(attributeData);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return datos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        ListItemMapaConfig view = (ListItemMapaConfig)v;
        view.onClick();
        if(listener!=null){
            listener.onClick(v);
        }
    }

    @Override
    public void onDataChanged(int id, String value) {
        float fValue=0.0f;
        switch (id){
            case EditMapActivity2.ID_NAME:
                mapaActual.setNombre(value);
                break;
            case EditMapActivity2.ID_DESC:
                mapaActual.setDescripcion(value);
                break;
            case EditMapActivity2.ID_NUM_DESP:
                mapaActual.setNumDespacho(Integer.parseInt(value));
                break;
            case EditMapActivity2.ID_MAPA_X:
                fValue = Float.parseFloat(value);
                mapaActual.tam[0]=fValue;
                break;
            case EditMapActivity2.ID_MAPA_Y:
                fValue = Float.parseFloat(value);
                mapaActual.tam[1]=fValue;
                break;
            case EditMapActivity2.ID_MAPA_Z:
                fValue = Float.parseFloat(value);
                mapaActual.tam[2]=fValue;
                break;
            case EditMapActivity2.ID_MRK_X:
                fValue = Float.parseFloat(value);
                mapaActual.markerPos[0]=fValue;
                break;
            case EditMapActivity2.ID_MRK_Y:
                fValue = Float.parseFloat(value);
                mapaActual.markerPos[1]=fValue;
                break;
            case EditMapActivity2.ID_MRK_Z:
                fValue = Float.parseFloat(value);
                mapaActual.markerPos[2]=fValue;
                break;
        }
    }

    public Mapa getMapaActual() {
        return mapaActual;
    }

    class MapHeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView header;
        public MapHeaderViewHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.editMapHeader);
        }

        public void bindText(String headerText){
            header.setText(headerText);
        }
    }

    class MapAttributeViewHolder extends RecyclerView.ViewHolder{
        private ListItemMapaConfig listItemMapaConfig;

        public MapAttributeViewHolder(View itemView) {
            super(itemView);
            listItemMapaConfig = (ListItemMapaConfig)itemView;
        }

        public void bindData(AttributeData attributeData) {
            listItemMapaConfig.setData(attributeData);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return datos.get(position).getViewType();
    }

    abstract class EditMapContainer{
        int viewType;

        public int getViewType() {
            return viewType;
        }
    }
    class HeaderData extends EditMapContainer{
        public HeaderData(String headerText){ viewType=1; this.headerText=headerText;}
        String headerText;
    }
    class AttributeData extends EditMapContainer{
        public AttributeData(int valueId,String title, String value, String inputMessage, int inputType) {
            viewType=2;
            this.title = title;
            this.value = value;
            this.inputMessage = inputMessage;
            this.inputType = inputType;
        }

        String title;
        String value;
        String inputMessage;
        int inputType;
        int valueId;
    }
}
