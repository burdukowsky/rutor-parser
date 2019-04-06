package tk.burdukowsky.rutorparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController {

    private final Config config;

    public MainController(Config config) {
        this.config = config;
    }

    @GetMapping("/{query}")
    public List<Result> getResult(@PathVariable String query) {
        String requestUrl = String.format(
                "%s://%s/search/%s",
                config.getProtocol(),
                config.getRutorDomain(),
                UriUtils.encode(query, "UTF-8")
        );

        Document doc;
        try {
            doc = Jsoup
                    .connect(requestUrl)
                    .timeout(config.getTimeout())
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadGatewayException(e);
        }

        if (doc == null) {
            throw new ParseException();
        }

        Element body = doc.body();

        if (body == null) {
            throw new ParseException();
        }

        Element index = body.getElementById("index");

        if (index == null) {
            throw new ParseException();
        }

        Elements trs = index.select("tr:not(.backgr)");

        if (trs == null) {
            throw new ParseException();
        }

        var results = new ArrayList<Result>();

        trs.forEach(tr -> {
            Elements tds = tr.select("td");
            if (tds == null) {
                throw new ParseException();
            }
            var tdsSize = tds.size();
            if (tdsSize != 4 && tdsSize != 5) {
                throw new ParseException();
            }

            Elements secondTdAs = tds.get(1).select("a");
            if (secondTdAs == null || secondTdAs.size() < 3) {
                throw new ParseException();
            }

            var title = secondTdAs.get(2).text();
            var magnet = secondTdAs.get(1).attr("href");

            var size = tds.get(tdsSize - 2).text();

            Element lastTd = tds.last();
            Elements lastTdSpans = lastTd.select("span");
            if (lastTdSpans == null || lastTdSpans.size() < 2) {
                throw new ParseException();
            }

            long seeds, leaches;
            try {
                seeds = Utils.stringToLong(lastTdSpans.first().text());
                leaches = Utils.stringToLong(lastTdSpans.get(1).text());
            } catch (NumberFormatException e) {
                throw new ParseException();
            }

            results.add(new Result(title, magnet, size, seeds, leaches));
        });

        return results;
    }
}
