package com.campominado;

import campoMinado.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CampoMinadoGUI extends JFrame {

    private final GameController controller = new GameController();
    private EstadoJogo estadoAtual;
    private JButton[][] botoes;

    private Timer timer;
    private int segundos = 0;

    private int bestFacil = -1;
    private int bestMedio = -1;
    private int bestDificil = -1;

    private final JLabel tempoLabel = new JLabel("Tempo: 0s");
    private final JLabel bombasLabel = new JLabel();
    private final JLabel melhorTempoLabel = new JLabel();

    private BestTimeManager.Dificuldade dificuldadeAtual = BestTimeManager.Dificuldade.FACIL;
    private final BestTimeManager timeManager = new BestTimeManager();

    private JPanel painelTabuleiro;
    private JPanel painelTopo;
    private JPanel painelMenu;

    private boolean jogoEncerrado = false;

    public CampoMinadoGUI() {
        setTitle("Campo Minado - gRPC");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        criarMenuDificuldade();

        setVisible(true);
    }

    // ===================== MENU =====================
    private void criarMenuDificuldade() {
        getContentPane().removeAll();

        painelMenu = new JPanel(new GridLayout(4, 1, 10, 10));

        JLabel titulo = new JLabel("Selecione a dificuldade", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        JButton facil = new JButton("Fácil (8x10 - 10 bombas)");
        JButton medio = new JButton("Médio (14x18 - 40 bombas)");
        JButton dificil = new JButton("Difícil (20x30 - 99 bombas)");

        facil.addActionListener(e -> iniciarJogo("FACIL",8, 10, 10));
        medio.addActionListener(e -> iniciarJogo("MEDIO",14, 18, 40));
        dificil.addActionListener(e -> iniciarJogo("DIFICIL", 20, 24, 99));

        painelMenu.add(titulo);
        painelMenu.add(facil);
        painelMenu.add(medio);
        painelMenu.add(dificil);

        add(painelMenu, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // ===================== INICIAR JOGO =====================
    private void iniciarJogo(String dificuldade, int linhas, int colunas, int bombas) {
        switch (dificuldade) {
            case "FACIL":
                dificuldadeAtual = BestTimeManager.Dificuldade.FACIL;
                break;
            case "MEDIO":
                dificuldadeAtual = BestTimeManager.Dificuldade.MEDIO;
                break;
            case "DIFICIL":
                dificuldadeAtual = BestTimeManager.Dificuldade.DIFICIL;
                break;
        }
        getContentPane().removeAll();

        estadoAtual = controller.criarMapa(linhas, colunas, bombas);
        segundos = 0;
        jogoEncerrado = false;

        painelTopo = new JPanel();
        painelTopo.add(melhorTempoLabel);
        painelTopo.add(tempoLabel);
        painelTopo.add(bombasLabel);
        add(painelTopo, BorderLayout.NORTH);

        atualizarLabelMelhorTempo();
        construirTabuleiro();
    }

    // ===================== TABULEIRO =====================
    private void construirTabuleiro() {
        int linhas = estadoAtual.getMapa().getLinhasCount();
        int colunas = estadoAtual.getMapa().getLinhas(0).getValoresCount();

        painelTabuleiro = new JPanel(new GridLayout(linhas, colunas));
        botoes = new JButton[linhas][colunas];

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton botao = new JButton();
                botao.setFont(new Font("Arial", Font.BOLD, 16));

                final int x = i, y = j;

                botao.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (jogoEncerrado) {
                            criarMenuDificuldade();
                            return;
                        }
                        tratarClique(x, y, e);
                    }
                });

                botoes[i][j] = botao;
                painelTabuleiro.add(botao);
            }
        }

        add(painelTabuleiro, BorderLayout.CENTER);
        atualizarTabuleiro();
    }

    private void atualizarTabuleiro() {
        bombasLabel.setText("Bombas: " + estadoAtual.getBombasRestantes());

        int linhas = botoes.length;
        int colunas = botoes[0].length;

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                int valor = estadoAtual.getMapa().getLinhas(i).getValores(j);
                JButton b = botoes[i][j];

                if (estadoAtual.getQuadros() == linhas * colunas) {
                    b.setText("");
                    b.setBackground(Color.ORANGE);
                    continue;
                }

                switch (valor) {
                    case 10: // coberto
                        b.setText("");
                        b.setBackground(Color.ORANGE);
                        break;
                    case 20: // bandeira
                        b.setText("F");
                        b.setBackground(Color.ORANGE);
                        break;
                    case 30: // explodido
                        b.setText("X");
                        b.setBackground(Color.RED);
                        break;
                    case 9: // bomba
                        b.setText("ó");
                        b.setBackground(Color.RED);
                        break;
                    default:
                        b.setText(valor == 0 ? "" : String.valueOf(valor));
                        b.setBackground(Color.LIGHT_GRAY);
                        break;
                }
            }
        }

        revalidate();
        repaint();
    }

    // ===================== CLIQUE =====================
    private void tratarClique(int x, int y, MouseEvent e) {
        int valor = estadoAtual.getMapa().getLinhas(x).getValores(y);
        int total = botoes.length * botoes[0].length;

        if (estadoAtual.getQuadros() == total) {
            iniciarTimer();
            jogarPrimeiraJogada(x, y);
            return;
        }

        if (valor == 10) {
            if (SwingUtilities.isLeftMouseButton(e)) jogarCavar(x, y);
            else if (SwingUtilities.isRightMouseButton(e)) jogarBandeira(x, y);
        } else if (valor == 20) {
            if (SwingUtilities.isRightMouseButton(e)) jogarCavar(x, y);
        } else if (valor > 0) {
            if (SwingUtilities.isLeftMouseButton(e) &&
                    contarBandeirasAoRedor(x, y) == valor) {
                jogarLimpar(x, y);
            }
        }
    }

    private int contarBandeirasAoRedor(int x, int y) {
        int c = 0;
        int linhas = botoes.length;
        int colunas = botoes[0].length;

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < linhas && j >= 0 && j < colunas) {
                    if (estadoAtual.getMapa().getLinhas(i).getValores(j) == 20) c++;
                }
            }
        }
        return c;
    }

    // ===================== AÇÕES =====================
    private void jogarPrimeiraJogada(int x, int y) {
        estadoAtual = controller.primeiraJogada(x, y);
        atualizarTabuleiro();
    }

    private void jogarCavar(int x, int y) {
        estadoAtual = controller.cava(x, y);
        atualizarTabuleiro();
        verificarFim();
    }

    private void jogarLimpar(int x, int y) {
        estadoAtual = controller.limpa(x, y);
        atualizarTabuleiro();
        verificarFim();
    }

    private void jogarBandeira(int x, int y) {
        estadoAtual = controller.bandeira(x, y);
        atualizarTabuleiro();
    }

    // ===================== TIMER =====================
    private void iniciarTimer() {
        if (timer != null) timer.stop();

        timer = new Timer(1000, e -> {
            segundos++;
            tempoLabel.setText("Tempo: " + segundos + "s");
        });
        timer.start();
    }

    private void atualizarLabelMelhorTempo() {
        int best = timeManager.getBestTime(dificuldadeAtual);
        if (best == -1) {
            melhorTempoLabel.setText("Melhor: --");
        } else {
            melhorTempoLabel.setText("Melhor: " + best + "s");
        }
    }


    // ===================== FIM DE JOGO =====================
    private void verificarFim() {
        if (!estadoAtual.getJogoAtivo()) {
            timer.stop();
            jogoEncerrado = true;

            if (estadoAtual.getQuadros() == 0) {
                JOptionPane.showMessageDialog(this, "Você venceu!");
                timeManager.updateBestTime(dificuldadeAtual, segundos);
                atualizarLabelMelhorTempo();
            } else {
                JOptionPane.showMessageDialog(this, "Você perdeu!");
            }
        }
    }

    public static void main(String[] args) {
        new CampoMinadoGUI();
    }
}
