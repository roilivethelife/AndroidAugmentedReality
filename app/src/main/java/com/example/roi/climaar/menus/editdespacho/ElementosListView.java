package com.example.roi.climaar.menus.editdespacho;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.despacho.DespachoElement;

import java.util.ArrayList;

/**
 * Created by roi on 22/06/17.
 */

class ElementosListView extends LinearLayout implements View.OnClickListener{

    private static final String LOGTAG = "ElementosListView";
    private ArrayList<DespachoElement> elements;
    private ArrayList<ElementoInfoListItem> elementsView;
    private int elementoSeleccionado;

    //private ImageButton buttonAdd;
    private ImageButton buttonEdit;
    //private ImageButton buttonDel;
    private LinearLayout listView;
    private TextView textNoMoreElements;

    private ElementosListViewInterface listener;

    public ElementosListView(Context context) {
        super(context);
        inicializar(context);
    }

    public ElementosListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar(context);
    }

    private void inicializar(Context context) {
        //Utilizamos el layout menu opciones layout
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.list_view_elementos_layout, this, true);

        elementoSeleccionado = -1;
        elements = new ArrayList<>();
        elementsView = new ArrayList<>();
        //buttonAdd = (ImageButton) findViewById(R.id.imageButtonAdd);
        buttonEdit = (ImageButton) findViewById(R.id.imageButtonEdit);
        //buttonDel = (ImageButton) findViewById(R.id.imageButtonDel);
        listView = (LinearLayout) findViewById(R.id.linearLayoutListView);
        textNoMoreElements = (TextView) listView.findViewById(R.id.textNoMoreElements);
        textNoMoreElements.setOnClickListener(this);
        //buttonAdd.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        //buttonDel.setOnClickListener(this);
    }

    public void addElemento(DespachoElement element){
        if(elements.contains(element)) return;

        ElementoInfoListItem elementoView = new ElementoInfoListItem(getContext());
        elementoView.setNombreText(element.getName());
        elementoView.setDescText(element.getFigureTypeString());
        elementoView.setOnClickListener(this);
        elements.add(element);
        elementsView.add(elementoView);
        listView.addView(elementoView,listView.getChildCount()-1);
    }

    public void delElemento(DespachoElement element){
        if(!elements.contains(element)) return;
        int index = elements.indexOf(element);
        if(elementoSeleccionado==index){
            elementoSeleccionado=-1;
        }else if(elementoSeleccionado>index){
            elementoSeleccionado--;
            //ya esta pintado, no hace falta volver a pintarlo
        }
        listView.removeView(elementsView.get(index));
        elements.remove(index);
        elementsView.remove(index);
    }

    public DespachoElement getElementSeleccionado(){
        if(elementoSeleccionado==-1) return null;
        return elements.get(elementoSeleccionado);
    }


    private void newSelectedElement(int newindex){
        if(elementoSeleccionado!=-1){
            elementsView.get(elementoSeleccionado).setBackgroundColor(Color.parseColor("#FAFAFA"));
        }
        elementoSeleccionado = newindex;
        if(newindex!=-1) {
            elementsView.get(newindex).setBackgroundResource(R.color.colorPrimaryLight);
        }
    }

    public void setListener(ElementosListViewInterface listener) {
        this.listener = listener;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v instanceof ElementoInfoListItem){
            ElementoInfoListItem elementoView = (ElementoInfoListItem) v;
            int index = elementsView.indexOf(elementoView);
            newSelectedElement(index);
            if(listener!=null){
                listener.onSelectedElementoChanged();
            }
        }else {
            switch (v.getId()) {
                /*case R.id.imageButtonAdd:
                    if (listener != null) {
                        listener.onAddElementoPushed();
                    }
                    break;*/
                case R.id.imageButtonEdit:
                    if (listener != null) {
                        listener.onEditElementoPushed(getElementSeleccionado());
                    }
                    break;
                /*case R.id.imageButtonDel:
                    if (listener != null) {
                        listener.onDelElementoPushed(getElementSeleccionado());
                    }*/
                case R.id.textNoMoreElements:
                    if(listener!=null){
                        newSelectedElement(-1);
                        listener.onSelectedElementoChanged();
                    }
                    break;
            }
        }
    }

    public void delAllElements() {
        for (int i = 0; i < elementsView.size(); i++) {
            listView.removeView(elementsView.get(i));
        }
        elementsView.clear();
        elements.clear();
        elementoSeleccionado = -1;
    }


    public interface ElementosListViewInterface{
        void onSelectedElementoChanged();
        //void onAddElementoPushed();
        void onEditElementoPushed(DespachoElement element);
        //void onDelElementoPushed(DespachoElement element);
    }
}
