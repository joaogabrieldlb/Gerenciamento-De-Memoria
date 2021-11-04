import java.util.ArrayList;
import java.util.List;

public class Teste {
    public static void main(String[] args) throws Exception {
        Processo p1 = new Processo("p1", 10);
        Processo p2 = new Processo("p1", 10);

        System.out.println(p1.equals(p2));
        List<Processo> l = new ArrayList<>();
        l.add(p1);
        l.add(p2);
        System.out.println(l.indexOf(p1));
        System.out.println(l.indexOf(p2));
    }
}
