import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileManager {
    private static final Logger logger = LogManager.getLogger(FileManager.class);

    public static Set<Long[]> readFile(String filePath, Pattern pattern) {
        Set<Long[]> longSet;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            longSet = reader.lines()
                    .filter(s -> pattern.matcher(s).matches())
                    .filter(s -> (s.length() > 2))
                    .distinct()
                    .map(s -> s.replace("\"\"", "0"))
                    .map(s -> s.replace("\"", ""))
                    .map(s -> s.split(";"))
                    .map(parts -> Arrays.stream(parts)
                            .map(Long::valueOf)
                            .toArray(Long[]::new))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            logger.error("Error reading file or invalid path");
            longSet = Collections.emptySet();
        }
        logger.info("File processed");
        return longSet;
    }

    public static void writeFile(Set<Map<Long, Set<Long[]>>> result, int numberOfGroups) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH.mm");
        String formattedTime = now.format(formatter);
        String newFileName = "result_" + formattedTime + ".txt";
        Path outputPath = Path.of(newFileName);

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            writer.write("Total found: " + numberOfGroups+ " groups");
            writer.newLine();
            int groupCounter = 1;
            for (Map<Long, Set<Long[]>> map : result) {
                for (Map.Entry<Long, Set<Long[]>> entry : map.entrySet()) {
                    writer.write("Группа " + groupCounter);
                    writer.newLine();
                    groupCounter++;
                    for (Long[] array : entry.getValue()) {
                        writer.write(Arrays.toString(array));
                        writer.newLine();
                    }
                }
            }
            logger.info("The result is written to a file {}", newFileName);
        } catch (IOException e) {
            logger.error("Error writing to file");
        }
    }

}
