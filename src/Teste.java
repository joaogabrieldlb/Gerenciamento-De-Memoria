import java.nio.file.Paths;

public class Teste {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Programa p1 = new Programa(Paths.get("apps", "exemplo.txt"));
        System.out.println(p1.getCodigoFonte());
        System.out.println(5/2);
    }
}
