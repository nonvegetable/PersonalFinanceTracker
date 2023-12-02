package internalAssessment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;


public class ExpenseTracker {
    private Map<String, LinkedList<Double>> accountTransactions;
    private Map<String, LinkedList<Double>> categoryTransactions;
    private Map<String, Double> accountBalances;
    private Map<String, LinkedList<String>> accountTransactionDescriptions;
    private Map<String, LinkedList<String>> categoryTransactionDescriptions;
    private Map<String, LinkedList<String>> accountTransactionDates;
    private Map<String, LinkedList<String>> categoryTransactionDates;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String MENU_ASCII_ART = 
            "=======================================\n" +
            "        Expense Tracker Menu           \n" +
            "=======================================\n";
    public static final String SHINING_TEXT = 
            ANSI_BOLD + ANSI_WHITE + " ********                                                        **********                          **                   " + ANSI_RESET + "\n" +
            ANSI_BOLD + ANSI_CYAN + "/**/////          ******                                        /////**///                          /**                   " + ANSI_RESET + "\n" +
            ANSI_BOLD + ANSI_WHITE + "/**       **   **/**///**  *****  *******   ******  *****           /**     ******  ******    ***** /**  **  *****  ******" + ANSI_RESET + "\n" +
            ANSI_BOLD + ANSI_CYAN + "/******* //** ** /**  /** **///**//**///** **////  **///**          /**    //**//* //////**  **///**/** **  **///**//**/**" + ANSI_RESET + "\n" +
            ANSI_BOLD + ANSI_WHITE + "/**////   //***  /****** /******* /**  /**//***** /*******          /**     /** /   ******* /**  // /****  /******* /** / " + ANSI_RESET + "\n" +
            ANSI_BOLD + ANSI_CYAN + "/**        **/** /**///  /**////  /**  /** /////**/**////           /**     /**    **////** /**   **/**/** /**////  /**   " + ANSI_RESET + "\n" +
            ANSI_BOLD + ANSI_WHITE + "/******** ** //**/**     //****** ***  /** ****** //******          /**    /***   //********//***** /**//**//******/***   " + ANSI_RESET + "\n" +
            ANSI_BOLD + ANSI_CYAN + "//////// //   // //       ////// ///   // //////   //////           //     ///     ////////  /////  //  //  ////// ///   " + ANSI_RESET + "\n";


    private static final List<String> predefinedCategories = Arrays.asList("food", "transport", "shopping", "salary", "entertainment");

    public ExpenseTracker() {
        accountTransactions = new HashMap<>();
        categoryTransactions = new HashMap<>();
        accountBalances = new HashMap<>();
        accountTransactionDescriptions = new HashMap<>();
        categoryTransactionDescriptions = new HashMap<>();
        accountTransactionDates = new HashMap<>();
        categoryTransactionDates = new HashMap<>();
        
        for (String category : predefinedCategories) {
        	categoryTransactions.put(category, new LinkedList<>());
        }
    }

    public boolean addTransaction(String account, double amount, String category, boolean isDebit, String description, String date) {
        if (!predefinedCategories.contains(category)) {
            System.out.println("Invalid category. Please select a valid category.");
            return false;
        }

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            return false;
        }

        String[] dateParts = date.split("-");
        int year, month, day;

        try {
            year = Integer.parseInt(dateParts[0]);
            month = Integer.parseInt(dateParts[1]);
            day = Integer.parseInt(dateParts[2]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid date format. Please use numeric values for year, month, and day.");
            return false;
        }

        if (month < 1 || month > 12 || day < 1 || day > 31) {
            System.out.println("Invalid date. Month must be between 1 and 12, and day must be between 1 and 31.");
            return false;
        }

        LinkedList<Double> accountTransactionList = accountTransactions.getOrDefault(account, new LinkedList<>());
        accountTransactionList.add(isDebit ? -amount : amount);
        accountTransactions.put(account, accountTransactionList);

        LinkedList<Double> categoryTransactionList = categoryTransactions.getOrDefault(category, new LinkedList<>());
        categoryTransactionList.add(isDebit ? -amount : amount);
        categoryTransactions.put(category, categoryTransactionList);

        double balance = accountBalances.getOrDefault(account, 0.0);
        balance += isDebit ? -amount : amount;
        accountBalances.put(account, balance);

        accountTransactionDescriptions.computeIfAbsent(account, k -> new LinkedList<>()).add(description);
        categoryTransactionDescriptions.computeIfAbsent(category, k -> new LinkedList<>()).add(description);

        accountTransactionDates.computeIfAbsent(account, k -> new LinkedList<>()).add(date);
        categoryTransactionDates.computeIfAbsent(category, k -> new LinkedList<>()).add(date);

        return true;
    }

