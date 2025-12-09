package com.campominado;

import java.util.Random;

public class Engine {

    private byte[][] mapa; // Mapa real
    public byte[][] mapa2; // Mapa visível para o jogador
    private byte linha; // Quantidade de linhas do mapa
    private byte coluna; // Quantidade de colunas do mapa
    int bombas; // Quantidade de bombas no mapa
    private int quadros; // Quantidade de quadros sem bombas no mapa
    boolean jogo;
    /*
    Mapa:
    0 - 8 -> Contagem de bombas (Descoberta)
    9 -> Explosão (Bomba descoberta)
    10 - 18 -> Contagem de bombas +10 (Coberta)
    19 -> Bomba +10 (Coberta)
    20 - 28 -> Contagem de bombas +10 (Coberta) + 10 (Com bandeira)
    29 -> Bomba + 10 (Coberta) + 10 (Com bandeira)

    Mapa2:
    0 - 8 -> Contagem de bombas (Descoberta)
    9 -> Explosão (Bomba descoberta)
    10 -> Quadro coberto
    20 -> Quadro com bandeira
    30 -> Bandeira colocada em quadro sem bomba (Game-Over)

     */

    public int getQuadros() {
        return quadros;
    }

    public int getBombas() {
        return bombas;
    }

    public boolean isJogo() {
        return jogo;
    }

    public void criarMapa(byte linha, byte coluna, int bombas) {
        mapa = new byte[linha][coluna];
        mapa2 = new byte[linha][coluna];
        for(byte i = 0; i < linha; i++){
            for(byte j = 0; j < coluna; j++){
                mapa[i][j] = 10;
                mapa2[i][j] = 10;
            }
        }
        this.linha = linha;
        this.coluna = coluna;
        this.bombas = bombas;
        quadros = linha * coluna;
    }

    public void bandeira(byte jogada_x, byte jogada_y){
        mapa [jogada_x][jogada_y] = (byte) (mapa[jogada_x][jogada_y] + 10);
        mapa2 [jogada_x][jogada_y] = 20;
        bombas--;
    }

    public void gameOver(){
        for(int i = 0; i < linha; i++){
            for(int j = 0; j < coluna; j++){
                if(mapa[i][j] == 19){
                    mapa2[i][j] = 9;
                }
                else if(mapa[i][j] >= 20 && mapa[i][j] != 29){
                    mapa2[i][j] = 30;
                }
            }
        }
        jogo = false;
    }

    public void cava(byte jogada_x, byte jogada_y){
        mapa [jogada_x][jogada_y] = (byte) (mapa[jogada_x][jogada_y] - 10);
        if (mapa [jogada_x][jogada_y] < 10){
            mapa2[jogada_x][jogada_y] = mapa[jogada_x][jogada_y];
            if (mapa [jogada_x][jogada_y] == 9){
                gameOver();
            }
            else {
                quadros--;
                if (quadros == 0){
                    jogo = false;
                }
            }
        }
        else{
            mapa2[jogada_x][jogada_y] = 10;
            bombas++;
        }
        if (mapa[jogada_x][jogada_y] == 0){
            limpa(jogada_x, jogada_y);
        }
    }

    public void limpa(byte jogada_x, byte jogada_y){
        for(int i = jogada_x - 1; i <= jogada_x + 1; i++){
            if(i >= 0 && i < linha) {
                for (int j = jogada_y - 1; j <= jogada_y + 1; j++) {
                    if(j >= 0 && j < coluna && mapa[i][j] >= 10 && mapa[i][j] < 20) {
                        cava((byte) i, (byte) j);
                    }
                }
            }
        }
    }

    public void primeiraJogada(byte jogada_x, byte jogada_y){
        Random random = new Random();
        int x, y;
        //Protege a posição inicial e arredores
        for(int i = jogada_x - 1; i <= jogada_x + 1; i++){
            if(i >= 0 && i < linha) {
                for (int j = jogada_y - 1; j <= jogada_y + 1; j++) {
                    if(j >= 0 && j < coluna) {
                        mapa[i][j] = 0;
                        mapa2[i][j] = 0;
                        quadros--;
                    }
                }
            }
        }
        //Posiciona bombas
        for (int i = 0; i < bombas;){
            x = random.nextInt(linha);
            y = random.nextInt(coluna);
            if (mapa[x][y] >= 10 && mapa[x][y] < 19){
                mapa[x][y] = 19;
                i++;
                for(int k = x - 1; k <= x + 1; k++){
                    if(k >= 0 && k < linha){
                        for(int l = y - 1; l <= y + 1; l++){
                            if(l >= 0 && l < coluna && mapa[k][l] != 19){
                                mapa[k][l]++;
                                if (mapa[k][l] < 10){
                                    mapa2[k][l] = mapa[k][l];
                                }
                            }
                        }
                    }
                }
            }
        }
        quadros = quadros - bombas;
        //Limpeza inicial
        for(int i = jogada_x - 1; i <= jogada_x + 1; i++){
            if(i >= 0 && i < linha) {
                for (int j = jogada_y - 1; j <= jogada_y + 1; j++) {
                    if(j >= 0 && j < coluna && mapa[i][j] ==  0) {
                        limpa((byte) i, (byte) j);
                    }
                }
            }
        }
        jogo = true;
    }

    // Imprimir os dois mapas no console (Desenvolvimento)
    public void imprimeMapas(){
        for(int i = 0; i < linha; i++){
            for(int j = 0; j < coluna; j++){
                System.out.print(mapa[i][j] + " ");
                if (mapa[i][j] < 10)
                    System.out.print(" ");
            }
            System.out.print("  ");
            for(int j = 0; j < coluna; j++){
                System.out.print(mapa2[i][j] + " ");
                if (mapa[i][j] < 10)
                    System.out.print(" ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.criarMapa((byte) 13, (byte) 6, (byte) 30);
        engine.imprimeMapas();
        engine.primeiraJogada((byte) 6, (byte) 3);
        engine.imprimeMapas();
    }
}