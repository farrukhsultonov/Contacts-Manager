import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class Contacts {
    public static void main(String[] args) throws IOException {
        //Set data directory
        String directory = "src/data";
        //Set File name for contact
        String filename = "contacts.txt";
        //Create Path filepath variable for get directory and filename
        Path filepath = Paths.get(directory, filename);
        //create charConvert to encoding UTF-8 character
        Charset charConvert = StandardCharsets.UTF_8;

        //
        try {
            //If directory or file does not exist, then create directory and file
            if (Files.notExists(Path.of(directory))) {
                Files.createDirectories(Path.of(directory));
            }

            if (!Files.exists(filepath)) {
                Files.createFile(filepath);
            }

            //import scanner class for input
            Scanner input = new Scanner(System.in);

            //set choice to 0
            int choice = 0;
            //Do While for user choice input
            do {
                System.out.println("1. View contacts");
                System.out.println("2. Add a new contact.");
                System.out.println("3. Search a contact by name.");
                System.out.println("4. Delete an existing contact.");
                System.out.println("5. Exit.");
                System.out.print("Enter an option (1, 2, 3, 4 or 5): ");
                //try and catch to throw error message if user input enter other than integer
                try {
                    choice = input.nextInt();
                } catch (InputMismatchException e) {
                    System.err.println("Invalid Input. Please Enter valid Number (1-5)");
                    input.next();
                    continue;
                }
                //Switch cases
                switch (choice) {
                    case 1:
                        viewContacts(filepath, charConvert);
                        break;
                    case 2:
                        addContact(filepath, charConvert, input);
                        break;
                    case 3:
                        searchByName(filepath, charConvert, input);
                        break;
                    case 4:
                        deleteContact(filepath, charConvert, input);
                        break;
                    case 5:
                        System.out.println("Exiting program...");
                        break;
                    //if user input other than 1-5
                    default:
                        System.out.println("Invalid choice, please try again");
                }
            } while (choice != 5);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    //viewContacts Method to see filename
    private static void viewContacts (Path filepath, Charset charConvert) throws IOException {
        // Read all lines from the file
        List<String> lines = Files.readAllLines(filepath, charConvert);
        // Check if there are no contacts
        if (lines.isEmpty()) {
            System.out.println("No contacts found");
        } else {
//            for (int i = 0; i < lines.size(); i++) {
//                System.out.println((i + 1) + ": " + lines.get(i));
            //forEach loop line and print name and phone number
            for (String line : lines) {
                // Split the line by the pipe symbol (|) into an array of parts
                String[] parts = line.split("\\|");
                //name =1 phone number =2 (if array has two parts(true))
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String phoneNum = parts[1].trim().replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");

                    // print contact info with aligned columns
                    System.out.format("%-20s | %-15s\n", name, phoneNum);
                }
            }
        }

    }

    //addContact method - takes in three parameters: Path object, Charset object and Scanner object. Also, it may throw an IOException
    private static void addContact (Path filepath, Charset charConvert, Scanner input) throws IOException {
        //User to enter name - input is trimmed to remove any leading or trailing whitespace
        System.out.print("Enter name: ");
        input.nextLine(); // consume newline character left by previous input
        String name = input.nextLine().trim();
        //set nameExist to false
        boolean nameExist = false;
        //read all line from contact.txt file and store in List<String> object called lines
        List<String> lines = Files.readAllLines(filepath, charConvert);
        //For Loop (ForEach loop)
        for (String line : lines) {
            //line is split into an array of strings using '|' as separator. first index(0) is name and (1) is phone number
            String[] parts = line.split("\\|");
            String existingName = parts[0].trim();
            //if exisiting name is equals to name (String name = input.next().trim();) then nameExist set to true and loop is exited (break)
            if (existingName.equalsIgnoreCase(name)) {
                nameExist = true;
                break;
            }
        }
        //nameExist is true means there is already a contact with the same name. It prompts the user to confirm if they want to overwrite existing contact.
        if (nameExist) {
            System.out.println("There's already a contact named " + name + ". Do you want to overwrite it? (Yes/No)");

            String userInput = input.next().trim();
            //When user enters yes, it removes all  lines that start with the existing name(ignoring case) using removeIf method.
            if (userInput.equalsIgnoreCase("yes")) {
                lines.removeIf(line -> line.toLowerCase().startsWith(name.toLowerCase() + " "));
                System.out.print("Enter phone number: ");
                //It prompts to enter phone number and stores phoneNumber variable
                String phoneNumber = input.next().trim().replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
                String newContact = name + "  |  " + phoneNumber;
                lines.add(newContact);
                System.out.println("Contact added: " + newContact);
            } else {
                //if user's input is other than yes than it returns to initial menu
                System.out.println("Thank you. Here is Menu Again");
                return;
            }
            //nameExist is false, then it prompts the user to enter phone umber and stores in phoneNumber
        } else {
            System.out.print("Enter phone number: ");
            String phoneNumber = input.next().trim().replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
            String newContact = name + "  |  " + phoneNumber;
            lines.add(newContact);
            System.out.println("Contact added: " + newContact);
        }
        //filepath: the path to the file that will be written to.
        //lines: the list of strings that will be written to the file.
        //charConvert: the character encoding that will be used to write the file.
        // Files.write method is called to write the updated lines list back to the file. StandardOpenOption.TRUNCATE_EXISTING indicate tha the contents of an existing file should be deleted before writing new content to it.
        Files.write(filepath, lines, charConvert, StandardOpenOption.TRUNCATE_EXISTING);

    }
    //searchByName method using three parameter
    private static void searchByName (Path filepath, Charset charConvert, Scanner input) throws IOException {

        System.out.print("Enter name to search for: ");
        String name = input.next();
        //read all line from contact.txt file and store in List<String> object called lines
        List<String> lines = Files.readAllLines(filepath, charConvert);
        //set found variable as false
        boolean found = false;
        //These lines loop through each line in the lines list and check if it contains the name (ignoring case). If a line contains the name, it is printed to the console and found is set to true.
        for (String line : lines) {
            if (line.toLowerCase().contains(name.toLowerCase())) {
                System.out.println(line);
                found = true;
            }
        }
        //if found variable is false, print message that no contacts were found
        if (!found) {
            System.out.println("No contacts found for name: " + name);
        }
    }
    //deleteContact method to using three parameter
    private static void deleteContact (Path filepath, Charset charConvert, Scanner input) throws IOException {
        //user to input name to delete
        System.out.print("Enter name to delete: ");
        String name = input.next();
        //read all line from contact.txt file and store in List<String> object called lines
        List<String> lines = Files.readAllLines(filepath, charConvert);
        //set found variable as false
        boolean found = false;
        //iterates over each line in List of Strings
        for (int i = 0; i < lines.size(); i++) {
            //if line(i) contains name, prompt the user for confirmation to delete the contact (case-insensitive)
            if (lines.get(i).toLowerCase().contains(name.toLowerCase())) {
                System.out.println("Are you sure you want to delete this contact?");
                System.out.println(lines.get(i));
                System.out.println("Enter y for yes or n for no:");
                String choice = input.next();
                //if response is y, remove the line from List
                if (choice.equalsIgnoreCase("y")) {
                    lines.remove(i);
                    //print confirmation
                    System.out.println("Contact deleted");
                } else {
                    System.out.println("Contact not deleted");
                }
                //after if/else statement set found variable to true and break out of the loop
                found = true;
                break;
            }
        }
        //After the for loop that goes through each line in the file, the program checks if any contact was found to be deleted.
        //found is false then print message (line list remains unmodified)
        if (!found) {
            System.out.println("No contacts found for name: " + name);
            //if found is true, write the modified lines list back to the file(updated list)
        } else {
            Files.write(filepath, lines, charConvert);
        }
    }

}