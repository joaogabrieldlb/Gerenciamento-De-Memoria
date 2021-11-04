public class Processo
{
    private String nomeDoPrograma;
    private int tamanho;
    private int posicaoDeMemoria;
    private final long pid;

    public Processo(long pid, String nomeDoPrograma, int tamanho) {
        this.pid = pid;
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
        return "Processo [pid=" + pid + ", nomeDoPrograma=" + nomeDoPrograma + ", posicaoDeMemoria=" + posicaoDeMemoria + ", tamanho="
                + tamanho + "]";
    }
}
