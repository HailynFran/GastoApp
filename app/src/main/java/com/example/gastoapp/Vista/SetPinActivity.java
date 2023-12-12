package com.example.gastoapp.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gastoapp.R;

public class SetPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        EditText editTextNewPin = findViewById(R.id.editTextNewPin);
        EditText editTextConfirmPin = findViewById(R.id.editTextConfirmPin);
        Button savePinButton = findViewById(R.id.savePinButton);

        editTextNewPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        editTextConfirmPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        savePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPin = editTextNewPin.getText().toString();
                String confirmPin = editTextConfirmPin.getText().toString();

                if (newPin.length() >= 4 && newPin.equals(confirmPin)) {
                    savePin(newPin);

                    startActivity(new Intent(SetPinActivity.this, AuthenticationActivity.class));

                    finish();
                } else {
                    Toast.makeText(SetPinActivity.this, "Los PIN no coinciden o no tienen 4 dígitos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savePin(String pin) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("PIN", pin);
        editor.putBoolean("PIN_SET", true);
        editor.apply();
    }
}
