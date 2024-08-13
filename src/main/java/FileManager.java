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

    public static Set<String[]> readFile(String filePath, Pattern pattern) {
        Set<String[]> stringSet;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            stringSet = reader.lines()
                    .filter(s -> pattern.matcher(s).matches())
                    .distinct()
                    .map(s -> {
                        s = s.replace("\"\"", "");
                        String[] parts = s.split(";");
                        for (int i = 0; i < parts.length; i++) {
                            if (parts[i] == null || parts[i].trim().isEmpty()) {
                                parts[i] = "";
                            }
                        }
                        return parts;
                    })
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            logger.error("Error reading file or invalid path");
            stringSet = Collections.emptySet();
        }
        logger.info("File processed");
        return stringSet;
    }


    public static void writeFile(List<Set<String[]>> groups, int numberOfGroups) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH.mm");
        String formattedTime = now.format(formatter);
        String newFileName = "result_" + formattedTime + ".txt";
        Path outputPath = Path.of(newFileName);

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            writer.write("Всего найдено: " + numberOfGroups + " групп");
            writer.newLine();

            int groupCounter = 1;
            for (Set<String[]> group : groups) {
                writer.write("Группа " + groupCounter);
                writer.newLine();
                groupCounter++;
                for (String[] array : group) {
                    writer.write(String.join(";", array));
                    writer.newLine();
                }
            }

            logger.info("The result is written to a file " + newFileName);
        } catch (IOException e) {
            logger.error("Error writing to file");
        }
    }

}
