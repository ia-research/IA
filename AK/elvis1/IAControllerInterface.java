package elvis2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAControllerInterface extends Remote {
        public String getBot()throws RemoteException;
	public void receiveMessage(String s,String sender) throws RemoteException;
        public String colorInRoom(String color) throws RemoteException;
}
