public class Directive {
    private String name;
    private Length length;


    public Directive(String name, Length length) {
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }


    public Length getLength() {
        return length;
    }

    public void setLength(Length length) {
        this.length = length;
    }

    public void setName(String name) {
        this.name = name;
    }

}

enum Length{
    VAR, ONE, ZERO
}

