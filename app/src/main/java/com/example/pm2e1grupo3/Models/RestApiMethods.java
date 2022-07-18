package com.example.pm2e1grupo3.Models;

public class RestApiMethods {
    private static final String ipaddress = "192.168.20.21/";
    public static final String StringHttp = "http://";


    //EndPoint Urls
    private static final String CreateUsuario = "Examen/CrearUsuario.php";
    private static final String GETUsuario = "Examen/ListaUsuario.php";
    private static final String UpdateUsuarios = "Examen/UpdateEmpleado.php";
    private static final String DeleteUsuarios = "Examen/DeleteEmpleado.php";




    public static final String EndPointCreateUsuario = StringHttp + ipaddress + CreateUsuario;
    public static final String EndPointGetUsuario = StringHttp + ipaddress + GETUsuario;
    public static final String EndPointUpdateUsuario = StringHttp + ipaddress + UpdateUsuarios;
    public static final String EndPointDeleteUsuario = StringHttp + ipaddress + DeleteUsuarios;
}
