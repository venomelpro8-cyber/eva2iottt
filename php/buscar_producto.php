<?php
include("cn.php");

$rut = $_GET['rut'];
$contrasena = $_GET['password'];

// Usamos $c en lugar del $conexion que causaba error
$consulta = "SELECT * FROM usuarios WHERE rut = '$rut' AND contrasena = '$contrasena'";
$resultado = mysqli_query($c, $consulta);

// Si encuentra una fila que coincida con ambos datos, el login es correcto
if($fila = mysqli_fetch_array($resultado)){
    echo "ingreso_exitoso";
} else {
    echo "error_credenciales";
}
?>