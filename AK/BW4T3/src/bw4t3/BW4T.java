package bw4t3;

import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import java.util.HashMap;
import java.util.Map;
import nl.tudelft.bw4t.client.environment.Launcher;
import nl.tudelft.bw4t.client.environment.RemoteEnvironment;

public class BW4T {

    /**
     * register client on server
     */
    public static Identifier findParameter(String[] args, InitParam param) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equalsIgnoreCase("-" + param.nameLower())) {
                //LOGGER.debug("Found parameter '" + param.nameLower() + "' with '" + args[(i + 1)] + "'");
                return new Identifier(args[(i + 1)]);
            }
        }
        //LOGGER.debug("Defaulting parameter '" + param.nameLower() + "' with '" + param.getDefaultValue() + "'");
        return null;
    }

    public static void main(String[] args) {
        // TODO code application logic here
        Map<String, Parameter> init = new HashMap();
        for (InitParam param : InitParam.values()) {
            if (param == InitParam.GOAL) {
                //LOGGER.info("Setting parameter 'GOAL' with 'false' because we started from commandline.");
                init.put(param.nameLower(), new Identifier("false"));
            } else {
                Parameter value = findParameter(args, param);
                if (value != null) {
                    init.put(param.nameLower(), value);
                }
            }
        }
        RemoteEnvironment env = Launcher.launch(args);
        for(String st:env.storedPercepts.keySet()){
            for(Percept p:env.storedPercepts.get(st)){
                System.out.println(p.toProlog());
            }
        }
    }
}
