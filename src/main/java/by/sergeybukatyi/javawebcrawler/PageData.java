package by.sergeybukatyi.javawebcrawler;

import java.util.HashMap;
import java.util.Map;

public class PageData {
  private String url;
  private HashMap<String, Integer> results;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public HashMap<String, Integer> getResults() {
    return results;
  }

  public void setResults(HashMap<String, Integer> results) {
    this.results = results;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(url+",");
    for (Map.Entry<String, Integer> mapElement : results.entrySet())
    {
      sb.append(mapElement.getValue()+",");
    }
    sb.replace(sb.length()-1, sb.length(), "\n");
    return sb.toString();
  }
}
