package com.campominado;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class EngineServer {

    public static void main(String[] args) throws Exception {

        Server server = ServerBuilder.forPort(50051)
                .addService(new CampoMinadoServiceImpl())
                .build();

        System.out.println("Servidor gRPC do Campo Minado iniciado na porta 50051...");
        server.start();
        server.awaitTermination();
    }
}
