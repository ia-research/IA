/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trial;

import eis.iilang.Identifier;
import eis.iilang.Parameter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import nl.tudelft.bw4t.BW4TEnvironmentListener;
import nl.tudelft.bw4t.client.BW4TRemoteEnvironment;
/**
 *
 * @author ak
 */
public class Color extends UnicastRemoteObject implements ColorInterface{
    private String[] colors = new String[]{"BLUE","RED","PINK","WHITE","PINK","RED"};// RED YELLOW WHITE BLUE PINK GREEN ORANGE
    private int current=0;
    
    public Color()throws RemoteException, MalformedURLException{
        Registry r = LocateRegistry.createRegistry(3000);
        r.rebind("interface",this);
    }
    
    public synchronized String[] getColors() throws RemoteException {
        return colors;
    }

    public synchronized int getCurrent() throws RemoteException{
        return current;
    }
    
    public synchronized void putBox(String color) throws RemoteException{
        if(current<6 && colors[current] == color){
            current++;
        }
    }
    
    public static String findArgument(String[] args, InitParam param) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equalsIgnoreCase("-" + param.nameLower())) {
                return args[(i + 1)];
            }
        }
        return param.getDefaultValue();
    }
    
    public static void main(String[] args) throws Exception{
        Map<String, Parameter> initParameters = new HashMap<String, Parameter>();
        try {
            for (InitParam param : InitParam.values()) {
                initParameters.put(param.nameLower(), new Identifier(
                        findArgument(args, param)));
            }
            BW4TRemoteEnvironment env = new BW4TRemoteEnvironment();
            env.attachEnvironmentListener(new BW4TEnvironmentListener(env));
            env.init(initParameters);
        }catch(Exception ex){};
        try{
            ColorInterface c = new Color();
        }catch(Exception ex){
            System.err.println("Fail to create Color");
            ex.printStackTrace();
        }
    }
}
