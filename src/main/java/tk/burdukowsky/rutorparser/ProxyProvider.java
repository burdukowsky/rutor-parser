package tk.burdukowsky.rutorparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.Proxy;
import java.util.List;

import static tk.burdukowsky.rutorparser.Utils.getProxies;

@Component
@ConditionalOnProperty(value = "config.proxy-enabled", matchIfMissing = true, havingValue = "true")
public class ProxyProvider {

    private static final Logger log = LoggerFactory.getLogger(ProxyProvider.class);
    private Proxy proxy = null;
    private final String requestUrl;

    public ProxyProvider(Config config) {
        this.requestUrl = String.format(
                "%s://%s/search/%s",
                config.getProtocol(),
                config.getRutorDomain(),
                "test"
        );
    }

    @Nullable
    public Proxy getProxy() {
        return proxy;
    }

    @Scheduled(fixedRate = 600000) // 10 min
    private void updateProxy() {
        List<Proxy> proxies;
        try {
            proxies = getProxies(requestUrl);
        } catch (Exception e) {
            log.error("Ошибка подбора прокси-сервера", e);
            return;
        }
        if (proxies.isEmpty()) {
            log.error("Не найден подходящий прокси-сервер");
            return;
        }
        this.proxy = proxies.get(0);
        log.info("Используется прокси: " + proxy.toString());
    }
}
