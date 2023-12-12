package com.example.gastoapp.Vista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gastoapp.Controlador.ConexionHelper;
import com.example.gastoapp.Controlador.Utility;
import com.example.gastoapp.Modelo.Gasto;
import com.example.gastoapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Inicio extends AppCompatActivity {

    ListView listViewGastos;
    ArrayList<String> listaInformacion;
    ArrayList<Gasto> listaGastos;
    ConexionHelper conn;
    Button btnMostrarCalendario;
    TextView amountTextView;

    Calendar calendar;

    private int estadoFiltro = Utility.FILTRO_DIA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);


        amountTextView = findViewById(R.id.amountTextView);
        listViewGastos = findViewById(R.id.listViewGastos);

        conn = new ConexionHelper(getApplicationContext(), "bd_gastos", null, 2);

        try {
            consultarListaGastos();
            configurarListView();

        } catch (Exception e) {
            Log.e("Inicio", "Error en onCreate", e);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fabAgregar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inicio.this, AgregarMonto.class);
                startActivity(intent);
            }
        });

        //botn dia para que se muestre el calendario
        btnMostrarCalendario = findViewById(R.id.dayTextView);
        calendar = Calendar.getInstance();



        RadioButton totalTextView = findViewById(R.id.totalTextView);
        RadioButton dayTextView = findViewById(R.id.dayTextView);
        personalizarRadioButton(dayTextView);
        personalizarRadioButton(totalTextView);

        totalTextView.setBackgroundResource(R.drawable.btn_state_colors);
        dayTextView.setBackgroundResource(R.drawable.btn_state_colors);

        dayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoFiltro = Utility.FILTRO_DIA;
                try{
                    mostrarCalendario();
                    consultarListaGastos();
                    configurarListView();
                    calcularTotalDia();
                }catch (Exception e){
                    Log.e("Inicio", "Error al cargar lista con filtro total", e);
                }
                mostrarDayEnAmountTextView();
                //colores para cuando se selecciona uno
                totalTextView.setBackgroundResource(R.drawable.btn_state_colors);
                dayTextView.setBackgroundResource(R.drawable.btn_state_colorr_selected);
            }
        });



        totalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cambiar el estado del filtro a FILTRO_TOTAL
                estadoFiltro = Utility.FILTRO_TOTAL;

                // Volver a cargar la lista
                try {
                    consultarListaGastos();
                    configurarListView();
                    calcularTotal();
                } catch (Exception e) {
                    Log.e("Inicio", "Error al cargar lista con filtro total", e);
                }
                mostrarTotalEnAmountTextView();
                totalTextView.setBackgroundResource(R.drawable.btn_state_colorr_selected);
                dayTextView.setBackgroundResource(R.drawable.btn_state_colors);
            }
        });
    }
    private void personalizarRadioButton(RadioButton radioButton) {
        radioButton.setAllCaps(false);
        radioButton.setBackgroundResource(android.R.drawable.btn_default);
    }

    private void mostrarTotalEnAmountTextView() {
        int total = calcularTotal();
        amountTextView.setText("Total: $" + total);
    }

    private int calcularTotal() {
        int total = 0;

        for (Gasto gasto : listaGastos) {
            total += gasto.getMonto();
        }

        return total;
    }

    private int calcularTotalDia() {
        int totalDia = 0;

        for (Gasto gasto : listaGastos) {
            totalDia += gasto.getMonto();
        }
        return totalDia;

    }


    private void mostrarDayEnAmountTextView() {
        int totalDia = calcularTotalDia();
        amountTextView.setText("Total Día: $" + totalDia);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            consultarListaGastos();

            if (estadoFiltro == Utility.FILTRO_TOTAL) {
                mostrarTotalEnAmountTextView();
                mostrarDayEnAmountTextView();
            } else if (estadoFiltro == Utility.FILTRO_DIA) {
                mostrarDayEnAmountTextView();
            }
            configurarListView();
            ((ArrayAdapter) listViewGastos.getAdapter()).notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("Inicio", "Error en onResume", e);
        }
    }

    private void configurarListView() {
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaInformacion);
        listViewGastos.setAdapter(adaptador);

        listViewGastos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Gasto gastoSeleccionado = listaGastos.get(position);

                mostrarDialogoEliminarGasto(gastoSeleccionado.getId());
            }
        });
    }

    private void mostrarDialogoEliminarGasto(final int gastoId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_eliminar_gasto, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        Button btnEliminarGasto = view.findViewById(R.id.botonSi);
        Button btnCancelar = view.findViewById(R.id.botonCancelar);


        btnEliminarGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //para eliminar el gasto seleccionado de la db
                eliminarGasto(gastoId);
                dialog.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void eliminarGasto(int gastoId) {
        try {
            SQLiteDatabase db = conn.getWritableDatabase();

            String whereClause = Utility.CAMPO_ID + " = ?";
            String[] whereArgs = {String.valueOf(gastoId)};

            db.delete(Utility.TABLA_GASTOS, whereClause, whereArgs);

            String resetAutoIncrement = "DELETE FROM SQLITE_SEQUENCE WHERE NAME='" + Utility.TABLA_GASTOS + "'";
            db.execSQL(resetAutoIncrement);

            db.close();

            consultarListaGastos();
            configurarListView();

            ((ArrayAdapter) listViewGastos.getAdapter()).notifyDataSetChanged();

            Toast.makeText(getApplicationContext(), "Gasto eliminado", Toast.LENGTH_SHORT).show();

            if (estadoFiltro == Utility.FILTRO_TOTAL) {
                mostrarTotalEnAmountTextView();
            } else if (estadoFiltro == Utility.FILTRO_DIA) {
                mostrarDayEnAmountTextView();
            }
        } catch (Exception e) {
            Log.e("Inicio", "Error al eliminar gasto de la base de datos", e);
            // Mostrar un mensaje en caso de error
            Toast.makeText(getApplicationContext(), "Error al eliminar gasto", Toast.LENGTH_SHORT).show();
        }
    }


    private void consultarListaGastos() {
        SQLiteDatabase db = conn.getReadableDatabase();
        Gasto gastos;
        listaGastos = new ArrayList<>();

        String filtro = "";
        if (estadoFiltro == Utility.FILTRO_DIA) {
            String fechaSeleccionada = Utility.obtenerFechaSeleccionada(calendar);
            Log.d("Inicio", "Fecha seleccionada: " + fechaSeleccionada);

            filtro = " WHERE " + Utility.CAMPO_FECHA + " = '" + fechaSeleccionada + "'";
            Log.d("Inicio", "Consulta SQL: SELECT * FROM " + Utility.TABLA_GASTOS + filtro);
        }



        Cursor cursor = db.rawQuery("SELECT * FROM " + Utility.TABLA_GASTOS + filtro, null);
        Log.d("Inicio", "Número de filas: " + cursor.getCount());


        while (cursor.moveToNext()) {
            gastos = new Gasto();
            gastos.setId(cursor.getInt(0));
            gastos.setNombre(cursor.getString(1));
            gastos.setMonto(cursor.getInt(2));
            listaGastos.add(gastos);
        }
        obtenerLista();
    }

    private void obtenerLista() {
        listaInformacion = new ArrayList<>();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        SQLiteDatabase db = conn.getReadableDatabase();

        String filtro = "";
        if (estadoFiltro == Utility.FILTRO_DIA) {
            String fechaSeleccionada = Utility.obtenerFechaSeleccionada(calendar);
            Log.d("Inicio", "Fecha seleccionada: " + fechaSeleccionada);

            filtro = " WHERE " + Utility.CAMPO_FECHA + " = '" + fechaSeleccionada + "'";
            Log.d("Inicio", "Consulta SQL: SELECT * FROM " + Utility.TABLA_GASTOS + filtro);
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + Utility.TABLA_GASTOS + filtro, null);

        int indiceId = cursor.getColumnIndexOrThrow(Utility.CAMPO_ID);
        int indiceNombre = cursor.getColumnIndexOrThrow(Utility.CAMPO_NOMBRE);
        int indiceMonto = cursor.getColumnIndexOrThrow(Utility.CAMPO_MONTO);
        int indiceFecha = cursor.getColumnIndexOrThrow(Utility.CAMPO_FECHA);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(indiceId);
            String nombre = cursor.getString(indiceNombre);
            int monto = cursor.getInt(indiceMonto);

            String fechaFormateada = cursor.getString(indiceFecha);

            String infoGasto = nombre + " // $" + monto + " // " + fechaFormateada;
            listaInformacion.add(infoGasto);
        }

        cursor.close();
        db.close();
    }



    private void actualizarFechaEnBoton() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        btnMostrarCalendario.setText(dateFormat.format(calendar.getTime()));
    }



    /*
         no se si usaré esto !! :D

    private void mostrarDialogoMes() {
        final String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Mes");
        builder.setItems(meses, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int mesSeleccionado = which + 1;
                mostrarDialogoAnio(mesSeleccionado);
            }
        });
        builder.show();
    }

    private void mostrarDialogoAnio(final int mesSeleccionado) {
        final String[] anios = {"2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Año");
        builder.setItems(anios, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String anioSeleccionado = anios[which];
                btnSeleccionarMesAnio.setText(obtenerNombreMes(mesSeleccionado) + " " + anioSeleccionado);
            }
        });
        builder.show();
    }

     */

    private void mostrarCalendario() {
        // Obtener la fecha actual
        int año = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day);
                        actualizarFechaEnBoton();

                        String fechaSeleccionada = day + "/" + (month + 1) + "/" + year;
                        Toast.makeText(Inicio.this, "Fecha seleccionada: " + fechaSeleccionada, Toast.LENGTH_SHORT).show();

                        try {
                            consultarListaGastos();
                            configurarListView();
                            ((ArrayAdapter) listViewGastos.getAdapter()).notifyDataSetChanged();
                            if (estadoFiltro == Utility.FILTRO_TOTAL) {
                                mostrarTotalEnAmountTextView();
                            } else if (estadoFiltro == Utility.FILTRO_DIA) {
                                mostrarDayEnAmountTextView();
                            }
                        } catch (Exception e) {
                            Log.e("Inicio", "Error al cargar lista con filtro total", e);
                        }
                    }
                }, año, mes, dia);

        datePickerDialog.show();
        try {
            consultarListaGastos();
            configurarListView();
            ((ArrayAdapter) listViewGastos.getAdapter()).notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("Inicio", "Error al cargar lista con filtro total", e);
        }

    }

    /*
    private String obtenerNombreMes(int numeroMes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        if (numeroMes >= 1 && numeroMes <= 12) {
            return meses[numeroMes - 1];
        } else {
            return "Mes Desconocido";
        }
    }

     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.change_password) {
            Intent changePasswordIntent = new Intent(this, CambiarPass.class);
            changePasswordIntent.putExtra("changePassword", true);
            startActivity(changePasswordIntent);
            return true;
        } else if (id == R.id.clear_data) {
            mostrarDialogoConfirmacion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_confirmacion, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        Button btnEliminar = view.findViewById(R.id.btnEliminar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setPinIntent = new Intent(Inicio.this, SetPinActivity.class);
                startActivity(setPinIntent);

                Toast.makeText(Inicio.this, "Se han eliminado todos los datos", Toast.LENGTH_SHORT).show();
                finish();
                limpiarDatos();

                dialog.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void limpiarDatos() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        limpiarDatosEnBaseDeDatos();

    }

    private void limpiarDatosEnBaseDeDatos() {
        try {
            ConexionHelper conn = new ConexionHelper(getApplicationContext(), "bd_gastos", null, 2);
            SQLiteDatabase db = conn.getWritableDatabase();

            db.delete(Utility.TABLA_GASTOS, null, null);

            String resetAutoIncrement = "DELETE FROM SQLITE_SEQUENCE WHERE NAME='" + Utility.TABLA_GASTOS + "'";
            db.execSQL(resetAutoIncrement);

            Toast.makeText(getApplicationContext(), "Datos de la base de datos eliminados", Toast.LENGTH_SHORT).show();

            db.close();
        } catch (Exception e) {
            Log.e("LimpiarDatos", "Error al limpiar datos de la base de datos", e);
            Toast.makeText(getApplicationContext(), "Error al limpiar datos de la base de datos", Toast.LENGTH_SHORT).show();
        }
    }
}
