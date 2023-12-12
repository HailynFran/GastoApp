package com.example.gastoapp.Vista;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gastoapp.Controlador.ConexionHelper;
import com.example.gastoapp.Controlador.Utility;
import com.example.gastoapp.R;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String SESSION_PIN_ENTERED = "SESSION_PIN_ENTERED";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        EditText editTextPin = findViewById(R.id.editTextPin);
        Button authenticateButton = findViewById(R.id.button);
        TextView borrarDatos = findViewById(R.id.buttonPassOlvidada);

        editTextPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        borrarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogo();
            }
        });

        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredPin = editTextPin.getText().toString();

                if (checkPin(enteredPin)) {
                    setPinEnteredThisSession();

                    Intent intent = new Intent(AuthenticationActivity.this, Inicio.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AuthenticationActivity.this, "PIN incorrecto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_pin_olvidado, null);
        builder.setView(view);

        dialog = builder.create();

        Button btnEliminar = view.findViewById(R.id.btnEliminarTodo);
        Button btnCancelar = view.findViewById(R.id.btnCancelarAccion);

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setPinIntent = new Intent(AuthenticationActivity.this, SetPinActivity.class);
                startActivity(setPinIntent);

                Toast.makeText(AuthenticationActivity.this, "Se han eliminado todos los datos", Toast.LENGTH_SHORT).show();
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







    /*

    private void limpiarDatosEnBaseDeDatos() {
        try {
            ConexionHelper conn = new ConexionHelper(getApplicationContext(), "bd_gastos", null, 2);
            SQLiteDatabase db = conn.getWritableDatabase();

            // Elimina todas las filas de la tabla
            db.delete(Utility.TABLA_GASTOS, null, null);

            // Opcional: Restablece el ID automático si es una columna AUTOINCREMENT
            String resetAutoIncrement = "DELETE FROM SQLITE_SEQUENCE WHERE NAME='" + Utility.TABLA_GASTOS + "'";
            db.execSQL(resetAutoIncrement);

            Toast.makeText(getApplicationContext(), "Datos de la base de datos eliminados", Toast.LENGTH_SHORT).show();

            db.close();
        } catch (Exception e) {
            Log.e("LimpiarDatos", "Error al limpiar datos de la base de datos", e);
            Toast.makeText(getApplicationContext(), "Error al limpiar datos de la base de datos", Toast.LENGTH_SHORT).show();
        }
    }
    */






    private boolean checkPin(String enteredPin) {
        String storedPin = getStoredPin();
        return storedPin != null && enteredPin.equals(storedPin);
    }

    private String getStoredPin() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return preferences.getString("PIN", null);
    }

    private void setPinEnteredThisSession() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        preferences.edit().putBoolean(SESSION_PIN_ENTERED, true).apply();
    }
}
