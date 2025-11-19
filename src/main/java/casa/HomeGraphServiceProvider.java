package casa;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.homegraph.v1.HomeGraphService;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class HomeGraphServiceProvider {

	@ConfigProperty(name = "SA_FILE")
	String saCredentialFile;

	@Produces
	public HomeGraphService getHomeGraphService() throws IOException, GeneralSecurityException {

		GoogleCredentials credentials = GoogleCredentials.fromStream(this.getClass().getResourceAsStream("/" + saCredentialFile))
				.createScoped(List.of("https://www.googleapis.com/auth/homegraph"));

		HttpCredentialsAdapter credential = new HttpCredentialsAdapter(credentials);

		HomeGraphService.Builder builder = new HomeGraphService.Builder(GoogleNetHttpTransport.newTrustedTransport(),
				GsonFactory.getDefaultInstance(), credential);

		return builder.setApplicationName("CasaGHome/1.0").build();

	}

}
