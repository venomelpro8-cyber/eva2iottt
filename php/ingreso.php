<?php
include("cn.php");

$rut = $_GET['rut'];
$contrasena = $_GET['password'];
$nombre = $_GET['nombre'];
$apellido = $_GET['apellido'];
$telefono = $_GET['telefono'];
$correo = $_GET['correo'];
$fecha = $_GET['fecha'];

// 1. Verificamos si el RUT ya está registrado en la base de datos
$consulta_previa = "SELECT * FROM usuarios WHERE rut = '$rut'";
$resultado_previo = mysqli_query($c, $consulta_previa);

// Si encuentra al menos 1 registro, enviamos la alerta de duplicado
if(mysqli_num_rows($resultado_previo) > 0) {
    echo "duplicado";
} else {
    // 2. Si no existe, procedemos a insertarlo
    $insercion = "INSERT INTO usuarios (rut, contrasena, nombre, apellido, telefono, correo, fecha) 
                  VALUES ('$rut', '$contrasena', '$nombre', '$apellido', '$telefono', '$correo', '$fecha')";
    
    $resultado_insercion = mysqli_query($c, $insercion);
    
    if($resultado_insercion){
        echo "exito";
    } else {
        echo "error";
    }
}
?>