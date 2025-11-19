package casa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Handle Account linking request provides always a FAKE user.
 */
@Path("fakeauth")
public class FakeOAuthAServerRest {

	public static final Logger logger = LoggerFactory.getLogger(FakeOAuthAServerRest.class);

	@GET
	@Path("authorize")
	public Response authorize(@QueryParam("response_type") String responseType, @QueryParam("client_id") String clientId,
			@QueryParam("redirect_uri") String redirectUri, @QueryParam("state") String state) {
		logger.info("Authorize request: response_type={}, client_id={}, redirect_uri={}, state={}", responseType, clientId, redirectUri,
				state);

		if (redirectUri == null)
			return Response.status(Response.Status.BAD_REQUEST).entity("redirect_uri is required").build();

		String code = "fake_auth_code";
		String location = redirectUri + "?code=" + code + "&state=" + (state != null ? state : "");

		return Response.seeOther(URI.create(location)).build();
	}

	@POST
	@Path("token")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response token(@FormParam("grant_type") String grantType, @FormParam("code") String code,
			@FormParam("redirect_uri") String redirectUri, @FormParam("client_id") String clientId,
			@FormParam("client_secret") String clientSecret) {
		logger.info("Token request: grant_type={}, code={}, redirect_uri={}, client_id={}", grantType, code, redirectUri, clientId);

		String jsonResponse = """
				{
					"token_type": "Bearer",
					"access_token": "fake_access_token",
					"refresh_token": "fake_refresh_token",
					"expires_in": 3600
				}
				""";

		return Response.ok(jsonResponse).build();
	}
}
