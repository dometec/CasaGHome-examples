package casa;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.homegraph.v1.HomeGraphService;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import it.osys.casa.ghome.CasaGHome;
import it.osys.casa.ghome.UserDevices;
import it.osys.casa.ghome.device.Device;
import it.osys.casa.ghome.device.Light;
import it.osys.casa.ghome.dto.DeviceName;
import it.osys.casa.ghome.listener.CasaReportStateAndNotification;
import it.osys.casa.ghome.trait.onoff.OnOffTrait;
import it.osys.casa.ghome.trait.onoff.OnOffTraitWantListener;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class Main {

	public static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static final String TEST_AGENTUSERID = "82f453bd-86f4-45f3-a0fa-0daf4af3e655";
	public static final String TEST_DEVICEIDLAMP = "82f453l3";

	@Inject
	ScheduledExecutorService scheduledExecutorService;

	@Inject
	DevicesStore homesDevicesTreeStore;

	@Inject
	ObjectMapper objectMapper;

	@Inject
	HomeGraphService homegraphService;

	CasaGHome casaGHome;

	CasaReportStateAndNotification casaReport;

	void onStart(@Observes StartupEvent ev) throws IOException, GeneralSecurityException {

		logger.info("The application is starting...");

		casaGHome = new CasaGHome(objectMapper);
		casaReport = new CasaReportStateAndNotification(homegraphService, objectMapper, scheduledExecutorService, 150);

		// Set all command listener
		casaGHome.setOnOffTraitWantListener(new OnOffTraitWantListener() {
			@Override
			public CompletableFuture<Void> onWantOnOff(Device device, String requertId, boolean on) {

				// TODO Turn on or off the light on your platform

				// When you receive the ACK from you platform, update device
				// status and send the report state
				OnOffTrait onOffTrait = ((Light) device).getOnOffTrait();
				onOffTrait.setOn(on);

				casaReport.sendReportState(device, onOffTrait);

				CompletableFuture<Void> cf = new CompletableFuture<>();
				// Where we have an ACK from the field we can complete the
				// CompletableFuture
				cf.complete(null);
				return cf;
			}
		});

		// Create all device for my Users

		Light light = new Light(TEST_DEVICEIDLAMP, new DeviceName("Lampada")).withOnOffTrait(false, false);

		UserDevices casa = new UserDevices(TEST_AGENTUSERID);
		casa.addDevice(light);

		homesDevicesTreeStore.putCasa(TEST_AGENTUSERID, casa);

		scheduledExecutorService.scheduleAtFixedRate(() -> {
			OnOffTrait onOffTrait = light.getOnOffTrait();
			onOffTrait.setOn(!onOffTrait.isOn());
			casaReport.sendReportState(light, onOffTrait);
		}, 1, 1, TimeUnit.MINUTES);

	}

	void onStop(@Observes ShutdownEvent ev) {
		logger.info("The application is stopping...");
	}

}
