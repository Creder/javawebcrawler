package by.sergeybukatyi.javawebcrawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlerRestController {

  @PostMapping(value = "/startCrawler", produces = {MediaType.APPLICATION_JSON_VALUE})
  @CrossOrigin(origins = "http://localhost:8080")
  public String startMapping(@RequestParam("maxPages") String maxPages,
      @RequestParam("deep") String deep,
      @RequestParam("words") String words, @RequestParam("startPage") String startPage) {
    String[] wordsArray = words.split(", ");
    Crawler crawler = new Crawler(Integer.parseInt(maxPages), Integer.parseInt(deep), wordsArray,
        startPage);
    if(crawler.start()){
      return "Done";
    }
    return "Trouble with seed";
  }
}
