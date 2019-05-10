import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
      Assembler.Pass1();
      ObjectFile o = new ObjectFile();
      o.generateHeadRecord();
      o.generateTextRecord();
      o.generateEndRecord();
      String x = o.Format4ObjectCode(new Instruction(new Label("Hi"), "+TIX", new String[]{"STR"},"" ));
      System.out.println(o.getHeader());
      System.out.println(o.getEnd());
      System.out.println(x);

    }
}
