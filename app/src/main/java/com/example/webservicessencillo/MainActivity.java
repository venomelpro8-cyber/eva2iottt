package com.example.webservicessencillo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText etRut, etPassword, etNombre, etApellido, etTelefono, etCorreo, etFecha;
    private Button btnGuardar, btnLimpiar; // Se mantiene el nombre de variable, pero su función es volver atrás

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etRut = findViewById(R.id.rut);
        etPassword = findViewById(R.id.password);
        etNombre = findViewById(R.id.nombre);
        etApellido = findViewById(R.id.apellido);
        etTelefono = findViewById(R.id.telefono);
        etCorreo = findViewById(R.id.correo);
        etFecha = findViewById(R.id.fecha);

        btnGuardar = findViewById(R.id.acceso);
        btnLimpiar = findViewById(R.id.mostrar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearUsuario();
            }
        });

        // AQUÍ ESTÁ EL CAMBIO: Ahora ejecuta finish() para volver al Login
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void crearUsuario() {
        String rut = etRut.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String fechaIngresada = etFecha.getText().toString().trim();

        if (rut.isEmpty() || password.isEmpty() || nombre.isEmpty() ||
                apellido.isEmpty() || telefono.isEmpty() || correo.isEmpty() || fechaIngresada.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarRut(rut)) {
            Toast.makeText(this, "El RUT ingresado no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Por favor ingrese un correo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaParaEnviar = "";
        try {
            SimpleDateFormat formatoUsuario = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatoBaseDatos = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaConvertida = formatoUsuario.parse(fechaIngresada);
            fechaParaEnviar = formatoBaseDatos.format(fechaConvertida);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto. Use DD/MM/AAAA", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordCifrada = cifrarSHA256(password);

        RequestQueue colaPeticiones = Volley.newRequestQueue(this);

        String urlServidor = "http://192.168.137.247/test1/ingreso.php?rut=" + rut +
                "&password=" + passwordCifrada +
                "&nombre=" + nombre +
                "&apellido=" + apellido +
                "&telefono=" + telefono +
                "&correo=" + correo +
                "&fecha=" + fechaParaEnviar;

        urlServidor = urlServidor.replace(" ", "%20");

        StringRequest peticion = new StringRequest(Request.Method.GET, urlServidor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Limpiamos la respuesta del PHP
                        String respuestaLimpia = response.trim();

                        if (respuestaLimpia.equals("duplicado")) {
                            // Si el PHP detecta que existe, mostramos el error y NO cambiamos de pantalla
                            Toast.makeText(MainActivity.this, "Error: Este RUT ya está registrado", Toast.LENGTH_LONG).show();

                        } else if (respuestaLimpia.equals("exito")) {
                            // Si el PHP guarda todo bien, mostramos el éxito y pasamos al Login
                            Toast.makeText(MainActivity.this, "Usuario creado exitosamente", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // Por si ocurre cualquier otro error en el servidor
                            Toast.makeText(MainActivity.this, "Error al guardar en la base de datos", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error conectando al servidor", Toast.LENGTH_LONG).show();
                    }
                });

        colaPeticiones.add(peticion);
    }

    private boolean validarRut(String rut) {
        boolean esValido = false;
        try {
            rut = rut.toUpperCase().replace(".", "").replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));
            char digitoVerificador = rut.charAt(rut.length() - 1);
            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (digitoVerificador == (char) (s != 0 ? s + 47 : 75)) {
                esValido = true;
            }
        } catch (Exception e) {
            return false;
        }
        return esValido;
    }

    private String cifrarSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder stringHex = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    stringHex.append('0');
                }
                stringHex.append(hex);
            }
            return stringHex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al cifrar", e);
        }
    }
}