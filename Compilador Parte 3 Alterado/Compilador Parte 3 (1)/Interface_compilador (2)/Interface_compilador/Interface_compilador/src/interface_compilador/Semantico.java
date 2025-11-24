package interface_compilador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

public class Semantico implements Constants {

    Stack<String> PilhaTipo = new Stack<>();
    Stack<String> PilhaOperador = new Stack<>();
    String codigo;

    public void executeAction(int action, Token token) throws SemanticError {
        switch (action) {
            case 100:
                callAcao100();
                break;
            case 101:
                callAcao101();
                break;
            case 102:
                callAcao102();
                break;
            case 103:
                callAcao103(token);
                break;
            case 104:
                callAcao104(token);
                break;
            case 105:
                callAcao105(token);
                break;
            case 106:
                callAcao106(token);
                break;
            case 107:
                callAcao107(token);
                break;
            case 108:
                callAcao108(token);
                break;
            case 109:
                callAcao109(token);
                break;
            case 110:
                callAcao110(token);
                break;
            case 111:
                callAcao111(token);
                break;
            case 112:
                callAcao112(token);
                break;
            case 113:
                callAcao113(token);
                break;
            case 114:
                callAcao114(token);
                break;
            case 115:
                callAcao115(token);
                break;
            case 116:
                callAcao116(token);
                break;
            case 117:
                callAcao117(token);
                break;
            case 118:
                callAcao118(token);
                break;
            case 119:
                callAcao119(token);
                break;
            case 120:
                callAcao120(token);
                break;
            case 121:
                callAcao121(token);
                break;
            case 122:
                callAcao122(token);
                break;
            case 123:
                callAcao123(token);
                break;
            case 124:
                callAcao124(token);
                break;
            case 125:
                callAcao125(token);
                break;
            case 126:
                callAcao126(token);
                break;
            case 127:
                callAcao127(token);
                break;
            case 128:
                callAcao128(token);
                break;
            case 129:
                callAcao129(token);
                break;
            case 130:
                callAcao130(token);
                break;
            default:
                PilhaTipo.push(codigo);
        }
        // System.out.println("Ação #"+action+", Token: "+token);
    }

    public void callAcao100() {
        String codigo = (".assembly extern mscorlib {}\n" +
                ".assembly _programa{}\n" +
                ".module _programa.exe\n" +
                "\n" +
                ".class public _unica{\n" +
                "    .method static public void _principal(){\n" +
                "        .entrypoint");

        escreverLinha(codigo);
    }

    public void callAcao101() {
        String codigo = ("ret\n" +
                "    }\n" +
                "}");

        escreverLinha(codigo);
    }

    public void callAcao102() {

    }

    public void callAcao103(Token token) {

        PilhaTipo.push("int64");
        String codigo = "ldc.i8 " + token.getLexeme() + "\n" +
                "conv.r8";

        escreverLinha(codigo);
    }

    public void callAcao104(Token token) {
        PilhaTipo.push("float64");
        String codigo = "ldc.r8 " + token.getLexeme();
        escreverLinha(codigo);
    }

    public void callAcao105(Token token) {
        PilhaTipo.push(token.getLexeme());
        String codigo = "ldstr " + token.getLexeme();
        escreverLinha(codigo);
    }

    public void callAcao106(Token token) {
        String tipo2 = PilhaTipo.pop();
        String tipo1 = PilhaTipo.pop();
        String codigo = "";

        if (tipo1.equals("int64") && tipo2.equals("int64")) {
            PilhaTipo.push("int64");
            codigo = "add";
        } else if ((tipo1.equals("int64") && tipo2.equals("float64")) ||
                (tipo1.equals("float64") && tipo2.equals("int64")) ||
                (tipo1.equals("float64") && tipo2.equals("float64"))) {
            PilhaTipo.push("float64");
            if (tipo1.equals("int64")) {
                escreverLinha("conv.r8");
            }
            if (tipo2.equals("int64")) {
                escreverLinha("conv.r8");
            }
            codigo = "add";
        }
        escreverLinha(codigo);
    }

    public void callAcao107(Token token) {
        String tipo2 = PilhaTipo.pop();
        String tipo1 = PilhaTipo.pop();
        String codigo = "";

        if (tipo1.equals("int64") && tipo2.equals("int64")) {
            PilhaTipo.push("int64");
            codigo = "sub";
        } else if ((tipo1.equals("int64") && tipo2.equals("float64")) ||
                (tipo1.equals("float64") && tipo2.equals("int64")) ||
                (tipo1.equals("float64") && tipo2.equals("float64"))) {
            PilhaTipo.push("float64");
            if (tipo1.equals("int64")) {
                escreverLinha("conv.r8");
            }
            if (tipo2.equals("int64")) {
                escreverLinha("conv.r8");
            }
            codigo = "sub";
        }
        escreverLinha(codigo);
    }

