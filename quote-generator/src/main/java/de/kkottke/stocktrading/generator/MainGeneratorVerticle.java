package de.kkottke.stocktrading.generator;

import de.kkottke.stocktrading.common.BaseVerticle;
import de.kkottke.stocktrading.common.model.Company;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import static de.kkottke.stocktrading.generator.QuoteGeneratorVerticle.CONFIG_COMPANY;
import static de.kkottke.stocktrading.generator.RestApiVerticle.DEFAULT_PORT;

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
        log.debug("starting MainGeneratorVerticle");
        Completable parentCompletable = super.rxStart();

        Single<JsonObject> retrievedConfig = ConfigRetriever.create(vertx)
                                                            .rxGetConfig()
                                                            .cache();
        Completable deployments = Flowable.fromArray(
            deployGeneratorVerticles(retrievedConfig),
            deployApiVerticle(retrievedConfig),
            registerServices(retrievedConfig)
        ).flatMapCompletable(comp -> comp);

        return parentCompletable.andThen(deployments);
    }

    private Completable deployGeneratorVerticles(Single<JsonObject> config) {
        return config.toFlowable()
                     .flatMap(conf -> Flowable.fromArray(Company.values())
                                              .map(company -> new DeploymentOptions().setConfig(conf.copy()
                                                                                                    .put(CONFIG_COMPANY, company.name()))))
                     .map(option -> vertx.rxDeployVerticle(QuoteGeneratorVerticle::new, option))
                     .map(Single::ignoreElement)
                     .flatMapCompletable(verticle -> verticle);
    }

    private Completable deployApiVerticle(Single<JsonObject> config) {
        return config.map(conf -> new DeploymentOptions().setConfig(conf))
                     .map(option -> vertx.rxDeployVerticle(RestApiVerticle::new, option))
                     .map(Single::ignoreElement)
                     .flatMapCompletable(verticle -> verticle);
    }

    private Completable registerServices(Single<JsonObject> config) {
        Completable registerGenerator = rxPublishMessageSource("market-data-stream", QuoteGeneratorVerticle.ADDRESS).ignoreElement();
        // TODO: Ugly, i know. Refactor to shared service discovery
        Completable registerApi = config.map(conf -> rxPublishHttpEndpoint("market-data-api", "localhost", conf.getInteger("HTTP_PORT", DEFAULT_PORT), "/quotes")).ignoreElement();

        return Completable.mergeArray(registerGenerator, registerApi);
    }
}
