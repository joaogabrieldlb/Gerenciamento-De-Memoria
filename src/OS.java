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
        // TODO: Reescrever help
        System.out.println("Inicia o OS.");
        imprimeLinhaDeComando();
        System.out.println();
        System.out.println("-T\t\tExecuta o MODO DE TESTE (ignora demais argumentos)");
        System.out.println();
        System.out.println("-P\t\tDefine a politica de escalonamento");
        System.out.println("politica\tEscolhe a politica de escalonamento:");
        System.out.println("PP\t\tPRIORIDADE COM PREEMPCAO");
        System.out.println("RR quantum\tROUND ROBIN com quantum");
        System.out.println("quantum\t\tDefine numero de passos executado por cada processo");
        System.out.println();
        System.out.println("-L\t\tDefine a lista de programas a ser executado");
        System.out.println("lista_de_programas [arrival_time] [prioridade]\n\t\tIndica o(s) programa(s) a ser(em) carregado(s)");
        System.out.println();
        System.out.println("arrival_time\tDefine o tempo de chegada de cada processo (tempo do passos de execucao do OS)");
        System.out.println();
        System.out.println("prioridade\tDefine a prioridade de execucao de cada processo (requer politica PP):");
        System.out.println("0\t\tprioridade ALTA");
        System.out.println("1\t\tprioridade MEDIA");
        System.out.println("2\t\tprioridade BAIXA");
        System.out.println();
        System.out.println("-V\t\tHabilita o MODO VERBOSO");
	}

    public static void imprimeLinhaDeComando()
    {
        System.out.println("\nUSO: java -jar OS.jar [-T | -P [-PF tamanho_da_particao | -PV politica_de_alocacao [FF | WF]] -M tamanho_da_memoria -A nome_do_arquivo [-V]]");
    }

}
