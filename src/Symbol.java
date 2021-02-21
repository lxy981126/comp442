public class Symbol {
    String name;

    protected Symbol(String name){
        this.name = name;
    }

    @Override
    public String toString(){ return name; }
}
