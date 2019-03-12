package de.kkottke.stocktrading.common;

import io.vertx.core.VertxOptions;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher extends io.vertx.core.Launcher {

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());

        new Launcher().dispatch(args);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        log.debug("enable clustering");
        options.setClustered(true).setClusterHost("localhost");
    }

}
