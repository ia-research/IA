package Elvis;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Test {
	public static void main(String[] args) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(8001);
		IAServerInterface server;
		System.out.println(server = (IAServerInterface) registry.lookup("IAServer"));
		System.out.println(server.getCurrent());
	}
}
