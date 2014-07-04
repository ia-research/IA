package bw4t3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAControllerInterface extends Remote {
	public void receiveMessage(String s) throws RemoteException;
}
