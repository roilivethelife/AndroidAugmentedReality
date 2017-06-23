package com.example.roi.climaar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by roi on 22/06/17.
 */

public class ListItemMapaConfig extends LinearLayout{
    public static final int TYPE_NONE = 0;
    public static final int TYPE_EDITTEXT = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_DECIMAL = 3;

    private TextView title;
    private TextView summary;
    private String descripcion;
    private AlertDialog alertDialog;
    private EditText input;
    private LinearLayout widgetLayout;
    private int inputType;

    public ListItemMapaConfig(Context context) {
        super(context);
        inicializar(context);
        inputType=0;
        descripcion = "";
    }

    public ListItemMapaConfig(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inicializar(context);

        // Procesamos los atributos XML personalizados
        TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.ListItemMapaConfig);
        String textoBoton = a.getString(R.styleable.ListItemMapaConfig_title);
        descripcion = a.getString(R.styleable.ListItemMapaConfig_descInputType);
        if(descripcion==null) descripcion="";
        inputType = a.getInt(R.styleable.ListItemMapaConfig_inputType,0);
        if(textoBoton!=null) {
            title.setText(textoBoton);
        }
        a.recycle();
    }

    private void inicializar(Context context) {
        //Utilizamos el layout menu opciones layout
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.list_item_edit_map_attribute, this, true);

        title = (TextView) findViewById(R.id.title);
        summary = (TextView) findViewById(R.id.summary);
        widgetLayout = (LinearLayout) findViewById(R.id.widget_frame);
        crearAlertDialog(context);
    }

    private void crearAlertDialog(Context context) {
        //if(inputType==0) return;
        alertDialog = new AlertDialog.Builder(context).create();
        input = new EditText(context);
        input.setHint("hint");
        alertDialog.setTitle("title");
        alertDialog.setMessage("message");
        alertDialog.setView(input);
        if(input==null || alertDialog==null){
            Log.d("LIst","CrearAlertDialog=Null");
        }else{
            Log.d("LIst","CrearAlertDialog=OK");
        }
    }

    public LinearLayout getWidgetLayout() {
        return widgetLayout;
    }

    public void setTitleText(String titleText) {
        title.setText(titleText);
    }

    public void setSummaryText(String summaryText) {
        summary.setText(summaryText);
    }

    public void setData(AdaptadorEditMap.AttributeData data){
        this.title.setText(data.title);
        this.summary.setText(data.summary);
        inputType = data.inputType;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    public void onClick() {
        Log.d("ListItem","onClick()");
        switch (inputType){
            case TYPE_EDITTEXT://text
                if(input!=null){
                    input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
                }
                alertDialog.show();
                break;
            case TYPE_DECIMAL://float
                if(input!=null)  input.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_FLAG_DECIMAL);
                alertDialog.show();
                break;
            default:
            case TYPE_NONE:
        }
    }
}
