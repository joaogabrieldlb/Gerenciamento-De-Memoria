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
> `java -jar OS.jar -P PF [tamanho_da_particao] | PV  [politica_de_alocacao] -M tamanho_da_memoria -A nome_do_arquivo`

Para recompilar o projeto, executar o seguinte script no terminal do sistema operacional (necessita JDK instalado):
> `./compila.sh`

### Parâmetros da linha de comando:

> USO: `java -jar OS.jar -P [PF tamanho_da_particao | PV politica_de_alocacao [FF | WF]] -M tamanho_da_memoria -A nome_do_arquivo`

`-P`                            Define o tipo de particionamento
+ `PF`                          Escolhe o particionamento fixo
    + `tamanho_da_particao`     Define o tamanho da particao (deve ser potencia de 2)
+ `PV`                          Escolhe o particionamento variavel
    + `politica_de_alocacao`    Define a politica de alocacao: 
        + `FF` politica First-fit
        + `WF` politica Worst-fit

`-M`                            Define o tamanho da memoria
+ `tamanho_da_memoria`          Define o tamanho em posicoes de memoria (deve ser potencia de 2)

`-A`                            Define o arquivo
+ `nome_do_arquivo`             Define o nome do arquivo (case-sensitive)

`-V`              Habilita o MODO VERBOSO

Exemplos:
> `java -jar OS.jar -t`
> 
> Executa o OS em MODO DE TESTE

> `java -jar OS.jar -p pp -l teste.txt 1 -v`
> 
> Executa o OS com a política de PRIORIDADE COM PREEMPÇÃO, o programa "teste.txt" (presente na pasta `apps`), com tempo de chegada no passo de execução 1, com a prioridade padrão e no MODO VERBOSO
