module it.osys.casa.ghome.example {
	requires it.osys.casa.ghome;
	requires jakarta.cdi;
	requires jakarta.ws.rs;
	requires jakarta.transaction;
	requires io.smallrye.common.annotation;
	requires io.smallrye.common.constraint;
	requires microprofile.config.api;
	requires quarkus.core;
	requires com.google.auth;
	requires com.google.auth.oauth2;
	requires com.google.api.client.json.gson;
}