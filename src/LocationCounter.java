import java.util.ArrayList;

public class LocationCounter {
    private ArrayList<String> addresses = new ArrayList<>();
    private int objectCodeCounter = 0;

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public int getObjectCodeCounter() {
        return objectCodeCounter;
    }

    public void setObjectCodeCounter(int objectCodeCounter) {
        this.objectCodeCounter = objectCodeCounter;
    }

    public void setAddresses(ArrayList<String> addresses) {
        this.addresses = addresses;
    }

    public void updateAddress(Instruction i){
        String mnemonic = i.getMnemonic().toUpperCase();
        if(mnemonic == null)
            return;
        if(mnemonic.length()< 1 || (!ErrorChecker.isOperation(i) && !ErrorChecker.isDirective(i))){
            addresses.add(addresses.get(addresses.size()-1));
            return;}

        if(mnemonic.equals("START") || mnemonic.equals("ORG")){
            String address = i.getOperands()[0];
            address = adjustAddress(address);
            addresses.add(address.toUpperCase());
            addresses.add(address.toUpperCase());
            return;
        }

        String currentAddress = addresses.get(addresses.size()-1);
        int value;
        if(Cache.dirtable.containsKey(mnemonic)){
           String directive = Cache.dirtable.get(mnemonic).getName();
            if(directive.equals("END")){

            }
            else if(directive.equals("RESB")){
                value = Integer.parseInt(i.getOperands()[0]);
                add(value);
            }
            else if(directive.equals("RESW")){
                value = Integer.parseInt(i.getOperands()[0]);
                add(3 * value);
            }
            else if(directive.equals("BYTE")){
                if(i.getOperands()[0].startsWith("X")){
                    value = (i.getOperands()[0].length()-3)/2;
                }
                else if(i.getOperands()[0].startsWith("C")){
                    value = (i.getOperands()[0].length()-3);
                }
                else{
                    value = i.getOperands().length;
                }
                add(value);
                objectCodeCounter += value;

            }
            else
                if(directive.equals("WORD")){
                value = i.getOperands().length;
                add(value * 3);
                objectCodeCounter += (value*3);
            }
            else add(1); // EQU Statement


        }
        else if(Cache.optable.containsKey(mnemonic)){
            add(Cache.optable.get(mnemonic).getFormat());
            objectCodeCounter += Cache.optable.get(mnemonic).getFormat();
        }

    }

    public String adjustAddress(String s){
        if(s.length() == 4){
            return s;
        }
        StringBuilder S = new StringBuilder(s);
        while(S.length()<4){
            S.insert(0, "0");
        }
        return S.toString();
    }

    public void add(int value){
        String currentAddress = addresses.get(addresses.size()-1);
        int addressValue = Integer.parseInt(currentAddress, 16);
        addressValue = addressValue+value;
        currentAddress = adjustAddress(Integer.toHexString(addressValue));
        addresses.add(currentAddress.toUpperCase());
    }

}
