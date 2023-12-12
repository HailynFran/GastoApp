package com.example.gastoapp.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gastoapp.R;

public class CambiarPass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pass);

        EditText editTextNewPin = findViewById(R.id.editTextNewPin);
        EditText editTextNewPin2 = findViewById(R.id.editTextNewPin2);
        EditText editTextActual = findViewById(R.id.editTextActual);
        Button savePinButton = findViewById(R.id.savePinButton);

        savePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPin = editTextNewPin.getText().toString();
                String newPin2 = editTextNewPin2.getText().toString();
                String actualPin = editTextActual.getText().toString();

                if (checkPin(actualPin)) {
                    if (newPin.equals(newPin2) && newPin.length() >= 4) {
                        savePin(newPin);

                        Toast.makeText(CambiarPass.this, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        Toast.makeText(CambiarPass.this, "Los nuevos PIN no coinciden o no tienen al menos 4 dígitos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CambiarPass.this, "PIN actual incorrecto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkPin(String enteredPin) {
        String storedPin = getStoredPin();
        return storedPin != null && enteredPin.equals(storedPin);
    }

    private String getStoredPin() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return preferences.getString("PIN", null);
    }

    private void savePin(String pin) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("PIN", pin);
        editor.apply();
    }
}