public class Symbol {
    String name;

    public Symbol(String name){
        this.name = name;
    }

    @Override
    public String toString(){ return name; }
}