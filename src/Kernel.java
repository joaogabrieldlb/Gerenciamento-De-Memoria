import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kernel implements PrimitivasMemoria
{
    // private List<Processo> memoriaLista = new ArrayList<>();
    private final String[] memoria;
    private final int tamanhoMemoria;
    private String appsPath;
    private Programa arquivoDeExecucao;
    private List<Processo> listaDeProcessos = new ArrayList<>();
    private int passoDeExecucao = 1;

    // Definicao da politica de alocacao
    enum TipoDeParticao { PARTICOES_FIXAS, PARTICOES_VARIAVEIS };
    enum PoliticaDeAlocacao { FIRST_FIT, WORST_FIT };
    private TipoDeParticao tipoDeParticionamento;
    
    // Caso a politica de alocacao for Particoes Fixas
    private int tamanhoDaParticao;
    
    // Caso a politica de alocacao for Paricoes Variaveis
    private PoliticaDeAlocacao politicaDeAlocacao;

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

        // Definicao do tamanho da memoria (-M)
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
                throw new InvalidParameterException("Tamanho da memoria deve ser potencia de 2.");
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
            complementoA = listaDeParametros.get(indexA + 1);
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
            this.processaLinha(linhaDeCodigo);
            // imprime estado
            this.imprimeEstado();

            // IN(A,4)
            // IN(B,9)
            // IN(C,12)
            // [ A | A | A | A || null | null | null | null || null | null | null | null || null | null | null | null ]
            // print: | 9 |

            // aumenta passo
            passoDeExecucao++;
        }


    }

    private void processaLinha(String linhaDeCodigo)
    {
        int posicaoInicial = linhaDeCodigo.indexOf("(");
        int posicaoFinal = linhaDeCodigo.indexOf(")");
        String comando = linhaDeCodigo.substring(0, posicaoInicial).trim().toUpperCase();
        String parametros = linhaDeCodigo.substring(posicaoInicial + 1 , posicaoFinal);

        switch (comando)
        {
            case "IN":
                this.comandoIN(parametros);
                break;
            case "OUT":
                this.comandoOUT(parametros);
                break;
            default:
                System.err.println("Comando invalido: " + comando);
        }
    }

    public void comandoIN(String parametros)
    {
        // VERBOSE
        if (OS.verbose) System.out.println("PROCESSANDO COMANDO IN.");
        if (OS.verbose) System.out.println("PARAMETROS: " + parametros);
        
        String[] argumentos = parametros.replace(" ", "").split(",");
        String nomeDoProcesso = argumentos[0];
        int tamanhoDoProcesso;
        try {
            tamanhoDoProcesso = Integer.parseInt(argumentos[1]);
            if (tamanhoDoProcesso <= 0) throw new InvalidParameterException("Valor deve ser positivo.");
        } catch (Exception e) {
            System.err.println("Tamanho do processo invalido. " + e.getMessage());
            return;
        }

        Processo novoProcesso = new Processo(nomeDoProcesso, tamanhoDoProcesso);
        boolean processoAlocado = false;
        int posicaoInicial = 0;
        switch (this.tipoDeParticionamento)
        {
            case PARTICOES_FIXAS:
                int particoesNecessarias = (int) Math.ceil((double) novoProcesso.getTamanho() / this.tamanhoDaParticao);
                int contaParticoesLivres = 0;
                for (int i = 0; i < this.tamanhoMemoria; i += this.tamanhoDaParticao)
                {
                    if (this.memoria[i] == null)
                    {
                        contaParticoesLivres++;
                        if (contaParticoesLivres == particoesNecessarias)
                        {
                            posicaoInicial = i - (contaParticoesLivres - 1) * this.tamanhoDaParticao;
                            for (int j = posicaoInicial; j < posicaoInicial + novoProcesso.getTamanho(); j++)
                            {
                                this.memoria[j] = novoProcesso.getNomeDoPrograma();
                            }
                            processoAlocado = true;
                            break;
                        }
                    }
                }
                break;
            case PARTICOES_VARIAVEIS:
                switch (this.politicaDeAlocacao)
                {
                    case FIRST_FIT:
                        int contaEspacosLivresFF = 0;
                        for (int i = 0; i < this.tamanhoMemoria; i++)
                        {
                            if(this.memoria[i] == null)
                            {
                                contaEspacosLivresFF++;
                                if(contaEspacosLivresFF == novoProcesso.getTamanho())
                                {
                                    posicaoInicial = i - novoProcesso.getTamanho();
                                    for (int j = posicaoInicial; j < posicaoInicial + novoProcesso.getTamanho(); j++)
                                    {
                                        this.memoria[j] = novoProcesso.getNomeDoPrograma();
                                    }
                                    processoAlocado = true;
                                    break;
                                }
                            }
                        }
                        break;
                    case WORST_FIT:
                        int indiceMaiorEspacoLivre = 0;
                        int tamanhoMaiorEspacoLivreWF = 0;
                        for (int i = 0; i < this.tamanhoMemoria; i++)
                        {
                            if(this.memoria[i] == null)
                            {
                                int contaEspacosLivresWF = 0;
                                for (int j = i; j < this.tamanhoMemoria; j++)
                                {
                                    if(this.memoria[j] == null)
                                    {
                                        contaEspacosLivresWF++;
                                    }
                                    else
                                    {
                                        if (contaEspacosLivresWF > tamanhoMaiorEspacoLivreWF)
                                        {
                                            tamanhoMaiorEspacoLivreWF = contaEspacosLivresWF;
                                            indiceMaiorEspacoLivre = i;
                                        }
                                        i = j;
                                        break;
                                    }
                                }
                            }
                        }
                        if (tamanhoMaiorEspacoLivreWF >= novoProcesso.getTamanho())
                        {
                            posicaoInicial = indiceMaiorEspacoLivre;
                            for (int i = posicaoInicial; i < posicaoInicial + novoProcesso.getTamanho(); i++)
                            {
                                this.memoria[i] = novoProcesso.getNomeDoPrograma();
                            }
                            processoAlocado = true;
                        }
                        break;
                }
                break;
        }
        if (processoAlocado)
        {
            this.listaDeProcessos.add(novoProcesso);
            novoProcesso.setPosicaoDeMemoria(posicaoInicial);
        }
        else
        {
            System.out.println("ESPACO INSUFICIENTE DE MEMORIA. Processo " + novoProcesso.getNomeDoPrograma() + " nao alocado.");
        }
    }
    
    public void comandoOUT(String parametro)
    {
            // VERBOSE
            if (OS.verbose) System.out.println("PROCESSANDO COMANDO OUT.");
            if (OS.verbose) System.out.println("PARAMETRO: " + parametro);
            
            String nomeDoProcesso = parametro;
            Processo processo = this.listaDeProcessos.stream().filter(p -> p.getNomeDoPrograma().equals(nomeDoProcesso)).findFirst().orElse(null);
            if (processo != null)
            {
                for (int i = processo.getPosicaoDeMemoria(); i < processo.getPosicaoDeMemoria() + processo.getTamanho(); i++)
                {
                    this.memoria[i] = null;
                }
                this.listaDeProcessos.remove(processo);
            }
            else
            {
                System.err.println("PROCESSO NAO ENCONTRADO. Processo " + nomeDoProcesso + " nao encontrado.");
            }
    }

    private void imprimeEstado()
    {
        System.out.println("=========== ESTADO DA MEMORIA =========== PASSO DE EXECUCAO DO OS = " + passoDeExecucao);
        for (int i = 0; i < this.tamanhoMemoria; i++)
        {
            System.out.print("[ ");
            if (this.memoria[i] != null)
            {
                System.out.print(this.memoria[i] + " ");
            }
            else
            {
                System.out.print("- ");
            }
            System.out.print("] ");
        }
        System.out.println();
        // VERBOSE
        if (OS.verbose) imprimeDetalhesMemoria();
    }

    private void imprimeEstadoFinal()
    {
        System.out.println("========== ESTADO FINAL DO OS =========== ULTIMO PASSO DE EXECUCAO DO OS = " + passoDeExecucao);
        listaDeProcessos.forEach(p -> System.out.println(p.toString()));
    }

    private void imprimeDetalhesMemoria()
    {
        System.out.println("=========== DETALHES MEMORIA ============");
        // tamanho total
        // processos
        // memoria livre
        // blocos livres
        System.out.println("=========================================");
    }
}