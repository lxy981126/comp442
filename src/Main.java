import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter an input file: ");

        String inputFile = scanner.nextLine();
        Parser parser = new Parser(inputFile);

        boolean success= parser.parse();

        if (success) {
            System.out.println("Parsing success");
        }
        else {
            System.out.println("Parsing fail");
        }
    }

}
