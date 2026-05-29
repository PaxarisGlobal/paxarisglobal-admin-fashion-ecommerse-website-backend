package com.generated.fashionecommercewebsite.integration.paxaris;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generated.fashionecommercewebsite.config.properties.StyleHubProperties;
import com.generated.fashionecommercewebsite.exception.AuthException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class PaxarisIdentityClient {

    private static final Logger log = LoggerFactory.getLogger(PaxarisIdentityClient.class);

    private final RestTemplate restTemplate;
    private final StyleHubProperties properties;
    private final ObjectMapper objectMapper;

    public PaxarisIdentityClient(
            RestTemplate restTemplate,
            StyleHubProperties properties,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public void createUser(String username, String email, String firstName, String lastName, String password) {
        StyleHubProperties.PaxarisIdentity cfg = properties.getPaxaris();
        String url = integrationUrl(cfg, "/users");

        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("email", email);
        body.put("firstName", firstName);
        body.put("lastName", lastName != null ? lastName : "");
        body.put("enabled", true);
        body.put("credentials", List.of(Map.of(
                "type", "password",
                "value", password,
                "temporary", false)));

        try {
            restTemplate.postForEntity(url, new HttpEntity<>(body, jsonHeaders()), Map.class);
            log.info("Paxaris: created user '{}' in realm '{}'", username, cfg.getRealm());
        } catch (HttpClientErrorException.Conflict e) {
            log.info("Paxaris: user '{}' already exists in realm '{}'", username, cfg.getRealm());
        } catch (ResourceAccessException e) {
            throw new AuthException(
                    "Cannot reach Paxaris gateway at " + cfg.getGatewayUrl()
                            + ". Ensure the identity gateway is running and PAXARIS_GATEWAY_URL is set.",
                    "PAXARIS_UNREACHABLE",
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (HttpStatusCodeException e) {
            throw paxarisError("create user", e);
        }
    }

    public void assignRole(String username, String roleName) {
        StyleHubProperties.PaxarisIdentity cfg = properties.getPaxaris();
        String url = integrationUrl(cfg, "/users/" + username + "/roles");
        List<Map<String, String>> body = List.of(Map.of("name", roleName));

        try {
            restTemplate.postForEntity(url, new HttpEntity<>(body, jsonHeaders()), Map.class);
            log.info("Paxaris: assigned role '{}' to user '{}' on product '{}'", roleName, username, cfg.getProductId());
        } catch (ResourceAccessException e) {
            throw new AuthException(
                    "Cannot reach Paxaris gateway at " + cfg.getGatewayUrl(),
                    "PAXARIS_UNREACHABLE",
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (HttpStatusCodeException e) {
            throw paxarisError("assign role", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> login(String username, String password) {
        StyleHubProperties.PaxarisIdentity cfg = properties.getPaxaris();
        String url = gatewayBase(cfg) + "/identity/" + cfg.getRealm() + "/login";

        Map<String, String> body = Map.of(
                "username", username,
                "password", password,
                "client_id", cfg.getProductId());

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, jsonHeaders()), Map.class);
            if (response.getBody() == null) {
                throw new AuthException("Empty login response from Paxaris", "PAXARIS_LOGIN_FAILED", HttpStatus.BAD_GATEWAY);
            }
            return response.getBody();
        } catch (ResourceAccessException e) {
            throw new AuthException(
                    "Cannot reach Paxaris gateway at " + cfg.getGatewayUrl(),
                    "PAXARIS_UNREACHABLE",
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            throw new AuthException("Invalid email or password", "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
        } catch (HttpStatusCodeException e) {
            throw paxarisError("login", e);
        }
    }

    private String integrationUrl(StyleHubProperties.PaxarisIdentity cfg, String suffix) {
        return gatewayBase(cfg)
                + "/identity/product-integration/"
                + cfg.getRealm()
                + "/products/"
                + cfg.getProductId()
                + suffix;
    }

    private String gatewayBase(StyleHubProperties.PaxarisIdentity cfg) {
        String base = cfg.getGatewayUrl();
        if (base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private AuthException paxarisError(String action, HttpStatusCodeException e) {
        String detail = e.getResponseBodyAsString();
        log.error("Paxaris {} failed: {} {}", action, e.getStatusCode(), detail);
        String message = extractPaxarisMessage(detail);
        if (message != null) {
            if (message.contains("Product not found")) {
                return new AuthException(
                        "Product '"
                                + properties.getPaxaris().getProductId()
                                + "' is not registered in Paxaris. Set PAXARIS_PRODUCT_ID (e.g. yatrify). "
                                + message,
                        "PAXARIS_PRODUCT_NOT_FOUND",
                        HttpStatus.BAD_REQUEST);
            }
            return new AuthException(message, "PAXARIS_ERROR", HttpStatus.valueOf(e.getStatusCode().value()));
        }
        return new AuthException("Paxaris " + action + " failed: " + detail, "PAXARIS_ERROR", HttpStatus.valueOf(e.getStatusCode().value()));
    }

    private String extractPaxarisMessage(String detail) {
        if (detail == null || detail.isBlank()) {
            return null;
        }
        try {
            Map<String, Object> parsed = objectMapper.readValue(detail, new TypeReference<>() {});
            Object message = parsed.get("message");
            return message != null ? message.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
