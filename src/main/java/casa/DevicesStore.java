package casa;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import it.osys.casa.ghome.UserDevices;
import jakarta.inject.Singleton;

/**
 * Store all homes/devices in JVM memory 
 */
@Singleton
public class DevicesStore {

	// AgentUserId -> Casa
	private Map<String, UserDevices> casas = new HashMap<>();

	public void putCasa(String agentUserid, UserDevices casa) {
		assert casa.getAgentUserId().equals(agentUserid);
		casas.put(agentUserid, casa);
	}

	public Optional<UserDevices> getCasaFor(String agentUserId) {
		return Optional.ofNullable(casas.get(agentUserId));
	}

}
