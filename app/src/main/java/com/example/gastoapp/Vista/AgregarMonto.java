package com.example.gastoapp.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.gastoapp.Controlador.ConexionHelper;
import com.example.gastoapp.Controlador.Utility;
import com.example.gastoapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AgregarMonto extends AppCompatActivity {

    private List<ToggleButton> toggleButtons = new ArrayList<>();
    CalendarView calendarView;
    EditText editTextMonto;
    private Button btnAdd;

    private long fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_monto);

        toggleButtons.add(findViewById(R.id.toggleCompras));
        toggleButtons.add(findViewById(R.id.toggleOcio));
        toggleButtons.add(findViewById(R.id.toggleFacturas));
        toggleButtons.add(findViewById(R.id.toggleTransporte));
        toggleButtons.add(findViewById(R.id.toggleSalud));
        toggleButtons.add(findViewById(R.id.toggleRenta));
        toggleButtons.add(findViewById(R.id.toggleEducacion));
        toggleButtons.add(findViewById(R.id.toggleOtro));

        btnAdd = findViewById(R.id.btnAdd);
        editTextMonto = findViewById(R.id.editTextMonto);
        calendarView = findViewById(R.id.calendarView);

        // Configuración del listener para el cambio de fecha
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaSeleccionada = calendar.getTimeInMillis();
            }
        });

        for (final ToggleButton toggleButton : toggleButtons) {
            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onToggleButtonClicked(toggleButton);
                }
            });
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreSeleccionado = null;

                for (ToggleButton toggleButton : toggleButtons) {
                    if (toggleButton.isChecked()) {
                        nombreSeleccionado = toggleButton.getText().toString();
                        break;
                    }
                }

                if (nombreSeleccionado != null) {
                    if (fechaSeleccionada > 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(fechaSeleccionada);

                        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
                        String fechaComoTexto = formatoFecha.format(calendar.getTime());

                        String monto = editTextMonto.getText().toString();

                        if (!monto.isEmpty()) {
                            try {
                                ConexionHelper conn = new ConexionHelper(getApplicationContext(), "bd_gastos", null, 2);
                                SQLiteDatabase db = conn.getWritableDatabase();

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Utility.CAMPO_NOMBRE, nombreSeleccionado);
                                contentValues.put(Utility.CAMPO_MONTO, monto);
                                contentValues.put(Utility.CAMPO_FECHA, fechaComoTexto);

                                long idResultante = db.insert(Utility.TABLA_GASTOS, null, contentValues);

                                if (idResultante != -1) {
                                    Toast.makeText(getApplicationContext(), "Datos guardados con éxito", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                                }


                                db.close();
                            } catch (Exception e) {
                                Log.e("AgregarMonto", "Error al insertar en la base de datos", e);
                                Toast.makeText(getApplicationContext(), "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Por favor, ingrese un monto válido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Por favor, seleccione una fecha", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AgregarMonto.this, "Por favor, seleccione un tipo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnCancelar = findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AgregarMonto.this, Inicio.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //esto es para que no seseleccionen varios botones
    private void onToggleButtonClicked(ToggleButton clickedButton) {
        for (ToggleButton toggleButton : toggleButtons) {
            if (toggleButton != clickedButton) {
                toggleButton.setChecked(false);
            }
        }
    }


}