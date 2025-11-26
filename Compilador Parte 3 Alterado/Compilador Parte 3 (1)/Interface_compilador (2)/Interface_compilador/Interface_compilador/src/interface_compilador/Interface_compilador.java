package interface_compilador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import interface_compilador.Exception.ComentarioBlocoException;
import interface_compilador.Exception.ConstanteFloatException;
import interface_compilador.Exception.ConstanteStringException;
import interface_compilador.Exception.ErroCompilarExeption;
import interface_compilador.Exception.IdentificadorExecption;
import interface_compilador.Exception.ProgramaVazioException;
import interface_compilador.Exception.SimboloInvalidoException;

/**
 *
 * @author bruna
 * @author henrique
 */

public class Interface_compilador {

        private static File arquivoAtual = null;

        public static void main(String[] args) {
                JFrame frame = new JFrame("Compilador");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1500, 800);
                frame.setResizable(false);
                frame.setLocationRelativeTo(null);
                frame.setLayout(new BorderLayout());

                JToolBar toolBar = new JToolBar();
                toolBar.setPreferredSize(new Dimension(frame.getWidth(), 70));
                toolBar.setFloatable(false);

                ImageIcon iconCriar = new ImageIcon(
                                Interface_compilador.class.getResource("/Image/newDocument.png"));

                ImageIcon iconAbrir = new ImageIcon(
                                Interface_compilador.class.getResource("/Image/openFolder.png"));

                ImageIcon iconSalvar = new ImageIcon(
                                Interface_compilador.class.getResource("/Image/save.png"));

                ImageIcon iconCopiar = new ImageIcon(
                                Interface_compilador.class.getResource("/Image/copy.png"));

                ImageIcon iconColar = new ImageIcon(
                                Interface_compilador.class.getResource("/Image/toPaste.png"));

                ImageIcon iconRecortar = new ImageIcon(
                                Interface_compilador.class.getResource("/Image/cut.png"));

                ImageIcon iconCompilar = new ImageIcon(
                                Interface_compilador.class.getResource("/Image/compile.png"));

                ImageIcon iconTime = new ImageIcon(
                                Interface_compilador.class.getResource("/Image/team.png"));

