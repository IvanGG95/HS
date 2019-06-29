package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import es.uvigo.esei.dai.hybridserver.XMLConfigurationLoader;


public class Launcher {
	public static void main(String[] args) {
		Properties properties = new Properties();
		Configuration confi;
		switch (args.length) {
		case 0:
			properties.setProperty("numClients", "50");
			properties.setProperty("port", "8888");
			properties.setProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
			properties.setProperty("db.user", "hsdb");
			properties.setProperty("db.password", "hsdbpass");
			HybridServer hs = new HybridServer(properties);
			hs.start();
		break;
		case 1:
			System.out.println("Hola");
			try {
				properties.load(new FileInputStream(args[0]));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
//			File x=null;
//			XMLConfigurationLoader Loader = new XMLConfigurationLoader();
//			try {
//				confi=Loader.load(x);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			HybridServer hsc = new HybridServer(confi);
//			hsc.start();
//			break;
		default:
			System.out.println("Demasiados argumentos");
			System.exit(1);
			break;
		}

		
		
	}
}
