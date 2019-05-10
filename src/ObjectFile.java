import java.util.ArrayList;

public class ObjectFile {
    private StringBuilder header;
    private StringBuilder text;
    private StringBuilder end;
    private ArrayList<Instruction> code = Assembler.getInstance().getParser().getCode();
    private ArrayList<String> addresses = Assembler.getInstance().getParser().getLC().getAddresses();

    public ObjectFile(){
        header = new StringBuilder("H");
        text = new StringBuilder("T");
        end = new StringBuilder("E");
    }

    public void generateEndRecord(){
        int i;
        for (i = 0; i < code.size(); i++) {
            if(code.get(i).getMnemonic().equals("START"))
                break;
        }
        end.append("^" + addresses.get(i));
    }

    public void generateHeadRecord(){
        int i;
        for (i = 0; i < code.size(); i++) {
            if(code.get(i).getMnemonic().equals("START"))
                break;
        }
        String progName = code.get(i).getLabel().getName();
        if(progName.length() == 0) progName = "      ";
        String address = addresses.get(i);
        int length = Integer.parseInt(addresses.get(addresses.size()-1), 16) - Integer.parseInt(address, 16);
        String l = Integer.toHexString(length);
        header.append("^"+ progName + "^" + address + "^" + l);
    }

    public void generateTextRecord(){
        int i = Assembler.getInstance().getParser().getLC().getObjectCodeCounter();
        int numLines = i/30;
    }

    public String Format1ObjectCode(Instruction i){
        int opcode = Cache.optable.get(i.getMnemonic()).getOpcode();
        String objectCode = extend(Integer.toHexString(opcode),2);
        return objectCode.toUpperCase();


    }

    public String Format2ObjectCode(Instruction i){
        int opcode = Cache.optable.get(i.getMnemonic()).getOpcode();
        String[] ops = i.getOperands();
        int r1;
        int r2=0;
        String objectCode = extend(Integer.toHexString(opcode),2);
        r1 = Cache.registers.valueOf(ops[0]).ordinal();
        if(ops.length == 2)
            r2 = Cache.registers.valueOf(ops[1]).ordinal();
        objectCode = objectCode + Integer.toHexString(r1) + Integer.toHexString(r2);
        return objectCode.toUpperCase();
    }

    public String Format3and4ObjectCode(Instruction i){
        int format = Cache.optable.get(i.getMnemonic()).getFormat();
        int op = Integer.parseInt(binaryHandler(i, format), 2);
        return extend(Integer.toHexString(op),format*2).toUpperCase();

    }


    public String binaryHandler(Instruction bin, int format){
        char n,i,x,b,p,e;
        n = i = x = b = p = e = '0';
        int opcode = Cache.optable.get(bin.getMnemonic()).getOpcode();
        String opString = extend(Integer.toBinaryString(opcode), 8);
        opString = opString.substring(0,6);
        if(format == 4){
            e = n = i = '1';
            String address = Cache.symtable.get(bin.getOperands()[0]).getAddress();
            address = Integer.toBinaryString(Integer.parseInt(address,16));
            address = extend(address, 20);
            return opString + n + i + x + b + p + e + address;
        }
        n = isIndirect(bin.getOperands());
        i = isImmediate(bin.getOperands());
        x = isIndexed(bin.getOperands());
        p = isPCRelative(bin.getOperands());
        if (p == '0')
            b = isBase(bin.getOperands());

        if(n == i)
            n = i = '1';

        return new String();

    }

    public char isImmediate(String[] operands){
        if (operands[0].startsWith("#"))
            return '1';
        return '0';
    }

    public char isIndexed(String[] operands){
        if(operands.length == 2 )
            return '1';
        return '0';
    }

    public char isIndirect(String[] operands){
        if (operands[0].startsWith("@"))
            return '1';
        return '0';
    }

    public char isBase(String[] operands){
        String address = Cache.symtable.get(operands[0]).getAddress();
        int x = Integer.parseInt(address, 16);
        if(x >= 0 && x <= 4095)
            return '1';
        return '0';
    }

    public char isPCRelative(String[] operands){
        String address = Cache.symtable.get(operands[0]).getAddress();
        int x = Integer.parseInt(address, 16);
        if(x >= -2048 && x <= 2047)
            return '1';
        return '0';
    }


    public String extend(String s, int size){
        if(s.length() == size){
            return s;
        }
        StringBuilder S = new StringBuilder(s);
        while(S.length()<size){
            S.insert(0, "0");
        }
        return S.toString();
    }


    public StringBuilder getHeader() {
        return header;
    }

    public void setHeader(StringBuilder header) {
        this.header = header;
    }

    public StringBuilder getText() {
        return text;
    }

    public void setText(StringBuilder text) {
        this.text = text;
    }

    public StringBuilder getEnd() {
        return end;
    }

    public void setEnd(StringBuilder end) {
        this.end = end;
    }
}
