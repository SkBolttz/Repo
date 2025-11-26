package interface_compilador;

import java.util.Stack;
import java.util.HashMap;

public class Sintatico implements Constants {
    private Stack stack = new Stack();
    private Token currentToken;
    private Token previousToken;
    private Lexico scanner;
    private Semantico semanticAnalyser;

    private HashMap<String, Integer> tabelaTipos = new HashMap<>();
    private int tipoVariavelAtual = 0;
    private String nomeVariavelAtual = null;
    private boolean emContextoComando = false;
    private int ultimoComando = 0;

    private static final boolean isTerminal(int x) {
        return x < FIRST_NON_TERMINAL;
    }

    private static final boolean isNonTerminal(int x) {
        return x >= FIRST_NON_TERMINAL && x < FIRST_SEMANTIC_ACTION;
    }

    private static final boolean isSemanticAction(int x) {
        return x >= FIRST_SEMANTIC_ACTION;
    }

    private boolean step() throws LexicalError, SyntaticError, SemanticError {
        if (currentToken == null) {
            int pos = 0;
            if (previousToken != null)
                pos = previousToken.getPosition() + previousToken.getLexeme().length();
            currentToken = new Token(DOLLAR, "$", pos);
        }

        int x = ((Integer) stack.pop()).intValue();
        // int a = currentToken.getId();
        int tokenInput = currentToken.getId();

        // if (x == EPSILON)
        // {
        // return false;
        // }
        if (x == 22 || x == 27) {
            emContextoComando = true;
            ultimoComando = x;
        } else if (x == EPSILON || isInicioNovaInstrucao(x)) {
            emContextoComando = false;
            ultimoComando = 0;
        }

        if (isTerminal(x) && isTipoTerminal(x)) {
            tipoVariavelAtual = obterTipoDoTerminal(x);
        }

        if (isTerminal(x) && x == t_id && tipoVariavelAtual > 0) {
            nomeVariavelAtual = currentToken.getLexeme();
            tabelaTipos.put(nomeVariavelAtual, tipoVariavelAtual);
        }

        if (isTerminal(x) && x == tokenInput && isTokenConstante(tokenInput)) {
            if (!isEmContextoEspecifico()) {
                verificarCompatibilidadeConstante();
            }
        }

        if (x == EPSILON) {
            if (stack.empty() || isInicioNovaInstrucao((Integer) stack.peek())) {
                tipoVariavelAtual = 0;
                nomeVariavelAtual = null;
            }
            return false;
        }
        // else if (isTerminal(x))
        // {
        // if (x == a)
        // {
        // if (stack.empty())
        // return true;
        // else
        // {
        // previousToken = currentToken;
        // currentToken = scanner.nextToken();
        // return false;
        // }
        // }
        // else
        // {
        // throw new SyntacticError(PARSER_ERROR[x], currentToken.getPosition());
        // }
        // }
        else if (isTerminal(x)) {
            if (x == tokenInput) {
                if (stack.empty())
                    return true;
                else {
                    previousToken = currentToken;
                    currentToken = scanner.nextToken();
                    return false;
                }
            } else {
                String tokenEncontrado = formatarTokenEncontrado(currentToken, x);
                String simbolosEsperados = obterSimbolosEsperadosTerminal(x);

                throw new SyntaticError("encontrado " + tokenEncontrado + " esperado " + simbolosEsperados,
                        currentToken.getPosition());
            }
        } else if (isNonTerminal(x)) {
            if (pushProduction(x, tokenInput))
                return false;
            else {
                // ✅ DEBUG PERSONALIZADO PARA O CASO 53 - COLOCAR AQUI
                if (x == 53) { // <lista_identificadores>
                    System.out.println("=== DEBUG LISTA_IDENTIFICADORES ===");
                    System.out.println("Token atual: " + currentToken.getLexeme() + " (ID: " + tokenInput + ")");
                    System.out.println("Previous token: " + (previousToken != null
                            ? previousToken.getLexeme() + " (ID: " + previousToken.getId() + ")"
                            : "null"));
                    System.out.println("tipoVariavelAtual: " + tipoVariavelAtual);
                }

                String tokenEncontrado = formatarTokenEncontrado(currentToken, 0);
                String simbolosEsperados = obterSimbolosEsperadosNaoTerminal(x, tokenInput);

                System.out.println("=== DEBUG ERRO ===");
                System.out.println("Não-terminal: " + x + " (" + PARSER_ERROR[x] + ")");
                System.out.println("Token encontrado: " + currentToken.getLexeme() + " (ID: " + tokenInput + ")");
                System.out.println("Pilha atual: " + stack);
                System.out.println("Posição: " + currentToken.getPosition());

                throw new SyntaticError("encontrado " + tokenEncontrado + " esperado " + simbolosEsperados,
                        currentToken.getPosition());
            }
        } else {
            semanticAnalyser.executeAction(x - FIRST_SEMANTIC_ACTION, previousToken);
            return false;
        }
    }

