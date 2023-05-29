package configuracion;

public class ConfiguracionCliente extends Configuracion{
    //singleton
    private static ConfiguracionCliente config = null;
    private static final String path = "configcliente.xml";

    private ConfiguracionCliente(String IP, int puerto,String nickname) {
        super.setIp(IP);
        super.setPuerto(puerto);
        super.setNickname(nickname);
        leerArchivoConfiguracion();
    }

    public ConfiguracionCliente() {
        super.leerArchivoConfiguracion();
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
        super.escribirArchivoXML(path);
    }
    @Override
    public void leerArchivoConfiguracion(Object... args) {
        super.leerArchivoXML(path);
    }



}