    public void viewTransactionsForAccount(String account) {
        LinkedList<Double> transactions = accountTransactions.get(account);
        LinkedList<String> descriptions = accountTransactionDescriptions.get(account);
        LinkedList<String> dates = accountTransactionDates.get(account);

        if (transactions == null) {
            System.out.println("No transactions found for the account: " + account);
        } else {
            System.out.println("Transactions for account: " + account);

            List<SortableTransaction> sortedTransactions = createSortableTransactions(transactions, descriptions, dates);

            Collections.sort(sortedTransactions);

            for (SortableTransaction transaction : sortedTransactions) {
                System.out.println("Amount: " + transaction.getAmount());
                System.out.println("Description: " + transaction.getDescription());
                System.out.println("Date: " + transaction.getFormattedDate());
            }
        }
    }

    public void viewTransactionsByCategory(String category) {
        LinkedList<Double> transactions = categoryTransactions.get(category);
        LinkedList<String> descriptions = categoryTransactionDescriptions.get(category);
        LinkedList<String> dates = categoryTransactionDates.get(category);

        if (transactions == null) {
            System.out.println("No transactions found for the category: " + category);
        } else {
            System.out.println("Transactions for category: " + category);

            List<SortableTransaction> sortedTransactions = createSortableTransactions(transactions, descriptions, dates);

            Collections.sort(sortedTransactions);

            for (SortableTransaction transaction : sortedTransactions) {
                System.out.println("Amount: " + transaction.getAmount());
                System.out.println("Description: " + transaction.getDescription());
                System.out.println("Date: " + transaction.getFormattedDate());
            }
        }
    }


    public void printAccountBalance(String account) {
        Double balance = accountBalances.get(account);
        if (balance == null) {
            System.out.println("No account balance found for the account.");
            System.out.println("Account Balance for " + account + ": " + balance);
        } else {
            System.out.println("Account Balance for " + account + ": " + balance);
        }
    }
    
