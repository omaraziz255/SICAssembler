import java.util.HashMap;

public class Cache {
    public static HashMap<String, Operation> optable = new HashMap<>();
    public static HashMap<String, Directive> dirtable = new HashMap<>();
    public static HashMap<String, Label> symtable = new HashMap<>();
    public enum registers{ A, X, I, B, S, T, F, None,  PC, SW} //use valueOf(operand).ordinal to figure out register number

    static{
        load();
    }

    public static void load(){
        dirtable.put("BYTE", new Directive("BYTE", Length.VAR));
        dirtable.put("RESB", new Directive("RESB", Length.ONE));
        dirtable.put("WORD", new Directive("WORD", Length.VAR));
        dirtable.put("RESW", new Directive("RESW", Length.ONE));
        dirtable.put("START", new Directive("START", Length.ONE));
        dirtable.put("END", new Directive("END", Length.ZERO));
        dirtable.put("ORG", new Directive("ORG", Length.ZERO));
        dirtable.put("EQU", new Directive("EQU", Length.ONE));
        dirtable.put("BASE", new Directive("BASE", Length.ONE));

        optable.put("RMO", new Operation("RMO", 0xAC, 2, new OPR[]{OPR.REG, OPR.REG}));
        optable.put("LDA", new Operation("LDA", 0x00, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+LDA", new Operation("+LDA", 0x00, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("LDX", new Operation("LDX", 0x04, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+LDX", new Operation("+LDX",0x04, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("LDT", new Operation("LDT",0x74, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+LDT", new Operation("+LDT",0x74, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("LDS", new Operation("LDS", 0x6C,3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+LDS", new Operation("+LDS",0x6C, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("LDB", new Operation("LDB",0x68, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+LDB", new Operation("+LDB",0x68, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("STA", new Operation("STA",0x0C, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+STA", new Operation("+STA",0x0C, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("STX", new Operation("STX",0x10, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("STL", new Operation("STL",0x14, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+STL", new Operation("+STL",0x14, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+STX", new Operation("+STX",0x10, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("STT", new Operation("STT",0x84, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+STT", new Operation("+STT",0x84, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("STS", new Operation("STS",0x7C, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+STS", new Operation("+STS",0x7C, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("STB", new Operation("STB",0x78, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+STB", new Operation("+STB",0x78, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("LDCH", new Operation("LDCH",0x50, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+LDCH", new Operation("+LDCH",0x50, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("STCH", new Operation("STCH",0x54, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+STCH", new Operation("+STCH",0x54, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("ADDR", new Operation("ADDR",0x90, 2, new OPR[]{OPR.REG, OPR.REG}));
        optable.put("SUBR", new Operation("SUBR",0x94, 2, new OPR[]{OPR.REG, OPR.REG}));
        optable.put("COMPR", new Operation("COMPR",0xA0, 2, new OPR[]{OPR.REG, OPR.REG}));
        optable.put("TIXR", new Operation("TIXR",0xB8, 2, new OPR[]{OPR.REG, OPR.NONE}));
        optable.put("TIX", new Operation("TIX",0x2C, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+TIX", new Operation("+TIX",0x2C, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("ADD", new Operation("ADD",0x18, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+ADD", new Operation("+ADD",0x18, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("SUB", new Operation("SUB",0x1C, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+SUB", new Operation("+SUB",0x1C, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("COMP", new Operation("COMP",0x28, 3, new OPR[]{OPR.VAL, OPR.REG}));
        optable.put("+COMP", new Operation("+COMP",0x28, 4, new OPR[]{OPR.VAL, OPR.REG}));
        optable.put("J", new Operation("J",0x3C, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+J", new Operation("+J", 0x3C,4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("JEQ", new Operation("JEQ",0x30, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+JEQ", new Operation("+JEQ", 0x30,4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("JLT", new Operation("JLT",0x38, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+JLT", new Operation("+JLT",0x38, 4, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("JGT", new Operation("JGT",0x34, 3, new OPR[]{OPR.VAL, OPR.NONE}));
        optable.put("+JGT", new Operation("+JGT",0x34, 4, new OPR[]{OPR.VAL, OPR.NONE}));


    }
}
