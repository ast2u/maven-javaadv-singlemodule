import java.util.*;

public class TaskActivity {
    private int row,col;
    private String[][] asciiArray;
    private static Scanner sc = new Scanner(System.in);

    static int[] getDimension() {
        //Gets input of the user and takes the dimension
        int[] dimensions = new int[2];
        while (true) {
            System.out.print("Input Table Dimension: ");
            String input = sc.next();
            
            if (input.contains("x")) {
                String[] d = input.split("x");
                dimensions[0] = Integer.parseInt(d[0].trim());
                dimensions[1] = Integer.parseInt(d[1].trim());
                return dimensions;
                
            } else {
                System.out.println("Give a Dimension eg.('RxC')");
            }
        }
        
    }

    String generateAscii(Random random) {
        //Generates ascii 32-126
        String text = "";
        for (int i = 0; i<3;i++) {
            int ascii = random.nextInt(95)+32;
            text+= ((char) ascii);
        }
        return text;
    }
    
    void generateArr(int row ,int col) {
        //Generates the Array.. Calls on the generateAscii to generate the Ascii Character
        this.row = row;
        this.col = col;
        this.asciiArray = new String[row][col];
        Random rnd = new Random();
            for (int i = 0;i<row;i++) {
                for (int j=0;j<col;j++) {
                    asciiArray[i][j] = generateAscii(rnd);
                }
            }
            printArray();
    }
    

    
    void editArray () {
        //Method that will edit the Array
        int[] editD = new int[2];
        System.out.print("Edit Dimension#: ");
        String input = sc.next();
        if (input.length()==3 && input.charAt(1)=='x') {
            String[] d = input.split("x");
            editD[0] = Integer.parseInt(d[0].trim());
            editD[1] = Integer.parseInt(d[1].trim());
            System.out.print("Value: ");
            String newAscii = sc.next();
            if (newAscii.length()==3) {
                System.out.println("Edited "+asciiArray[editD[0]][editD[1]]+" => "+newAscii);
                asciiArray[editD[0]][editD[1]] = newAscii;
            } else {
                System.out.println("Invalid input");
            }
        } else {
            System.out.println("Give a Dimension eg.('RxC')");
        }
    }

    void searchPatt () {
        //Search using Linear Search checking if the dimension index contains the target
        System.out.print("Search: ");
        String target = sc.next();
        String output = "";
        int count = 0;
        int letterCount = 0;
        char targetChar = target.charAt(0);
        for (int i=0;i<row; i++) {
            for (int j=0;j<col; j++) {
                if (asciiArray[i][j].contains(target)) {
                    count++;
                    output += i+"x"+j+" ";
                }

                for (char c : asciiArray[i][j].toCharArray()){
                    if (c == targetChar){
                        letterCount++;
                    }
                    
                }
                }
            
        }
        if(count > 0 && letterCount > 0) {
            System.out.println("Total Occurences: "+count);
            System.out.println("Total Letter: "+letterCount);
            System.out.print(output);
        } else {
            System.out.println("Not found");
        }
    }

    void printArray() {
        // To print the 2D Array
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.print(asciiArray[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        TaskActivity select = new TaskActivity();
        int[] initialD = getDimension();
        select.generateArr(initialD[0],initialD[1]);
        
        boolean exit = false;
        while(!exit) {    
            printMenu();
            System.out.print("Action: ");
            String menu = sc.next();
            switch (menu) {
                case "1":
                    select.searchPatt();
                    break;
                case "2":
                    select.editArray();
                    break;
                case "3":
                    select.printArray();
                    break;
                case "4":
                    initialD = getDimension();
                    select.generateArr(initialD[0],initialD[1]);
                    break;
                case "x":
                    exit=true;
                    System.out.println("[EXIT PROGRAM]");
                    break;
                default:
                    System.out.println("Select only the menu items!");
                    break;
                }
        }
        sc.close();
        
    }
    static void printMenu() {
        System.out.println("\n-------------");
        System.out.println("[MENU]");
        System.out.println("[1] - Search");
        System.out.println("[2] - Edit");
        System.out.println("[3] - Print");
        System.out.println("[4] - Reset");
        System.out.println("[x] - Exit");
    }
    }
