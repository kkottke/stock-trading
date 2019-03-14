package de.kkottke.stocktrading.trading;

import de.kkottke.stocktrading.common.BaseVerticle;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.shareddata.LocalMap;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainTradingVerticle extends BaseVerticle {

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(MainTradingVerticle.class.getName()).subscribe(
            success -> log.info("Deployment of MainTradingVerticle succeeded"),
            error -> {
                log.error("Deployment of MainTradingVerticle failed -> shutdown application...");
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
            deployTradingVerticle(retrievedConfig),
            registerServices(retrievedConfig)
        ).flatMapCompletable(comp -> comp);

        return parentCompletable.andThen(deployments);
    }

    private Completable deployTradingVerticle(Single<JsonObject> config) {
        return config.map(conf -> new DeploymentOptions().setConfig(conf))
                     .map(option -> vertx.rxDeployVerticle(new TradingServiceVerticle(serviceDiscovery), option))
                     .map(Single::ignoreElement)
                     .flatMapCompletable(verticle -> verticle);
    }

    private Completable registerServices(Single<JsonObject> config) {
        Completable tradingService = rxPublishMessageSource("trading-event-stream", TradingService.EVENT_ADDRESS).ignoreElement();
        Completable tradingEvents = rxPublishService("trading-service", TradingService.SERVICE_ADDRESS, TradingService.class.getName()).ignoreElement();

        return Completable.mergeArray(tradingService, tradingEvents);
    }

    private Completable shareDiscovery() {
        return Completable.fromAction(() -> {
            LocalMap<String, Object> localMap = vertx.sharedData().getLocalMap("DATA");
            localMap.putIfAbsent("service-discovery", new SharableDiscovery(serviceDiscovery.getDelegate()));
        });
    }

    public static class SharableDiscovery extends ServiceDiscovery implements Shareable {

        SharableDiscovery(io.vertx.servicediscovery.ServiceDiscovery delegate) {
            super(delegate);
        }
    }
}
