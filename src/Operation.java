public class Operation {
    private String name;
    private int opcode;
    private int format;
    private OPR[] operands;

    public Operation(String name, int format, OPR[] operands) {
        this.name = name;
        this.format = format;
        this.operands = operands;
    }

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public Operation(String name, int opcode, int format, OPR[] operands) {
        this.name = name;
        this.opcode = opcode;
        this.format = format;
        this.operands = operands;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public OPR[] getOperands() {
        return operands;
    }

    public void setOperands(OPR[] operands) {
        this.operands = operands;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

 enum OPR{
    REG, VAL, NONE
}
