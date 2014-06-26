/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AK;

import eis.iilang.Identifier;
import eis.iilang.Parameter;
import java.util.HashMap;
import java.util.Map;
import nl.tudelft.bw4t.BW4TEnvironmentListener;
import nl.tudelft.bw4t.client.BW4TRemoteEnvironment;
/**
 *
 * @author ak
 */
public class Color {
    private static String[] colors = new String[]{"WHITE","BLUE","WHITE","BLUE","PINK","RED"};// RED YELLOW WHITE BLUE PINK GREEN ORANGE
    private static int current=0;

    public static synchronized String[] getColors() {
        return colors;
    }

    public static synchronized int getCurrent(){
        return current;
    }
    
    public static synchronized void putBox(String color){
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
    
    public static void main(String[] args){
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
    }
}
