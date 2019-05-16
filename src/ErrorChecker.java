public class ErrorChecker {

    public static Instruction checkError(Instruction i){


        if(isDirective(i))
            i= checkDirectiveError(i);
        else if(isOperation(i.getMnemonic()) || isOperation(i.getMnemonic().substring(1)))
            i= checkOpError(i);
        else
            i.setError("Error: Operation/Directive does not exist");
        return i;
    }

    public static Instruction checkDirectiveError(Instruction i){

        if(i.getMnemonic().equals("END") || i.getMnemonic().equals("ORG") || i.getMnemonic().equals("BASE")){
            if(i.getLabel().getName().length() > 0){
                i.setError("Error: Can't assign label to this statement");
            }
        }
        else if(i.getLabel().getName().length() < 0){
            i.setError("Error: Missing Label");
        }

        return i;
    }

    public static Instruction checkOpError(Instruction i){
        if(!Cache.optable.containsKey(i.getMnemonic()) && i.getMnemonic().startsWith("+")) {
            i.setError("Error: Instruction is not valid for Format 4");
            return i;
        }


        return i;
    }

    public static Instruction checkMissing(Instruction i){
        if(i.getMnemonic().length() < 1){
            i.setError("Error: Missing Operation");
            return i;
        }
        if(isOperation(i)){
            if (i.getOperands().length == 0)
                i.setError("Error: Missing Operands");
            else if(i.getOperands().length == 1 && Cache.optable.get(i.getMnemonic()).getOperands()[1] != OPR.NONE )
                i.setError("Error: Missing Second Operand");
            return i;
        }
        else {
            if(i.getOperands().length > 0 && Cache.dirtable.get(i.getMnemonic()).getLength() == Length.ZERO)
                i.setError("Error: Can't have operands");
            else if((i.getOperands().length > 1 || i.getOperands().length == 0) && Cache.dirtable.get(i.getMnemonic()).getLength() == Length.ONE)
                i.setError("Error: Wrong number of operands");
            else if(i.getOperands().length == 0 && Cache.dirtable.get(i.getMnemonic()).getLength() == Length.VAR)
                i.setError("Error: Missing Operands");
        }
        return i;
    }

    public static boolean isOperation(Instruction i){
        return Cache.optable.containsKey(i.getMnemonic());
    }

    public static boolean isOperation(String m){
        return Cache.optable.containsKey(m);
    }

    public static boolean isDirective(Instruction i){
        return Cache.dirtable.containsKey(i.getMnemonic());
    }

    public static boolean containsError(Instruction i) {
        //i = ErrorChecker.SecondPassError(i);
        return i.getError() != null ;}

    public static Instruction SecondPassError(Instruction i){
        if(i.getError() != null || !isOperation(i)) return i;
        int format = Cache.optable.get(i.getMnemonic()).getFormat();
        if( format == 3 || format == 4){
            if(isLabel(i.getOperands()[0]) && !Cache.symtable.containsKey(i.getOperands()[0]))
                i.setError("Error: This Label is not defined");
        }
        return i;
    }

    public static boolean isLabel(String op){
        if (isSimpleExpression(op) && op.startsWith("*")) return false;
        else
        if (op.startsWith("*") || op.startsWith("#") || op.startsWith("@") ) return !isNumber(op.substring(1));
        return true;
    }

    public static boolean isSimpleExpression(String ops){
        if(ops.contains("+") || ops.contains("-") || ops.contains("/") || ops.contains("*"))
            return true;
        return false;
    }

    public static boolean isNumber(String num) {
        boolean n = true;
        if(num.startsWith("#") || num.startsWith("@"))
            num = num.substring(1);
        try {
            Integer.parseInt(num);
        } catch (Exception e) {
            n = false;
        }
        return n;
    }
}
