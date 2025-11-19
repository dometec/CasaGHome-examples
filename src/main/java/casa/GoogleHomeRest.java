package casa;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.common.annotation.RunOnVirtualThread;
import it.osys.casa.ghome.UserDevices;
import it.osys.casa.ghome.dto.RequestDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Handle All Google Home Requests
 */
@Path("smarthome")
public class GoogleHomeRest {

	public static final Logger logger = LoggerFactory.getLogger(GoogleHomeRest.class);

	@Inject
	DevicesStore homesDevicesTreeStore;

	@Inject
	Main main;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RunOnVirtualThread
	public ObjectNode homeRequest(RequestDto req) {
		Optional<UserDevices> casa = homesDevicesTreeStore.getCasaFor(Main.TEST_AGENTUSERID);
		return main.casaGHome.handleRequest(req, Main.TEST_AGENTUSERID, casa);
	}

}
