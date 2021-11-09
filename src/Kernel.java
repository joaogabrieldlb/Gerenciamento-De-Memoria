import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kernel implements PrimitivasDeMemoria
{
    // private List<Processo> memoriaLista = new ArrayList<>();
    private final String[] memoria;
    private final int tamanhoMemoria;
    private final String appsPath;
    private Programa arquivoDeExecucao;
    private List<Processo> listaDeProcessos = new ArrayList<>();
    private int passoDeExecucao = 0;
    private long pidCounter = 1;


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
        
        // VERBOSE
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
            // VERBOSE
            if (OS.verbose) System.out.println("COMPLEMENTO DO PARAMETRO -M: " + complementoM);

        } catch (IndexOutOfBoundsException e) {
            throw new InvalidParameterException("Parametro -m sem complemento.");
        }
        // Verifica se o tamanho e potencia de 2
        try {
            this.tamanhoMemoria = Integer.parseInt(complementoM);
            if ((this.tamanhoMemoria != 0) && ((this.tamanhoMemoria & (this.tamanhoMemoria - 1)) == 0))
            {
                this.memoria = new String[this.tamanhoMemoria];
            }
            else
            {
                throw new InvalidParameterException("Tamanho da memoria deve ser potencia de 2.");
            }
            // VERBOSE
            if (OS.verbose) System.out.println("TAMANHO DA MEMORIA: " + tamanhoMemoria);
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
                        break;
                    case "wf":
                    case "WF":
                        this.politicaDeAlocacao = PoliticaDeAlocacao.WORST_FIT;
                        break;
                    default:
                        throw new InvalidParameterException("Politica de alocacao invalida.");
                }
                // VERBOSE
                if (OS.verbose) System.out.println("POLITICA DE ALOCACAO: " + this.politicaDeAlocacao);
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
        if (OS.verbose) System.out.println("\nINICIANDO EXECUCAO DO PROGRAMA");
        // imprime estado inicial da memoria
        System.out.println("ESTADO INICIAL DA MEMORIA");
        System.out.println("PASSO DE EXECUCAO DO OS = " + passoDeExecucao);
        this.imprimeEstado();
        // executa o programa
        for (String linhaDeCodigo : this.arquivoDeExecucao.getCodigoFonte()) {
            // aumenta passo de execucao
            passoDeExecucao++;
            System.out.println("PASSO DE EXECUCAO DO OS = " + passoDeExecucao);
            System.out.println("EXECUTANDO LINHA: " + linhaDeCodigo);
            this.processaLinha(linhaDeCodigo);
            // imprime estado
            this.imprimeEstado();
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
        if (OS.verbose) System.out.println("PROCESSANDO INSTRUCAO \"IN\"");
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

        Processo novoProcesso = new Processo(pidCounter, nomeDoProcesso, tamanhoDoProcesso);
        pidCounter++;
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
                                    posicaoInicial = i - (novoProcesso.getTamanho() - 1);
                                    for (int j = posicaoInicial; j < posicaoInicial + novoProcesso.getTamanho(); j++)
                                    {
                                        this.memoria[j] = novoProcesso.getNomeDoPrograma();
                                    }
                                    processoAlocado = true;
                                    break;
                                }
                            }
                            else
                            {
                                contaEspacosLivresFF = 0;
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
                                int posicaoInicialIndice = i;
                                for ( ; i < this.tamanhoMemoria; i++)
                                {
                                    if(this.memoria[i] == null)
                                    {
                                        contaEspacosLivresWF++;
                                    }
                                    else
                                    {
                                        break;
                                    }
                                }
                                if (contaEspacosLivresWF > tamanhoMaiorEspacoLivreWF)
                                {
                                    tamanhoMaiorEspacoLivreWF = contaEspacosLivresWF;
                                    indiceMaiorEspacoLivre = posicaoInicialIndice;
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
            System.out.println("ERRO> ESPACO INSUFICIENTE DE MEMORIA. Processo \"" + novoProcesso.getNomeDoPrograma() + "\" nao alocado na memoria.");
        }
    }
    
    public void comandoOUT(String parametro)
    {
        // VERBOSE
        if (OS.verbose) System.out.println("PROCESSANDO INSTRUCAO \"OUT\"");
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
            System.err.println("ERRO> PROCESSO NAO ENCONTRADO. Processo \"" + nomeDoProcesso + "\" nao encontrado alocado na memoria.");
        }
    }

    private void imprimeEstado()
    {
        System.out.println("=========== ESTADO DA MEMORIA ===========");
        switch (this.tipoDeParticionamento)
        {
            case PARTICOES_FIXAS:
                for (int i = 0; i < this.tamanhoMemoria; i++)
                {
                    if (i % this.tamanhoDaParticao == 0)
                    {
                        if (i != 0)
                        {
                            System.out.println();
                        }
                        System.out.print("Bloco #" + (i / this.tamanhoDaParticao) + ": ");
                    }
                    System.out.print("[");
                    if (this.memoria[i] != null)
                    {
                        System.out.print(this.memoria[i]);
                    }
                    else
                    {
                        System.out.print(" ");
                    }
                    System.out.print("] ");
                }
                System.out.println();
                break;
            case PARTICOES_VARIAVEIS:
                for (int i = 0; i < this.tamanhoMemoria; i++)
                {
                    System.out.print("[");
                    if (this.memoria[i] != null)
                    {
                        System.out.print(this.memoria[i]);
                    }
                    else
                    {
                        System.out.print(" ");
                    }
                    System.out.print("] ");
                }
                System.out.println();
                break;
        }

        imprimeResumoMemoriaLivre();
        // VERBOSE
        if (OS.verbose) imprimeDetalhesMemoria();

        System.out.println("=========================================");
    }

    private void imprimeResumoMemoriaLivre()
    {
        System.out.println("======== RESUMO DA MEMORIA LIVRE ========");
        System.out.print("| ");
        int contEspacosLivres = 0;
        for (int i = 0; i < this.tamanhoMemoria; i++)
        {
            if (this.memoria[i] == null)
            {
                contEspacosLivres++;
            }
            else
            {
                if (contEspacosLivres > 0)
                {
                    System.out.print(contEspacosLivres + " | ");
                    contEspacosLivres = 0;
                }
            }
        }
        if (contEspacosLivres != 0)
        {
            System.out.print(contEspacosLivres);
        }
        System.out.println(" |");
    }

    private void imprimeDetalhesMemoria()
    {
        System.out.println("========== DETALHES DA MEMORIA ==========");
        // tamanho total
        System.out.println("TAMANHO TOTAL = " + this.tamanhoMemoria + " posicoes");
        
        // tamanho ocupada
        long memoriaOcupada = Arrays.stream(this.memoria).filter(p -> p != null).count();
        System.out.println("MEMORIA OCUPADA = " + (memoriaOcupada) + " posicoes");
        // memoria livre
        System.out.println("MEMORIA LIVRE = " + (this.tamanhoMemoria - memoriaOcupada) + " posicoes");

        // blocos livres
        switch (this.tipoDeParticionamento)
        {
            case PARTICOES_FIXAS:
                long blocosDeMemoriaLivre = 0;
                String listaBlocosDeMemoriaLivre = "[";
                for (int i = 0; i < tamanhoMemoria; i += tamanhoDaParticao)
                {
                    if (this.memoria[i] == null)
                    {
                        if (listaBlocosDeMemoriaLivre.length() > 1)
                        {
                            listaBlocosDeMemoriaLivre += ", ";
                        }
                        listaBlocosDeMemoriaLivre += "Bloco #" + blocosDeMemoriaLivre;
                        blocosDeMemoriaLivre++;
                    }
                }
                listaBlocosDeMemoriaLivre += "]";
                System.out.print("BLOCOS DE MEMORIA LIVRE: " + blocosDeMemoriaLivre);
                System.out.println(" " + listaBlocosDeMemoriaLivre);
                
                break;
            case PARTICOES_VARIAVEIS:
                Map<Integer, Integer> mapaDeBlocosDeMemoriaLivre = new HashMap<>();
                for (int i = 0; i < this.tamanhoMemoria; i++)
                {
                    if(this.memoria[i] == null)
                    {
                        int contaEspacosLivres = 0;
                        int posicaoInicialIndice = i;
                        for ( ; i < this.tamanhoMemoria; i++)
                        {
                            if(this.memoria[i] == null)
                            {
                                contaEspacosLivres++;
                            }
                            else
                            {
                                break;
                            }
                        }
                        mapaDeBlocosDeMemoriaLivre.put(posicaoInicialIndice, contaEspacosLivres);
                    }
                }
                System.out.print("BLOCOS DE MEMORIA LIVRE: " + mapaDeBlocosDeMemoriaLivre.size() + " ");
                int counter = 0;
                for (Map.Entry<Integer, Integer> entry : mapaDeBlocosDeMemoriaLivre.entrySet())
                {
                    System.out.print("[#" + counter + ": posicao=" + entry.getKey() + ", tamanho=" + entry.getValue() + "] ");
                    counter++;
                }
                System.out.println();
            break;
        }

        // processos
        System.out.println("LISTA DE PROCESSOS ALOCADOS:");
        listaDeProcessos.sort((p1, p2) -> p1.getPosicaoDeMemoria() - p2.getPosicaoDeMemoria());
        if (listaDeProcessos.size() > 0)
        {
            listaDeProcessos.forEach(p -> System.out.println(p.toString()));
        }	
        else
        {
            System.out.println("Nenhum processo alocado.");
        }
    }
}