package net.ingtra.korhal;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import net.ingtra.korhal.wrapper.HannanumWrapper;
import net.ingtra.korhal.wrapper.KomoranWrapper;
import net.ingtra.korhal.wrapper.OpenKoreanTextWrapper;


import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class KorhalJavaServer {
    private static final Logger logger = Logger.getLogger(KorhalJavaServer.class.getName());
    private Server server;
    private int port;
    private boolean called;
    private int wait_min;

    public KorhalJavaServer() {
        this(12248, 0);
    }

    public KorhalJavaServer(int port, int wait_min) {
        this.port = port;
        this.wait_min = wait_min;
        this.server = ServerBuilder.forPort(this.port)
                .addService(new TokenizerImpl())
                .build();
    }

    private void start() throws IOException, InterruptedException {
        this.server.start();
        this.logger.info("Server started. Listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                this.stop();
            }
        });

        if (this.wait_min > 0) {
            while (true) {
                Thread.sleep(this.wait_min * 60 * 1000);

                if (!this.called) {
                    this.stop();
                    break;
                }
                this.called = false;
            }
        } else {
            server.awaitTermination();
        }
    }

    private void stop() throws InterruptedException {
        this.server.shutdown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final KorhalJavaServer server = new KorhalJavaServer();
        server.start();
    }

    class TokenizerImpl extends TokenizerGrpc.TokenizerImplBase {
        @Override
        public void tokenize(TextRequest request, StreamObserver<TokensResponse> responseObserver) {
            KorhalJavaServer.this.called = true;
            String text = request.getText();
            String tokenizer = request.getTokenizer();
            List<String> args = request.getArgsList();


            TokensResponse response;

            try {
                switch (tokenizer) {
                    case "okt":
                        response = OpenKoreanTextWrapper.tokenize(text, args);
                        break;
                    case "kmr":
                        response = KomoranWrapper.tokenize(text, args);
                        break;
                    case "hnn":
                        response = HannanumWrapper.tokenize(text, args);
                        break;
                    default:
                        response = TokensResponse.newBuilder().build();
                        break;
                }

                responseObserver.onNext(response);
            } catch (Exception e) {
                responseObserver.onError(e);
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void splitSentence(TextRequest request, StreamObserver<TextsResponse> responseObserver) {
            KorhalJavaServer.this.called = true;
            String text = request.getText();
            String tokenizer = request.getTokenizer();
            List<String> args = request.getArgsList();

            TextsResponse response;

            switch (tokenizer) {
                case "okt":
                    response = OpenKoreanTextWrapper.splitSentence(text, args);
                    break;
                default:
                    response = TextsResponse.newBuilder().build();
                    break;
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
