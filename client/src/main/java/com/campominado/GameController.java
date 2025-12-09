package com.campominado;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import campoMinado.*;

public class GameController {

    private final ManagedChannel channel;
    private final CampoMinadoServiceGrpc.CampoMinadoServiceBlockingStub client;

    public GameController() {
        channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        client = CampoMinadoServiceGrpc.newBlockingStub(channel);
    }

    public EstadoJogo criarMapa(int linhas, int colunas, int bombas) {
        return client.criarMapa(
                CriarMapaRequest.newBuilder()
                        .setLinhas(linhas)
                        .setColunas(colunas)
                        .setBombas(bombas)
                        .build()
        );
    }

    public EstadoJogo primeiraJogada(int x, int y) {
        return client.primeiraJogada(
                PrimeiraJogadaRequest.newBuilder()
                        .setX(x).setY(y).build()
        );
    }

    public EstadoJogo cava(int x, int y) {
        return client.cava(
                CavarRequest.newBuilder()
                        .setX(x).setY(y).build()
        );
    }

    public EstadoJogo bandeira(int x, int y) {
        return client.bandeira(
                BandeiraRequest.newBuilder()
                        .setX(x).setY(y).build()
        );
    }

    public EstadoJogo limpa(int x, int y) {
        return client.limpa(
                LimparRequest.newBuilder()
                        .setX(x).setY(y).build()
        );
    }

    public void shutdown() {
        channel.shutdownNow();
    }
}