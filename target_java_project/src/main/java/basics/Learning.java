package basics;

public class Learning {
    public static void main(String[] args) {
        var d = new Dima("Dima");
        System.out.println(d.lala());
    }
}

class Dima implements Cloneable {
    String name;

    public Dima(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public Dima lala() {
        var a = (Dima) this.clone();
        return a;
    }
}