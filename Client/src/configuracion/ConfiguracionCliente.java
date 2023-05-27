package configuracion;

public class ConfiguracionCliente extends Configuracion{
    //singleton
    private static ConfiguracionCliente config = null;
    private static final String path = "chat.config";

    private ConfiguracionCliente(String IP, int puerto,String nickname) {
        super.setIp(IP);
        super.setPuerto(puerto);
        super.setNickname(nickname);
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

    public static ConfiguracionCliente getConfig(String IP, int puerto,String nickname){
        if (config == null)
            config = new ConfiguracionCliente(IP, puerto,nickname);
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
