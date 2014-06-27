package Elvis;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import nl.tudelft.bw4t.BW4TEnvironmentListener;
import nl.tudelft.bw4t.client.BW4TRemoteEnvironment;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.iilang.Identifier;
import eis.iilang.Parameter;

public class IAServerImpl extends UnicastRemoteObject implements IAServerInterface {
	// PINK
	// WHITE
	// YELLOW
	// RED
	// BLUE
	// GREEN
	// ORANGE
	//private String[] colors = new String[] {"ORANGE", "RED", "YELLOW", "BLUE", "WHITE", "YELLOW"};
	private String[] colors = new String[] {"ORANGE", "RED", "YELLOW", "BLUE", "WHITE",
			"YELLOW", "GREEN", "PINK", "WHITE", "RED",
			"BLUE", "YELLOW", "YELLOW", "WHITE", "YELLOW",
			"ORANGE", "ORANGE", "PINK", "WHITE", "GREEN"};
	private int current = 0;
	private Map<String, IAControllerInterface> bots = new HashMap<String, IAControllerInterface>();
	long t = 0;
	PrintWriter pw;
	
	public IAServerImpl() throws RemoteException {
		try {
			pw = new PrintWriter("IAServer_log.txt");
		} catch (Exception ex) {}
	}
	
	@Override
	synchronized public String[] getColors() throws RemoteException {
		return colors;
	}

	@Override
	synchronized public void setColors(String[] colors) throws RemoteException {
		this.colors = colors;
	}

	@Override
	synchronized public int getCurrent() throws RemoteException {
		System.out.println("getCurrent() : " + current);
		return current;
	}

	@Override
	synchronized public void setCurrent(int current) throws RemoteException {
		this.current = current;
	}

	@Override
	synchronized public void putBox(String color) throws RemoteException {
		System.out.println(color + "(current: " + current + ")");
		if (current < colors.length && color.equals(colors[current]))
			current++;
		if (current == colors.length) {
			try {
				pw.println((System.currentTimeMillis() - t) / 1000.0 + "sec(s)");
				pw.close();
			} catch (Exception ex) {}
			//System.out.println((System.currentTimeMillis() - t) / 1000.0 + "sec(s)");
		}
	}

	@Override
	synchronized public String getCurrentColor() throws RemoteException {
		if (t == 0) {
			t = System.currentTimeMillis();
			System.out.println("Stopwatch starts");
		}
		
		System.out.println("getCurrentColor()" + "(current: " + current + ")");
		if (current < colors.length)
			return colors[current];
		return null;
	}
	
	@Override
	synchronized public void registerBot(String botName, IAControllerInterface bot) throws RemoteException {
		bots.put(botName, bot);
	}

	@Override
	synchronized public void sendMessage(String s) throws RemoteException {
		for (IAControllerInterface bot: bots.values()) {
			bot.receiveMessage(s);
		}
	}
	
	@Override
	public void sendMessage(String botName, String s) throws RemoteException {
		for (String bot: bots.keySet()) {
			if (bot.equals(botName))
				bots.get(bot).receiveMessage(s);
		}
	}
	
	private static String findArgument(String[] args, InitParam param) {
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equalsIgnoreCase("-" + param.nameLower())) {
				return args[(i + 1)];
			}
		}
		return param.getDefaultValue();
	}
	
	public static void main(String[] args) throws RemoteException, ManagementException, NoEnvironmentException {
		Map<String, Parameter> initParameters = new HashMap<String, Parameter>();
		for (InitParam param : InitParam.values()) {
			initParameters.put(param.nameLower(),
					new Identifier(findArgument(args, param)));
		}
		BW4TRemoteEnvironment env = new BW4TRemoteEnvironment();
		env.attachEnvironmentListener(new BW4TEnvironmentListener(env));
		env.init(initParameters);		
		
		IAServerImpl server = new IAServerImpl();
		Registry registry;
		
		try {
			registry = LocateRegistry.createRegistry(8001);
		} catch (Exception ex) {
			registry = LocateRegistry.getRegistry(8001);
		}
		
		registry.rebind("IAServer", server);
		System.out.println("IAServer ready");
	}
}
