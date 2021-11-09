import java.security.InvalidParameterException;

public class OS {
    
    public static boolean verbose = false;
    public static final String appsDirectory = "apps";
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            imprimeHelp();
            return;
        }

        if (args[0].toUpperCase().equals("-T"))
        {
            System.out.println("================ MODO TESTE ================");
            String[] argsTeste = {"-opcao_ignorada1", "complemento_ignorado1", "-p", "pv", "ff", "-m", "8", "-a", "teste.txt", "-v", "parametro_ignorado1"};
            Kernel testeOs = new Kernel(argsTeste);
            testeOs.run();
            return;
        }

        Kernel polvoOs = null;
        // Trata exceções do construtor e de execução
        try {
            polvoOs = new Kernel(args);
            polvoOs.run();
        } catch (InvalidParameterException e) {
            System.out.println(e.getMessage());
            imprimeLinhaDeComando();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
    
	public static void imprimeHelp()
    {
        System.out.println("Inicia o Gerenciador de Memoria do OS.");
        imprimeLinhaDeComando();
        System.out.println();
        System.out.println("-T\t\tExecuta o MODO DE TESTE (ignora demais argumentos)");
        System.out.println();
        System.out.println("-P\t\tDefine o tipo de particionamento:");
        System.out.println("PF\t\tPARTICIONAMENTO FIXO");
        System.out.println("tamanho_da_particao\tDefine o tamanho da particao (deve ser potencia de 2)");
        System.out.println();
        System.out.println("PV\t\tPARTICIONAMENTO VARIAVEL");
        System.out.println("politica_de_alocacao\tDefine a politica de alocacao:");
        System.out.println("\t\t\tFF\tFITST-FIT");
        System.out.println("\t\t\tWF\tWORST-FIT");
        System.out.println();
        System.out.println("-M\t\tDefine o tamanho da memoria");
        System.out.println("\t\ttamanho_da_memoria\tDefine o tamanho em posicoes de memoria (deve ser potencia de 2)");
        System.out.println();
        System.out.println("-A\t\tDefine o arquivo");
        System.out.println("\tnome_do_arquivo\tDefine o nome do arquivo (case-sensitive)");
        System.out.println();
        System.out.println("-V\t\tHabilita o MODO VERBOSO");
	}

    public static void imprimeLinhaDeComando()
    {
        System.out.println("\nUSO: java -jar OS.jar [-T | -P [-PF tamanho_da_particao | -PV politica_de_alocacao [FF | WF]] -M tamanho_da_memoria -A nome_do_arquivo [-V]]");
    }

}