                Image imgCriar = iconCriar.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgAbrir = iconAbrir.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgSalvar = iconSalvar.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgCopiar = iconCopiar.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgColar = iconColar.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgRecortar = iconRecortar.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgCompilar = iconCompilar.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                Image imgTime = iconTime.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);

                JButton btnCriar = new JButton("novo [cntl-n]");
                toolBar.add(btnCriar);
                btnCriar.setIcon(new ImageIcon(imgCriar));
                btnCriar.setHorizontalTextPosition(SwingConstants.CENTER);
                btnCriar.setVerticalTextPosition(SwingConstants.BOTTOM);

                JButton btnAbrir = new JButton("abrir [cntl-o]");
                toolBar.add(btnAbrir);
                btnAbrir.setIcon(new ImageIcon(imgAbrir));
                btnAbrir.setHorizontalTextPosition(SwingConstants.CENTER);
                btnAbrir.setVerticalTextPosition(SwingConstants.BOTTOM);

                JButton btnSalvar = new JButton("salvar [cntl-s]");
                toolBar.add(btnSalvar);
                btnSalvar.setIcon(new ImageIcon(imgSalvar));
                btnSalvar.setHorizontalTextPosition(SwingConstants.CENTER);
                btnSalvar.setVerticalTextPosition(SwingConstants.BOTTOM);

                JButton btnCopiar = new JButton("copiar [cntl-c]");
                toolBar.add(btnCopiar);
                btnCopiar.setIcon(new ImageIcon(imgCopiar));
                btnCopiar.setHorizontalTextPosition(SwingConstants.CENTER);
                btnCopiar.setVerticalTextPosition(SwingConstants.BOTTOM);

                JButton btnColar = new JButton("colar [cntl-v]");
                toolBar.add(btnColar);
                btnColar.setIcon(new ImageIcon(imgColar));
                btnColar.setHorizontalTextPosition(SwingConstants.CENTER);
                btnColar.setVerticalTextPosition(SwingConstants.BOTTOM);

                JButton btnRecortar = new JButton("recortar [cntl-x]");
                toolBar.add(btnRecortar);
                btnRecortar.setIcon(new ImageIcon(imgRecortar));
                btnRecortar.setHorizontalTextPosition(SwingConstants.CENTER);
                btnRecortar.setVerticalTextPosition(SwingConstants.BOTTOM);

                JButton btnCompilar = new JButton("compilar [F7]");
                toolBar.add(btnCompilar);
                btnCompilar.setIcon(new ImageIcon(imgCompilar));
                btnCompilar.setHorizontalTextPosition(SwingConstants.CENTER);
                btnCompilar.setVerticalTextPosition(SwingConstants.BOTTOM);

                JButton btnEquipe = new JButton("equipe [F1]");
                toolBar.add(btnEquipe);
                btnEquipe.setIcon(new ImageIcon(imgTime));
                btnEquipe.setHorizontalTextPosition(SwingConstants.CENTER);
                btnEquipe.setVerticalTextPosition(SwingConstants.BOTTOM);

                frame.add(toolBar, BorderLayout.NORTH);

                JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                splitPane.setResizeWeight(0.5);
                splitPane.setDividerLocation(500);

                JTextArea editorPrograma = new JTextArea();
                editorPrograma.setFont(new Font("Monospaced", Font.PLAIN, 12));

                JTextArea linhaNumeros = new JTextArea("1");
                linhaNumeros.setBackground(Color.LIGHT_GRAY);
                linhaNumeros.setEditable(false);
                linhaNumeros.setFont(new Font("Monospaced", Font.PLAIN, 12));
                linhaNumeros.setMargin(new Insets(0, 5, 0, 5));

                editorPrograma.getDocument().addDocumentListener(new DocumentListener() {
                        private void atualizarLinhas() {
                                int linhas = editorPrograma.getLineCount();
                                StringBuilder sb = new StringBuilder();
                                for (int i = 1; i <= linhas; i++) {
                                        sb.append(i).append("\n");
                                }
                                linhaNumeros.setText(sb.toString());
                        }

                        @Override
                        public void insertUpdate(DocumentEvent e) {
                                atualizarLinhas();
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                                atualizarLinhas();
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                                atualizarLinhas();
                        }
                });

                JScrollPane editorScrollPane = new JScrollPane(editorPrograma, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

                editorScrollPane.setRowHeaderView(linhaNumeros);
                splitPane.setTopComponent(editorScrollPane);

                JTextArea mensagem = new JTextArea();
                mensagem.setEditable(false);
                mensagem.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane mensagemScrollPane = new JScrollPane(mensagem, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                splitPane.setBottomComponent(mensagemScrollPane);
                frame.add(splitPane, BorderLayout.CENTER);

                JLabel statusLabel = new JLabel("Pasta/Nome do arquivo");
                statusLabel.setPreferredSize(new Dimension(frame.getWidth(), 25));
                frame.add(statusLabel, BorderLayout.SOUTH);

                frame.setVisible(true);

                InputMap inputMap = editorPrograma.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "novo");
                editorPrograma.getActionMap().put("novo", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                editorPrograma.setText("");
                        }
                });

                if (btnCriar.getActionCommand().equals("novo [cntl-n]")) {
                        btnCriar.addActionListener((ActionListener) new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        editorPrograma.setText("");
                                        mensagem.setText("");
                                        statusLabel.setText("Arquivo limpo!");
                                }
                        });
                }

                inputMap = editorPrograma.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "novo");
                editorPrograma.getActionMap().put("novo", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                editorPrograma.setText("");
                                mensagem.setText("");
                                statusLabel.setText("Arquivo limpo!");
                        }
                });

                btnAbrir.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                                FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos de texto",
                                                "txt");
                                fileChooser.setFileFilter(filter);
                                int returnVal = fileChooser.showOpenDialog(frame);

                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                        File arqSelecionado = fileChooser.getSelectedFile();

                                        try {
                                                BufferedReader reader = new BufferedReader(
                                                                new FileReader(arqSelecionado));
                                                StringBuilder conteudo = new StringBuilder();
                                                String linha;
                                                while ((linha = reader.readLine()) != null) {
                                                        conteudo.append(linha).append("\n");
                                                }
                                                reader.close();

                                                editorPrograma.setText(conteudo.toString());
                                                mensagem.setText("");
                                                statusLabel.setText("Arquivo aberto: " + arqSelecionado.getName());
                                        } catch (IOException ex) {
                                                JOptionPane.showMessageDialog(frame, "Erro ao abrir o arquivo!", "Erro",
                                                                JOptionPane.ERROR_MESSAGE);
                                        }
                                }
                        }
                });

                inputMap = editorPrograma.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "abrir");
                editorPrograma.getActionMap().put("abrir", new AbstractAction() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                                FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos de texto",
                                                "txt");
                                fileChooser.setFileFilter(filter);
                                int returnVal = fileChooser.showOpenDialog(frame);

                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                        File arqSelecionado = fileChooser.getSelectedFile();

                                        try {
                                                BufferedReader reader = new BufferedReader(
                                                                new FileReader(arqSelecionado));
                                                StringBuilder conteudo = new StringBuilder();
                                                String linha;
                                                while ((linha = reader.readLine()) != null) {
                                                        conteudo.append(linha).append("\n");
                                                }
                                                reader.close();

                                                editorPrograma.setText(conteudo.toString());
                                                mensagem.setText("");
                                                statusLabel.setText("Arquivo aberto: " + arqSelecionado.getName());
                                        } catch (IOException ex) {
                                                JOptionPane.showMessageDialog(frame, "Erro ao abrir o arquivo!", "Erro",
                                                                JOptionPane.ERROR_MESSAGE);
                                        }
                                }
                        }

                });

                inputMap = editorPrograma.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "salvar");
                editorPrograma.getActionMap().put("salvar", new AbstractAction() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                try {
                                        if (arquivoAtual == null) {
                                                JFileChooser fileChooser = new JFileChooser();
                                                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                                                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                                "Arquivos de texto", "txt");
                                                fileChooser.setFileFilter(filter);

                                                int returnVal = fileChooser.showSaveDialog(frame);

                                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                                        arquivoAtual = fileChooser.getSelectedFile();

                                                        if (!arquivoAtual.getName().toLowerCase().endsWith(".txt")) {
                                                                arquivoAtual = new File(arquivoAtual.getAbsolutePath()
                                                                                + ".txt");
                                                        }

                                                        BufferedWriter writer = new BufferedWriter(
                                                                        new java.io.FileWriter(arquivoAtual));
                                                        writer.write(editorPrograma.getText());
                                                        writer.close();

                                                        mensagem.setText("");
                                                        statusLabel.setText("Arquivo salvo: " + arquivoAtual.getName());
                                                }
                                        } else {
                                                BufferedWriter writer = new BufferedWriter(
                                                                new java.io.FileWriter(arquivoAtual));
                                                writer.write(editorPrograma.getText());
                                                writer.close();

                                                mensagem.setText("");
                                        }
                                } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(frame, "Erro ao salvar o arquivo.", "Erro",
                                                        JOptionPane.ERROR_MESSAGE);
                                }
                        }

                });
                btnSalvar.addActionListener(new ActionListener() {
                        @Override

                        public void actionPerformed(ActionEvent e) {
                                try {
                                        if (arquivoAtual == null) {
                                                JFileChooser fileChooser = new JFileChooser();
                                                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                                                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                                "Arquivos de texto", "txt");
                                                fileChooser.setFileFilter(filter);

                                                int returnVal = fileChooser.showSaveDialog(frame);

                                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                                        arquivoAtual = fileChooser.getSelectedFile();

                                                        if (!arquivoAtual.getName().toLowerCase().endsWith(".txt")) {
                                                                arquivoAtual = new File(arquivoAtual.getAbsolutePath()
                                                                                + ".txt");
                                                        }

                                                        BufferedWriter writer = new BufferedWriter(
                                                                        new java.io.FileWriter(arquivoAtual));
                                                        writer.write(editorPrograma.getText());
                                                        writer.close();

                                                        mensagem.setText("");
                                                        statusLabel.setText("Arquivo salvo: " + arquivoAtual.getName());
                                                }
                                        } else {
                                                BufferedWriter writer = new BufferedWriter(
                                                                new java.io.FileWriter(arquivoAtual));
                                                writer.write(editorPrograma.getText());
                                                writer.close();

                                                mensagem.setText("");
                                        }
                                } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(frame, "Erro ao salvar o arquivo.", "Erro",
                                                        JOptionPane.ERROR_MESSAGE);
                                }
                        }
                });

                inputMap = editorPrograma.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "copiar");
                editorPrograma.getActionMap().put("copiar", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                editorPrograma.copy();
                        }
                });

                if (btnCopiar.getActionCommand().equals("copiar [cntl-c]")) {
                        btnCopiar.addActionListener((ActionListener) new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        editorPrograma.copy();
                                }
                        });
                }

                inputMap = editorPrograma.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "colar");
                editorPrograma.getActionMap().put("colar", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                editorPrograma.paste();
                        }
                });

                if (btnColar.getActionCommand().equals("colar [cntl-v]")) {
                        btnColar.addActionListener((ActionListener) new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        editorPrograma.paste();
                                }
                        });
                }

                inputMap = editorPrograma.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), "recortar");
                editorPrograma.getActionMap().put("recortar", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                editorPrograma.cut();
                        }
                });

                if (btnRecortar.getActionCommand().equals("recortar [cntl-x]")) {
                        btnRecortar.addActionListener((ActionListener) new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        editorPrograma.cut();
                                }
                        });
                }

                btnCompilar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                mensagem.setText(""); // limpa a área de mensagens
                                String programaFonte = editorPrograma.getText();

                                if (programaFonte.trim().isEmpty()) {
                                        mensagem.setText("O programa está vazio.");
                                        return;
                                }

                                // Verificação de comentários não finalizados
                                boolean comentarioNaoFinalizado = false;
                                int linhaComentarioInicio = 1;
                                String[] linhas = programaFonte.split("\n");

                                for (int i = 0; i < linhas.length; i++) {
                                        String linha = linhas[i];
                                        int numeroLinha = i + 1;

                                        if (comentarioNaoFinalizado) {
                                                if (linha.contains("*)")) {
                                                        comentarioNaoFinalizado = false;
                                                } else if (i == linhas.length - 1) {
                                                        mensagem.setText(String.format(
                                                                        "Linha %d: comentário inválido ou não finalizado",
                                                                        linhaComentarioInicio));
                                                        return;
                                                }
                                        } else {
                                                if (linha.contains("(*")) {
                                                        if (!linha.contains("*)")) {
                                                                comentarioNaoFinalizado = true;
                                                                linhaComentarioInicio = numeroLinha;
                                                        }
                                                }
                                        }
                                }

                                if (comentarioNaoFinalizado) {
                                        mensagem.setText(
                                                        String.format("Linha %d: comentário inválido ou não finalizado",
                                                                        linhaComentarioInicio));
                                        return;
                                }

                                StringBuilder resultadoFinal = new StringBuilder();
                                boolean erroEncontrado = false;
                                boolean programaCompiladoComSucesso = true;

                                // PRIMEIRO: Análise Léxica
                                try {
                                        Lexico lexicoParaAnalise = new Lexico();
                                        lexicoParaAnalise.setInput(new StringReader(programaFonte));

                                        String[] palavrasReservadas = {
                                                        "add", "and", "begin", "bool", "count", "delete", "do",
                                                        "elementOf",
                                                        "else", "end", "false", "float", "if", "int", "list", "not",
                                                        "or",
                                                        "print", "read", "size", "string", "true", "until"
                                        };

                                        Token token;

                                        while ((token = lexicoParaAnalise.nextToken()) != null) {
                                                int linha = calcularLinha(token.getPosition(), programaFonte);

                                                // Ignora comentários
                                                if (token.getId() == Constants.t_Comentario_linha
                                                                || token.getId() == Constants.t_Comentario_bloco) {
                                                        if (token.getId() == Constants.t_Comentario_bloco
                                                                        && !token.getLexeme().endsWith("*)")) {
                                                                resultadoFinal.append(String.format(
                                                                                "Linha %d: comentário inválido ou não finalizado\n",
                                                                                linha));
                                                                erroEncontrado = true;
                                                        }
                                                        continue;
                                                }

                                                if (token.getId() == Constants.t_Const_string) {
                                                        String lexema = token.getLexeme();
                                                        if (!lexema.endsWith("\"") || lexema.contains("\n")) {
                                                                resultadoFinal.append(String.format(
                                                                                "Linha %d: constante_string inválida\n",
                                                                                linha, lexema));
                                                                erroEncontrado = true;
                                                                programaCompiladoComSucesso = false;
                                                                continue;
                                                        }
                                                }

                                                // Verifica por erros léxicos (tokens desconhecidos)
                                                String classe = obterClasseToken(token.getId(), token.getLexeme(),
                                                                palavrasReservadas);

                                                if ("desconhecido".equals(classe)) {
                                                        resultadoFinal.append(
                                                                        String.format("Linha %d: %s símbolo inválido\n",
                                                                                        linha, token.getLexeme()));
                                                        erroEncontrado = true;
                                                }
                                                // NÃO ADICIONA TOKENS RECONHECIDOS À LISTA - apenas verifica erros
                                        }

                                } catch (LexicalError erroLexico) {
                                        int linha = calcularLinha(erroLexico.getPosition(), programaFonte);
                                        resultadoFinal.append(
                                                        montarMensagemErroLexico(erroLexico, linha, programaFonte));
                                        erroEncontrado = true;
                                } catch (Exception ex) {
                                        resultadoFinal.append("Erro inesperado na análise léxica: ")
                                                        .append(ex.getMessage()).append("\n");
                                        erroEncontrado = true;
                                }

                                // SEGUNDO: Análise Sintática (apenas se não houve erro léxico)
                                if (!erroEncontrado) {
                                        try {
                                                Lexico lexicoParaSintatico = new Lexico();
                                                Sintatico sintatico = new Sintatico();
                                                Semantico semantico = new Semantico();

                                                lexicoParaSintatico.setInput(new StringReader(programaFonte));
                                                sintatico.parse(lexicoParaSintatico, semantico);

                                                // Se chegou aqui sem exceção, compilação foi bem-sucedida
                                                mensagem.setText("programa compilado com sucesso");
                                                return;

                                        } catch (SyntaticError errosintatico) {
                                                int linha = calcularLinha(errosintatico.getPosition(), programaFonte);
                                                resultadoFinal.append("Linha ").append(linha).append(": ")
                                                                .append(errosintatico.getMessage()).append("\n");
                                                erroEncontrado = true;
                                        } catch (SemanticError erroSemantico) {
                                                int linha = calcularLinha(erroSemantico.getPosition(), programaFonte);
                                                resultadoFinal.append("Linha ").append(linha).append(": ")
                                                                .append(erroSemantico.getMessage()).append("\n");
                                                erroEncontrado = true;
                                        } catch (Exception ex) {
                                                resultadoFinal.append("Erro inesperado na análise sintática: ")
                                                                .append(ex.getMessage()).append("\n");
                                                erroEncontrado = true;
                                        }
                                }

                                // Se houve erro em qualquer fase
                                if (erroEncontrado) {
                                        mensagem.setText(resultadoFinal.toString());
                                }
                        }

                        // Métodos auxiliares (mantenha os que você já tem)
                        private int calcularLinha(int posicao, String texto) {
                                if (posicao < 0 || texto == null || texto.isEmpty()) {
                                        return 1;
                                }
                                int linha = 1;
                                for (int i = 0; i < posicao && i < texto.length(); i++) {
                                        if (texto.charAt(i) == '\n') {
                                                linha++;
                                        }
                                }
                                return linha;
                        }

                        private String obterClasseToken(int idClasse, String lexema, String[] palavrasReservadas) {
                                if (lexema.trim().isEmpty()) {
                                        return "ignorar";
                                }

                                // PRIMEIRO: Verifica se é símbolo especial por LEXEMA
                                String[] simbolosEspeciais = { "+", "-", "*", "/", "==", "~=", "<", ">", "=", "<-", "(",
                                                ")", ";", "," };

                                for (String simbolo : simbolosEspeciais) {
                                        if (simbolo.equals(lexema)) {
                                                return "símbolo especial";
                                        }
                                }

                                // SEGUNDO: Lógica normal por ID
                                switch (idClasse) {
                                        case Constants.t_id:
                                                for (String palavra : palavrasReservadas) {
                                                        if (palavra.equals(lexema)) {
                                                                return "palavra reservada";
                                                        }
                                                }
                                                if (lexema.matches("[a-zA-Z][a-zA-Z0-9_]*") && !lexema.endsWith("_")) {
                                                        return "identificador";
                                                }
                                                return "desconhecido";

                                        case Constants.t_Const_int:
                                                if (lexema.matches("\\d{1,5}")) {
                                                        if (lexema.length() > 1 && lexema.startsWith("0")) {
                                                                return "desconhecido";
                                                        }
                                                        return "constante_int";
                                                }
                                                return "desconhecido";

                                        case Constants.t_Const_float:
                                                if (lexema.matches("\\d{1,5}\\.\\d{1,5}")) {
                                                        String[] partes = lexema.split("\\.");
                                                        String parteInteira = partes[0];
                                                        if (parteInteira.length() > 1 && parteInteira.startsWith("0")) {
                                                                return "desconhecido";
                                                        }
                                                        return "constante_float";
                                                }
                                                return "desconhecido";

                                        case Constants.t_Const_string:
                                                if (lexema.startsWith("\"") && lexema.endsWith("\"")
                                                                && !lexema.contains("\n")) {
                                                        return "constante_string";
                                                }
                                                // Se chegou aqui, é string não fechada
                                                return "desconhecido";

                                        case Constants.t_Comentario_linha:
                                        case Constants.t_Comentario_bloco:
                                                return "comentario_valido";

                                        case Constants.t_pr_tipoInt:
                                        case Constants.t_pr_tipoFloat:
                                        case Constants.t_pr_tipoString:
                                        case Constants.t_pr_tipoBoolean:
                                        case Constants.t_pr_list:
                                        case Constants.t_pr_add:
                                        case Constants.t_pr_delete:
                                        case Constants.t_pr_read:
                                        case Constants.t_pr_print:
                                        case Constants.t_pr_if:
                                        case Constants.t_pr_else:
                                        case Constants.t_pr_end:
                                        case Constants.t_pr_do:
                                        case Constants.t_pr_until:
                                        case Constants.t_pr_begin:
                                        case Constants.t_pr_and:
                                        case Constants.t_pr_or:
                                        case Constants.t_pr_not:
                                        case Constants.t_pr_count:
                                        case Constants.t_pr_size:
                                        case Constants.t_pr_elementOf:
                                        case Constants.t_pr_true:
                                        case Constants.t_pr_false:
                                                return "palavra reservada";

                                        case Constants.t_TOKEN_3:
                                        case Constants.t_TOKEN_32:
                                        case Constants.t_TOKEN_33:
                                        case Constants.t_TOKEN_34:
                                        case Constants.t_TOKEN_35:
                                        case Constants.t_TOKEN_36:
                                        case Constants.t_TOKEN_37:
                                        case Constants.t_TOKEN_38:
                                        case Constants.t_TOKEN_39:
                                        case Constants.t_TOKEN_40:
                                        case Constants.t_TOKEN_41:
                                        case Constants.t_TOKEN_42:
                                        case Constants.t_TOKEN_43:
                                        case Constants.t_TOKEN_44:
                                                return "símbolo especial";

                                        default:
                                                return "desconhecido";
                                }
                        }

                        private String montarMensagemErroLexico(LexicalError erro, int linha, String texto) {
                                int pos = Math.min(erro.getPosition(), texto.length() - 1);
                                char simbolo = pos >= 0 && pos < texto.length() ? texto.charAt(pos) : ' ';

                                // Verifica se é erro de string não fechada
                                if (simbolo == '"') {
                                        // Procura pela próxima quebra de linha após a aspas não fechada
                                        int i = pos + 1;
                                        while (i < texto.length() && texto.charAt(i) != '\n'
                                                        && texto.charAt(i) != '"') {
                                                i++;
                                        }

                                        // Se chegou no final do arquivo ou na quebra de linha sem encontrar aspas de
                                        // fechamento
                                        if (i >= texto.length() || texto.charAt(i) == '\n') {
                                                // Extrai o conteúdo da string a partir da posição inicial
                                                StringBuilder stringContent = new StringBuilder();
                                                int startPos = pos;
                                                int currentPos = pos + 1;

                                                while (currentPos < texto.length()
                                                                && texto.charAt(currentPos) != '\n') {
                                                        stringContent.append(texto.charAt(currentPos));
                                                        currentPos++;
                                                }

                                                String conteudoString = stringContent.toString().trim();
                                                return String.format("Linha %d: constante_string inválida\n",
                                                                linha, conteudoString);
                                        }
                                }

                                if (simbolo == '\\') {
                                        return String.format("Linha %d: constante_string inválida\n", linha);
                                }

                                // Detecta e quebrar números longos
                                if (Character.isDigit(simbolo)) {
                                        String resultadoNumeros = processarNumerosLongos(pos, linha, texto);
                                        if (resultadoNumeros != null) {
                                                return resultadoNumeros;
                                        }
                                }

                                // Capturar identificadores inválidos
                                if (Character.isLetter(simbolo)) {
                                        StringBuilder identificador = new StringBuilder();
                                        int i = pos;
                                        while (i < texto.length() && (Character.isLetterOrDigit(texto.charAt(i))
                                                        || texto.charAt(i) == '_')) {
                                                identificador.append(texto.charAt(i));
                                                i++;
                                        }
                                        String idStr = identificador.toString();
                                        if (idStr.length() > 0 && (idStr.matches(".*[0-9].*") || idStr.endsWith("_"))) {
                                                return String.format("Linha %d: %s identificador inválido\n", linha,
                                                                idStr);
                                        }
                                }

                                // Verifica caracteres especiais inválidos
                                if (!Character.isLetterOrDigit(simbolo) && !Character.isWhitespace(simbolo)) {
                                        String simbolosValidos = "+-*/=~<>();,\"()";
                                        if (simbolosValidos.indexOf(simbolo) == -1) {
                                                return String.format("Linha %d: %c símbolo inválido\n", linha, simbolo);
                                        }
                                }

                                return String.format("Linha %d: %c símbolo inválido\n", linha, simbolo);
                        }

                        private String processarNumerosLongos(int pos, int linha, String texto) {
                                StringBuilder numero = new StringBuilder();
                                int i = pos;
                                boolean temPonto = false;

                                while (i < texto.length() && (Character.isDigit(texto.charAt(i))
                                                || (!temPonto && texto.charAt(i) == '.'))) {
                                        if (texto.charAt(i) == '.') {
                                                temPonto = true;
                                        }
                                        numero.append(texto.charAt(i));
                                        i++;
                                }

                                String numStr = numero.toString();
                                StringBuilder resultado = new StringBuilder();

                                if (numStr.contains(".")) {
                                        String[] partes = numStr.split("\\.");
                                        String parteInteira = partes[0];
                                        String parteDecimal = partes.length > 1 ? partes[1] : "";

                                        while (parteInteira.length() > 5) {
                                                resultado.append("Linha ").append(linha).append(": ")
                                                                .append(parteInteira.substring(0, 5))
                                                                .append(" constante_int\n");
                                                parteInteira = parteInteira.substring(5);
                                        }

                                        if (parteDecimal.length() > 5) {
                                                resultado.append("Linha ").append(linha).append(": ")
                                                                .append(parteInteira).append(".")
                                                                .append(parteDecimal.substring(0, 5))
                                                                .append(" constante_float\n");
                                                parteDecimal = parteDecimal.substring(5);

                                                while (!parteDecimal.isEmpty()) {
                                                        int tamanho = Math.min(5, parteDecimal.length());
                                                        resultado.append("Linha ").append(linha).append(": ")
                                                                        .append(parteDecimal.substring(0, tamanho))
                                                                        .append(" constante_int\n");
                                                        parteDecimal = parteDecimal.substring(tamanho);
                                                }
                                        } else {
                                                resultado.append("Linha ").append(linha).append(": ")
                                                                .append(parteInteira).append(".").append(parteDecimal)
                                                                .append(" constante_float\n");
                                        }
                                } else {
                                        String restante = numStr;
                                        while (!restante.isEmpty()) {
                                                int tamanho = Math.min(5, restante.length());
                                                resultado.append("Linha ").append(linha).append(": ")
                                                                .append(restante.substring(0, tamanho))
                                                                .append(" constante_int\n");
                                                restante = restante.substring(tamanho);
                                        }
                                }
                                return resultado.toString();
                        }
                });

                inputMap = mensagem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "compilar");
                mensagem.getActionMap().put("compilar", new AbstractAction() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                try {
                                        verificarIdentificador();
                                } catch (Exception e1) {
                                        e1.printStackTrace();
                                }
                        }

                        private void verificarIdentificador() throws ErroCompilarExeption, ComentarioBlocoException {
                                mensagem.setText("");
                                String texto = editorPrograma.getText();
                                if (texto.trim().isEmpty()) {
                                        mensagem.setText("O programa está vazio.");
                                        throw new ProgramaVazioException("Erro ao compilar o programa.",
                                                        "O programa está vazio.");
                                }

                                String palavrasReservadas = "(add|and|begin|bool|count|delete|do|elementOf|else|"
                                                + "                   end|false|float|if|int|list|not|or|print|read|size|string|true|until)";

                                String tipos = "(int|float|string|add)";

                                String simbolos = "(\\+|\\-|\\*|\\/|<|>|=|\\(|\\)|;|,)";
                                String operadoresCompostos = "(==|~=|>=|<=|<-|\\|\\|)";

                                String constanteFloat = "([0-1]?[0-9]?[0-9]?[0-9]?[0-9]?|0)\\.[0-9]+";
                                String constanteInt = "([0-1][0-9]?[0-9]?[0-9]?[0-9]?|0)";

                                String constanteString = "\"[^\\n\\\"]*\"";
                                String identificador = "[a-zA-Z_][a-zA-Z0-9_]*";

                                String espacos = "[\\s\\t]+";
                                String comentarioLinha = "%.*";

                                String[] linhas = texto.split("\n");
                                boolean dentroComentarioBloco = false;
                                int linhaComentarioInicio = -1;

                                for (int i = 0; i < linhas.length; i++) {
                                        String linha = linhas[i];
                                        int numeroLinha = i + 1;

                                        if (dentroComentarioBloco) {
                                                if (linha.contains("*)")) {
                                                        dentroComentarioBloco = false;
                                                }
                                                continue;
                                        }

                                        if (linha.contains("(*")) {
                                                if (!linha.contains("*)")) {
                                                        dentroComentarioBloco = true;
                                                }
                                                continue;
                                        }

                                        if (linha.trim().matches(comentarioLinha)) {
                                                continue;
                                        }

                                        if (dentroComentarioBloco && i == linhas.length - 1) {
                                                mensagem.setText("Comentario invalido ou nao finalizado na linha"
                                                                + linhaComentarioInicio);
                                                throw new ComentarioBlocoException("Erro ao compilar o programa.",
                                                                "Comentario invalido ou nao finalizado na linha");
                                        }

                                        if (linha.contains("%")) {
                                                linha = linha.substring(0, linha.indexOf("%"));
                                        }

                                        if (linha.trim().isEmpty()) {
                                                continue;
                                        }

                                        List<String> finalTokens = new ArrayList<>();
                                        StringBuilder currentToken = new StringBuilder();
                                        boolean dentroString = false;
                                        char aspaChar = '"';

                                        for (int k = 0; k < linha.length(); k++) {
                                                char c = linha.charAt(k);

                                                if (dentroComentarioBloco) {
                                                        if (linha.contains("*)")) {
                                                                dentroComentarioBloco = false;
                                                        }
                                                        continue;
                                                }

                                                if (linha.contains("(*")) {
                                                        if (!linha.contains("*)")) {
                                                                dentroComentarioBloco = true;
                                                        }
                                                        continue;
                                                }

                                                if (linha.matches(espacos)) {
                                                        continue;
                                                }

                                                if (dentroString) {
                                                        currentToken.append(c);
                                                        if (c == aspaChar) {
                                                                finalTokens.add(currentToken.toString());
                                                                currentToken = new StringBuilder();
                                                                dentroString = false;
                                                        }
                                                        continue;
                                                }
                                                if (c == '"') {
                                                        if (currentToken.length() > 0) {
                                                                finalTokens.add(currentToken.toString());
                                                                currentToken = new StringBuilder();
                                                        }
                                                        currentToken.append(c);
                                                        dentroString = true;
                                                        aspaChar = c;
                                                        continue;
                                                }

                                                if (dentroString && k == linha.length() - 1) {
                                                        mensagem.setText("Constante_string invalida na linha "
                                                                        + numeroLinha);
                                                        throw new ConstanteStringException(
                                                                        "Erro ao compilar o programa.",
                                                                        "Constante_string invalida");
                                                }

                                                if (Character.isWhitespace(c)) {
                                                        if (currentToken.length() > 0) {
                                                                finalTokens.add(currentToken.toString());
                                                                currentToken = new StringBuilder();
                                                        }
                                                        continue;
                                                }

                                                if ("+-*/<>=~(){}[];,".indexOf(c) >= 0) {
                                                        if (currentToken.length() > 0) {
                                                                finalTokens.add(currentToken.toString());
                                                                currentToken = new StringBuilder();
                                                        }
                                                        finalTokens.add(String.valueOf(c));
                                                        continue;
                                                }
                                                currentToken.append(c);
                                        }
                                        if (currentToken.length() > 0) {
                                                finalTokens.add(currentToken.toString());
                                        }

                                        for (int j = 0; j < finalTokens.size(); j++) {
                                                String token = finalTokens.get(j);

                                                if (token.matches(espacos))
                                                        continue;

                                                if (token.matches(operadoresCompostos)) {
                                                        mensagem.setText(mensagem.getText() + "Linha: " + (i + 1)
                                                                        + " Simbolo Especial: " + token + "\n");
                                                }
                                                if (token.matches(tipos)) {
                                                        mensagem.setText(mensagem.getText() + "Linha: " + (i + 1)
                                                                        + " Palavra Reservada: " + token + "\n");

                                                        if (j + 1 < finalTokens.size()) {
                                                                String id = finalTokens.get(j + 1);
                                                                if (!id.matches(palavrasReservadas) &&
                                                                                !id.matches(simbolos) &&
                                                                                !id.matches(operadoresCompostos) &&
                                                                                !id.matches(constanteFloat) &&
                                                                                !id.matches(constanteInt) &&
                                                                                !id.matches(constanteString)) {

                                                                        mensagem.setText(mensagem.getText() + "Linha: "
                                                                                        + (i + 1) + " Identificador: "
                                                                                        + id + "\n");
                                                                        j++;
                                                                } else {
                                                                        mensagem.setText("Esperado identificador após "
                                                                                        + token + " na linha "
                                                                                        + (i + 1));
                                                                        throw new IdentificadorExecption(
                                                                                        "Erro ao compilar o programa.",
                                                                                        "Esperado identificador");
                                                                }
                                                        } else {
                                                                mensagem.setText("Esperado identificador após " + token
                                                                                + " na linha " + (i + 1));
                                                                throw new IdentificadorExecption(
                                                                                "Erro ao compilar o programa.",
                                                                                "Esperado identificador");
                                                        }
                                                } else if (token.matches(simbolos)) {
                                                        mensagem.setText(mensagem.getText() + "Linha: " + (i + 1)
                                                                        + " Simbolo Especial: " + token + "\n");
                                                } else if (token.matches(palavrasReservadas)) {
                                                        mensagem.setText(mensagem.getText() + "Linha: " + (i + 1)
                                                                        + " Palavra Reservada: " + token + "\n");
                                                } else if (token.matches(identificador)) {
                                                        mensagem.setText(mensagem.getText() + "Linha: " + (i + 1)
                                                                        + " Identificador: " + token + "\n");
                                                } else if (token.matches(constanteFloat)) {
                                                        if (token.endsWith("0") || token.endsWith("00")
                                                                        || token.endsWith(".0")) {
                                                                if (token.substring(token.length() - 2)
                                                                                .equals("00")) {
                                                                        token = token.replace("00", "0");

                                                                        mensagem.setText(mensagem.getText() + "Linha: "
                                                                                        + (i + 1)
                                                                                        + " Constante Float: " + token
                                                                                        + "\n");

                                                                        mensagem.setText(mensagem.getText() + "Linha: "
                                                                                        + (i + 2)
                                                                                        + " Constante Int: " + "0"
                                                                                        + "\n");
                                                                } else if (token.endsWith(".0")) {

                                                                        mensagem.setText(mensagem.getText() + "Linha: "
                                                                                        + (i + 1)
                                                                                        + " Constante Float: " + token
                                                                                        + "\n");

                                                                } else if (token.endsWith("0")) {
                                                                        token = token.substring(0, token.length() - 1);

                                                                        mensagem.setText(mensagem.getText() + "Linha: "
                                                                                        + (i + 1)
                                                                                        + " Constante Float: " + token
                                                                                        + "\n");

                                                                        mensagem.setText(mensagem.getText() + "Linha: "
                                                                                        + (i + 2)
                                                                                        + " Constante Int: " + "0"
                                                                                        + "\n");
                                                                } else {
                                                                        mensagem.setText(mensagem.getText() + "Linha: "
                                                                                        + (i + 1)
                                                                                        + " Constante Float: " + token
                                                                                        + "\n");
                                                                }

                                                        } else {
                                                                mensagem.setText(mensagem.getText() + "Linha: "
                                                                                + (i + 1)
                                                                                + " Constante Float: " + token
                                                                                + "\n");
                                                        }
                                                } else if (token.matches(constanteInt)) {
                                                        mensagem.setText(mensagem.getText() + "Linha: " + (i + 1)
                                                                        + " Constante Int: " + token + "\n");

                                                } else if (token.matches(constanteString)) {
                                                        if (token.contains("\n") || token.contains("\r")
                                                                        || token.contains("\t")) {

                                                                mensagem.setText(mensagem.getText() + "Linha: "
                                                                                + (i + 1)
                                                                                + " Constante String: " + token
                                                                                + "\n");

                                                                throw new ConstanteStringException(
                                                                                "Erro ao compilar o programa.",
                                                                                "Constante String inválido.");
                                                        } else {
                                                                mensagem.setText(mensagem.getText() + "Linha: "
                                                                                + (i + 1)
                                                                                + " Constante String: " + token
                                                                                + "\n");
                                                        }
                                                }

                                                else if (token.matches(identificador)) {
                                                        if (!token.matches(palavrasReservadas)) {
                                                                mensagem.setText(mensagem.getText() + "Linha: "
                                                                                + numeroLinha + "Identificador: "
                                                                                + token + "\n");
                                                        }
                                                } else if (token.matches(identificador)) {
                                                        if (!token.matches(palavrasReservadas)) {
                                                                mensagem.setText(mensagem.getText() + "Linha: "
                                                                                + numeroLinha + "Identificador: "
                                                                                + token + "\n");
                                                        }
                                                }
                                                if (!(token.matches(constanteInt)
                                                                || token.matches(constanteFloat)
                                                                || token.matches(constanteString)
                                                                || token.matches(identificador)
                                                                || token.matches(palavrasReservadas)
                                                                || token.matches(simbolos)
                                                                || token.matches(operadoresCompostos)
                                                                || token.matches(comentarioLinha))) {

                                                        if (token.startsWith("\"") && !token.endsWith("\"")) {
                                                                mensagem.setText("linha " + numeroLinha + ": "
                                                                                + " constante_string inválida\n");
                                                                throw new ConstanteStringException(
                                                                                "Constante String Invalida", token);

                                                        } else if (token.matches("[0-9]+\\.[0-9]+\\.[0-9]+")) {
                                                                mensagem.setText("linha " + numeroLinha + ": " + token
                                                                                + " constante_float inválida\n");
                                                                throw new ConstanteFloatException(
                                                                                "Constante Float Invalida", token);

                                                        } else if (!token.matches(identificador)) {
                                                                mensagem.setText("linha " + numeroLinha + ": " + token
                                                                                + " identificador inválido\n");
                                                                throw new IdentificadorExecption(
                                                                                "Identificador Invalido", token);

                                                        } else {
                                                                mensagem.setText("linha " + numeroLinha + ": " + token
                                                                                + " símbolo inválido\n");
                                                                throw new SimboloInvalidoException("Simbolo Invalido",
                                                                                token);
                                                        }
                                                }
                                        }
                                }

                                mensagem.setText("\nPrograma compilado com sucesso.");
                        }
                });

                inputMap = mensagem.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "equipe");
                mensagem.getActionMap().put("equipe", new AbstractAction() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                mensagem.setText("Desenvolvido por Bruna Carvalho e Pedro Borba");
                        }
                });

                btnEquipe.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                mensagem.setText("Desenvolvido por Bruna Carvalho e Pedro Borba");
                        }
                });
        }
}
