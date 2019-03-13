package de.kkottke.stocktrading.common;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.MessageSource;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class BaseVerticle extends AbstractVerticle {

    private ServiceDiscovery serviceDiscovery;
    private Set<Record> registeredServices = new HashSet<>();

    @Override
    public Completable rxStart() {
        Future<Void> future = Future.future();
        ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()), svcDiscovery -> {
            log.debug("service discovery is initialized");
            this.serviceDiscovery = svcDiscovery;
            future.complete();
        });

        return future.rxSetHandler().ignoreElement();
    }

    protected Single<Record> rxPublishMessageSource(String name, String address) {
        log.debug("publish message source {} on address {}", name, address);
        Record record = MessageSource.createRecord(name, address);
        return serviceDiscovery.rxPublish(record)
                               .doOnSuccess(service -> {
                                   log.info("service {} is registered", service.getName());
                                   registeredServices.add(service);
                               });
    }

    @Override
    public Completable rxStop() {
        List<Completable> completables = registeredServices.stream()
                                                           .map(service -> {
                                                               log.debug("unpublish message source {}", service.getName());
                                                               return serviceDiscovery.rxUnpublish(service.getRegistration());
                                                           })
                                                           .collect(Collectors.toList());

        return Completable.merge(completables)
                          .doOnComplete(() -> serviceDiscovery.close());
    }
}
