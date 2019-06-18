package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;




public class HybridServer {
	private int SERVICE_PORT;
	private Thread serverThread;
	private boolean stop;
	private ExecutorService threadPool;
	private List<String> array;
	public Properties properties;
	
	public HybridServer() {
		threadPool = Executors.newFixedThreadPool(50);
		SERVICE_PORT = 8888;
	}
	
//	public HybridServer(Map<String, String> map) {
//		pages = new HTMLMapDAO(map);
//		threadPool = Executors.newFixedThreadPool(50);
//		SERVICE_PORT = 8888;
//	}
	
	public HybridServer(Configuration conf) {
		array.add(conf.getDbURL());
		array.add(conf.getDbUser());
		array.add(conf.getDbPassword());
		threadPool = Executors.newFixedThreadPool(conf.getNumClients());
		SERVICE_PORT = conf.getHttpPort();
	}
	
	public HybridServer(Properties properties) {
		SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
		threadPool = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("numClients")));
		array = new ArrayList<>();
		array.add(0, properties.getProperty("db.url"));
		array.add(1, properties.getProperty("db.user"));
		array.add(2, properties.getProperty("db.password"));
		//pages = new HTMLDBDAO(array);
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					while (true) {
						Socket clientSocket = serverSocket.accept();
						if (stop)
							break;

						threadPool.execute(new ServiceThread(clientSocket, array));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
			// Esta conexiÃ³n se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		threadPool.shutdownNow();

		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.serverThread = null;
	}
}
