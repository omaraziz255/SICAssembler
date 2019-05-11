import java.util.ArrayList;

public class Assembler {
    private Parser parser;
    private static Assembler assembler;
    private ObjectFile object;

    public static Assembler getAssembler() {
        return assembler;
    }

    public static void setAssembler(Assembler assembler) {
        Assembler.assembler = assembler;
    }

    public ObjectFile getObject() {
        return object;
    }

    public void setObject(ObjectFile object) {
        this.object = object;
    }

    public static Assembler getInstance() {
        if (assembler == null) {
            assembler = new Assembler();
        }
        return assembler;
    }

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    private Assembler() {
        this.parser = new Parser();
        this.object = new ObjectFile();
    }

    public static void Pass1() {
        FileRead f = new FileRead("test_files/src.txt");
        ArrayList<String> file = f.loadFile();
        Cache.load();
        Assembler a = Assembler.getInstance();
        a.getParser().parse(file);
        a.getParser().printArray();
        System.out.println("Pass 1 successfully completed");
    }

    public static void Pass2() {
        Assembler a = Assembler.getInstance();
        a.object.generateHeadRecord();
        a.object.generateTextRecord();
        a.object.generateEndRecord();
        System.out.println(a.object.getHeader());
        System.out.println(a.object.getText());
        System.out.println(a.object.getEnd());

    }
}
