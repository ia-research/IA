package Elvis;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class IAServerImpl extends UnicastRemoteObject implements IAServerInterface {
	// PINK
	// WHITE
	// YELLOW
	// RED
	// BLUE
	// GREEN
	// ORANGE
	private String[] colors = new String[] {"GREEN", "BLUE", "RED", "WHITE", "RED", "GREEN"};
	private int current = 0;
	
	public IAServerImpl() throws RemoteException {}
	
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
	}

	@Override
	synchronized public String getCurrentColor() throws RemoteException {
		System.out.println("getCurrentColor()" + "(current: " + current + ")");
		if (current < colors.length)
			return colors[current];
		return null;
	}

	public static void main(String[] args) throws RemoteException {
		IAServerImpl server = new IAServerImpl();
		Registry registry;
		
		try {
			registry = LocateRegistry.createRegistry(8001);
		} catch (Exception ex) {
			registry = LocateRegistry.getRegistry(8001);
		}
		
		registry.rebind("IAServer", server);
	}
}
