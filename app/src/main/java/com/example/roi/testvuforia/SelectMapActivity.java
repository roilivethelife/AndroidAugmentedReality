package com.example.roi.testvuforia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.roi.testvuforia.graficos.Mapa.Mapa;

import java.util.ArrayList;
import java.util.List;

public class SelectMapActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener{

    private ListView listView;
    private Button aceptarButton;
    private Button cancelButton;
    private TextView textViewAccion;

    private ItemAdapter itemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_map);

        aceptarButton = (Button) findViewById(R.id.button_aceptar);
        cancelButton = (Button) findViewById(R.id.button_cancelar);
        listView = (ListView) findViewById(R.id.list_view);
        textViewAccion = (TextView) findViewById(R.id.textViewAccion);

        cancelButton.setOnClickListener(this);
        aceptarButton.setOnClickListener(this);

        itemAdapter= new ItemAdapter(this);
        listView.setOnItemClickListener(this);
        listView.setAdapter(itemAdapter);

        aceptarButton.setOnClickListener(this);

        textViewAccion.setText(getIntent().getStringExtra("TITULO"));
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        itemAdapter.seleccionado = position;
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_aceptar:
                Intent resultIntent = new Intent();
                if(itemAdapter.seleccionado>=0) {
                    Mapa map = (Mapa)itemAdapter.getItem(itemAdapter.seleccionado);
                    resultIntent.putExtra("MAPA",map);
                }
                setResult(RESULT_OK,resultIntent);
                finish();
                break;
            case R.id.button_cancelar:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    private class ItemAdapter extends BaseAdapter{
        Context context;
        ArrayList<Mapa> mapas;
        int seleccionado = -1;

        ItemAdapter(Context context){
            this.context = context;
            mapas = AppInstance.getInstance().getMapas();
        }

        @Override
        public int getCount() {
            return mapas.size();
        }

        @Override
        public Object getItem(int i) {
            return mapas.get(i);
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
                rowView = inflater.inflate(R.layout.list_item_mapa, parent, false);
            }

            // Set data into the view.
            TextView tvNombre = (TextView) rowView.findViewById(R.id.textViewNombre);
            TextView tvDescripcion = (TextView) rowView.findViewById(R.id.textViewDescripcion);

            Mapa map = mapas.get(i);
            tvNombre.setText(map.getNombre());
            tvDescripcion.setText(map.getDescripcion());
            //Log.d("REDRAW","I="+i+"   Seleccionado="+seleccionado);
            if(i==seleccionado){
                rowView.setBackgroundColor(Color.parseColor("#DDDDDD"));//grisaceo
            }else {
                rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));//grisaceo
            }

            return rowView;
        }


    }
}
