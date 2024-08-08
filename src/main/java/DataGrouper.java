import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class DataGrouper {

    private static int getMaxNumberOfColumns(Set<Long[]> data) {
        return data.stream()
                .mapToInt(arr -> arr.length)
                .max()
                .orElse(0);
    }
    private static long[] extractKeyValues(Set<Long[]> data, int size) {
        return data.stream()
                .flatMapToLong(arr -> LongStream.of(
                        Arrays.stream(arr)
                                .skip(size - 1)
                                .findFirst()
                                .orElse(0L)))
                .toArray();
    }

    private static Set<Long> findDuplicateElements(long[] result) {
        Set<Long> duplicateValues = new HashSet<>();
        Set<Long> seenValues = new HashSet<>();
        for (long value : result) {
            if (value != 0 && !seenValues.add(value)) {
                duplicateValues.add(value);
            }
        }
        return duplicateValues;
    }

    private static Map<Long, Set<Long[]>> groupDataByMatches(Set<Long[]> data, Set<Long> duplicateValues, int size) {
        return data.stream()
                .filter(arr -> Arrays.stream(arr)
                        .skip(size - 1)
                        .limit(1)
                        .anyMatch(duplicateValues::contains))
                .collect(Collectors.groupingBy(
                        arr -> Arrays.stream(arr)
                                .skip(size - 1)
                                .limit(1)
                                .findFirst()
                                .orElse(0L),
                        Collectors.toSet()
                ));
    }

    public static Set<Map<Long, Set<Long[]>>> groupBySimilarElements(Set<Long[]> data) {
        int maxElements = getMaxNumberOfColumns(data);
        Set<Map<Long, Set<Long[]>>> result = new LinkedHashSet<>();

        for (int size = 1; size <= maxElements; size++) {
            long[] seenValues = extractKeyValues(data, size);
            Set<Long> duplicateValues = findDuplicateElements(seenValues);
            Map<Long, Set<Long[]>> rowsWithMatches = groupDataByMatches(data, duplicateValues, size);

            if (!rowsWithMatches.isEmpty()) {
                result.add(rowsWithMatches);
            }
        }
        return result;
    }

    static int countTotalGroups(Set<Map<Long, Set<Long[]>>> results) {
        return results.stream()
                .mapToInt(Map::size)
                .sum();
    }
}
