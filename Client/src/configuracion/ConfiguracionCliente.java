package configuracion;

public class ConfiguracionCliente extends Configuracion{
    //singleton
    private static ConfiguracionCliente config = null;

    private ConfiguracionCliente(String IP, int puerto,String nickname) {
        super(IP,puerto,nickname);
    }

    public ConfiguracionCliente() {
        super();
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


}
