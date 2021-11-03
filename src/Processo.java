import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Processo processo = (Processo) o;
        return Objects.equals(nomeDoPrograma, processo.nomeDoPrograma);
    }

    @Override
    public String toString() {
        return "Processo [nomeDoPrograma=" + nomeDoPrograma + ", posicaoDeMemoria=" + posicaoDeMemoria + ", tamanho="
                + tamanho + "]";
    }
}
