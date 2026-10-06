<?php
include("cn.php");

$rut = $_GET['rut'];
$password = $_GET['password'];

// Buscamos al usuario que coincida con el RUT y la contraseña
$consulta = "SELECT nombre FROM usuarios WHERE rut = '$rut' AND contrasena = '$password'";
$resultado = mysqli_query($c, $consulta);

if(mysqli_num_rows($resultado) > 0) {
    // Si existe, sacamos su nombre de la base de datos
    $fila = mysqli_fetch_assoc($resultado);
    $nombre = $fila['nombre'];
    
    // Enviamos el mensaje de éxito unido al nombre por una coma (Ej: ingreso_exitoso,Benjamin)
    echo "ingreso_exitoso," . $nombre;
} else {
    echo "error";
}
?>