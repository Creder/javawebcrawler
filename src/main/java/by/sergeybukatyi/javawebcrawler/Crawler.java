package by.sergeybukatyi.javawebcrawler;

import com.trigonic.jrobotx.RobotExclusion;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

public class Crawler {

  private static final Logger logger = LoggerFactory.getLogger("Log");
  private final List<PageData> pageDataList = new ArrayList<>();
  private final List<String> links = new LinkedList<>();
  private final Set<String> visitedPages = new TreeSet<>();
  private final HashMap<String, Integer> totalResult;
  private final RobotExclusion robotExclusion;
  private final int maxPages;
  private final int deep;
  private final String startPage;
  private final String[] words;
  private boolean exit = false;
  private int counterPages = 0;

  public Crawler(int maxPages, int deep, String[] words, String startPage) {
    this.words = words;
    this.startPage = startPage;
    this.deep = deep;
    this.maxPages = maxPages;
    totalResult = new HashMap<>();
    robotExclusion = new RobotExclusion();

    for (String word : words) {
      totalResult.put(word, 0);
    }
  }

  private static ArrayList<String> getHrefs(Document page) {
    return Parser.getHrefs(page);
  }

  private PageData firstStep() {
    PageData pageData = new PageData();
    try {
      Document page = getPage(startPage);
      links.addAll(getHrefs(page));
      if (!links.isEmpty()) {
        counterPages++;
        HashMap<String, Integer> map = counterWords(page);
        pageData.setUrl(startPage);
        pageData.setResults(map);
      }
    } catch (NullPointerException e) {
      pageData.setUrl(startPage);
    }
    return pageData;
  }

  public boolean start() {
    int currentDeep = 0;

    ArrayList<AnotherThread> threads = new ArrayList<>();

    PageData pageData = firstStep();
    pageDataList.add(pageData);
    currentDeep++;
    if (!links.isEmpty()) {
      createThreads(threads);
      try {
        while (counterPages <= maxPages && currentDeep <= deep && !exit) {
          Thread.sleep(5);
          for (int i = 0; i < threads.size(); i++) {
            if (!threads.get(i).isAlive() || threads.get(i).isInterrupted()) {
              threads.remove(i);
            }
          }
          if (threads.isEmpty()) {
            currentDeep++;
            logger.info(String.valueOf(currentDeep));
            threads = new ArrayList<>();
            createThreads(threads);
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      for (PageData data : pageDataList) {
        HashMap<String, Integer> map = data.getResults();
        for (Map.Entry<String, Integer> mapElement : map.entrySet()) {
          totalResult.put(mapElement.getKey(),
              totalResult.get(mapElement.getKey()) + data.getResults().get(mapElement.getKey()));
        }
      }
      pageData = new PageData();
      pageData.setUrl("_TOTAL_");
      pageData.setResults(totalResult);
      pageDataList.add(pageData);

      createCsvAllData(pageDataList);
      createCsvTop10(pageDataList);
      return true;
    } else {
      return false;
    }
  }

  private void createCsvTop10(List<PageData> pageDataList) {
    File top = new File("top-10.csv");
    try (FileWriter fileWriter = new FileWriter(top);) {
      Collections.sort(pageDataList, Collections.reverseOrder(new DataComparator()));
      StringBuilder sb = new StringBuilder();
      if (!top.exists()) {
        top.createNewFile();
      }
      sb.append("url,");
      for (Map.Entry<String, Integer> mapElement : pageDataList.get(0).getResults().entrySet()) {
        sb.append(mapElement.getKey() + ",");
      }
      sb.replace(sb.length() - 1, sb.length(), "\n");
      for (int i = 1; i < 11; i++) {
        sb.append(pageDataList.get(i).toString());
      }
      fileWriter.write(sb.toString());
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void createCsvAllData(List<PageData> pageDataList) {
    File csvFile = new File("all_results.csv");
    try (FileWriter fileWriter = new FileWriter(csvFile)) {
      if (!csvFile.exists()) {
        csvFile.createNewFile();
      }
      StringBuilder sb = new StringBuilder();
      sb.append("url,");
      for (Map.Entry<String, Integer> mapElement : pageDataList.get(0).getResults().entrySet()) {
        sb.append(mapElement.getKey() + ",");
      }
      sb.replace(sb.length() - 1, sb.length(), "\n");
      for (PageData data : pageDataList) {
        sb.append(data.toString());
      }
      fileWriter.write(sb.toString());
      fileWriter.flush();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void createThreads(ArrayList<AnotherThread> threads) {
    int chunks = links.size() / 10;
    while (!links.isEmpty()) {
      ArrayList<String> urls = new ArrayList<>(links.subList(0, Math.min(links.size(), chunks)));
      threads.add(new AnotherThread(this, urls));
      links.subList(0, Math.min(links.size(), chunks)).clear();
    }
    for (AnotherThread thread : threads) {
      thread.start();
    }
  }

  @Async
  public void workWithPages(ArrayList<String> urls) {
    PageData pageData;
    Document page;
    while (urls.size() != 0 && counterPages <= maxPages && !exit) {
      String url = urls.remove(0);
      try {
        Thread.sleep(25);

        page = getPage(url);
        pageData = new PageData();

        logger.info("Page: " + page.location());
        if (!exit) {
          links.addAll(getHrefs(page));

          pageData.setUrl(page.location());
          pageData.setResults(counterWords(page));

          addPageDataToList(pageData);
          increaseCounter();
        }
      } catch (NullPointerException e) {
        logger.info("Trouble with page");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
    logger.info("Visited pages: " + counterPages);
  }

  synchronized private void increaseCounter() {
    counterPages++;
    if (counterPages >= maxPages) {
      exit = true;
    }
  }

  synchronized private void addPageDataToList(PageData pageData) {
    pageDataList.add(pageData);
  }

  private Document getPage(String pageUrl) throws NullPointerException {
    Document document;
    try {
      URL url = new URL(pageUrl);
      if (robotExclusion.allows(url,
          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1")) {
        if (!visitedPages.contains(pageUrl)) {
          Connection connection = Jsoup.connect(pageUrl);
          document = connection.get();
          visitedPages.add(pageUrl);
          if (connection.response().contentType().contains("text/html")) {
            return document;
          }
        } else {
          throw new NullPointerException();
        }
      }
    } catch (IOException | IllegalArgumentException e) {
      logger.error(e.getMessage());
    }
    throw new NullPointerException();
  }

  private HashMap<String, Integer> counterWords(Document page) {
    HashMap<String, Integer> map = new HashMap<>();
    for (String word : this.words) {
      map.put(word, 0);
    }
    for (Map.Entry<String, Integer> mapElement : map.entrySet()) {
      mapElement.setValue(Parser.wordCounter(page, mapElement.getKey()));
    }
    return map;
  }

}
