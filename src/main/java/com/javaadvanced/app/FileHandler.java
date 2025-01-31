package com.javaadvanced.app;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Arrays;


public class FileHandler {
    private static final String DELIMITER = "\u001F";
    private static final String PAIR_DELIMITER = "\u001E";

    public List<Row> parseFile(String filePath) throws IOException {
        File file = new File(filePath);
        String fileContent = FileUtils.readFileToString(file, "UTF-8");

        return Arrays.stream(fileContent.split(System.lineSeparator()))
                .map(this::parseLine)
                .collect(Collectors.toList());
    }

    private Row parseLine(String line) {
        Row row = new Row();
        Arrays.stream(line.trim().split(PAIR_DELIMITER))
            .map(pair -> pair.replaceAll("^\\[|\\]$", ""))
            .forEach(pair -> {
                if (!pair.contains(DELIMITER)) {
                    int midPoint = pair.length() / 2;
                    pair = pair.substring(0, midPoint) + DELIMITER + pair.substring(midPoint);
                }
                String[] keyValue = pair.split(DELIMITER, 2);
                String value = keyValue.length > 1 ? keyValue[1] : "";
                row.addCell(new KeyValuePair(keyValue[0], value));
            });
        return row;
    }

    public void saveToFile(String filePath, List<Row> rows){
        File file = new File(filePath);
        List<String> lines = rows.stream()
                .map(row -> row.getCells().stream()
                        .map(cell -> String.format("[%s%s%s]", cell.getKey(), DELIMITER, cell.getValue()))
                        .collect(Collectors.joining(PAIR_DELIMITER)))
                .collect(Collectors.toList());

        try {
            FileUtils.writeLines(file, "UTF-8", lines);
            System.out.println("Data saved to " + filePath + " successfully.");
        } catch (IOException e) {
            System.out.println("Error saving to file.");
        }
    }
}