    private List<SortableTransaction> createSortableTransactions(LinkedList<Double> amounts, LinkedList<String> descriptions, LinkedList<String> dates) {
        List<SortableTransaction> sortableTransactions = new LinkedList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < amounts.size(); i++) {
            try {
                Date date = dateFormat.parse(dates.get(i));
                sortableTransactions.add(new SortableTransaction(amounts.get(i), descriptions.get(i), date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return sortableTransactions;
    }

    private static class SortableTransaction implements Comparable<SortableTransaction> {
        private double amount;
        private String description;
        private Date date;

        public SortableTransaction(double amount, String description, Date date) {
            this.amount = amount;
            this.description = description;
            this.date = date;
        }

        public double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public String getFormattedDate() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(date);
        }

        @Override
        public int compareTo(SortableTransaction other) {
            return this.date.compareTo(other.date);
        }
    }
    
    public static void printMenu() {
    	System.out.println(ANSI_CYAN + MENU_ASCII_ART + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "1. Credit Transaction" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "2. Debit Transaction" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "3. View Transactions" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "4. View Transactions by Category" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "5. Print Account Balance" + ANSI_RESET);
        System.out.println("---------------------------------------");
        System.out.println(ANSI_YELLOW + "6. Create or Log in to an Account" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "7. Log Out" + ANSI_RESET);
        System.out.println("---------------------------------------");
        System.out.println(ANSI_YELLOW + "8. Exit" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "=======================================" + ANSI_RESET);
        System.out.print(ANSI_GREEN + "Select an option: " + ANSI_RESET);
    }

    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        Scanner sc = new Scanner(System.in);
        String currentAccount = null; 
        
        System.out.println(SHINING_TEXT);
        System.out.println("                                                                                -By Vighnesh Palande and Swapnil Ranadive");
        System.out.println();
        
        while (true) {
        	printMenu();

            try {
	            int choice = sc.nextInt();
	            sc.nextLine(); 
            
	            switch (choice) {
	            case 1:
	                if (currentAccount != null) {
	                    System.out.print("Enter Amount: ");
	                    double amount = sc.nextDouble();
	                    sc.nextLine();
	                    System.out.println("Enter a description: ");
	                    String description = sc.nextLine();
	                    System.out.println("Select a category:");
	                    for (int i = 0; i < predefinedCategories.size(); i++) {
	                        System.out.println((i + 1) + ". " + predefinedCategories.get(i));
	                    }
	                    System.out.print("Enter category number: ");
	                    int categoryChoice = sc.nextInt();
	                    sc.nextLine();
	                    System.out.print("Enter Date (YYYY-MM-DD): ");
	                    String date = sc.nextLine();
	
	                    if (categoryChoice >= 1 && categoryChoice <= predefinedCategories.size()) {
	                        String category = predefinedCategories.get(categoryChoice - 1);
	                        if (tracker.addTransaction(currentAccount, amount, category, false, description, date)) {
	                            System.out.println(ANSI_GREEN + "Credit Transaction added successfully." + ANSI_RESET);
	                        } else {
	                            System.out.println(ANSI_RED + "Credit Transaction failed to add." + ANSI_RESET);
	                        }
	                    } else {
	                        System.out.println("Invalid category selection.");
	                    }
	                } else {
                        System.out.println(ANSI_RED + "Please log in to an account first." + ANSI_RESET);
	                }
	                System.out.println();
	                break;
	            case 2:
	                if (currentAccount != null) {
	                    System.out.print("Enter Amount: ");
	                    double amount = sc.nextDouble();
	                    sc.nextLine();
	                    Double balance = tracker.accountBalances.get(currentAccount);
	                    if (balance == null || balance < amount) {
	                        System.out.println("Debit Transaction failed: Insufficient balance.");
	                    } else {
	                        System.out.println("Enter a description: ");
	                        String description = sc.nextLine();
	                        System.out.println("Select a category:");
	                        for (int i = 0; i < predefinedCategories.size(); i++) {
	                            System.out.println((i + 1) + ". " + predefinedCategories.get(i));
	                        }
	                        System.out.print("Enter category number: ");
	                        int categoryChoice = sc.nextInt();
	                        sc.nextLine();
	                        System.out.print("Enter Date (YYYY-MM-DD): ");
	                        String date = sc.nextLine();
	
	                        if (categoryChoice >= 1 && categoryChoice <= predefinedCategories.size()) {
	                            String category = predefinedCategories.get(categoryChoice - 1);
	                            if (tracker.addTransaction(currentAccount, amount, category, true, description, date)) {
	                                System.out.println(ANSI_GREEN + "Debit Transaction added successfully." + ANSI_RESET);
	                            } else {
	                                System.out.println(ANSI_RED + "Debit Transaction failed to add." + ANSI_RESET);
	                            }
	                        } else {
	                            System.out.println("Invalid category selection.");
	                        }
	                    }
	                } else {
                        System.out.println(ANSI_RED + "Please log in to an account first." + ANSI_RESET);
	                }
	                System.out.println();
	                break;
	            case 3:
	                if (currentAccount != null) {
	                    tracker.viewTransactionsForAccount(currentAccount);
	                } else {
                        System.out.println(ANSI_RED + "Please log in to an account first." + ANSI_RESET);
	                }
	                System.out.println();
	                break;
	            case 4:
	                if (currentAccount != null) {
	                    System.out.print("Enter Category: ");
	                    String category = sc.nextLine();
	                    tracker.viewTransactionsByCategory(category);
	                } else {
                        System.out.println(ANSI_RED + "Please log in to an account first." + ANSI_RESET);
	                }
	                System.out.println();
	                break;
	            case 5:
	                if (currentAccount != null) {
	                    tracker.printAccountBalance(currentAccount);
	                } else {
                        System.out.println(ANSI_RED + "Please log in to an account first." + ANSI_RESET);
	                }
	                System.out.println();
	                break;
	            case 6:
	                System.out.print("Enter Account Name: ");
	                currentAccount = sc.nextLine();
	                System.out.println("Logged in to the account: " + currentAccount);
	                System.out.println();
	                break;
	            case 7:
	                currentAccount = null;
	                System.out.println("Logged out from the current account.");
	                System.out.println();
	                break;
	            case 8:
                    System.out.println(ANSI_YELLOW + "Exiting Expense Tracker." + ANSI_RESET);
	                System.exit(0);
	            default:
	                System.out.println("Invalid option. Please try again.");
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid input. Please enter a valid menu option (1-8).");
                System.out.println();
	            sc.nextLine(); 
	        }
        }
    }
}
