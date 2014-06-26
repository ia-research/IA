package Elvis;

import java.util.HashMap;
import java.util.Map;

import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tudelft.bw4t.BW4TEnvironmentListener;
import nl.tudelft.bw4t.client.BW4TRemoteEnvironment;

public class BW4T {
	private static String findArgument(String[] args, InitParam param) {
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equalsIgnoreCase("-" + param.nameLower())) {
				return args[(i + 1)];
			}
		}
		return param.getDefaultValue();
	}

	public static void main(String[] args) throws ManagementException,
			NoEnvironmentException {
		Map<String, Parameter> initParameters = new HashMap<String, Parameter>();
		for (InitParam param : InitParam.values()) {
			initParameters.put(param.nameLower(),
					new Identifier(findArgument(args, param)));
		}
		BW4TRemoteEnvironment env = new BW4TRemoteEnvironment();
		env.attachEnvironmentListener(new BW4TEnvironmentListener(env));
		env.init(initParameters);
	}
}
