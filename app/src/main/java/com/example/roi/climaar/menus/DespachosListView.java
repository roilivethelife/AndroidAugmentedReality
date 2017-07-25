package com.example.roi.climaar.menus;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.despacho.Despacho;

/**
 * Created by roi on 22/06/17.
 */

public class DespachosListView extends LinearLayout implements AdapterView.OnItemClickListener, View.OnClickListener{

    private ItemAdapter mItemAdapter;
    private ListView mListView;
    private ImageButton buttonAdd;
    private ImageButton buttonEdit;
    private ImageButton buttonDel;

    private DespachosListViewInterface listener;

    public DespachosListView(Context context) {
        super(context);
        inicializar(context);
    }

    public DespachosListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar(context);
    }

    private void inicializar(Context context) {
        //Utilizamos el layout menu opciones layout
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.list_view_despachos_layout, this, true);

        mItemAdapter = new ItemAdapter(context);
        mListView = (ListView) findViewById(R.id.list_view_mapas);
        mListView.setAdapter(mItemAdapter);
        mListView.setOnItemClickListener(this);
        buttonAdd = (ImageButton) findViewById(R.id.imageButtonAdd);
        buttonEdit = (ImageButton) findViewById(R.id.imageButtonEdit);
        buttonDel = (ImageButton) findViewById(R.id.imageButtonDel);
        buttonAdd.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        buttonDel.setOnClickListener(this);
        buttonEdit.setEnabled(false);
        buttonEdit.setEnabled(false);

    }

    public void setListener(DespachosListViewInterface listener) {
        this.listener = listener;
    }

    public boolean deleteSelectedItem(){
        if(mItemAdapter.seleccionado==-1 || mItemAdapter.seleccionado>=Modelo.getInstance().getMapasSize()){
            return false;
        }else{
            Modelo.getInstance().deleteMapa(mItemAdapter.seleccionado);
            mItemAdapter.seleccionado = -1;
            mItemAdapter.notifyDataSetChanged();
            buttonEdit.setEnabled(false);
            buttonEdit.setEnabled(false);
            return true;
        }
    }

    public void notifyNewData(){
        mItemAdapter.seleccionado = -1;
        mItemAdapter.notifyDataSetChanged();
    }

    public Despacho getMapaSeleccionado(){
        if(mItemAdapter.seleccionado==-1) return null;
        return Modelo.getInstance().getMapa(mItemAdapter.seleccionado);
    }


    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mItemAdapter.seleccionado = position;
        mItemAdapter.notifyDataSetChanged();
        if(getMapaSeleccionado()!=null){
            buttonEdit.setEnabled(true);
            buttonDel.setEnabled(true);
        }
        if(listener!=null){
            listener.onSelectedDespachoChanged();
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageButtonAdd:
                if(listener!=null){
                    listener.onAddNewDespachoPushed();
                }
                break;
            case R.id.imageButtonEdit:
                if(listener!=null){
                    listener.onEditDespachoPushed(getMapaSeleccionado());
                }
                break;
            case R.id.imageButtonDel:
                if(listener!=null) {
                    listener.onDelDespachoPushed(getMapaSeleccionado());
                }
                break;
        }
    }

    private class ItemAdapter extends BaseAdapter {
        Context context;
        int seleccionado = -1;

        ItemAdapter(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return Modelo.getInstance().getMapasSize();
        }

        @Override
        public Object getItem(int i) {
            return Modelo.getInstance().getMapa(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            View rowView = view;

            if (view == null) {
                // Create a new view into the list.
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.list_item_name_desc, parent, false);
            }

            // Set data into the view.
            TextView tvNombre = (TextView) rowView.findViewById(R.id.textViewNombre);
            TextView tvDescripcion = (TextView) rowView.findViewById(R.id.textViewDescripcion);

            Despacho map = Modelo.getInstance().getMapa(i);
            tvNombre.setText(map.getNombre());
            tvDescripcion.setText(map.getDescripcion());
            //Log.d("REDRAW","I="+i+"   Seleccionado="+seleccionado);
            if(i==seleccionado){
                rowView.setBackgroundResource(R.color.colorPrimaryLight);
            }else {
                rowView.setBackgroundColor(Color.parseColor("#FAFAFA"));//grisaceo
            }

            return rowView;
        }


    }

    public interface DespachosListViewInterface {
        void onSelectedDespachoChanged();
        void onAddNewDespachoPushed();
        void onEditDespachoPushed(Despacho despachoSeleccionado);
        void onDelDespachoPushed(Despacho despachoSeleccionado);
    }
}
