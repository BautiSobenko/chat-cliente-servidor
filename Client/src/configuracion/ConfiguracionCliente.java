package configuracion;

public class ConfiguracionCliente extends Configuracion{
    //singleton
    private static ConfiguracionCliente config = null;
    private static final String path = "chat.config";

    private ConfiguracionCliente(String IP, int puerto) {
        super.setIp(IP);
        super.setPuerto(puerto);
        leerArchivoConfiguracion();
    }

    private ConfiguracionCliente() {
        leerArchivoConfiguracion();
    }

    public static ConfiguracionCliente getConfig(){
        if (config==null)
            config = new ConfiguracionCliente();
        return config;
    }

    public static ConfiguracionCliente getConfig(String IP, int puerto){
        if (config == null)
            config = new ConfiguracionCliente(IP, puerto);
        return config;
    }

    @Override
    public void escribirArchivoConfiguracion(Object... args) throws Exception{
        super.escribirArchivo(path);
    }
    @Override
    public void leerArchivoConfiguracion(Object... args) {
        this.setPuerto(1500);
        super.leerArchivo(path);
    }



}
