package com.javaadvanced.app;

import com.javaadvanced.service.KeyValueService;
import com.javaadvanced.utils.FileHandler;

import java.util.Scanner;

public class KeyValue2DProgram {

    private static Scanner sc = new Scanner(System.in);
    private KeyValueService service;
    private FileHandler fileHandler;

    public KeyValue2DProgram(String[] args) {
        fileHandler = new FileHandler();
        String filePath = args.length > 0 ? args[0] : promptFilePath();
        service = new KeyValueService(filePath, fileHandler);
        service.loadData();
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

    public static void printMenu() {
        System.out.println("\n-------------");
        System.out.println("[MENU] ");
        System.out.println("[ search ] - Search");
        System.out.println("[ edit ] - Edit");
        System.out.println("[ add_row ] - Add Row");
        System.out.println("[ sort ] - Sort");
        System.out.println("[ print ] - Print");
        System.out.println("[ reset ] - Reset");
        System.out.println("[ x ] - Exit");
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            printMenu();
            System.out.print("Action: ");
            String action = sc.next();

            switch (action.toLowerCase()) {
                case "search" -> searchPatt();
                case "edit" -> editMenu();
                case "add_row" -> addRow();
                case "sort" -> sortRow();
                case "print" -> print2DStructure();
                case "reset" -> resetData();
                case "x" -> {
                    exit = true;
                    service.saveFile();
                    System.out.println("[EXIT PROGRAM]");
                }
                default -> System.out.println("Select only the menu items!");
            }
        }
    }

    private void searchPatt() {
        System.out.print("Search: ");
        String target = sc.next();
        service.searchPatt(target);
    }

    private void editMenu() {
        System.out.print("Enter Dimension (row x col): ");
        String input = sc.next();
        String[] parts = input.split("x");
        int rowIndex = Integer.parseInt(parts[0]);
        int colIndex = Integer.parseInt(parts[1]);

        System.out.print("Choose to edit Key (k), Value (v), or Both (b): ");
        String choice = sc.next().toLowerCase();

        if (choice.equals("b")) {
            System.out.print("Enter new key and value (key:value): ");
            String[] newValueForBoth = sc.next().split(":");
            service.editKeyOrValue(rowIndex, colIndex, false, null, newValueForBoth);
        } else {
            boolean isKey = choice.equals("k");
            System.out.print("Enter new value: ");
            String newValue = sc.next();
            service.editKeyOrValue(rowIndex, colIndex, isKey, newValue, null);
        }
    }

    private void addRow() {
        System.out.print("Enter row index: ");
        int rowIndex = sc.nextInt();

        System.out.print("Enter number of cells: ");
        int numCells = sc.nextInt();

        service.addRow(rowIndex, numCells);
    }

    private void sortRow() {
        System.out.print("Enter row index to sort: ");
        int rowIndex = sc.nextInt();

        System.out.print("Enter sort order (asc/desc): ");
        String sortOrder = sc.next();

        service.sortRow(rowIndex, sortOrder);
    }

    private void print2DStructure() {
        service.getData().forEach(row -> {
            row.getCells().forEach(cell -> System.out.print(cell + " "));
            System.out.println();
        });
    }

    private void resetData() {
        System.out.print("Enter Dimension (rows x cols): ");
        String input = sc.next();
        String[] parts = input.split("x");
        int rows = Integer.parseInt(parts[0]);
        int cols = Integer.parseInt(parts[1]);

        service.resetData(rows, cols);
        print2DStructure();
    }

    public static void main(String[] args) {
        KeyValue2DProgram app = new KeyValue2DProgram(args);
        app.run();
    }
}
