package de.kkottke.stocktrading.generator;

import de.kkottke.stocktrading.common.BaseVerticle;
import io.reactivex.Completable;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainGeneratorVerticle extends BaseVerticle {

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(MainGeneratorVerticle.class.getName()).subscribe(
            success -> log.info("Deployment of MainGeneratorVerticle succeeded"),
            error -> {
                log.error("Deployment of MainGeneratorVerticle failed -> shutdown application...");
                System.exit(1);
            }
        );
    }

    @Override
    public Completable rxStart() {
        log.debug("starting MainVerticle");
        return super.rxStart();
    }
}
