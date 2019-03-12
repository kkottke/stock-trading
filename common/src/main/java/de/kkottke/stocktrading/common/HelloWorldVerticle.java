package de.kkottke.stocktrading.common;

import io.reactivex.Completable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
public class HelloWorldVerticle extends AbstractVerticle {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(HelloWorldVerticle.class.getName()).subscribe(
            success -> log.info("Deployment of HelloWorldVerticle succeeded"),
            error -> {
                log.error("Deployment of HelloWorldVerticle failed -> shutdown application...");
                System.exit(1);
            }
        );
    }

    @Override
    public Completable rxStart() {
        return vertx.createHttpServer()
                    .requestHandler(request -> request.response().end("Hello World!\n"))
                    .rxListen(config().getInteger("http.port", DEFAULT_PORT)).ignoreElement();
    }
}
