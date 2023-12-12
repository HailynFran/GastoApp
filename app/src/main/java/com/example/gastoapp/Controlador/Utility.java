package com.example.gastoapp.Controlador;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utility {

    public static final String TABLA_GASTOS = "gastos";
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_MONTO = "monto";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_FECHA = "fecha";
    public static final String FORMATO_FECHA = "dd/MM/yyyy"; // Formato de fecha


    public static final String CREAR_TABLA_GASTOS = "CREATE TABLE " + TABLA_GASTOS +
            "(" + CAMPO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CAMPO_NOMBRE + " TEXT, " +
            CAMPO_MONTO + " INTEGER, " +
            CAMPO_FECHA + " TEXT)";

    public static final int FILTRO_DIA = 1;
    public static final int FILTRO_MES = 2;
    public static final int FILTRO_TOTAL = 3;


    public static String obtenerFechaSeleccionada(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

}
