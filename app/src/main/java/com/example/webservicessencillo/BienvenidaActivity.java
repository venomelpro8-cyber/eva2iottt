package com.example.webservicessencillo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BienvenidaActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // 1. Recibimos el nombre que nos envía la pantalla de Login
        String nombreRecibido = getIntent().getStringExtra("nombre_usuario");

        if (nombreRecibido != null && !nombreRecibido.trim().isEmpty()) {
            // Formateamos el nombre para que empiece con mayúscula
            String nombreFormateado = nombreRecibido.substring(0, 1).toUpperCase() + nombreRecibido.substring(1).toLowerCase();

            // Lo mostramos en el centro de la pantalla
            tvBienvenida.setText("¡Bienvenido al sistema,\n" + nombreFormateado + "!");

            // ¡NUEVO! Cambiamos el texto de la barra verde superior (ActionBar)
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Smarth Greenhouse");
            }

        } else {
            // Si por algún motivo el nombre llega vacío
            tvBienvenida.setText("¡Bienvenido al sistema!");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Mi Perfil");
            }
        }

        // 2. Acción del botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volvemos al Login
                Intent intent = new Intent(BienvenidaActivity.this, LoginActivity.class);
                startActivity(intent);

                // Cerramos esta pantalla para que no pueda volver atrás
                finish();
            }
        });
    }
}