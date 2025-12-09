package com.campominado;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import campoMinado.*;

public class ClientMain {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() // sem TLS
                .build();

        CampoMinadoServiceGrpc.CampoMinadoServiceBlockingStub client =
                CampoMinadoServiceGrpc.newBlockingStub(channel);

        System.out.println("Conectado ao servidor gRPC!");

        // Testando criar mapa
        EstadoJogo estado = client.criarMapa(
                CriarMapaRequest.newBuilder()
                        .setLinhas(10)
                        .setColunas(10)
                        .setBombas(10)
                        .build()
        );

        System.out.println("Mapa criado! Jogo ativo? " + estado.getJogoAtivo());
        System.out.println("Bombas restantes: " + estado.getBombasRestantes());

        // Testando cavar
        estado = client.cava(
                CavarRequest.newBuilder().setX(3).setY(4).build()
        );

        System.out.println("Depois de cavar:");
        System.out.println("Jogo ativo? " + estado.getJogoAtivo());

        channel.shutdown();
    }
}