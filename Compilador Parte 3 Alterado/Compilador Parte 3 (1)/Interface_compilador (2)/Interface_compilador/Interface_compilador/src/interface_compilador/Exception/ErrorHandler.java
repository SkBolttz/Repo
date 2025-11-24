package interface_compilador.Exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorHandler {
    private static final Map<Integer, String> ERROR_MAPPING = new HashMap<>();
    
    static {
        // Mapeamento de códigos de erro para mensagens formatadas
        ERROR_MAPPING.put(0, "fim de programa");
        ERROR_MAPPING.put(1, "id");
        ERROR_MAPPING.put(2, "\"+\"");
        ERROR_MAPPING.put(3, "Const_int");
        ERROR_MAPPING.put(4, "Const_float");
        ERROR_MAPPING.put(5, "Const_string");
        // Continue para todos os tokens...
    }
    
    public static String getExpectedSymbols(int errorCode) {
        return ERROR_MAPPING.getOrDefault(errorCode, "símbolo");
    }
    
    public static String formatTokenForError(String token) {
        if (token == null || token.isEmpty()) return "EOF";
        if (token.equals("$")) return "EOF";
        if (token.startsWith("\"") && token.endsWith("\"")) return "constante_string";
        if (token.matches("\\d+")) return "constante_int";
        if (token.matches("\\d+\\.\\d+")) return "constante_float";
        return token;
    }
}