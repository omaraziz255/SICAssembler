import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ObjectFile {
    private StringBuilder header;
    private StringBuilder text;
    private StringBuilder end;
    private ArrayList<Instruction> code;
    private ArrayList<String> addresses;
    private int PC;
    private int Base;

    public ArrayList<Instruction> getCode() {
        return code;
    }

    public void setCode(ArrayList<Instruction> code) {
        this.code = code;
    }

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<String> addresses) {
        this.addresses = addresses;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public int getBase() {
        return Base;
    }

    public void setBase(int base) {
        Base = base;
    }


    public ObjectFile() {
        header = new StringBuilder("H");
        text = new StringBuilder("T");
        end = new StringBuilder("E");
        Base = -1;
    }

    public void generateEndRecord() {
        int i;
        for (i = 0; i < code.size(); i++) {
            if (code.get(i).getMnemonic().equals("START") || code.get(i).getMnemonic().equals("ORG"))
                break;
        }
        end.append("^" + addresses.get(i));
    }

    public void generateHeadRecord() {
        int i;
        this.code = Assembler.getInstance().getParser().getCode();
        this.addresses = Assembler.getInstance().getParser().getLC().getAddresses();
        for (i = 0; i < code.size(); i++) {
            if (code.get(i).getMnemonic().equals("START") || code.get(i).getMnemonic().equals("ORG"))
                break;
        }
        String progName = code.get(i).getLabel().getName();
        if (progName.length() == 0) progName = "      ";
        String address = addresses.get(i);
        this.PC = Integer.parseInt(address, 16);
        int length = Integer.parseInt(addresses.get(addresses.size() - 1), 16) - Integer.parseInt(address, 16);
        String l = Integer.toHexString(length).toUpperCase();
        header.append("^" + progName + "^" + address + "^" + l);
    }

    public void generateTextRecord() {
        int i = Assembler.getInstance().getParser().getLC().getObjectCodeCounter();
        int lineStart = 0;
        int coveredBytes = 0;
        String start = extend(Integer.toHexString(PC), 6);
        text.append("^" + start.toUpperCase());
        String length = Integer.toHexString(i);
        text.append("^" + length.toUpperCase());
        int oldPC = PC;
        int bytes = 0;
        int oldbytes = 0;
        for (Instruction instn : code) {
            if (ErrorChecker.containsError(instn)) continue;
            String obj = generateObjectCode(instn);
            oldbytes = bytes;
            bytes += (PC - oldPC);
            int oldcovered = coveredBytes;
            coveredBytes += (PC - oldPC);
            int olderPC = oldPC;
            oldPC = PC;
            if (bytes > 30) {
                int newLineStart = text.length()+1;
                text.append("\nT^" + extend(Integer.toHexString(olderPC), 6).toUpperCase() + "^" + Integer.toHexString(i-oldcovered).toUpperCase());
                text.replace(lineStart+9,lineStart+11, Integer.toHexString(oldbytes).toUpperCase());
                bytes = PC - olderPC;
                lineStart = newLineStart;
            }
            if (obj != null)
                text.append("^" + obj);
        }
       // if(bytes < 30)
         //   text.replace(lineStart+9,lineStart+11, Integer.toHexString(bytes).toUpperCase());

    }



    public String generateObjectCode(Instruction i) {
        if (ErrorChecker.isOperation(i)) {
            int format = Cache.optable.get(i.getMnemonic()).getFormat();
            PC = PC + format;
            switch (format) {
                case 1:
                    return Format1ObjectCode(i);
                case 2:
                    return Format2ObjectCode(i);
                case 3:
                    return Format3ObjectCode(i);
                case 4:
                    return Format4ObjectCode(i);
            }
        } else if (!i.getMnemonic().equals("WORD") && !i.getMnemonic().equals("BYTE")) {
            return null;
        }
        return directiveObjectCode(i);
    }

    public String Format1ObjectCode(Instruction i) {
        int opcode = Cache.optable.get(i.getMnemonic()).getOpcode();
        String objectCode = extend(Integer.toHexString(opcode), 2);
        return objectCode.toUpperCase();


    }

    public String Format2ObjectCode(Instruction i) {
        int opcode = Cache.optable.get(i.getMnemonic()).getOpcode();
        String[] ops = i.getOperands();
        int r1;
        int r2 = 0;
        String objectCode = extend(Integer.toHexString(opcode), 2);
        r1 = Cache.registers.valueOf(ops[0]).ordinal();
        if (ops.length == 2)
            r2 = Cache.registers.valueOf(ops[1]).ordinal();
        objectCode = objectCode + Integer.toHexString(r1) + Integer.toHexString(r2);
        return objectCode.toUpperCase();
    }

   /* public String Format3and4ObjectCode(Instruction i) {
        int format = Cache.optable.get(i.getMnemonic()).getFormat();
        int op = Integer.parseInt(binaryHandler(i, format), 2);
        return extend(Integer.toHexString(op), format * 2).toUpperCase();

    }*/

    public String Format4ObjectCode(Instruction i) {
        long opcode = Cache.optable.get(i.getMnemonic()).getOpcode();
        String objectCode = (extend(Long.toBinaryString(opcode), 8)).substring(0, 6);
        String[] ops = i.getOperands();
        String address;
        if(isTerminator(ops))
            ops[0] = ops[0].replace("*", String.valueOf(PC));
        if (isIndirect(ops)) {
            objectCode += "10";
            ops[0] = ops[0].replace("@", "");
        } else if (isImmediate(ops)) {
            objectCode += "01";
            ops[0] = ops[0].replace("#", "");
        } else
            objectCode += "11";
        if (isIndexed(ops))
            objectCode += "1";
        else
            objectCode += "0";
        objectCode += "001";

        if(isSimpleExpression(ops))
            ops[0] = String.valueOf(calculateOP(ops[0]));
        if(isNumber(ops[0]))
            address = Integer.toHexString(Integer.parseInt(ops[0]));
        else
            address = Cache.symtable.get(ops[0]).getAddress();
        address = Integer.toBinaryString(Integer.parseInt(address, 16));
        address = extend(address, 20);
        objectCode += address;
        opcode = Long.parseLong(objectCode, 2);
        objectCode = Long.toHexString(opcode).toUpperCase();
        return extend(objectCode, 8);

    }

    public String Format3ObjectCode(Instruction i) {
        long opcode = Cache.optable.get(i.getMnemonic()).getOpcode();
        String objectCode = (extend(Long.toBinaryString(opcode), 8)).substring(0, 6);
        String[] ops = i.getOperands();
        int disp;
        if(isTerminator(ops))
            ops[0] = ops[0].replace("*", String.valueOf(PC));
        if (isIndirect(ops)) {
            objectCode += "10";
            ops[0] = ops[0].replace("@", "");
        } else if (isImmediate(ops)) {
            objectCode += "01";
            ops[0] = ops[0].replace("#", "");
        } else
            objectCode += "11";
        if (isIndexed(ops))
            objectCode += "1";
        else
            objectCode += "0";
        if(isSimpleExpression(ops))
            ops[0] = String.valueOf(calculateOP(ops[0]));
        if (isNumber(ops[0])){
            disp = Integer.parseInt(ops[0]);
            objectCode += "000";
            }
        else {
            int TA = Integer.parseInt(Cache.symtable.get(ops[0]).getAddress(), 16);
            disp = TA - PC;
            if (isPCRelative(disp))
                objectCode += "010";
            else {
                disp = TA - Base;
                if (isBase(disp))
                    objectCode += "100";
                else{
                    disp = TA;
                    objectCode += "000";
                }
            }
        }
        objectCode += extend(Integer.toBinaryString(disp), 12);
        objectCode = Long.toHexString(Long.parseLong(objectCode, 2));
        objectCode = extend(objectCode, 6);
        return objectCode.toUpperCase();

    }

    public boolean isNumber(String num) {
        boolean n = true;
        try {
            Integer.parseInt(num);
        } catch (Exception e) {
            n = false;
        }
        return n;
    }

    public int calculateOP(String op){
        String x = findOperation(op);
        int a, b;
        StringTokenizer tokenizer = new StringTokenizer(op, x);
        String op1 = tokenizer.nextToken();
        String op2 = tokenizer.nextToken();
        b = Integer.parseInt(op2);
        if(isNumber(op1))
            a = Integer.parseInt(op1);
        else {
            int TA = Integer.parseInt(Cache.symtable.get(op1).getAddress(), 16);
            a = TA - PC;
            if(!isPCRelative(a)){
                a = TA-Base;
                if(!isBase(a))
                    a = TA;
            }

        }
        return simpleCalc(a,b,x);
    }

    public String findOperation(String op){
        if(op.contains("+"))
            return "+";
        else if(op.contains("-"))
            return "-";
        else if(op.contains("*"))
            return "*";
        else
            return "/";
    }

    public int simpleCalc(int x, int y, String op){
        switch(op){
            case "+":
                return x + y;
            case "-":
                return x - y;
            case "*":
                return x * y;
            default:
                return x / y;
        }
    }

    public boolean isTerminator(String[] ops){
        if(ops[0].startsWith("*"))
            return true;
        return false;
    }

    public boolean isSimpleExpression(String[] ops){
        if(ops[0].contains("+") || ops[0].contains("-") || ops[0].contains("/") || ops[0].contains("*"))
            return true;
        return false;
    }


   /* public String binaryHandler(Instruction bin, int format) {
        char n, i, x, b, p, e;
        n = i = x = b = p = e = '0';
        int opcode = Cache.optable.get(bin.getMnemonic()).getOpcode();
        String opString = extend(Integer.toBinaryString(opcode), 8);
        opString = opString.substring(0, 6);
        if (format == 4) {
            e = n = i = '1';
            String address = Cache.symtable.get(bin.getOperands()[0]).getAddress();
            address = Integer.toBinaryString(Integer.parseInt(address, 16));
            address = extend(address, 20);
            return opString + n + i + x + b + p + e + address;
        }
        n = isIndirect(bin.getOperands());
        i = isImmediate(bin.getOperands());
        x = isIndexed(bin.getOperands());

        if (n == i) {
            n = i = '1';
            p = isPCRelative(bin.getOperands());
            if (p == '0')
                b = isBase(bin.getOperands());
        } else
            p = b = '0';


        String disp = calculateDisp(bin.getOperands(), new char[]{n, i, b, p});
        disp = extend(Integer.toBinaryString(Integer.parseInt(disp, 16)), 12);
        return opString + n + i + x + b + p + e + disp;

    }

    public String calculateDisp(String[] operands, char[] bits) {
        String disp;
        if (bits[0] == '1' && bits[1] != '1') {
            operands[0] = operands[0].replace("@", "");
            disp = Cache.symtable.get(operands[0]).getAddress();
            disp = extend(disp, 3);
        } else if (bits[1] == '1' && bits[0] != '1')
            disp = operands[0].replace("#", "");
        else if (bits[3] == '1') {
            disp = Cache.symtable.get(operands[0]).getAddress();
            int x = Integer.parseInt(disp, 16);
            x = x - PC;
            disp = extend(Integer.toHexString(x), 3);
        }//PC
        else {
            disp = Cache.symtable.get(operands[0]).getAddress();
            int x = Integer.parseInt(disp, 16);
            x = x - Integer.parseInt(Base, 16);
            disp = extend(Integer.toHexString(x), 3);
        }//Base

        return disp;

    } */

    public boolean isImmediate(String[] operands) {
        if (operands[0].startsWith("#"))
            return true;
        return false;
    }

    public boolean isIndexed(String[] operands) {
        if (operands.length == 2)
            return true;
        return false;
    }

    public boolean isIndirect(String[] operands) {
        if (operands[0].startsWith("@"))
            return true;
        return false;
    }

    public boolean isBase(int disp) {
        if (Base == -1) return false;
        if (disp >= 0 && disp <= 4095)
            return true;
        return false;
    }

    public boolean isPCRelative(int disp) {

        if (disp >= -2048 && disp <= 2047)
            return true;
        return false;
    }

    public String directiveObjectCode(Instruction i) {
        String value = "";
        String[] ops = i.getOperands();
        if (i.getMnemonic().equals("WORD")) {
            int val = Integer.parseInt(ops[0]);
            value = Integer.toHexString(val);
            value = extend(value, 6);
            PC += 3;
        } else {
            if (ops[0].startsWith("C")) {
                PC += (ops[0].length() - 3);
                ops[0] = ops[0].substring(2, ops[0].length() - 1);
                char[] chars = ops[0].toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    value = value + extend(Integer.toHexString((int) chars[j]), 2);
                }
            } else if (ops[0].startsWith("X")) {
                PC += (ops[0].length() - 3) / 2;
                ops[0] = ops[0].substring(2, ops[0].length() - 1);
                value = ops[0];

            } else {
                PC += ops.length;
                for (int j = 0; j < ops.length; j++) {
                    int val = Integer.parseInt(ops[j]);
                    value = value + extend(Integer.toHexString(val), 2);
                }
            }
        }
        return value.toUpperCase();
    }


    public String extend(String s, int size) {
        if (s.length() == size) {
            return s;
        }
        StringBuilder S = new StringBuilder(s);
        while (S.length() < size) {
            S.insert(0, "0");
        }
        while (S.length() > size){
            S.deleteCharAt(0);
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

    public void objectFile(){
        try {
            PrintWriter writer = new PrintWriter("object_file.txt", "UTF-8");
            PrintWriter error = new PrintWriter("error_file.txt", "UTF-8");
            writer.println(header);
            writer.println(text);
            writer.println(end);
            writer.close();
            code.forEach((i) -> {
                if(i.getError() != null)
                    error.println(i.getError() + " at address " + addresses.get(code.indexOf(i)).toUpperCase());
            });
            error.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    }
