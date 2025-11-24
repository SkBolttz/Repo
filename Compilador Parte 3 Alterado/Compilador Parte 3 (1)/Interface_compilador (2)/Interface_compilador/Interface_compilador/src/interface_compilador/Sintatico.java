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
                String tokenEncontrado = formatarTokenEncontrado(currentToken, 0);
                String simbolosEsperados = obterSimbolosEsperadosNaoTerminal(x, tokenInput);

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
            return "EOF";
        case t_Const_string:
            return "constante_string";  // Mantém a classe (não está na lista)
        case t_Const_int:
            return lexeme;  // MOSTRA LEXEMA (está na lista)
        case t_Const_float:
            return lexeme;  // MOSTRA LEXEMA (está na lista)
        case t_id:
            return lexeme;  // MOSTRA LEXEMA (está na lista)
        
        // Palavras reservadas - MOSTRAM LEXEMA (estão na lista)
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
            return lexeme;  // MOSTRA LEXEMA
        
        // Símbolos especiais - MOSTRAM LEXEMA (estão na lista)
        case t_TOKEN_3:   // "+"
        case t_TOKEN_32:  // "-"
        case t_TOKEN_33:  // "*"
        case t_TOKEN_34:  // "/"
        case t_TOKEN_35:  // "=="
        case t_TOKEN_36:  // "~="
        case t_TOKEN_37:  // "<"
        case t_TOKEN_38:  // ">"
        case t_TOKEN_39:  // "="
        case t_TOKEN_40:  // "<-"
        case t_TOKEN_41:  // "("
        case t_TOKEN_42:  // ")"
        case t_TOKEN_43:  // ";"
        case t_TOKEN_44:  // ","
            return lexeme;  // MOSTRA LEXEMA
        
        default:
            return lexeme;  // Para outros tokens, mostra lexema
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
        if (currentToken != null && currentToken.getId() == DOLLAR) {

            if (terminal == 36 && previousToken != null && previousToken.getId() == t_pr_elementOf) {
                return "expressão";
            }

            if (terminal == t_TOKEN_37) {
                if (stack.contains(46) || stack.contains(58) || stack.contains(53)) {
                    return "else end";
                }
                if (stack.contains(44) || stack.contains(55)) {
                    return "end";
                }
            }
        }

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
                return "<-";
            case t_TOKEN_33:
                return "=";
            case t_TOKEN_34:
                return ",";
            case t_TOKEN_35:
                return ")";
            case t_TOKEN_36:
                return "(";
            case t_TOKEN_37:
                return ";";
            case t_TOKEN_38:
                return "==";
            case t_TOKEN_39:
                return "<";
            case t_TOKEN_40:
                return ">";
            case t_TOKEN_41:
                return "-";
            case t_TOKEN_42:
                return "*";
            case t_TOKEN_43:
                return "/";
            default:
                return PARSER_ERROR[terminal];
        }
    }

    private String getMensagemTipoPorContexto(int terminal) {
        if (isChamadoPor(45)) {
            return "tipo";
        } else if (isChamadoPor(48)) {
            return getNomeTipoIndividual(terminal);
        } else if (isChamadoPor(50)) {
            return "tipo primitivo";
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

    if (naoTerminal == 52 && previousToken != null && previousToken.getId() == t_TOKEN_34) {
        return "identificador";
    }

    if (naoTerminal == 68 && currentToken != null && currentToken.getId() == t_TOKEN_34) {
        return "constante_string";
    }

    if (naoTerminal == 81 && currentToken != null && currentToken.getId() == t_pr_elementOf) {
        return "expressão";
    }

    if (currentToken != null && currentToken.getId() == DOLLAR) {

        if (naoTerminal == 55 && previousToken != null && previousToken.getId() == t_TOKEN_37) {
            return "end"; 
        }

        if ((naoTerminal == 53 || naoTerminal == 58) &&
                stack.contains(46)) { 
            return "else end";
        }
        
        switch (naoTerminal) {
            case 44: 
                return "else end";
            case 55: 
                return "end";
            case 58:
                return "expressão";
            case 53:
                return "expressão";
            case 70:
                return "end";
            case 46: 
                return "expressão";
            default:
                if (naoTerminal >= 54 && naoTerminal <= 85) {
                    return "expressão"; 
                }

                if (stack.contains(44)) { 
                    return "end";
                }
                return PARSER_ERROR[naoTerminal];
        }
    }

    if (previousToken != null) {
        if (previousToken.getId() == t_id && tokenAtual == t_TOKEN_37) {
            return "= <- add delete";
        }
        if (isTipoTerminal(previousToken.getId()) && isTokenConstante(tokenAtual)) {
            return "identificador";
        }

        if (isTipoTerminal(previousToken.getId()) && naoTerminal == 51) {
            return "identificador";
        }
    }

    if (tokenAtual == t_TOKEN_36) {
        return "list";
    }

    if (naoTerminal == 81 && currentToken != null &&
            currentToken.getId() == t_pr_elementOf) {
        return "expressão"; 
    }

    if ((naoTerminal == 59 || naoTerminal == 65) &&
            currentToken != null && isTokenConstante(currentToken.getId())) {
        if (currentToken.getId() != t_Const_string) {
            return "constante_string";
        }
    }

    if (naoTerminal == 1 && isTipoIndividualEsperado(tokenAtual)) {
        return getNomeTipoIndividual(tokenAtual);
    }

    if (previousToken != null && previousToken.getId() == t_id && tokenAtual == t_TOKEN_37) {
        return "= <- add delete";
    }

    // CORREÇÕES CONFORME ESPECIFICAÇÃO
    switch (naoTerminal) {
        case 0:  // <dec_program> / <programa>
            return "begin";
        case 1:  // <lista_instrucoes>
            return "identificador do if print read tipo";
        case 6:  // <tipo>
            return "tipo";
        case 7:  // <tipo> ou <dec_var>
            return "tipo";
        case 11: // <Lista>
            return "list";
        case 12: // produção da lista
            return "(";
        case 18: // <lista_identificadores> / <lista_id>
            return "identificador";
        case 22: // <comando_id> / <atribuicao_manipulacao_listas>
            return "= <- add delete";
        case 27: // <comando_id> / <atribuicao_manipulacao_listas>
            return "= <- add delete";
        case 35: // <entrada_dados>
            return "read";
        case 42: // <saida_dados>
            return "print";
        case 43: // <expressao>
            return "expressão";
        case 46: // <comando_selecao>
            return "if";
        case 52: // <comando_repeticao>
            return "do";
        case 53: // <expressao> em until
            return "expressão";
        case 60: // <expressao> em contexto de comando
            return "expressão";
            
        // ADICIONANDO OUTROS NÃO-TERMINAIS IMPORTANTES
        case 44: // <parte_else>
            return "else end";
        case 45: // <instrucao>
            return "identificador do if print read tipo";
        case 48: // <tipoSimples>
            return "tipo primitivo";
        case 50: // <tipoSimples> em outro contexto
            return "tipo primitivo";
        case 51: // <lista_identificadores>
            return "identificador";
        case 54: // <valor>
            return "expressão";
        case 55: // <expressao_>
            return "expressão";
        case 56: // <relacional>
            return "expressão";
        case 57: // <relacional_>
            return "expressão";
        case 58: // <aritmetica>
            return "expressão";
        case 59: // <aritmetica_>
            return "expressão";
        case 61: // <termo>
            return "expressão";
        case 62: // <termo_>
            return "expressão";
        case 63: // <fator>
            return "expressão";
        case 64: // <fator_>
            return "expressão";
        case 65: // <elemento>
            return "expressão";
        case 66: // <posicao>
            return "expressão";
    }

    // Para não-terminais de expressão (54-85)
    if (naoTerminal >= 54 && naoTerminal <= 85)
        return "expressão";

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