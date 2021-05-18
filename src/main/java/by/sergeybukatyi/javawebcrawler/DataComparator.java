package by.sergeybukatyi.javawebcrawler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DataComparator implements Comparator<PageData> {

  @Override
  public int compare(PageData o1, PageData o2) {
    int a1 = 0;
    int a2 = 0;
    HashMap<String, Integer> map1 = o1.getResults();
    HashMap<String, Integer> map2 = o2.getResults();
    for (Map.Entry<String, Integer> mapElement : map1.entrySet()) {
      a1 += mapElement.getValue();
    }
    for (Map.Entry<String, Integer> mapElement : map2.entrySet()) {
      a2 += mapElement.getValue();
    }

    if(a1 > a2){
      return 1;
    }
    if(a1 == a2){
      return  0;
    }
    else return -1;
  }

  @Override
  public boolean equals(Object obj) {
    return false;
  }
}
