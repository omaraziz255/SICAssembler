public class Instruction {
    private Label label;
    private String mnemonic;
    private String[] operands;
    private String comment;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Instruction(Label label, String mnemonic, String[] operands, String comment) {
        this.label = label;
        this.mnemonic = mnemonic;
        this.operands = operands;
        this.comment = comment;
        this.error = null;
    }

    public Instruction(String comment) {
        this.label = null;
        this.mnemonic = null;
        this.operands = null;
        this.comment = comment;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String[] getOperands() {
        return operands;
    }

    public void setOperands(String[] operands) {
        this.operands = operands;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
