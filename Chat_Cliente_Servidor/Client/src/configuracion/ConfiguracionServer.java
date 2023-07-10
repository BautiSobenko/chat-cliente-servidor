package configuracion;

public class ConfiguracionServer extends Configuracion{

    //singleton
    private static ConfiguracionServer config = null;
    private static final String path = "server.config";

    private ConfiguracionServer(String IP, int puerto) {
        super.setIp(IP);
        super.setPuerto(puerto);
        leerArchivoConfiguracion();
    }

    private ConfiguracionServer() {
        leerArchivoConfiguracion();
    }

    public static ConfiguracionServer getConfig(){
        if (config == null)
            config = new ConfiguracionServer();
        return config;
    }

    public static ConfiguracionServer getConfig(String IP, int puerto){
        if (config == null)
            config = new ConfiguracionServer(IP, puerto);
        return config;
    }

    @Override
    public void escribirArchivoConfiguracion(Object... args) throws Exception{
        super.escribirArchivo(path);
    }
    @Override
    public void leerArchivoConfiguracion(Object... args) {
        this.setPuerto(9090);
        super.leerArchivo(path);
    }
}