    private boolean isEmContextoEspecifico() {
        return stack.contains(35) ||
                stack.contains(59) ||
                stack.contains(65) ||
                stack.contains(44) ||
                stack.contains(58);
    }

    private void verificarCompatibilidadeConstante() throws SyntaticError {
        if (nomeVariavelAtual == null)
            return;

        Integer tipoVariavel = tabelaTipos.get(nomeVariavelAtual);
        if (tipoVariavel == null)
            return;

        int tipoConstante = obterTipoDoToken(currentToken);
        if (tipoConstante == 0)
            return;

        if (tipoVariavel == 1 && tipoConstante != 1) {
            if (tipoConstante == 3) {
                throw new SyntaticError(
                        "encontrado " + formatarTokenEncontrado(currentToken, t_Const_int) + " esperado constante_int",
                        currentToken.getPosition());
            } else if (tipoConstante == 2) {
                throw new SyntaticError("encontrado " + currentToken.getLexeme() + " esperado constante_int",
                        currentToken.getPosition());
            }
        } else if (tipoVariavel == 2 && tipoConstante != 2) {
            if (tipoConstante == 3) {
                throw new SyntaticError(
                        "encontrado " + formatarTokenEncontrado(currentToken, t_Const_float)
                                + " esperado constante_float",
                        currentToken.getPosition());
            } else if (tipoConstante == 1) {
                throw new SyntaticError("encontrado " + currentToken.getLexeme() + " esperado constante_float",
                        currentToken.getPosition());
            }
        } else if (tipoVariavel == 3 && tipoConstante != 3) {
            if (tipoConstante == 1) {
                throw new SyntaticError("encontrado " + currentToken.getLexeme() + " esperado constante_string",
                        currentToken.getPosition());
            } else if (tipoConstante == 2) {
                throw new SyntaticError("encontrado " + currentToken.getLexeme() + " esperado constante_string",
                        currentToken.getPosition());
            }
        }
    }

    private boolean isTokenConstante(int token) {
        return token == t_Const_int || token == t_Const_float || token == t_Const_string;
    }

    private int obterTipoDoToken(Token token) {
        if (token == null)
            return 0;
        switch (token.getId()) {
            case t_Const_string:
                return 3;
            case t_Const_int:
                return 1;
            case t_Const_float:
                return 2;
            default:
                return 0;
        }
    }

    private boolean isTipoTerminal(int terminal) {
        return terminal == t_pr_tipoInt || terminal == t_pr_tipoFloat ||
                terminal == t_pr_tipoString || terminal == t_pr_tipoBoolean;
    }

    private int obterTipoDoTerminal(int terminal) {
        switch (terminal) {
            case t_pr_tipoInt:
                return 1;
            case t_pr_tipoFloat:
                return 2;
            case t_pr_tipoString:
                return 3;
            case t_pr_tipoBoolean:
                return 4;
            default:
                return 0;
        }
    }

    private boolean isInicioNovaInstrucao(int simbolo) {
        return simbolo == 45 || simbolo == 46;
    }

