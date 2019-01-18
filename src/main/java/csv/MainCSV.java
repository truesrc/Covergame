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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.System.getProperty;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class MainCSV {
    private static final int LIM_IDs = 20;
    private static final int LIM_PRDS = 1000;
    private static String pathIn = getProperty("user.dir") + "/src/main/java/csv/csv_in/";
    private static String pathOut = getProperty("user.dir") + "/src/main/java/csv/csv_out/";

    public static void main(String[] args) {
        if (!isNull(args)) {
            pathIn = args[0];
            if (args.length == 2)
                pathOut = args[1];
        }
        try (Stream<Path> paths = Files.walk(Paths.get(pathIn))) {
            Optional<List<Product>> reduce = paths
                    .filter(Files::isRegularFile)
                    .parallel()
                    .map(path -> {
                        try {
                            List<Product> parse = new CsvToBeanBuilder<Product>(new FileReader(path.toString()))
                                    .withType(Product.class)
                                    .build()
                                    .parse();
                            if (!isNull(parse)) {
                                return getProducts(parse);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return new ArrayList<Product>(1);
                    }).reduce((list1000_1, list1000_2) -> {
                        list1000_1.addAll(list1000_2);
                        return getProducts(list1000_1);

                    });

            Writer writer = new FileWriter(pathOut + "out.csv");
            StatefulBeanToCsv<Product> build = new StatefulBeanToCsvBuilder<Product>(writer).build();
            build.write(reduce.get());
            writer.close();

        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
    }

    private static List<Product> getProducts(List<Product> parse) {
        return parse
                .parallelStream()
                .collect(groupingBy(Product::getID))
                .values()
                .parallelStream()
                .map(listSameIds -> {
                    if (listSameIds.size() > LIM_IDs) {
                        listSameIds.sort(Product::compareTo);
                        return listSameIds.subList(0, LIM_IDs);
                    } else return listSameIds;
                }).reduce((listNoMore20_1, listNoMore20_2) -> {
                    listNoMore20_1.addAll(listNoMore20_2);
                    return listNoMore20_1;
                }).get()
                .parallelStream()
                .sorted()
                .limit(LIM_PRDS)
                .collect(toList());
    }
}
