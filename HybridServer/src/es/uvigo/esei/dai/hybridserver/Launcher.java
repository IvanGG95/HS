package es.uvigo.esei.dai.hybridserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;


public class Launcher {
	public static void main(String[] args) {
		Properties properties = new Properties();
		switch (args.length) {
		case 0:
			properties.setProperty("numClients", "50");
			properties.setProperty("port", "8888");
			properties.setProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
			properties.setProperty("db.user", "hsdb");
			properties.setProperty("db.password", "hsdbpass");
		break;
		case 1:
			try {
				properties.load(new FileInputStream(args[0]));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			break;
		default:
			System.out.println("Demasiados argumentos");
			System.exit(1);
			break;
		}

		HybridServer hs = new HybridServer(properties);
		hs.start();
		
	}
}
