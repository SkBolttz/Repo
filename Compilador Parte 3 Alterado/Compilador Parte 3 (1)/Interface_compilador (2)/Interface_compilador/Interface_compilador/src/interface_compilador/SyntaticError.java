package interface_compilador;

public class SyntaticError extends AnalysisError {
    public SyntaticError(String message, int position) {
        super(message, position);
    }
    
    // public SyntaticError(String msg)
    // {
    //     super(msg);
    // }
    
    public static SyntaticError criarComLinha(String message, int position, String programaFonte) {
        int linha = calcularLinha(position, programaFonte);
        return new SyntaticError("linha " + linha + ": " + message, position);
    }
    
    private static int calcularLinha(int position, String texto) {
        if (position < 0 || texto == null || texto.isEmpty())
            return 1;
        
        int linha = 1;
        for (int i = 0; i < position && i < texto.length(); i++) {
            if (texto.charAt(i) == '\n') {
                linha++;
            }
        }
        return linha;
    }
}