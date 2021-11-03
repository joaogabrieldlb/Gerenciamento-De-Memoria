import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Kernel
{
    // Definicao da politica de alocacao
    enum TipoDeParticao { PARTICOES_FIXAS, PARTICOES_VARIAVEIS };
    enum PoliticaDeAlocacao { FIRST_FIT, WORST_FIT };
    private TipoDeParticao tipoDeParticionamento;
    
    private long pidCounter = 1;
    private String appsPath;
    
    private List<Processo> memoriaLista = new ArrayList<>();
    private String[] memoria;
    private final int tamanhoMemoria;
    
    // Caso a politica de alocacao for Particoes Fixas
    private int tamanhoDaParticao;
    
    // Caso a politica de alocacao for Paricoes Variaveis
    private PoliticaDeAlocacao politicaDeAlocacao;

    private int passoDeExecucao = 0;

    private Programa arquivoDeExecucao;
    private List<Processo> listaDeProcessos = new ArrayList<>();

    public Kernel(String[] args)
    {
        this.appsPath = OS.appsDirectory;
        // Verificacao de existencia de argumentos
        if (args.length == 0)
        {
            throw new ExceptionInInitializerError("OS sem argumentos. Saindo.");
        }
        
        // Tratamento de argumentos para UpperCase
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].startsWith("-")) args[i] = args[i].toUpperCase();
        }
        List<String> listaDeParametros = new ArrayList<>(Arrays.asList(args));

        int indexV = listaDeParametros.indexOf("-V");
        if (indexV >= 0)
        {
            OS.verbose = true;
            System.out.println("=============== MODO VERBOSO ===============");
        }

        // VERBOSE
        if (OS.verbose) System.out.println("ARGS: "+ listaDeParametros);
        
        // VERBOSE - mostra index do -m
        if (OS.verbose) System.out.println("INDICE DO PARAMETRO -M: " + listaDeParametros.indexOf("-M"));

        // Definicao do tamanho da memoria (-T)
        int indexM = listaDeParametros.indexOf("-M");
        if (indexM < 0)
        {
            throw new InvalidParameterException("Parametro -m nao encontrado.");
        }
        // Configuracao do tamanho da memoria
        String complementoM;
        try {
            complementoM = listaDeParametros.get(indexM + 1);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidParameterException("Parametro -m sem complemento.");
        }
        // Verifica se o tamanho e potencia de 2
        try {
            tamanhoMemoria = Integer.parseInt(complementoM);
            if ((tamanhoMemoria != 0) && ((tamanhoMemoria & (tamanhoMemoria - 1)) == 0))
            {
                this.memoria = new String[tamanhoMemoria];
            }
            else
            {
                throw new InvalidParameterException("Tamanho da memoria invalido.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Tamanho de memoria deve ser um numero inteiro.");
        }

        // Definicao do tipo de particionamento (-P)
        int indexP = listaDeParametros.indexOf("-P");
        if (indexP < 0)
        {
            throw new InvalidParameterException("Parametro -p nao encontrado.");
        }
        // Configuracao do tipo de particionamento 
        String complementoP;
        try {
            complementoP = listaDeParametros.get(indexP + 1);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidParameterException("Parametro -p sem complemento.");
        }
        switch (complementoP)
        {
            case "pf":
            case "PF":
                this.tipoDeParticionamento = TipoDeParticao.PARTICOES_FIXAS;
                // VERBOSE
                if (OS.verbose) System.out.println("TIPO DE PARTIOCIONAMENTO: " + this.tipoDeParticionamento);
                // Configuracao do tamanho da particao de memoria
                try {
                    this.tamanhoDaParticao = Integer.parseInt(listaDeParametros.get(indexP + 2));
                    // VERBOSE
                    if (OS.verbose) System.out.println("VALOR DO TAMANHO DA PARTICAO: " + this.tamanhoDaParticao);
                    if ((tamanhoDaParticao <= 0) || ((tamanhoDaParticao & (tamanhoDaParticao - 1)) != 0)) throw new InvalidParameterException("Valor deve ser potencia de 2.");
                    if (tamanhoDaParticao > this.tamanhoMemoria) throw new InvalidParameterException("Valor nao pode ser maior que o tamanho da memoria.");
                } catch (Exception e) {
                    throw new InvalidParameterException("Tamanho da particao invalido. " + e.getMessage());
                }
                break;
            case "pv":
            case "PV":
                this.tipoDeParticionamento = TipoDeParticao.PARTICOES_VARIAVEIS;
                // VERBOSE
                if (OS.verbose) System.out.println("TIPO DE PARTICIONAMENTO: " + this.tipoDeParticionamento);
                // Configuracao da politica de alocacao
                switch (listaDeParametros.get(indexP + 2))
                {
                    case "ff":
                    case "FF":
                        this.politicaDeAlocacao = PoliticaDeAlocacao.FIRST_FIT;
                        // VERBOSE
                        if (OS.verbose) System.out.println("POLITICA DE ALOCACAO: " + this.politicaDeAlocacao);
                        break;
                    case "wf":
                    case "WF":
                        this.politicaDeAlocacao = PoliticaDeAlocacao.WORST_FIT;
                        // VERBOSE
                        if (OS.verbose) System.out.println("POLITICA DE ALOCACAO: " + this.politicaDeAlocacao);
                        break;
                    default:
                        throw new InvalidParameterException("Politica de alocacao invalida.");
                }
                break;
            default:
                throw new InvalidParameterException("Parametro -p com complemento invalido: \"" + complementoP + "\"");
        }
        
        // Definicao do arquivo de execucao (-A)
        int indexA = listaDeParametros.indexOf("-A");
        if (indexA < 0)
        {
            throw new InvalidParameterException("Parametro -a nao encontrado.");
        }
        // VERBOSE
        if (OS.verbose) System.out.println("INDICE DO PARAMETRO -A: " + indexA);

        // Configuracao do programa de execucao
        String complementoA;
        try {
            complementoA = listaDeParametros.get(indexP + 1);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidParameterException("Parametro -a sem complemento.");
        }
        // VERBOSE
        if (OS.verbose) System.out.println("ARQUIVO DE EXECUCAO: " + complementoA);


        if (Files.isReadable(Paths.get(appsPath, complementoA)))
        {
            // VERBOSE
            if (OS.verbose) System.out.println("ARQUIVO ACESSADO: " + complementoA);
            Path arquivoDoPrograma = Paths.get(appsPath, complementoA);

            try {
                this.arquivoDeExecucao = new Programa(arquivoDoPrograma);
            } catch (Exception e) {
                System.err.println("Erro ao carregar \"" + complementoA + "\": " + e.getMessage());
            }
        }
        else
        {
            System.err.println("Erro ao carregar \"" + complementoA + "\": arquivo inexistente.");
        }
    }

    public void run()
    {
        // VERBOSE
        if (OS.verbose) System.out.println("INICIANDO EXECUCAO DO PROGRAMA.");
        
        for (String linhaDeCodigo : this.arquivoDeExecucao.getCodigoFonte()) {
            // VERBOSE
            if (OS.verbose) System.out.println("EXECUTANDO COMANDO: " + linhaDeCodigo);
            processaLinha(linhaDeCodigo);
        }


    }

    public void processaLinha(String linhaDeCodigo)
    {
        int posicaoInicial = linhaDeCodigo.indexOf("(");
        int posicaoFinal = linhaDeCodigo.indexOf(")");
        String comando = linhaDeCodigo.substring(0, posicaoInicial).trim().toUpperCase();
        String[] parametros = linhaDeCodigo.substring(posicaoInicial + 1 , posicaoFinal).replace(" ", "").split(",");

        switch (comando)
        {
            case "IN":
                this.processaComandoIN(parametros);
                break;
            case "OUT":
                this.processaComandoOUT(parametros);
                break;
            default:
                System.err.println("Comando invalido: " + comando);
        }
    }

    private void processaComandoOUT(String[] parametros) {
    }

    private void processaComandoIN(String[] parametros) {
        // VERBOSE
        if (OS.verbose) System.out.println("PROCESSANDO COMANDO IN.");
        if (OS.verbose) System.out.println("PARAMETROS: " + Arrays.toString(parametros));

        Processo novoProcesso = new Processo(parametros[0], Integer.parseInt(parametros[1]));
        this.listaDeProcessos.add(novoProcesso);

        
        // IN(A,4)
        // IN(B,9)
        // IN(C,12)
        // [ A | A | A | A || null | null | null | null || null | null | null | null || null | null | null | null ]
        // print: | 9 |


        
        switch (this.tipoDeParticionamento)
        {
            case PARTICOES_FIXAS:
                int particoesNecessarias = (int) Math.ceil(novoProcesso.getTamanho() / this.tamanhoDaParticao);
                int contaParticoesLivres = 0;
                boolean processoAlocado = false;
                for (int i = 0; i < this.tamanhoMemoria; i += this.tamanhoDaParticao)
                {
                    if (this.memoria[i] == null)
                    {
                        contaParticoesLivres++;
                        if (contaParticoesLivres == particoesNecessarias)
                        {
                            for (int j = i - ((contaParticoesLivres - 1) * this.tamanhoDaParticao); j < j + novoProcesso.getTamanho(); j++)
                            {
                                this.memoria[j] = novoProcesso.getNomeDoPrograma();
                                processoAlocado = true;
                            }
                            break;
                        }
                    }
                }
                if (!processoAlocado)
                {   
                    System.out.println("ESPACO INSUFICIENTE DE MEMORIA. Processo " + novoProcesso.getNomeDoPrograma() + " nao alocado.");
                }
                break;
            case PARTICOES_VARIAVEIS:
                
                break;        
        }
    }

    private void imprimeEstado()
    {
        System.out.println("=========== ESTADO DOS PROCESSOS =========== PASSO DE EXECUCAO DO OS = " + passoDeExecucao);
        listaDeProcessos.forEach(System.out::println);
        System.out.println("============================================");
        // VERBOSE
        if (OS.verbose) imprimeFilaDeProntos();
    }

    private void imprimeEstadoFinal()
    {
        System.out.println("============ ESTADO FINAL DO OS ============ ULTIMO PASSO DE EXECUCAO DO OS = " + passoDeExecucao);
        listaDeProcessos.forEach(p -> System.out.println(p.toString() + "\n" + p.tempoDeEstadoString()));
    }

    private void imprimeFilaDeProntos()
    {
        System.out.println("============= FILA DE PRONTOS ==============");
        switch(this.politicaDoEscalonador)
        {
            case PRIORIDADE_COM_PREEMPCAO:
                filaProcessosProntosAltaPrioridade.forEach(System.out::println);
                filaProcessosProntosMediaPrioridade.forEach(System.out::println);
                filaProcessosProntosBaixaPrioridade.forEach(System.out::println);
                break;
            case ROUND_ROBIN:
                filaProcessosProntosRR.forEach(System.out::println);
                break;
            default:
                break;
        }
        System.out.println("============================================");
    }
}