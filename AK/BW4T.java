/*
 * To change this template, choose Tools | Templates
 * 
 */
package AK;

import eis.eis2java.util.EIS2JavaUtil;
import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import nl.tudelft.bw4t.BW4TEnvironmentListener;
import nl.tudelft.bw4t.client.BW4TRemoteEnvironment;
import java.lang.reflect.Method;
import java.util.Map.Entry;
/**
 *
 * @author ak
 */
public class BW4T {

    public static String findArgument(String[] args, InitParam param) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equalsIgnoreCase("-" + param.nameLower())) {
                return args[(i + 1)];
            }
        }
        return param.getDefaultValue();
    }

    public static void main(String[] args) throws Exception {
        Map<String, Parameter> initParameters = new HashMap<String, Parameter>();
        try {
            for (InitParam param : InitParam.values()) {
                initParameters.put(param.nameLower(), new Identifier(
                        findArgument(args, param)));
            }
            BW4TRemoteEnvironment env = new BW4TRemoteEnvironment();
            env.attachEnvironmentListener(new BW4TEnvironmentListener(env));
            env.init(initParameters);
            env.getAllPercepts("Agent0", "Bot1");
            env.getEntities();
            LinkedList<String> s = env.getAgents();
            for (String st : s) {
                System.out.println(st);
                HashSet<String> hs = env.getAssociatedEntities(st);
                for (String x : hs) {
                    System.out.println(x);
                    Map<String,Method> sm = EIS2JavaUtil.processActionAnnotations(x.getClass());
                    for(String q:sm.keySet()){
                        System.out.println(q);
                    }
                    //Action a = new Action("Go to ");
                    //env.performAction(st, a, x);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
