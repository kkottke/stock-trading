package de.kkottke.stocktrading.audit;

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

@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainAuditVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(MainAuditVerticle.class.getName()).subscribe(
            success -> log.info("Deployment of MainAuditVerticle succeeded"),
            error -> {
                log.error("Deployment of MainAuditVerticle failed -> shutdown application...");
                System.exit(1);
            }
        );
    }

    @Override
    public Completable rxStart() {
        log.debug("starting MainAuditVerticle");
        Single<JsonObject> retrievedConfig = ConfigRetriever.create(vertx)
                                                            .rxGetConfig()
                                                            .cache();

        return deployAuditVerticle(retrievedConfig);
    }

    private Completable deployAuditVerticle(Single<JsonObject> config) {
        return config.map(conf -> new DeploymentOptions().setConfig(conf))
                     .map(option -> vertx.rxDeployVerticle(AuditVerticle::new, option))
                     .map(Single::ignoreElement)
                     .flatMapCompletable(verticle -> verticle);
    }
}
