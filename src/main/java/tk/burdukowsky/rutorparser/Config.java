package tk.burdukowsky.rutorparser;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("config")
public class Config {
    private String rutorDomain;
    private String protocol;
    private int timeout;
    private boolean proxyEnabled;

    public String getRutorDomain() {
        return rutorDomain;
    }

    public void setRutorDomain(String rutorDomain) {
        this.rutorDomain = rutorDomain;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }
}
