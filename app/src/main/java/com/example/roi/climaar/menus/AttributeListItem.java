package com.example.roi.climaar.menus;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.roi.climaar.R;


/**
 * Created by roi on 22/06/17.
 */

public class AttributeListItem extends LinearLayout{
    public static final int TYPE_NONE = 0;
    public static final int TYPE_EDITTEXT = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_DECIMAL = 3;

    private TextView title;
    private TextView summary;


    public AttributeListItem(Context context) {
        super(context);
        inicializar();
    }

    public AttributeListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inicializar();

        // Procesamos los atributos XML personalizados
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AttributeListItem);
        String textoTitle = a.getString(R.styleable.AttributeListItem_title);
        if(textoTitle!=null) {
            title.setText(textoTitle);
        }
        a.recycle();
    }

    private void inicializar() {
        //Utilizamos el layout menu opciones layout
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.list_item_edit_despacho_attribute, this, true);
        title = (TextView) findViewById(R.id.title);
        summary = (TextView) findViewById(R.id.summary);
    }

    public void setTitleText(String titleText) {
        title.setText(titleText);
    }

    public void setSummaryText(String summaryText) {
        summary.setText(summaryText);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

}
