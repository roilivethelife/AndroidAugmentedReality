package com.example.roi.climaar;

import android.content.Context;
import android.content.DialogInterface;
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
    private AlertDialog.Builder alertDialogBuilder;
    private EditText input;
    private LinearLayout widgetLayout;

    private int valueId;
    private int inputType;
    private String value;
    private String inputText;

    private ListItemMapaConfigInterface listener;

    public ListItemMapaConfig(Context context) {
        super(context);
        inputType=0;
        value = "";
        inputText = "";
        inicializar(context);
    }

    public ListItemMapaConfig(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inicializar(context);

        // Procesamos los atributos XML personalizados
        TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.ListItemMapaConfig);
        String textoBoton = a.getString(R.styleable.ListItemMapaConfig_title);
        inputText = a.getString(R.styleable.ListItemMapaConfig_descInputType);
        if(inputText==null) inputText="";
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
        crearAlertDialogBuilder(context);
    }

    private void crearAlertDialogBuilder(Context context) {
        //if(inputType==0) return;
        alertDialogBuilder = new AlertDialog.Builder(context);
        input = new EditText(context);
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setPositiveButton("Guardar",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                value = input.getText().toString();
                summary.setText(value);
                if (listener != null) {
                    listener.onDataChanged(valueId,value);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
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
        value = data.value;
        inputText = data.inputMessage;
        inputType = data.inputType;
        this.title.setText(data.title);
        this.summary.setText(value);
        alertDialogBuilder.setTitle(data.title);
        alertDialogBuilder.setMessage(inputText);
        input.setText(value);
        valueId= data.valueId;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    public void setOnDataChandedListener(ListItemMapaConfigInterface listener){
        this.listener = listener;
    }

    public void onClick() {
        Log.d("ListItem","onClick()");
        switch (inputType){
            case TYPE_EDITTEXT://text
                input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
                alertDialogBuilder.create().show();
                break;
            case TYPE_DECIMAL://float
                input.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_FLAG_DECIMAL);
                alertDialogBuilder.create().show();
                break;
            case TYPE_NUMBER://numero
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                alertDialogBuilder.create().show();
                break;
            default:
            case TYPE_NONE:
        }
    }

    public interface ListItemMapaConfigInterface{
        void onDataChanged(int id, String value);
    }
}
