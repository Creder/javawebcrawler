package by.sergeybukatyi.javawebcrawler;

import java.util.ArrayList;

public class AnotherThread extends Thread{
  private final Crawler crawler;
  private final ArrayList<String> urls;

  public AnotherThread(Crawler crawler, ArrayList<String> urls){
    this.crawler = crawler;
    this.urls = urls;
  }
  @Override
  public void run(){
    crawler.workWithPages(urls);
  }



}
