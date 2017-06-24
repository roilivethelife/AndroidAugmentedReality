package com.example.roi.climaar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.SimpleOnItemTouchListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.mapa.Mapa;

public class EditMapActivity2 extends AppCompatActivity implements View.OnClickListener{
    public static final int ID_NAME = 1;
    public static final int ID_DESC = 2;
    public static final int ID_NUM_DESP= 3;
    public static final int ID_MAPA_X = 4;
    public static final int ID_MAPA_Y = 5;
    public static final int ID_MAPA_Z = 6;
    public static final int ID_MRK_X = 7;
    public static final int ID_MRK_Y = 8;
    public static final int ID_MRK_Z = 9;
    public static final int ID_EDIT_ELEMENTS = 10;


    private Mapa mapa;
    private boolean editMap;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_new_map);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        if(myToolbar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent data = getIntent();
        mapa = null;
        if(data!=null && data.getExtras()!=null) {
            mapa = (Mapa) data.getExtras().getSerializable("MAPA");
            if(mapa!=null){
                editMap = true;
                myToolbar.setTitle("Editar mapa");
            }
        }
        if(mapa==null){
            editMap = false;
            mapa = new Mapa("nuevo mapa");
            myToolbar.setTitle("Nuevo mapa");
        }

        Mapa editMap = new Mapa(mapa);//TODO: editar en un mapa copia y al guardar los cambios reemplazar los mapas

        recyclerView = (RecyclerView) findViewById(R.id.recView);
        recyclerView.setHasFixedSize(true);
        AdaptadorEditMap adaptador = new AdaptadorEditMap(mapa);
        adaptador.setOnClickListener(this);
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_edit_map,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbarSaveEditMap:
                break;
            case R.id.toolbarHelpEditMap:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Log.i("DemoRecView", "Pulsado el elemento " + recyclerView.getChildAdapterPosition(v));
    }
}
