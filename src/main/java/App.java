import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {

    static String filePath;
    private static final Logger logger = LogManager.getLogger(App.class);
    static final String regex = "^(\"\\d*\")(;\"\\d*\")*$";
    static final Pattern pattern = Pattern.compile(regex);

    public static void main(String[] args) {
        try {
            filePath = args[0];
        }catch (ArrayIndexOutOfBoundsException e){
            logger.error("Error: File path not specified. Completing the program");
            System.exit(1);
        }

        long startTime = System.currentTimeMillis();

        logger.info("Reading the file {}", filePath);

        Set<Long[]> copyOfMainSet = FileManager.readFile(filePath, pattern);
        if (!copyOfMainSet.isEmpty()) {
            Set<Map<Long, Set<Long[]>>> result = DataGrouper.groupBySimilarElements(copyOfMainSet);
            int numberOfGroups = DataGrouper.countTotalGroups(result);
            FileManager.writeFile(result, numberOfGroups);
        } else {
            logger.info("There is no correct data in the file or it is empty");
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        logger.info("Program completed, running time: {} seconds", duration);
    }
}
