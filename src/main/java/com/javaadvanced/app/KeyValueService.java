package com.javaadvanced.app;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.InputMismatchException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Scanner;
import java.io.File;
import java.util.Collections;


public class KeyValueService {

    private FileHandler fileHandler;
    private List<Row> data;
    private String filePath;

    public KeyValueService(String filePath, FileHandler fileHandler) {
        this.filePath = filePath;
        this.fileHandler = fileHandler;
        this.data = new ArrayList<>();
    }

    public void loadData() {
        data = fileHandler.parseFile(filePath);
    }

    public void saveFile() {
        fileHandler.saveToFile(filePath, data);
    }

    public void generateNewFileTable(int rows, int cols) {
        data.clear();
        for (int i = 0; i < rows; i++) {
            Row row = new Row();
            for (int j = 0; j < cols; j++) {
                row.addCell(new KeyValuePair(generateAscii(), generateAscii()));
            }
            data.add(row);
        }
        saveFile();
    }

    public void searchPatt(String target) {
        List<String> results = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < data.size(); i++) {
            Row row = data.get(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                KeyValuePair pair = row.getCells().get(j);
                int keyMatches = pair.countOccurrences(pair.getKey(), target);
                int valueMatches = pair.countOccurrences(pair.getValue(), target);

                if (keyMatches > 0 || valueMatches > 0) {
                    results.add(String.format("%dx%d", i, j));
                    count += keyMatches + valueMatches;
                }
            }
        }

        if (count > 0) {
            System.out.println("Target found at: " + String.join(", ", results));
            System.out.println("Occurrences: " + count);
        } else {
            System.out.println("Target not found.");
        }
    }

    public void editKeyOrValue(int rowIndex, int colIndex, boolean isKey, String newValue, String[] newValueForBoth) {
        Row row = data.get(rowIndex);
        KeyValuePair pair = row.getCells().get(colIndex);

        if (newValueForBoth != null) {
            pair.setKey(newValueForBoth[0]);
            pair.setValue(newValueForBoth[1]);
        } else if (isKey) {
            pair.setKey(newValue);
        } else {
            pair.setValue(newValue);
        }
        saveFile();
    }

    public void addRow(int rowIndex, int numCells) {
        Row newRow = new Row();
        for (int i = 0; i < numCells; i++) {
            newRow.addCell(new KeyValuePair(generateAscii(), generateAscii()));
        }

        data.add(rowIndex, newRow);
        saveFile();
    }

    public void sortRow(int rowIndex, String sortOrder) {
        Row rowToSort = data.get(rowIndex);
        rowToSort.sortIndexRow(sortOrder);
        saveFile();
    }

    public void resetData(int rows, int cols) {
        data.clear();
        generateNewFileTable(rows, cols);
        saveFile();
    }

    private String generateAscii() {
        return new Random().ints(3, 32, 127)
            .mapToObj(c -> String.valueOf((char) c))
            .collect(Collectors.joining());
    }

    public List<Row> getData() {
        return data;
    }
}
