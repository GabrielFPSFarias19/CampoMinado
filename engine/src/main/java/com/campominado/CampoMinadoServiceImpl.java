package com.campominado;

import campoMinado.*;
import campoMinado.CampoMinadoServiceGrpc;

import io.grpc.stub.StreamObserver;

public class CampoMinadoServiceImpl extends CampoMinadoServiceGrpc.CampoMinadoServiceImplBase {

    private final Engine engine = new Engine();

    private EstadoJogo buildEstadoJogo() {
        // Monta mapa visível
        MapaVisivel.Builder mapaBuilder = MapaVisivel.newBuilder();

        for (byte i = 0; i < engine.mapa2.length; i++) {
            Linha.Builder linha = Linha.newBuilder();
            for (byte j = 0; j < engine.mapa2[i].length; j++) {
                linha.addValores(engine.mapa2[i][j]);
            }
            mapaBuilder.addLinhas(linha);
        }

        return EstadoJogo.newBuilder()
                .setMapa(mapaBuilder.build())
                .setBombasRestantes(engine.getBombas())
                .setJogoAtivo(engine.isJogo())
                .setQuadros(engine.getQuadros())
                .build();
    }

    @Override
    public void criarMapa(CriarMapaRequest request, StreamObserver<EstadoJogo> responseObserver) {
        engine.criarMapa(
                (byte) request.getLinhas(),
                (byte) request.getColunas(),
                request.getBombas()
        );

        responseObserver.onNext(buildEstadoJogo());
        responseObserver.onCompleted();
    }

    @Override
    public void primeiraJogada(PrimeiraJogadaRequest request, StreamObserver<EstadoJogo> responseObserver) {
        engine.primeiraJogada(
                (byte) request.getX(),
                (byte) request.getY()
        );

        responseObserver.onNext(buildEstadoJogo());
        responseObserver.onCompleted();
    }

    @Override
    public void cava(CavarRequest request, StreamObserver<EstadoJogo> responseObserver) {
        engine.cava(
                (byte) request.getX(),
                (byte) request.getY()
        );

        responseObserver.onNext(buildEstadoJogo());
        responseObserver.onCompleted();
    }

    @Override
    public void limpa(LimparRequest request, StreamObserver<EstadoJogo> responseObserver) {
        engine.limpa(
                (byte) request.getX(),
                (byte) request.getY()
        );

        responseObserver.onNext(buildEstadoJogo());
        responseObserver.onCompleted();
    }

    @Override
    public void bandeira(BandeiraRequest request, StreamObserver<EstadoJogo> responseObserver) {
        engine.bandeira(
                (byte) request.getX(),
                (byte) request.getY()
        );

        responseObserver.onNext(buildEstadoJogo());
        responseObserver.onCompleted();
    }
}