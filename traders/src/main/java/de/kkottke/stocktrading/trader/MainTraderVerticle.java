package de.kkottke.stocktrading.trader;

import de.kkottke.stocktrading.common.model.Company;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import static de.kkottke.stocktrading.trader.TraderVerticle.CONFIG_COMPANY;

@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainTraderVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(MainTraderVerticle.class.getName()).subscribe(
            success -> log.info("Deployment of MainTraderVerticle succeeded"),
            error -> {
                log.error("Deployment of MainTraderVerticle failed -> shutdown application...");
                System.exit(1);
            }
        );
    }

    @Override
    public Completable rxStart() {
        log.debug("starting MainTraderVerticle");
        Single<JsonObject> retrievedConfig = ConfigRetriever.create(vertx)
                                                            .rxGetConfig()
                                                            .cache();

        return deployTraderVerticles(retrievedConfig);
    }

    private Completable deployTraderVerticles(Single<JsonObject> config) {
        return config.toFlowable()
                     .flatMap(conf -> Flowable.fromArray(Company.values())
                                              .map(company -> new DeploymentOptions().setConfig(conf.copy()
                                                                                                    .put(CONFIG_COMPANY, company.name()))))
                     .map(option -> vertx.rxDeployVerticle(TraderVerticle::new, option))
                     .map(Single::ignoreElement)
                     .flatMapCompletable(verticle -> verticle);
    }

}
