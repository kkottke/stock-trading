package de.kkottke.stocktrading.generator;

import de.kkottke.stocktrading.common.BaseVerticle;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.kkottke.stocktrading.generator.QuoteGeneratorVerticle.CONFIG_COMPANY;

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
        Completable parentCompletable = super.rxStart();

        List<Completable> genVerticles = Arrays.stream(Company.values())
                                               .map(company -> new DeploymentOptions().setConfig(config().copy().put(CONFIG_COMPANY, company.name())))
                                               .map(option -> vertx.rxDeployVerticle(QuoteGeneratorVerticle::new, option))
                                               .map(Single::ignoreElement)
                                               .collect(Collectors.toList());
        Completable genCompletable = Completable.merge(genVerticles);

        return parentCompletable.andThen(genCompletable);
    }
}
