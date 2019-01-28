package csv;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Solution {

    public static void main(String[] args) {
        int n = 10_000_000;
        Product[] arr = new Product[n];
        int k = 20;
        int m = 1000;
        Comparator<Product> cmp = (c1, c2) -> Float.compare(c2.getPrice(), c1.getPrice());
        Map<Integer, PriorityQueue<Product>> map = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            Product Product = arr[i];
            if (!map.containsKey(Product.getID())) {
                PriorityQueue<Product> integers = new PriorityQueue<>(cmp);
                integers.add(arr[i]);
                map.put(Product.getID(), integers);
            } else {
                PriorityQueue<Product> integers = map.get(Product.getID());
                integers.add(arr[i]);
                map.put(Product.getID(), integers);
                if (integers.size() > k) {
                    integers.poll();
                }
            }
        }
        PriorityQueue<Product> priorityQueueM = new PriorityQueue<>(m, cmp);
        for (PriorityQueue<Product> values : map.values()) {
            for (int i = 0; i < values.size(); ) {
                priorityQueueM.add(values.poll());
                if (priorityQueueM.size() > m) {
                    priorityQueueM.poll();
                }
            }
        }

    }
}