    private String formatarTokenEncontrado(Token token, int tipoEsperado) {
        if (token == null)
            return "EOF";

        int tokenId = token.getId();

        // ✅ CORREÇÃO: Se é EOF, sempre retorna "EOF"
        if (tokenId == DOLLAR) {
            return "EOF";
        }

        String lexeme = token.getLexeme();

        boolean emContextoDeclaracao = isEmContextoDeclaracao();

        if (emContextoDeclaracao && isTipoIndividualEsperado(tipoEsperado)) {
            switch (tokenId) {
                case t_Const_string:
                    return lexeme;
                case t_Const_int:
                    return lexeme;
                case t_Const_float:
                    return lexeme;
                case t_id:
                    return lexeme;
                default:
                    return lexeme;
            }
        }

        if (isTipoIndividualEsperado(tipoEsperado)) {
            switch (tokenId) {
                case t_Const_string:
                    return lexeme;
                case t_Const_int:
                    return lexeme;
                case t_Const_float:
                    return lexeme;
                case t_id:
                    return lexeme;
                default:
                    return lexeme;
            }
        }

        if (isTipoEspecificoEtapa1(tipoEsperado)) {
            switch (tokenId) {
                case t_Const_string:
                    return lexeme;
                case t_Const_int:
                    return lexeme;
                case t_Const_float:
                    return lexeme;
                case t_id:
                    return lexeme;
                default:
                    return lexeme;
            }
        }

        switch (tokenId) {
            case DOLLAR:
                return "EOF"; // ✅ Já tratado acima, mas mantido por segurança
            case t_Const_string:
                return "constante_string";
            case t_Const_int:
                return lexeme;
            case t_Const_float:
                return lexeme;
            case t_id:
                return lexeme;

            // Palavras reservadas - MOSTRAM LEXEMA
            case t_pr_tipoInt:
            case t_pr_tipoFloat:
            case t_pr_tipoString:
            case t_pr_tipoBoolean:
            case t_pr_list:
            case t_pr_begin:
            case t_pr_end:
            case t_pr_if:
            case t_pr_else:
            case t_pr_do:
            case t_pr_until:
            case t_pr_read:
            case t_pr_print:
            case t_pr_add:
            case t_pr_delete:
            case t_pr_and:
            case t_pr_or:
            case t_pr_not:
            case t_pr_true:
            case t_pr_false:
            case t_pr_count:
            case t_pr_size:
            case t_pr_elementOf:
                return lexeme;

            // Símbolos especiais - MOSTRAM LEXEMA
            case t_TOKEN_3: // "+"
            case t_TOKEN_32: // "-"
            case t_TOKEN_33: // "*"
            case t_TOKEN_34: // "/"
            case t_TOKEN_35: // "=="
            case t_TOKEN_36: // "~="
            case t_TOKEN_37: // "<"
            case t_TOKEN_38: // ">"
            case t_TOKEN_39: // "="
            case t_TOKEN_40: // "<-"
            case t_TOKEN_41: // "("
            case t_TOKEN_42: // ")"
            case t_TOKEN_43: // ";"
            case t_TOKEN_44: // ","
                return lexeme;

            default:
                return lexeme;
        }
    }

    private boolean isEmContextoDeclaracao() {
        return currentToken != null &&
                (stack.contains(45) || stack.contains(46));
    }

    private boolean isTipoIndividualEsperado(int token) {
        return token == t_pr_tipoInt || token == t_pr_tipoFloat ||
                token == t_pr_tipoString || token == t_pr_tipoBoolean || token == t_pr_list;
    }

    private boolean isTipoEspecificoEtapa1(int terminal) {
        return terminal == t_id ||
                terminal == t_Const_int ||
                terminal == t_Const_float ||
                terminal == t_Const_string;
    }

