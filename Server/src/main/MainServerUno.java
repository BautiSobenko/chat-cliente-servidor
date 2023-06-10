package main;

import servidor.Servidor;

public class MainServerUno {

    public static void main(String[] args) {

        Servidor.getServer(9999);
        
        //Aca se podria instanciar los dos servers
        //Le pasamos por parametro el puerto correspondiente a cada uno

    }

}
