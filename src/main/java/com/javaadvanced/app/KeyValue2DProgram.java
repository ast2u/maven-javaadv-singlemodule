package com.javaadvanced.app;
import java.io.IOException;
import java.io.File;
import java.util.InputMismatchException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;

import java.util.Optional;

public class KeyValue2DProgram {
    private List<Row> data;
    private FileHandler fileHandler;
    private static Scanner sc = new Scanner(System.in);
    private String filePath;

    public KeyValue2DProgram(String[] args) {
        fileHandler = new FileHandler();
        filePath = args.length > 0 ? args[0] : promptFilePath();
        data = new ArrayList<>();
        loadData();
    }

    private String promptFilePath() {
        System.out.print("Please enter the path to the input file: ");
        String path = sc.nextLine();
        if (!path.endsWith(".txt")) {
            System.out.println("Invalid file type. Please provide a .txt file.");
            return promptFilePath();
        }
        return path;
    }

    public void loadData() {
        while (!filePath.endsWith(".txt")) {
            System.out.println("Invalid file type. Please provide a .txt file.");
            filePath = promptFilePath();
        }
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            String response = "";
            while(!response.equals("yes") && !response.equals("no")){
                System.out.print("Do you want to create this file? (yes/no): ");
                response = sc.nextLine().trim().toLowerCase();

                if (response.equals("yes")) {
                    generateNewFileTable();
                } else if (response.equals("no")){
                    System.out.println("Exiting the program.");
                    System.exit(0);  // Exit if user doesn't want to create a file
                }else{
                    System.out.println("Must be yes or no.");
                }
            }
            
        } else {
            try {
                data = fileHandler.parseFile(filePath);
                print2DStructure();
            } catch (IOException e) {
                System.out.println("Error loading data: " + e.getMessage());
            }
        }
    }

    public void saveFile() {
        fileHandler.saveToFile(filePath, data); 
    }

    private void generateNewFileTable() {
        System.out.println("Generate a new table. Provide dimension:");
        boolean validInput = false;
        while (!validInput) {
            System.out.println("Enter Dimension:");
            String input = sc.nextLine().trim();
            String[] parts = input.split("x");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid format. Use 'RxC' (e.g., 3x3).");
            }
            int rows, cols;
            try {
                rows = Integer.parseInt(parts[0].trim());
                cols = Integer.parseInt(parts[1].trim());
                if(rows < 0 || cols < 0) {
                    System.out.println("Dimensions must be positive.");
                    continue;
                }

                // Generate the table with random KeyValuePairs
                data = new ArrayList<>();
                for (int i = 0; i < rows; i++) {
                    Row row = new Row();
                    for (int j = 0; j < cols; j++) {
                        row.addCell(new KeyValuePair(generateAscii(), generateAscii()));
                    }
                    data.add(row);
                }
                // Save the new data to the file
                fileHandler.saveToFile(filePath, data);
                System.out.println("New file created successfully at " + filePath);
                print2DStructure();
                validInput = true;

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter valid integers for rows and columns.");
            }
        }
    }

    public void searchPatt() {
        System.out.print("Search: ");
        String target = sc.next();

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
            System.out.println("Updated both key and value at " + rowIndex + "x" + colIndex + ": " + pair);
        } else if (isKey) {
            pair.setKey(newValue); // Edit key
            System.out.println("Updated key at " + rowIndex + "x" + colIndex + ": " + pair);
        } else {
            pair.setValue(newValue); // Edit value
            System.out.println("Updated value at " + rowIndex + "x" + colIndex + ": " + pair);
        }
        saveFile();
    }

    void editMenu(){
        String inputChoice = "";
        int rowIndex = 0;
        int colIndex = 0;
        boolean exitEdit = false;
        while (!exitEdit) {
            System.out.print("Enter Dimension: ");
            String input = sc.next().trim();
            Optional<String[]> optionalDimension = Optional.ofNullable(input.split("x"));
            try {
                // Extract dimensions safely
                String[] inputDimension = optionalDimension
                        .filter(arr -> arr.length == 2)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid dimension format"));
                
                rowIndex = Integer.parseInt(inputDimension[0]);
                colIndex = Integer.parseInt(inputDimension[1]);

                // Validate dimensions
                if (rowIndex < 0 || colIndex < 0) {
                    System.out.println("Dimensions must be positive.");
                    continue;
                }

                if (rowIndex >= data.size()) {
                    System.out.println("Row index out of range. Please enter a valid row index.");
                    continue;
                }

                if (colIndex >= data.get(rowIndex).getCells().size()) {
                    System.out.println("Column index out of range for the selected row. Please enter a valid column index.");
                    continue;
                }

                exitEdit = true;

            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                System.out.println("Invalid dimension format. Please enter in 'RowxCol' format (e.g., 1x2).");
            }
        }
            
        while (!inputChoice.equals("k") && !inputChoice.equals("v") && !inputChoice.equals("b")) {
            System.out.print("Choose to edit Key (k), Value (v), or Both (b): ");
            inputChoice = sc.next().trim().toLowerCase();

            if (!inputChoice.equals("k") && !inputChoice.equals("v") && !inputChoice.equals("b")) {
                System.out.println("Invalid input! Please enter 'k' for Key, 'v' for Value, or 'b' for Both.");
            }
        }

        if(inputChoice.equalsIgnoreCase("b")) { // For both input
            while(true) {
                System.out.print("Enter the new key and value (format: key:value): ");
                String[] newValueForBoth = sc.next().split(":");
                if(newValueForBoth.length == 2) {
                    editKeyOrValue(rowIndex, colIndex, false, null, newValueForBoth);
                    break;
                }else {
                    System.out.println("Invalid format for newValueForBoth. Please use the format 'key:value'.");
                }
            }
        }else{
            boolean isKey = inputChoice.equalsIgnoreCase("k"); // For Key or Value
            System.out.print("Enter the new value: ");
            String newValue = sc.next();
            editKeyOrValue(rowIndex, colIndex, isKey, newValue, null);
        }
    }

    public void addRow() {
        boolean addingRow = false;
        while(!addingRow){
            System.out.print("Enter row number: ");
            int rowIndex = sc.nextInt();
            

            if (rowIndex < 0 || rowIndex > data.size()) {
                System.out.println("Invalid input. row input out of bounds.");
                continue;
            }

            System.out.print("Number of cells: ");
            int numCells = sc.nextInt();

            if(numCells < 1 || numCells > 10){
                System.out.println("Invalid input. Try again only upto 10.");
                continue;
            }

            Row newRow = new Row();
            for (int i = 0; i < numCells; i++) {
                newRow.addCell(new KeyValuePair(generateAscii(), generateAscii()));
            }

            data.add(rowIndex, newRow);
            System.out.println("Row added successfully.");
            saveFile();
            addingRow = true;
        }
    }

    public void sortRow() {
        boolean sorting = false;
        while(!sorting){
            System.out.print("Enter sorting preference (e.g., 0-asc or 1-desc): ");
            String input = sc.next().toLowerCase();

            String[] parts = input.split("-");
            if (parts.length != 2) {
                System.out.println("Invalid input format. Please use '0-asc' or '1-desc'.");
                continue;
            }

            int rowIndex;
            String sortOrder;
            try {
                rowIndex = Integer.parseInt(parts[0]);
                sortOrder = parts[1];
            } catch (NumberFormatException e) {
                System.out.println("Invalid row index. Please provide a numeric value.");
                continue;
            }

            if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
                System.out.println("Invalid sort order. Use 'asc' or 'desc'.");
                continue;
            }

            if (rowIndex < 0 || rowIndex >= data.size()) {
                System.out.println("Row index out of range.");
                continue;
            }

            Row rowToSort = data.get(rowIndex);
            rowToSort.sortIndexRow(sortOrder);
            System.out.println("Row " + rowIndex + " sorted in " + sortOrder + " order.");
            saveFile();
            sorting = true;
        }
        
    }

    private String generateAscii() {
        return new Random().ints(3, 32, 127)
            .mapToObj(c -> String.valueOf((char) c))
            .collect(Collectors.joining());
    }

    public void print2DStructure() {
        data.forEach(row -> {
            row.getCells().forEach(cell -> System.out.print(cell + " "));
            System.out.println();
        });
    }

    void resetData() {
        boolean reset = false;
        while(!reset){
            System.out.print("Enter Dimension: ");
            String input = sc.next();
            String[] dimensions = input.split("x");
            if(dimensions.length == 2){
                try{
                    int rows = Integer.parseInt(dimensions[0].trim());
                    int cols = Integer.parseInt(dimensions[1].trim());
                    if(rows < 0 || cols < 0) {
                        System.out.println("Dimensions must be positive.");
                        continue;
                    }
                    data.clear();
                    for(int i = 0; i < rows; i++){
                        Row row = new Row();
                        for(int j = 0; j < cols; j++){
                            row.addCell(new KeyValuePair(generateAscii(), generateAscii()));
                        }
                        data.add(row);
                        
                    }
                    System.out.println("Data Reset");
                    print2DStructure();
                    saveFile();
                    reset = true;
                    
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Dimension input. Please use 'RxC' all numbers eg. RxC");
                }
            } else {
                System.out.println("Invalid input format. The format must be 'RxC' eg. 3x3, 4x4");
            }
        }
    }


    public static void main(String[] args) {
        KeyValue2DProgram app = new KeyValue2DProgram(args);

        boolean exit = false;
        while (!exit) {
            printMenu();
            System.out.print("Action: ");
            String action = sc.next();

            switch (action.toLowerCase()) {
                case "search" -> app.searchPatt();
                case "edit" -> app.editMenu();
                case "add_row" -> app.addRow();
                case "sort" -> app.sortRow();
                case "print" -> app.print2DStructure();
                case "reset" -> app.resetData();
                case "x" -> {
                    exit=true;
                    app.saveFile();
                    System.out.println("[EXIT PROGRAM]");
                }
                default -> System.out.println("Select only the menu items!");
            }
        }
    }

    static void printMenu(){
        System.out.println("\n-------------");
        System.out.println("[MENU]");
        System.out.println("[ search ] - Search");
        System.out.println("[ edit ] - Edit");
        System.out.println("[ add_row ] - Add Row");
        System.out.println("[ sort ] - Sort");
        System.out.println("[ print ] - Print");
        System.out.println("[ reset ] - Reset");
        System.out.println("[ x ] - Exit");
    }
}