    private String obterSimbolosEsperadosTerminal(int terminal) {
        // ✅ PRIMEIRO: Verificar se o token atual é EOF
        if (currentToken != null && currentToken.getId() == DOLLAR) {
            // Quando encontramos EOF inesperadamente, precisamos dizer o que estava
            // faltando

            // Caso 1: Programa principal sem 'end'
            if (stack.contains(0) && (stack.size() <= 3 || !stack.contains(1))) {
                return "end";
            }

            // Caso 2: Verificações específicas baseadas no terminal esperado
            switch (terminal) {
                case 44: // <parte_else>
                    return "else ou end";
                case 55: // <comando_selecao>
                case 56: // <lista_comandos>
                    return "end";
                case 53: // <expressao>
                case 58: // <entrada_dados>
                    return "expressão";
                case 70: // <comando_repeticao>
                    return "end";
                case 46: // <instrucao>
                    return "if, print, read ou identificador";
                default:
                    // Para terminais de expressão
                    if (terminal >= 54 && terminal <= 85) {
                        return "expressão";
                    }
                    // Se está em contexto de if
                    if (stack.contains(44)) {
                        return "end";
                    }
                    // Fallback para a mensagem padrão
                    return PARSER_ERROR[terminal] != null ? PARSER_ERROR[terminal] : "símbolo";
            }
        }

        // ✅ SEGUNDO: Para tokens normais (não EOF), usar as mensagens padrão
        switch (terminal) {
            case DOLLAR:
                return "EOF";
            case t_id:
                return "identificador";
            case t_Const_int:
                return "constante_int";
            case t_Const_float:
                return "constante_float";
            case t_Const_string:
                return "constante_string";

            case t_pr_tipoInt:
            case t_pr_tipoFloat:
            case t_pr_tipoString:
            case t_pr_tipoBoolean:
            case t_pr_list:
                return getMensagemTipoPorContexto(terminal);

            case t_pr_begin:
                return "begin";
            case t_pr_end:
                return "end";
            case t_pr_if:
                return "if";
            case t_pr_else:
                return "else";
            case t_pr_do:
                return "do";
            case t_pr_until:
                return "until";
            case t_pr_read:
                return "read";
            case t_pr_print:
                return "print";
            case t_pr_add:
                return "add";
            case t_pr_delete:
                return "delete";
            case t_pr_and:
                return "and";
            case t_pr_or:
                return "or";
            case t_pr_not:
                return "not";
            case t_pr_true:
                return "true";
            case t_pr_false:
                return "false";
            case t_TOKEN_3:
                return "+";
            case t_TOKEN_32:
                return "-";
            case t_TOKEN_33:
                return "*";
            case t_TOKEN_34:
                return "/";
            case t_TOKEN_35:
                return "==";
            case t_TOKEN_36:
                return "~=";
            case t_TOKEN_37:
                return "<";
            case t_TOKEN_38:
                return ">";
            case t_TOKEN_39:
                return "=";
            case t_TOKEN_40:
                return "<-";
            case t_TOKEN_41:
                return "(";
            case t_TOKEN_42:
                return ")";
            case t_TOKEN_43:
                return ";";
            case t_TOKEN_44:
                return ",";
            default:
                return PARSER_ERROR[terminal] != null ? PARSER_ERROR[terminal] : "símbolo";
        }
    }

    private String getMensagemTipoPorContexto(int terminal) {
        if (isChamadoPor(48) || isChamadoPor(50)) {
            return "tipo primitivo";
        } else if (isChamadoPor(6)) {
            return "tipo";
        } else {
            return getNomeTipoIndividual(terminal);
        }
    }

    private boolean isChamadoPor(int naoTerminal) {
        return stack.contains(naoTerminal) ||
                (stack.search(naoTerminal) >= 0);
    }

    private String getNomeTipoIndividual(int terminal) {
        switch (terminal) {
            case t_pr_tipoInt:
                return "int";
            case t_pr_tipoFloat:
                return "float";
            case t_pr_tipoString:
                return "string";
            case t_pr_tipoBoolean:
                return "bool";
            case t_pr_list:
                return "list";
            default:
                return "tipo";
        }
    }

