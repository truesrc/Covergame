package csv;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.System.getProperty;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.toList;

public class MainCSV {
    private static final int LIM_IDs = 7;
    private static final int LIM_PRDS = 30;
    private static String pathIn = getProperty("user.dir") + "/src/main/java/csv/csv_in/";
    private static String pathOut = getProperty("user.dir") + "/src/main/java/csv/csv_out/";

    public static void main(String[] args) {
        try (Stream<Path> paths = Files.walk(Paths.get(pathIn))) {
            Optional<List<Product>> reduce =
                    paths
                            .filter(Files::isRegularFile)
                            .collect(toList())
                            .parallelStream()
                            .map(path -> {
                                try {
                                    return new CsvToBeanBuilder<Product>(new FileReader(path.toString()))
                                            .withType(Product.class)
                                            .build()
                                            .parse();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return new ArrayList<Product>(1);
                            })
                            .map(MainCSV::getProducts)
                            .reduce((list1000_1, list1000_2) -> {
                                list1000_1.addAll(list1000_2);
                                return getProducts(list1000_1);
                            });

            Writer writer = new FileWriter(pathOut + "out.csv");
            StatefulBeanToCsv<Product> build = new StatefulBeanToCsvBuilder<Product>(writer).build();
            List<Product> products = reduce.get();
            sort(products);
            build.write(products);
            writer.close();
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
    }

    private static List<Product> getProducts(List<Product> parse) {
        Comparator<Product> cmp = (c1, c2) -> Float.compare(c2.getPrice(), c1.getPrice());
        Map<Integer, PriorityQueue<Product>> map = new HashMap<>();
        for (Product product : parse) {
            if (!map.containsKey(product.getID())) {
                PriorityQueue<Product> integers = new PriorityQueue<>(cmp);
                integers.add(product);
                map.put(product.getID(), integers);
            } else {
                PriorityQueue<Product> integers = map.get(product.getID());
                integers.add(product);
                map.put(product.getID(), integers);
                if (integers.size() > LIM_IDs) {
                    integers.poll();
                }
            }
        }
        PriorityQueue<Product> priorityQueueM = new PriorityQueue<>(LIM_PRDS, cmp);
        for (PriorityQueue<Product> values : map.values()) {
            for (int i = 0; i < values.size(); ) {
                priorityQueueM.add(values.poll());
                if (priorityQueueM.size() > LIM_PRDS) {
                    priorityQueueM.poll();
                }
            }
        }
        return new ArrayList<>(priorityQueueM);
    }
}
