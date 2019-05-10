public class Label {
    private String name;
    private String address;

    public Label(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Label(String name){
        this.name = name;
        this.address = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



}
