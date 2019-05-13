public class ErrorChecker {

    public static Instruction checkError(Instruction i){


        if(isDirective(i))
            i= checkDirectiveError(i);
        else if(isOperation(i))
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
        if(!Cache.optable.containsKey(i.getMnemonic().substring(1)) && i.getMnemonic().startsWith("*")) {
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

    public static boolean isDirective(Instruction i){
        return Cache.dirtable.containsKey(i.getMnemonic());
    }

    public static Instruction SecondPassError(Instruction i){
        return i;
    }
}
