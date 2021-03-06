package Elvis;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAServerInterface extends Remote {
	public String[] getColors() throws RemoteException;
	public void setColors(String[] colors) throws RemoteException;
	public int getCurrent() throws RemoteException;
	public void setCurrent(int current) throws RemoteException;
	public void putBox(String color) throws RemoteException;
	public String getCurrentColor() throws RemoteException;
	public void registerBot(String botName, IAControllerInterface bot) throws RemoteException;
	public void sendMessage(String s) throws RemoteException;
	public void sendMessage(String botName, String s) throws RemoteException;
        public void askForColor(IAControllerInterface self, String color) throws RemoteException;
}
