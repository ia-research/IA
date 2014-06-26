package Elvis;

public enum InitParam {
	CLIENTIP("localhost"),
	CLIENTPORT("2000"),
	SERVERIP("localhost"),
	SERVERPORT("8000"),
	AGENTCOUNT("0"),
	LAUNCHGUI("true"), // agent set false
	HUMANCOUNT("2"), // agent set 0
	AGENTCLASS("nl.tudelft.bw4t.agent.BW4TAgent"),
	GOAL("false");
	
	private final String defaultvalue;
	
	private InitParam(String def) {
		this.defaultvalue = def;
	}
	
	public String getDefaultValue() {
		return this.defaultvalue;
	}
	
	public String nameLower() {
		return name().toLowerCase();
	}
}
