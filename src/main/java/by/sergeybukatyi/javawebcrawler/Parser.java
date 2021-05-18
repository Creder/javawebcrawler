package by.sergeybukatyi.javawebcrawler;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
  public static ArrayList<String> getHrefs(Document page){
    ArrayList<String> hrefs = new ArrayList<>();
    Elements links = page.select("a[href]");
    for (Element element: links){
      String link = element.absUrl("href");
      if(!link.contains("#") && !link.contains("web.archive.org")){
        hrefs.add(link);
      }
    }
    return hrefs;
  }

  public static int wordCounter(Document page, String word){
    int counter = 0;
    if(page.body() != null){
      Elements elements = page.body().getElementsContainingOwnText(word);
      for (Element element : elements) {
        Matcher matcher = Pattern.compile(word, Pattern.CASE_INSENSITIVE).matcher(element.text());
        while(matcher.find()) {
          counter++;
        }
      }
    }
    return counter;
  }
}
