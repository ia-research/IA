package Elvis;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAControllerInterface extends Remote {
	public void receiveMessage(String s) throws RemoteException;
}
