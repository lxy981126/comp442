import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String inputFile = "test/a4/test";
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
