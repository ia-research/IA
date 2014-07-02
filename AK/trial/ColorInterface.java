/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IA.AK.trial;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ak
 */
interface ColorInterface extends Remote{
    public String[] getColors() throws RemoteException;
    public int getCurrent() throws RemoteException;
    public void putBox(String color) throws RemoteException;
}
