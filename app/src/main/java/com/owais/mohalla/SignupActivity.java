package com.owais.mohalla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextName,editTextPhone,editTextAddress;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        editTextName=findViewById(R.id.signup_editTextName);
        editTextPhone=findViewById(R.id.signup_editTextPhone);
        editTextAddress=findViewById(R.id.signup_editTextAddress);
        button=findViewById(R.id.signup_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editTextName.getText().toString().trim();
                final String phone = editTextPhone.getText().toString().trim();
                final String address = editTextAddress.getText().toString().trim();

                if (!validateEditText(editTextName)) {
                    return;
                }
                if (!validateEditText(editTextPhone)) {
                    return;
                }
                if (!validateEditText(editTextAddress)) {
                    return;
                }

                Intent intent = new Intent(SignupActivity.this, VerificationActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });

    }
    private boolean validateEditText(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError("This field is required");
            editText.requestFocus();

            return false;
        }
        return true;
    }
}
