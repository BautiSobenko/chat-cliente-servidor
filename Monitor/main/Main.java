package main;

import monitor.Monitor;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

        Monitor hbCheck = new Monitor();
        Thread hiloHeartbeatCheck = new Thread(hbCheck);
        hiloHeartbeatCheck.start();
	}

}
