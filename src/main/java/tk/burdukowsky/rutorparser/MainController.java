package tk.burdukowsky.rutorparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private final Config config;
    private final Optional<ProxyProvider> proxyProvider;

    public MainController(Config config, Optional<ProxyProvider> proxyProvider) {
        this.config = config;
        this.proxyProvider = proxyProvider;
    }

    @GetMapping("/{query}")
    public List<Result> getResult(@PathVariable String query) {
        String requestUrl = String.format(
                "%s://%s/search/%s",
                config.getProtocol(),
                config.getRutorDomain(),
                UriUtils.encode(query, "UTF-8")
        );

        var proxy = proxyProvider.map(ProxyProvider::getProxy).orElse(null);

        Document doc;
        try {
            doc = Jsoup
                    .connect(requestUrl)
                    .proxy(proxy)
                    .timeout(config.getTimeout())
                    .get();
        } catch (IOException e) {
            String message;
            if (proxy == null) {
                message = String.format("Ошибка запроса к %s без помощи прокси", requestUrl);
            } else {
                message = String.format("Ошибка запроса к %s с помощью прокси %s", requestUrl, proxy);
            }
            log.error(message, e);
            throw new BadGatewayException(message);
        }

        if (doc == null) {
            throw new ParseException("document not found");
        }

        Element body = doc.body();

        if (body == null) {
            throw new ParseException("body not found");
        }

        Element index = body.getElementById("index");

        if (index == null) {
            throw new ParseException("element with id=\"index\" not found");
        }

        Elements trs = index.select("tr:not(.backgr)");

        if (trs == null) {
            throw new ParseException("table rows not found");
        }

        var results = new ArrayList<Result>();

        trs.forEach(tr -> {
            Elements tds = tr.select("td");
            if (tds == null) {
                throw new ParseException("row cells not found");
            }
            var tdsSize = tds.size();
            if (tdsSize != 4 && tdsSize != 5) {
                throw new ParseException("wrong row cells number");
            }

            Elements secondTdAs = tds.get(1).select("a");
            if (secondTdAs == null || secondTdAs.size() < 3) {
                throw new ParseException("wrong links at second row cell");
            }

            var title = secondTdAs.get(2).text();
            var magnet = secondTdAs.get(1).attr("href");

            var size = tds.get(tdsSize - 2).text();

            Element lastTd = tds.last();
            Elements lastTdSpans = lastTd.select("span");
            if (lastTdSpans == null || lastTdSpans.size() < 2) {
                throw new ParseException("wrong spans at last row cell");
            }

            long seeds, leaches;
            try {
                seeds = Utils.stringToLong(lastTdSpans.first().text());
                leaches = Utils.stringToLong(lastTdSpans.get(1).text());
            } catch (NumberFormatException e) {
                throw new ParseException("seeds & leaches parse error");
            }

            results.add(new Result(title, magnet, size, seeds, leaches));
        });

        return results;
    }
}
