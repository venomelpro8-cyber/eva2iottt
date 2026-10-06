package com.example.webservicessencillo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class LoginActivity extends AppCompatActivity {

    private EditText etRutLogin, etPasswordLogin;
    private Button btnIngresar, btnIrRegistro, btnLimpiar, btnCambiarPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // IDs vinculados al XML
        etRutLogin = findViewById(R.id.loginRut);
        etPasswordLogin = findViewById(R.id.loginPassword);
        btnIngresar = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnCrearUsuario);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnCambiarPass = findViewById(R.id.btnCambiarPass);

        // Acción: Botón para iniciar sesión
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        // Acción: Botón para limpiar los campos
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRutLogin.setText("");
                etPasswordLogin.setText("");
            }
        });

        // Acción: Botón para ir a la pantalla de crear usuario
        btnIrRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Botón para mostrar la ventana de recuperar contraseña
        if (btnCambiarPass != null) {
            btnCambiarPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarDialogoCambiarPassword();
                }
            });
        }
    }

    private void iniciarSesion() {
        String rut = etRutLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (rut.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese RUT y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordCifrada = cifrarSHA256(password);

        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        String urlServidor = "http://192.168.137.247/test1/login.php?rut=" + rut + "&password=" + passwordCifrada;

        StringRequest peticion = new StringRequest(Request.Method.GET, urlServidor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String respuestaLimpia = response.trim();

                        // Como ahora PHP manda "ingreso_exitoso,Nombre", usamos startsWith
                        if (respuestaLimpia.startsWith("ingreso_exitoso")) {

                            // Separamos la respuesta en dos partes usando la coma
                            String[] partes = respuestaLimpia.split(",");
                            String nombreUsuario = "";
                            if (partes.length > 1) {
                                nombreUsuario = partes[1]; // Aquí atrapamos el nombre
                            }

                            // Viajamos a la nueva pantalla y le enviamos el nombre
                            Intent intent = new Intent(LoginActivity.this, BienvenidaActivity.class);
                            intent.putExtra("nombre_usuario", nombreUsuario);
                            startActivity(intent);

                            // Cerramos el Login para que no se pueda retroceder
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "RUT o contraseña incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error conectando al servidor", Toast.LENGTH_LONG).show();
                    }
                });

        colaPeticiones.add(peticion);
    }

    // --- FUNCIONES PARA CAMBIAR LA CONTRASEÑA ---

    // 1. Muestra la ventanita emergente (Popup)
    private void mostrarDialogoCambiarPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Contraseña");

        // Layout para los campos de texto
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 10);

        final EditText inputRut = new EditText(this);
        inputRut.setHint("Ingrese su RUT (Ej: 12345678-9)");
        layout.addView(inputRut);

        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("Escriba su nueva contraseña");
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputPassword);

        builder.setView(layout);

        // Botón Guardar del Popup
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String rut = inputRut.getText().toString().trim();
                String nuevaPass = inputPassword.getText().toString().trim();

                if (rut.isEmpty() || nuevaPass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "RUT y nueva contraseña son obligatorios", Toast.LENGTH_SHORT).show();
                } else {
                    cambiarPasswordEnServidor(rut, nuevaPass);
                }
            }
        });

        // Botón Cancelar del Popup
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // 2. Envía la nueva contraseña cifrada a AWS mediante PHP
    private void cambiarPasswordEnServidor(String rut, String nuevaPass) {
        String passwordCifrada = cifrarSHA256(nuevaPass);

        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        String urlServidor = "http://192.168.137.247/test1/cambiar_password.php?rut=" + rut + "&password=" + passwordCifrada;

        StringRequest peticion = new StringRequest(Request.Method.GET, urlServidor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String respuestaLimpia = response.trim();
                        if (respuestaLimpia.equals("exito")) {
                            Toast.makeText(LoginActivity.this, "Contraseña actualizada correctamente", Toast.LENGTH_LONG).show();
                        } else if (respuestaLimpia.equals("no_existe")) {
                            Toast.makeText(LoginActivity.this, "Error: El RUT no está registrado", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error al actualizar la contraseña", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error conectando al servidor", Toast.LENGTH_LONG).show();
                    }
                });

        colaPeticiones.add(peticion);
    }

    // Método para cifrar en SHA-256
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