    private String obterSimbolosEsperadosNaoTerminal(int naoTerminal, int tokenAtual) {
        System.out.println("=== INICIO DO METODO ===");

        // SEÇÃO 1 - EOF
        System.out.println("=== ANTES DA SEÇÃO 1 (EOF) ===");
        if (currentToken != null && currentToken.getId() == DOLLAR) {
            System.out.println("=== ENTROU NA SEÇÃO 1 - RETORNO EOF ===");
            // ... código EOF
        }
        System.out.println("=== PASSOU DA SEÇÃO 1 ===");

        // SEÇÃO 2 - Contextual
        System.out.println("=== ANTES DA SEÇÃO 2 (CONTEXTUAL) ===");
        if (naoTerminal == 51 && currentToken != null && currentToken.getId() == DOLLAR) {
            System.out.println("=== RETORNO SEÇÃO 2.1 ===");
            return ";";
        }
        System.out.println("=== PASSOU DA SEÇÃO 2.1 ===");

        if (naoTerminal == 52 && previousToken != null && previousToken.getId() == t_TOKEN_34) {
            System.out.println("=== RETORNO SEÇÃO 2.2 ===");
            return "identificador";
        }
        System.out.println("=== PASSOU DA SEÇÃO 2.2 ===");

        if (naoTerminal == 68 && currentToken != null && currentToken.getId() == t_TOKEN_34) {
            System.out.println("=== RETORNO SEÇÃO 2.3 ===");
            return "constante_string";
        }
        System.out.println("=== PASSOU DA SEÇÃO 2.3 ===");

        if (naoTerminal == 81 && currentToken != null && currentToken.getId() == t_pr_elementOf) {
            System.out.println("=== RETORNO SEÇÃO 2.4 ===");
            return "expressao";
        }
        System.out.println("=== PASSOU DA SEÇÃO 2 TODA ===");

        // SEÇÃO 3 - Tokens anteriores
        System.out.println("=== ANTES DA SEÇÃO 3 (TOKENS ANTERIORES) ===");
        if (previousToken != null) {
            System.out.println("=== previousToken NÃO É NULL ===");
            if (previousToken.getId() == t_id && tokenAtual == t_TOKEN_37) {
                System.out.println("=== RETORNO SEÇÃO 3.1 ===");
                return "= <- add delete";
            }
            System.out.println("=== PASSOU SEÇÃO 3.1 ===");

            if (isTipoTerminal(previousToken.getId()) && isTokenConstante(tokenAtual)) {
                System.out.println("=== RETORNO SEÇÃO 3.2 ===");
                // Usar o previousToken para saber qual tipo estava sendo declarado
                switch (previousToken.getId()) {
                    case t_pr_tipoInt:
                        return "int";
                    case t_pr_tipoFloat:
                        return "float";
                    case t_pr_tipoString:
                        return "string";
                    case t_pr_tipoBoolean:
                        return "bool";
                    case t_pr_list:
                        return "list";
                    default:
                        return "identificador";
                }
            }
            System.out.println("=== PASSOU SEÇÃO 3.2 ===");

            if (isTipoTerminal(previousToken.getId()) && naoTerminal == 51) {
                System.out.println("=== RETORNO SEÇÃO 3.3 ===");
                return "identificador";
            }
            System.out.println("=== PASSOU SEÇÃO 3.3 ===");
        } else {
            System.out.println("=== previousToken É NULL ===");
        }
        System.out.println("=== PASSOU DA SEÇÃO 3 TODA ===");

        // VERIFICAÇÕES INDIVIDUAIS
        System.out.println("=== ANTES DAS VERIFICAÇÕES INDIVIDUAIS ===");
        if (tokenAtual == t_TOKEN_36) {
            System.out.println("=== RETORNO INDIVIDUAL 1 ===");
            return "list";
        }
        System.out.println("=== PASSOU INDIVIDUAL 1 ===");

        if ((naoTerminal == 59 || naoTerminal == 65) && currentToken != null
                && isTokenConstante(currentToken.getId())) {
            if (currentToken.getId() != t_Const_string) {
                System.out.println("=== RETORNO INDIVIDUAL 2 ===");
                return "constante_string";
            }
        }
        System.out.println("=== PASSOU INDIVIDUAL 2 ===");

        if (naoTerminal == 1 && isTipoIndividualEsperado(tokenAtual)) {
            System.out.println("=== RETORNO INDIVIDUAL 3 ===");
            return getNomeTipoIndividual(tokenAtual);
        }
        System.out.println("=== PASSOU INDIVIDUAL 3 ===");

        System.out.println("=== CHEGOU NO SWITCH! ===");

        // SWITCH PRINCIPAL
        switch (naoTerminal) {
            case 0:
                return "begin";
            case 1:
                return "identificador do if ou print ou read ou tipo";
            case 6:
                return "tipo";
            case 7:
                return "tipo";
            case 11:
                return "list";
            case 12:
                return "(";
            case 18:
                return "identificador";
            case 22:
                return "= <- add delete";
            case 27:
                return "= <- add delete";
            case 35:
                return "read";
            case 42:
                return "print";
            case 43:
                return "expressao";
            case 46:
                if (tokenAtual == t_Const_int || tokenAtual == t_Const_float ||
                        tokenAtual == t_Const_string || tokenAtual == t_pr_true ||
                        tokenAtual == t_pr_false || tokenAtual == t_pr_list) {
                    return "tipo";
                }
                return "identificador do if ou print ou read ou tipo";
            case 52:
                return "do";
            case 53: // <lista_identificadores>
                System.out.println("=== ENTROU NO CASO 53 ===");
                // Usar previousToken para detectar qual tipo estava sendo declarado
                System.out.println("Previous token ID: " + previousToken.getId());
                switch (previousToken.getId()) {
                    case t_pr_tipoInt:
                        System.out.println("Retornando: int");
                        return "int";
                    case t_pr_tipoFloat:
                        System.out.println("Retornando: float");
                        return "float";
                    case t_pr_tipoString:
                        System.out.println("Retornando: string");
                        return "string";
                    case t_pr_tipoBoolean:
                        System.out.println("Retornando: bool");
                        return "bool";
                    case t_pr_list:
                        System.out.println("Retornando: list");
                        return "list";
                }
                System.out.println("Retornando: identificador");
                return "identificador";
            case 60:
                return "expressao";
            case 44:
                return "else end";
            case 45: // <instrucao>
                // Se encontrou um número/string/operador, era esperado um tipo para declaração
                if (tokenAtual == t_Const_int || tokenAtual == t_Const_float ||
                        tokenAtual == t_Const_string || tokenAtual == t_pr_true ||
                        tokenAtual == t_pr_false) {
                    return "tipo";
                }
                // Mantém a mensagem original para outros casos
                return "identificador do if ou print ou read ou tipo";
            case 48:
                return "tipo primitivo";
            case 50:
                return "tipo primitivo";
            case 51:
                return "tipo primitivo";
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 61:
                // Se é a primeira instrução onde todos os 5 tipos são esperados
                if (isChamadoPor(45) && isChamadoPor(1)) { // <instrucao> chamado por <lista_instrucoes>
                    return "tipo"; // ← APENAS "tipo" conforme especificação
                } else {
                    return "tipo";
                }
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
                return "expressao";
        }

        // 5. QUINTO: Fallback para não-terminais de expressão
        if (naoTerminal >= 54 && naoTerminal <= 85) {
            return "expressao";
        }

        return PARSER_ERROR[naoTerminal] != null ? PARSER_ERROR[naoTerminal] : "símbolo";
    }

