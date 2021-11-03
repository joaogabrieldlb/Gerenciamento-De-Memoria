import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Programa
{
    protected List<String> codigoFonte = new ArrayList<>();
    
    public Programa(Path arquivoDoPrograma) throws IOException
    {
        this.codigoFonte = Files.readAllLines(arquivoDoPrograma, StandardCharsets.UTF_8);
    }

    public List<String> getCodigoFonte() {
        return Collections.unmodifiableList(codigoFonte);
    }
}
