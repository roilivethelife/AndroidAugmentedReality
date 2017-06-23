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

public class AdaptadorEditMap extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private View.OnClickListener listener;
    ArrayList<EditMapContainer> datos;
    Mapa mapaActual;



    public AdaptadorEditMap(Mapa mapaActual) {
        this.mapaActual=mapaActual;
        datos = new ArrayList<>();
        datos.add(new HeaderData("Datos mapa"));
        datos.add(new AttributeData("Nombre",
                mapaActual.getNombre(),"Nombre del mapa",ListItemMapaConfig.TYPE_EDITTEXT));
        datos.add(new AttributeData("Descripcion",
                mapaActual.getDescripcion(),"Descripción del mapa",ListItemMapaConfig.TYPE_EDITTEXT));
        datos.add(new AttributeData("NºDespacho",
                ""+mapaActual.getNumDespacho(),"Número de despacho del mapa",ListItemMapaConfig.TYPE_NUMBER));
        datos.add(new HeaderData("Tamaño de la sala"));
        datos.add(new AttributeData("Ancho",
                ""+mapaActual.tam[0],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new AttributeData("Alto",
                ""+mapaActual.tam[1],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new AttributeData("Largo",
                ""+mapaActual.tam[2],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new HeaderData("Posición Marcador"));
        datos.add(new AttributeData("Posición X",
                ""+mapaActual.markerPos[0],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new AttributeData("Posición Y",
                ""+mapaActual.markerPos[1],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new AttributeData("Posición Z",
                ""+mapaActual.markerPos[2],"",ListItemMapaConfig.TYPE_DECIMAL));
        datos.add(new HeaderData("Elementos de climatización"));
        datos.add(new AttributeData("Editar elementos",
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
        public AttributeData(String title, String summary, String message, int inputType) {
            viewType=2;
            this.title = title;
            this.summary = summary;
            this.message = message;
            this.inputType = inputType;
        }

        String title;
        String summary;
        String message;
        int inputType;
    }
}