    private boolean pushProduction(int topStack, int tokenInput) {
        // Ajuste para lidar com casos onde tokenInput pode ser 0 ou negativo
        if (tokenInput <= 0) {
            return false;
        }

        try {
            int p = PARSER_TABLE[topStack - FIRST_NON_TERMINAL][tokenInput - 1];
            if (p >= 0) {
                int[] production = PRODUCTIONS[p];
                // Adicione tratamento para produção vazia
                if (production.length == 1 && production[0] == EPSILON) {
                    return true;
                }
                for (int i = production.length - 1; i >= 0; i--) {
                    stack.push(new Integer(production[i]));
                }
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Log para debug
            System.out.println("Erro na tabela: topStack=" + topStack + ", tokenInput=" + tokenInput);
        }
        return false;
    }

    private boolean isEmContextoDeclaracaoCorrigido() {
        return stack.contains(45) || stack.contains(46) ||
                (previousToken != null && isTipoTerminal(previousToken.getId()));
    }

    public void parse(Lexico scanner, Semantico semanticAnalyser) throws LexicalError, SyntaticError, SemanticError {
        this.scanner = scanner;
        this.semanticAnalyser = semanticAnalyser;
        stack.clear();
        stack.push(new Integer(DOLLAR));
        stack.push(new Integer(START_SYMBOL));
        currentToken = scanner.nextToken();
        while (!step()) {
        }
    }
}