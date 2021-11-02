# TP2 - Projeto Gerenciamento de Memória

Trabalho de desenvolvimento de um OS com políticas de alocação de espaços de memória para processos, para a disciplina de Sistemas Operacionais do curso de Sistemas de Informação da PUCRS.

## Autores

Alunos:
- João Gabriel Dalla Lasta Bergamaschi
- Rafael Dias Coll Oliveira

Prof. Fabiano Passuelo Hessel

Sistemas Operacionais - Turma 010

## Preparação

Para execução do programa é necessário possuir instalado a plataforma Java JRE (Java Runtime Environment) na versão 8 ou superior.

Para compilação do programa é necessário possuir instalado o JDK (Java Development Kit) na versão 8 ou superior.

Os programas para execução deverão ser colocados na pasta `apps`.

## Instruções

Para executar o OS, executar no terminal do sistema operacional o seguinte comando:
> `java -jar OS.jar [-T | -P politica -L lista_de_programas [arrival_time] [prioridade] [-V]]]`

Para recompilar o projeto, executar o seguinte script no terminal do sistema operacional (necessita JDK instalado):
> `./compila.sh`

### Parâmetros da linha de comando:

> USO: `java -jar OS.jar [-T | -P politica -L lista_de_programas [arrival_time] [prioridade] [-V]]]`

`-T`              Executa o MODO DE TESTE (ignora demais argumentos)

`-P`              Define a politica de escalonamento

`politica`        Escolhe a politica de escalonamento:
+ `PP`             PRIORIDADE COM PREEMPCAO
+ `RR quantum`     ROUND ROBIN com quantum
    + `quantum`     Define numero de passos executado por cada processo

`-L`              Define a lista de programas a ser executado

+ `lista_de_programas [arrival_time] [prioridade]`
    Indica o(s) programa(s) a ser(em) carregado(s)

    + `arrival_time` Define o tempo de chegada de cada processo (tempo do passos de execucao do OS)
        + `indefinido` tempo de execucao padrao (0)

    + `prioridade`   Define a prioridade de execucao de cada processo (requer politica PP):
        + `0`          prioridade ALTA
        + `1`          prioridade MEDIA
        + `2`          prioridade BAIXA
        + `indefinida` prioridade padrao (2)

`-V`              Habilita o MODO VERBOSO

Exemplos:
> `java -jar OS.jar -t`
> 
> Executa o OS em MODO DE TESTE

> `java -jar OS.jar -p pp -l teste.txt 1 -v`
> 
> Executa o OS com a política de PRIORIDADE COM PREEMPÇÃO, o programa "teste.txt" (presente na pasta `apps`), com tempo de chegada no passo de execução 1, com a prioridade padrão e no MODO VERBOSO
