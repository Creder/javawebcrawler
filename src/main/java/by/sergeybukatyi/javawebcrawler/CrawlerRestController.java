package by.sergeybukatyi.javawebcrawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
  public String startMapping(@RequestParam("maxPages") String maxPages, @RequestParam("deep") String deep,
      @RequestParam("words") String words, @RequestParam("startPage") String startPage){
    String[] wordsArray = words.split(", ");
    Crawler crawler = new Crawler(Integer.parseInt(maxPages), Integer.parseInt(deep), wordsArray,startPage);
    File csvFile = new File("temp/csvFile.csv");
    try(FileWriter fileWriter = new FileWriter(csvFile)){
      if(!csvFile.exists()) {
        csvFile.createNewFile();
      }
      StringBuilder sb = new StringBuilder();
      ArrayList<PageData> pageDataList = (ArrayList<PageData>) crawler.start();

      sb.append("url ");
      for (Map.Entry<String, Integer> mapElement: pageDataList.get(0).getResults().entrySet()){
        sb.append(mapElement.getKey()+" ");
      }
      sb.replace(sb.length()-1, sb.length(), "\n");
      for (PageData data : pageDataList){
        sb.append(data.toString());
      }
        fileWriter.write(sb.toString());
      } catch (IOException e) {
        e.printStackTrace();
      }

    return "Done";
  }


}
