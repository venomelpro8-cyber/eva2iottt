<?php
include("cn.php");

$rut = $_GET['rut'];
$nueva_password = $_GET['password']; // Esta ya llegará cifrada en SHA-256 desde Android

// 1. Verificamos si el RUT realmente existe en la base de datos
$consulta_previa = "SELECT * FROM usuarios WHERE rut = '$rut'";
$resultado_previo = mysqli_query($c, $consulta_previa);

if(mysqli_num_rows($resultado_previo) > 0) {
    // 2. Si el usuario existe, actualizamos su contraseña
    $actualizar = "UPDATE usuarios SET contrasena = '$nueva_password' WHERE rut = '$rut'";
    $resultado_update = mysqli_query($c, $actualizar);
    
    if($resultado_update){
        echo "exito";
    } else {
        echo "error";
    }
} else {
    // Si no existe, enviamos un aviso
    echo "no_existe";
}
?>