import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Parser {
    private ArrayList<Instruction> code;
    private LocationCounter LC;


    public ArrayList<Instruction> getCode() {
        return code;
    }

    public void setCode(ArrayList<Instruction> code) {
        this.code = code;
    }

    public LocationCounter getLC() {
        return LC;
    }

    public void setLC(LocationCounter LC) {
        this.LC = LC;
    }

    public Parser(){
        code = new ArrayList<>();
        LC = new LocationCounter();
    }

    public void parse(ArrayList<String> file){
        file.forEach(l -> parseLine(l));
        if(!code.get(code.size()-1).getMnemonic().equals("END")){
            code.get(code.size()-1).setError("Error: Missing END Statement");
        }
        code.forEach((i)-> LC.updateAddress(i));
        code.forEach((i)->{
            if (i.getLabel().getName().length() > 0) {
                if (Cache.symtable.containsKey(i.getLabel().getName())){
                    i.setError("Error: Duplicate Labels");
                }
                Cache.symtable.put(i.getLabel().getName(), new Label(i.getLabel().getName(), LC.getAddresses().get(code.indexOf(i))));
            }
        });

    }

    public void parseLine(String line){

        if (line.startsWith(".")){
            code.add(new Instruction(line));
        }

        if(line.length() < 2)
            return;

        line = extend(line);
        String label = line.substring(0,8);
        String mnemonic = line.substring(9, 16);
        String operands = line.substring(17, 36);
        String comment = line.substring(36);
        label = label.replaceAll("\\s", "");
        mnemonic = mnemonic.replaceAll("\\s", "");
        comment = comment.replaceAll("\\s", "");
        String[] ops = operandSplitter(operands);
        for (int i = 0; i < ops.length; i++) {
            ops[i] = ops[i].replaceAll("\\s", "");
        }
        Label l = new Label(label);
        Instruction i =  new Instruction(l, mnemonic, ops, comment);
        if(mnemonic.length() == 0 || (ops[0].length() == 0 && !mnemonic.equals("END"))){
            i = ErrorChecker.checkMissing(i);
            code.add(i);
        }
        else{

            i=ErrorChecker.checkError(i);
            code.add(i);
        }






    }

    public void parseFree(String line){     //works with limitation: 1 string = mnemonic, 2 strings = mnemonic, operands, 3 strings = label,mnemonic, operands, 4 strings = label, mnemonic, operands, comment
        if (line.startsWith(".")){
            code.add(new Instruction(line));
        }
        String mnemonic;
        String label = "";
        String comment = "";
        String operands = "";
        line = extend(line);
        line = line.trim().replaceAll(" +", " ");
        String[] parts = line.split("\\s");
        ArrayList<String> p = new ArrayList<>(Arrays.asList(parts));
        if(p.size() == 0)
            return;
        else if(p.size() == 1){
            mnemonic = p.get(0);
        }
        else if(p.size() == 2){
            mnemonic = p.get(0);
            operands = p.get(1);
        }
        else if(p.size() == 3){
            label = p.get(0);
            mnemonic = p.get(1);
            operands = p.get(2);
        }
        else{
        label = p.get(0);
        mnemonic = p.get(1);
        operands = p.get(2);
        comment = p.get(3);}
        String[] ops = operandSplitter(operands);
        for (int i = 0; i < ops.length; i++) {
            ops[i] = ops[i].replaceAll("\\s", "");
        }
        Label l = new Label(label);
        Instruction i =  new Instruction(l, mnemonic, ops, comment);
        if(mnemonic.length() == 0 || (ops[0].length() == 0 && !mnemonic.equals("END"))){
            i = ErrorChecker.checkMissing(i);
            code.add(i);
        }
        else{

            i=ErrorChecker.checkError(i);
            code.add(i);
        }
    }

    public String extend(String line){
        StringBuilder l = new StringBuilder(line);
        while (l.length() < 69){
            l.append(" ");
        }
        return l.toString();
    }

    public String[] operandSplitter(String operands){
        ArrayList<String> ops = new ArrayList<String>();
        if(!operands.contains(",")){
            return new String[]{operands};
        }
        StringTokenizer tokenizer = new StringTokenizer(operands, ",");
        while(tokenizer.hasMoreTokens()){
            ops.add(tokenizer.nextToken());
        }
        Object[] obj = ops.toArray();
        return Arrays.copyOf(obj, obj.length, String[].class);
    }

    public void printArray(){
        try {
            PrintWriter writer = new PrintWriter("address_file.txt", "UTF-8");
            PrintWriter symbol = new PrintWriter("symbol_file.txt", "UTF-8");
            LC.getAddresses().forEach((a)->{
                if(a.startsWith("A") || a.startsWith("B") || a.startsWith("C") || a.startsWith("D") || a.startsWith("E") || a.startsWith("F") ){
                    String b = "0" + a;
                    LC.getAddresses().set(LC.getAddresses().indexOf(a), b);
                }
            });
            code.forEach((i) -> {
                writer.println(LC.getAddresses().get(code.indexOf(i)) + " " + i.getLabel().getName() + " " + i.getMnemonic() + " " + i.getOperands()[0] + " ");
                if(i.getError() != null){
                    writer.println("******************* " + i.getError() + " ************************");
                }

            });
            writer.close();
            Cache.symtable.forEach((k, v) -> {
                symbol.println(k + " " + v.getAddress());
            });
            symbol.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
