package jdk.util.map;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * Created by shanyao on 2018/7/10.
 */
public class LinkedHashMapStudy {
    public static void main(String[] args) {
        LinkedHashMap l = new LinkedHashMap();

        l.put("1","1");
        l.put("3","3");
        l.put("2","2");
        l.put("5","6");
        l.put("2","6");
        l.get(2);

        Iterator iterator = l.entrySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        TreeMap treeMap = new TreeMap();
        treeMap.put("1","1");
        treeMap.put("3","1");
        treeMap.put("2","1");
        treeMap.put("5","1");
        treeMap.put("4","1");
        System.out.println(l);
    }
}
