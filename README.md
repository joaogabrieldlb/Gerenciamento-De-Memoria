# TP2 - Projeto de Gerenciamento de Memória

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
> `java -jar OS.jar [-T | -P [-PF tamanho_da_particao | -PV politica_de_alocacao [FF | WF]] -M tamanho_da_memoria -A nome_do_arquivo [-V]]`

Para recompilar o projeto, executar o seguinte script no terminal do sistema operacional (necessita JDK instalado):
> `./compila.sh`

### Parâmetros da linha de comando:

> USO: `java -jar OS.jar [-T | -P [-PF tamanho_da_particao | -PV politica_de_alocacao [FF | WF]] -M tamanho_da_memoria -A nome_do_arquivo [-V]]`

`-T`                            Executa o MODO DE TESTE (ignora demais argumentos)

`-P`                            Define o tipo de particionamento:
+ `PF`                          PARTICIONAMENTO FIXO
    + `tamanho_da_particao`     Define o tamanho da particao (deve ser potencia de 2)
+ `PV`                          PARTICIONAMENTO VARIAVEL
    + `politica_de_alocacao`    Define a politica de alocacao:
        + `FF` FIRST-FIT
        + `WF` WORST-FIT

`-M`                            Define o tamanho da memoria
+ `tamanho_da_memoria`          Define o tamanho em posicoes de memoria (deve ser potencia de 2)

`-A`                            Define o arquivo
+ `nome_do_arquivo`             Define o nome do arquivo (case-sensitive)

`-V`                            Habilita o MODO VERBOSO

Exemplos:
> `java -jar OS.jar -t`
> 
> Executa o OS em MODO DE TESTE

> `java -jar OS.jar -p pf 4 -m 16 -a teste.txt -v`
> 
> Executa o OS com o tipo de PARTICIONAMENTO FIXO, define o tamanho da particao (4), o tamanho da memoria em posicoes de memoria (16), o programa "teste.txt" (presente na pasta `apps`) e executa em MODO VERBOSO

> `java -jar OS.jar -p pv wf -m 8 -a teste.txt -v`
> 
> Executa o OS com o tipo de PARTICIONAMENTO VARIAVEL, define a politica de alocacao (WORST_FIT), o tamanho da memoria em posicoes de memoria (8), o programa "teste.txt" (presente na pasta `apps`) e executa em MODO VERBOSO
