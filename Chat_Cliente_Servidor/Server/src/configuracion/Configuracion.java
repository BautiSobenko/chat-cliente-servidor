package configuracion;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.io.*;

public abstract class Configuracion implements Configurar{

    private String ip;
    private int puerto;

    public boolean puertoValido(int puerto){
        return (puerto>0 && puerto<65535);
    }

    public boolean ipValida (String ip){
        String[] groups = ip.split("\\.");
        if (groups.length != 4) {
            return false;
        }
        try {
            return Arrays.stream(groups)
                    .filter(s -> s.length() >= 0) // && s.startsWith("0"))
                    .map(Integer::parseInt)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() == 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean validarConfiguracion(Object... args) throws UnknownHostException{
        if (this.ip.equals("localhost")){
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        }
        return ipValida(ip) && puertoValido(this.puerto);
    }


    public String getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    @Override
    public String[] getParametros() {
        String[] param = new String[2];
        param[0] = this.getIp();
        param[1] = String.valueOf(this.getPuerto());
        return param;
    }

    public void leerArchivo(String path){
        this.setIp("localhost");
        try {
            File file = new File(path);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
                escribirArchivoConfiguracion(this.getIp(), String.valueOf(this.getPuerto()));
            }
            else {
                FileReader fw = new FileReader(file);
                BufferedReader bw = new BufferedReader(fw);
                this.setIp( bw.readLine());
                this.setPuerto(Integer.parseInt(bw.readLine()));
                bw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void escribirArchivo(String path) throws Exception{
        try {
            File file = new File(path);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(this.getIp());
            bw.newLine();
            bw.write(String.valueOf(this.getPuerto()));
            bw.close();
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

}