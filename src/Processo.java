public class Processo
{
    private String nomeDoPrograma;
    private int tamanho;
    private int posicaoDeMemoria;

    public Processo(String nomeDoPrograma, int tamanho) {
        this.nomeDoPrograma = nomeDoPrograma;
        this.tamanho = tamanho;
    }

    public int getPosicaoDeMemoria() {
        return posicaoDeMemoria;
    }

    public void setPosicaoDeMemoria(int posicaoDeMemoria) {
        this.posicaoDeMemoria = posicaoDeMemoria;
    }

    public String getNomeDoPrograma() {
        return nomeDoPrograma;
    }

    public int getTamanho() {
        return tamanho;
    }

    @Override
    public String toString() {
        return "[nomeDoPrograma=" + nomeDoPrograma + ", posicaoDeMemoria=" + posicaoDeMemoria + ", tamanho="
                + tamanho + "]";
    }
}
