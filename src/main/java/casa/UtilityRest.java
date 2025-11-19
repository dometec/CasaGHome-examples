package casa;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpResponse;
import com.google.api.services.homegraph.v1.HomeGraphService;
import com.google.api.services.homegraph.v1.HomeGraphService.Devices.RequestSync;
import com.google.api.services.homegraph.v1.model.AgentDeviceId;
import com.google.api.services.homegraph.v1.model.QueryRequest;
import com.google.api.services.homegraph.v1.model.QueryRequestInput;
import com.google.api.services.homegraph.v1.model.QueryRequestPayload;
import com.google.api.services.homegraph.v1.model.QueryResponse;
import com.google.api.services.homegraph.v1.model.RequestSyncDevicesRequest;
import com.google.api.services.homegraph.v1.model.SyncRequest;
import com.google.api.services.homegraph.v1.model.SyncResponse;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.common.constraint.NotNull;
import it.osys.casa.ghome.enums.ErrorCodeEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/utility")
@ApplicationScoped
public class UtilityRest {

	public static final Logger logger = LoggerFactory.getLogger(UtilityRest.class);

	@Inject
	Main main;

	@Inject
	GoogleHomeRest googleHomeRest;

	@Inject
	HomeGraphService homegraphService;

	@Inject
	DevicesStore homesDevicesTreeStore;

	Optional<ErrorCodeEnum> generalError = Optional.empty();


	@GET
	@Path("requestSync")
	@Produces(MediaType.TEXT_PLAIN)
	@RunOnVirtualThread
	public String requestSync(@NotNull @QueryParam("agentUserId") String agentUserId,
			@DefaultValue("false") @QueryParam("async") boolean async) throws IOException {

		if (agentUserId == null || agentUserId.isBlank())
			return "You need to specify agentUserId query";

		logger.info("Request SYNC for {}.", agentUserId);

		RequestSyncDevicesRequest request = new RequestSyncDevicesRequest().setAgentUserId(agentUserId).setAsync(async);
		RequestSync requestSync = homegraphService.devices().requestSync(request);
		HttpResponse response = requestSync.executeUnparsed();

		logger.info("RequestSyncDevices status code: {}.", response.getStatusCode());

		return "Requested SYNC for " + agentUserId;

	}

	// Query Home Graph about devices state
	@GET
	@Path("query")
	@Produces(MediaType.TEXT_PLAIN)
	@RunOnVirtualThread
	public String queryAllDeviceForAgentUserId(@NotNull @QueryParam("agentUserId") String agentUserId) throws IOException {

		if (agentUserId == null || agentUserId.isBlank())
			return "You need to specify agentUserId query";

		List<AgentDeviceId> devices = homesDevicesTreeStore.getCasaFor(agentUserId).get().getDevices().stream()
				.map(d -> new AgentDeviceId().setId(d.getId())).toList();
		QueryRequestPayload payload = new QueryRequestPayload().setDevices(devices);
		QueryRequestInput queryInput = new QueryRequestInput().setPayload(payload);
		QueryRequest queryRequest = new QueryRequest().setRequestId(UUID.randomUUID().toString()).setAgentUserId(agentUserId)
				.setInputs(Collections.singletonList(queryInput));

		HomeGraphService.Devices.Query queryCall = homegraphService.devices().query(queryRequest);
		QueryResponse queryResponse = queryCall.execute();

		String out = queryResponse.toPrettyString();

		return "Requested QUERY for " + agentUserId + ", response: " + out;

	}

	// Query Home Graph about devices and attributes
	@GET
	@Path("sync")
	@Produces(MediaType.TEXT_PLAIN)
	@RunOnVirtualThread
	public String getGraphForUser(@NotNull @QueryParam("agentUserId") String agentUserId) throws IOException {

		if (agentUserId == null || agentUserId.isBlank())
			return "You need to specife agentUserId query";

		SyncRequest syncRequest = new SyncRequest().setRequestId(UUID.randomUUID().toString()).setAgentUserId(agentUserId);
		HomeGraphService.Devices.Sync queryCall = homegraphService.devices().sync(syncRequest);
		SyncResponse syncResponse = queryCall.execute();
		String out = syncResponse.toPrettyString();
		return "Graph for " + agentUserId + ", response: " + out;

	}

	// Set General Error
	@POST
	@Path("generalerror")
	public void setGeneralError(@FormParam("error") Optional<ErrorCodeEnum> generalError) {
		logger.info("Set General Error to {}.", generalError);
		main.casaGHome.setGeneralError(generalError);
	}

	@POST
	@Path("setLightOnOff")
	public void setLightOnOff(@FormParam("on") Boolean on) {
		logger.info("Set General Error to {}.", generalError);
		main.casaGHome.setGeneralError(generalError);
	}

}
