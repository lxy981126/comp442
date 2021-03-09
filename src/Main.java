public class Main {
    public static void main(String[] args) {
        String inputFile = "test/a2/test.src";
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
