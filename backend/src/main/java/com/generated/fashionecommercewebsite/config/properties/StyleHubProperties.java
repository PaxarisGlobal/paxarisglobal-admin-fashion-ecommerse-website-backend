package com.generated.fashionecommercewebsite.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "stylehub")
public class StyleHubProperties {

    private final PaxarisIdentity paxaris = new PaxarisIdentity();

    public PaxarisIdentity getPaxaris() {
        return paxaris;
    }

    public static class PaxarisIdentity {
        /** e.g. http://host.docker.internal:8085 or http://127.0.0.1:8085 */
        private String gatewayUrl;
        private String realm = "paxarisglobal";
        /** Keycloak client_id for the StyleHub product */
        private String productId = "yatrify";
        /** Role assigned on signup (product-integration API) */
        private String defaultUserRole = "user";
        private boolean enabled = true;

        public String getGatewayUrl() {
            return gatewayUrl;
        }

        public void setGatewayUrl(String gatewayUrl) {
            this.gatewayUrl = gatewayUrl;
        }

        public String getRealm() {
            return realm;
        }

        public void setRealm(String realm) {
            this.realm = realm;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getDefaultUserRole() {
            return defaultUserRole;
        }

        public void setDefaultUserRole(String defaultUserRole) {
            this.defaultUserRole = defaultUserRole;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
