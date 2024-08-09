import java.util.*;

public class DataGrouper {
    static class UnionFind {
        private final Map<String, String> parent = new HashMap<>();
        private final Map<String, Integer> rank = new HashMap<>();

        public String find(String s) {
            parent.putIfAbsent(s, s);
            if (!s.equals(parent.get(s))) {
                parent.put(s, find(parent.get(s)));
            }
            return parent.get(s);
        }

        public void union(String s1, String s2) {
            String root1 = find(s1);
            String root2 = find(s2);

            if (!root1.equals(root2)) {
                int rank1 = rank.getOrDefault(root1, 0);
                int rank2 = rank.getOrDefault(root2, 0);

                if (rank1 > rank2) {
                    parent.put(root2, root1);
                } else if (rank1 < rank2) {
                    parent.put(root1, root2);
                } else {
                    parent.put(root1, root2);
                    rank.put(root2, rank2 + 1);
                }
            }
        }

        public Map<String, Set<String[]>> groupMembers(Set<String[]> data) {
            Map<String, Set<String[]>> groups = new HashMap<>();
            for (String[] row : data) {
                String root = find(Arrays.toString(row));
                groups.computeIfAbsent(root, k -> new HashSet<>()).add(row);
            }
            return groups;
        }
    }

    public static List<Set<String[]>> groupStrings(Set<String[]> data) {
        int maxColumns = data.stream().mapToInt(row -> row.length).max().orElse(0);
        UnionFind uf = new UnionFind();

        for (int col = 0; col < maxColumns; col++) {
            Map<String, List<String[]>> columnToRowsMap = new HashMap<>();
            for (String[] row : data) {
                if (col < row.length && !row[col].isEmpty()) {
                    columnToRowsMap.computeIfAbsent(row[col], k -> new ArrayList<>()).add(row);
                }
            }

            for (List<String[]> rows : columnToRowsMap.values()) {
                for (int i = 1; i < rows.size(); i++) {
                    String[] row1 = rows.get(0);
                    String[] row2 = rows.get(i);

                    boolean match = false;

                    for (int colIndex = 0; colIndex < Math.min(row1.length, row2.length); colIndex++) {
                        if (!row1[colIndex].isEmpty() && !row2[colIndex].isEmpty() && row1[colIndex].equals(row2[colIndex])) {
                            match = true;
                            break;
                        }
                    }

                    if (match) {
                        uf.union(Arrays.toString(row1), Arrays.toString(row2));
                    }
                }
            }
        }

        Map<String, Set<String[]>> groupedRows = uf.groupMembers(data);

        groupedRows.entrySet().removeIf(entry -> entry.getValue().stream()
                .allMatch(row -> Arrays.stream(row).allMatch(String::isEmpty)));

        groupedRows.entrySet().removeIf(entry -> entry.getValue().size() < 2);

        List<Set<String[]>> sortedGroups = new ArrayList<>(groupedRows.values());
        sortedGroups.sort((g1, g2) -> Integer.compare(g2.size(), g1.size()));

        return sortedGroups;
    }

}
