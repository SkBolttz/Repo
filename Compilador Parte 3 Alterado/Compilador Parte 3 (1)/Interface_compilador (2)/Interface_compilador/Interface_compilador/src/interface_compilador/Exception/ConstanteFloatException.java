package interface_compilador.Exception;

public class ConstanteFloatException extends RuntimeException {

    private String erro;
    private String detalhe;

    public ConstanteFloatException(String erro, String detalhe) {
        this.erro = erro;
        this.detalhe = detalhe;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }
}
