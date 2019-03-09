package tk.burdukowsky.rutorparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.io.IOException;

@RestController
public class MainController {

    @GetMapping("/{query}")
    public Result[] getResult(@PathVariable String query) {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://rutor.info/search/" + UriUtils.encode(query, "UTF-8")).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc.body().getElementById("index").select("tr:not(.backgr)").stream().map(tr -> {
            Elements tds = tr.select("td");
            Elements as = tds.get(1).select("a");
            return new Result(as.get(2).text(), as.get(1).attr("href"));
        }).toArray(Result[]::new);
    }
}
