package csv;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static java.util.Arrays.stream;
import static java.util.Comparator.reverseOrder;

public class Solution {

    public static void main(String[] args) {
        int n = 10_000_000;
        Product[] arr = new Product[n];
        int k = 20;
        int m = 1000;
        Map<Integer, PriorityQueue<Integer>> map = new HashMap<>();
        stream(arr).forEach(product -> {
            if (!map.containsKey(product.getID())) {
                PriorityQueue<Integer> integers = new PriorityQueue<>(reverseOrder());
                integers.add(product.getPrice());
                map.put(product.getID(), integers);
            } else {
                PriorityQueue<Integer> integers = map.get(product.getID());
                integers.add(product.getPrice());
                map.put(product.getID(), integers);
                if (integers.size() > k) {
                    integers.poll();
                }
            }
        });
        PriorityQueue<Integer> priorityQueueM = new PriorityQueue<>(m, reverseOrder());
        for (PriorityQueue<Integer> values : map.values()) {
            for (int i = 0; i < values.size(); ) {
                priorityQueueM.add(values.poll());
                if (priorityQueueM.size() > m) {
                    priorityQueueM.poll();
                }
            }
        }
    }

}
