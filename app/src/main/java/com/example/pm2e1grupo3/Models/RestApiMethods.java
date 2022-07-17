package com.example.pm2e1grupo3.Models;

public class RestApiMethods {
    private static final String ipaddress = "transportweb2.online";
    public static final String StringHttp = "http://";
    //EndPoint Urls
    private static final String GetEmple = "/APIexam/listacontactos.php";
    private static final String GetBuscar = "/APIexam/listasinglecontacto.php?nombre=";
    private static final String setUpdate = "/APIexam/actualizarcontacto.php";
    private static final String CreateUsuario = "/APIexam/crearcontacto.php";

    //metodo get
    public static final String EndPointGetContact = StringHttp + ipaddress + GetEmple;
    public static final String EndPointGetBuscarContact = StringHttp + ipaddress + GetBuscar;
    public static final String EndPointSetUpdateContact = StringHttp + ipaddress + setUpdate;
    public static final String EndPointCreateUsuario = StringHttp + ipaddress + CreateUsuario;
}
