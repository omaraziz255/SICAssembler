import java.util.ArrayList;

public class Assembler {
    private Parser parser;
    private static Assembler assembler;

    public static Assembler getInstance(){
        if (assembler == null){
            assembler = new Assembler();
        }
        return assembler;
    }

    public  Parser getParser() {
        return parser;
    }

    public  void setParser(Parser parser) {
        this.parser = parser;
    }

    public Assembler(){
        this.parser = new Parser();
    }

    public static void Pass1(){
        FileRead f =  new FileRead("test_files/src.txt");
        ArrayList<String> file = f.loadFile();
        Cache.load();
        Assembler a = Assembler.getInstance();
        a.getParser().parse(file);
        a.getParser().printArray();
        System.out.println("Pass 1 successfully completed");
    }
}
