package com.generated.fashionecommercewebsite.auth;

import com.generated.fashionecommercewebsite.auth.dto.LoginRequest;
import com.generated.fashionecommercewebsite.auth.dto.SignupRequest;
import com.generated.fashionecommercewebsite.config.properties.StyleHubProperties;
import com.generated.fashionecommercewebsite.exception.AuthException;
import com.generated.fashionecommercewebsite.integration.paxaris.PaxarisIdentityClient;
import com.generated.fashionecommercewebsite.user.UserAccountService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final Pattern NON_USERNAME = Pattern.compile("[^a-zA-Z0-9._-]");

    private final PaxarisIdentityClient paxarisIdentityClient;
    private final UserAccountService userAccountService;
    private final StyleHubProperties properties;

    public AuthService(
            PaxarisIdentityClient paxarisIdentityClient,
            UserAccountService userAccountService,
            StyleHubProperties properties) {
        this.paxarisIdentityClient = paxarisIdentityClient;
        this.userAccountService = userAccountService;
        this.properties = properties;
    }

    public Map<String, Object> signup(SignupRequest request) {
        if (!properties.getPaxaris().isEnabled()) {
            return demoAuthResponse(request.getEmail(), request.getFirstName(), request.getLastName());
        }

        String username = deriveUsername(request.getEmail());
        String defaultRole = properties.getPaxaris().getDefaultUserRole();

        paxarisIdentityClient.createUser(
                username,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPassword());

        if (defaultRole != null && !defaultRole.isBlank()) {
            try {
                paxarisIdentityClient.assignRole(username, defaultRole);
            } catch (Exception e) {
                log.warn("Default role '{}' not assigned for '{}': {}", defaultRole, username, e.getMessage());
            }
        }

        Map<String, Object> auth = buildAuthResponse(
                username,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPassword());

        userAccountService.syncUser(
                username,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone());

        return auth;
    }

    public Map<String, Object> login(LoginRequest request) {
        if (!properties.getPaxaris().isEnabled()) {
            return demoAuthResponse(request.getEmail(), null, null);
        }

        String username = deriveUsername(request.getEmail());
        Map<String, Object> auth = buildAuthResponse(username, request.getEmail(), null, null, request.getPassword());
        userAccountService.syncUser(username, request.getEmail(), username, null, null);
        return auth;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> buildAuthResponse(
            String username,
            String email,
            String firstName,
            String lastName,
            String password) {
        Map<String, Object> loginBody = paxarisIdentityClient.login(username, password);
        List<String> roles = extractRoles(loginBody);
        if (roles.isEmpty()) {
            roles = List.of("user");
        }

        String resolvedEmail = email != null ? email : username;
        String token = extractToken(loginBody);
        String fullName = buildFullName(firstName, lastName, username);

        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", username);
        user.put("email", resolvedEmail);
        user.put("fullName", fullName);
        user.put("firstName", firstName != null ? firstName : username);
        user.put("lastName", lastName);
        user.put("roles", roles);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", token);
        response.put("expiresAt", Instant.now().plusSeconds(86400).toString());
        response.put("user", user);
        return response;
    }

    private Map<String, Object> demoAuthResponse(String email, String firstName, String lastName) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", UUID.randomUUID().toString());
        user.put("email", email);
        user.put("fullName", buildFullName(firstName, lastName, "StyleHub User"));
        user.put("roles", List.of("STYLEHUB_USER"));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", "demo-jwt-token");
        response.put("expiresAt", Instant.now().plusSeconds(86400).toString());
        response.put("user", user);
        return response;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Map<String, Object> loginBody) {
        Object rolesObj = loginBody.get("roles");
        if (!(rolesObj instanceof List<?> list)) {
            return List.of();
        }
        List<String> roles = new ArrayList<>();
        for (Object item : list) {
            if (item != null) {
                roles.add(item.toString());
            }
        }
        return roles;
    }

    private String extractToken(Map<String, Object> loginBody) {
        for (String key : List.of("access_token", "accessToken", "token")) {
            Object value = loginBody.get(key);
            if (value != null && !value.toString().isBlank()) {
                return value.toString();
            }
        }
        return UUID.randomUUID().toString();
    }

    private String buildFullName(String firstName, String lastName, String fallback) {
        String fullName = String.join(" ", List.of(
                        firstName != null ? firstName : "",
                        lastName != null ? lastName : "")
                .stream()
                .filter(part -> !part.isBlank())
                .toList())
                .trim();
        return fullName.isBlank() ? fallback : fullName;
    }

    public String deriveUsername(String email) {
        if (email == null || !email.contains("@")) {
            throw new AuthException("Valid email is required for username", "INVALID_EMAIL", HttpStatus.BAD_REQUEST);
        }
        String local = email.substring(0, email.indexOf('@')).toLowerCase(Locale.ROOT);
        String sanitized = NON_USERNAME.matcher(local).replaceAll(".");
        if (sanitized.isBlank()) {
            throw new AuthException("Could not derive username from email", "INVALID_EMAIL", HttpStatus.BAD_REQUEST);
        }
        return sanitized;
    }
}