    public void callAcao108(Token token) {
        String tipo2 = PilhaTipo.pop();
        String tipo1 = PilhaTipo.pop();
        String codigo = "";

        if (tipo1.equals("int64") && tipo2.equals("int64")) {
            PilhaTipo.push("int64");
            codigo = "mul";
        } else if ((tipo1.equals("int64") && tipo2.equals("float64")) ||
                (tipo1.equals("float64") && tipo2.equals("int64")) ||
                (tipo1.equals("float64") && tipo2.equals("float64"))) {
            PilhaTipo.push("float64");
            if (tipo1.equals("int64")) {
                escreverLinha("conv.r8");
            }
            if (tipo2.equals("int64")) {
                escreverLinha("conv.r8");
            }
            codigo = "mul";
        }
        escreverLinha(codigo);
    }

    public void callAcao109(Token token) {
        String tipo2 = PilhaTipo.pop();
        String tipo1 = PilhaTipo.pop();
        String codigo = "";

        if (tipo1.equals("int64") && tipo2.equals("int64")) {
            PilhaTipo.push("int64");
            codigo = "div";
        } else if ((tipo1.equals("int64") && tipo2.equals("float64")) ||
                (tipo1.equals("float64") && tipo2.equals("int64")) ||
                (tipo1.equals("float64") && tipo2.equals("float64"))) {
            PilhaTipo.push("float64");
            if (tipo1.equals("int64")) {
                escreverLinha("conv.r8");
            }
            if (tipo2.equals("int64")) {
                escreverLinha("conv.r8");
            }
            codigo = "div";
        }
        escreverLinha(codigo);
    }

    public void callAcao110(Token token) {
        String tipo1 = PilhaTipo.pop();
        String codigo = "";

        if (tipo1.equals("int64")) {
            PilhaTipo.push("int64");
            codigo = "ldc.i8 -1\n" +
                    "conv.r8\n" +
                    "mul";
        } else if (tipo1.equals("float64")) {
            PilhaTipo.push("float64");
            codigo = "ldc.i8 -1\n" +
                    "conv.r8\n" +
                    "mul";
        }
        escreverLinha(codigo);
    }

    public void callAcao111(Token token) {
        String operador = token.getLexeme();
        PilhaOperador.push(operador);
    }

    public void callAcao112(Token token) {
        String tipo1 = PilhaTipo.pop();
        String tipo2 = PilhaTipo.pop();
        String codigo = "";
        String operador = PilhaOperador.pop();

        if((tipo1.equals("int64") && tipo2.equals("int64")) ||
           (tipo1.equals("float64") && tipo2.equals("float64")) ||
           (tipo1.equals("int64") && tipo2.equals("float64")) ||
           (tipo1.equals("float64") && tipo2.equals("int64")) ) {
            PilhaTipo.push("bool");
            switch (operador) {
                case "<":
                    codigo = "ceq";
                    break;
                case ">":
                    codigo = "ceq";
                    break;
                case "==":
                    codigo = "ceq";
                    break;
            }
        }
    }

    //para os operadores lógicos binários (ações #113, #114):
    //desempilhar dois tipos da pilha_tipos, empilhar o tipo resultante da operação conforme indicado na TABELA DE
    //TIPOS;
    //gerar código objeto para efetuar a operação correspondente. 
    public void callAcao113(Token token) {
        String tipo2 = PilhaTipo.pop();
        String tipo1 = PilhaTipo.pop();
        
    }

    public void callAcao114(Token token) {

    }

    public void callAcao115(Token token) {

    }

    public void callAcao116(Token token) {

    }

    public void callAcao117(Token token) {

    }

    public void callAcao118(Token token) {

    }

    public void callAcao119(Token token) {

    }

    public void callAcao120(Token token) {

    }

    public void callAcao121(Token token) {

    }

    public void callAcao122(Token token) {

    }

    public void callAcao123(Token token) {

    }

    public void callAcao124(Token token) {

    }

    public void callAcao125(Token token) {

    }

    public void callAcao126(Token token) {

    }

    public void callAcao127(Token token) {

    }

    public void callAcao128(Token token) {

    }

    public void callAcao129(Token token) {

    }

    public void callAcao130(Token token) {

    }

    private void escreverLinha(String string) {
        File arquivo = new File("teste.il");
        try {
            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo, true))) {
                pw.println(string);
                pw.println();
            }
        } catch (IOException e) {
            System.out.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }
}
