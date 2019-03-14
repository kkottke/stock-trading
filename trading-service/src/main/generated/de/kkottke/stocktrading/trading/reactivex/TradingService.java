/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package de.kkottke.stocktrading.trading.reactivex;

import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.vertx.core.json.JsonObject;
import de.kkottke.stocktrading.trading.Portfolio;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@io.vertx.lang.rx.RxGen(de.kkottke.stocktrading.trading.TradingService.class)
public class TradingService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TradingService that = (TradingService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rx.TypeArg<TradingService> __TYPE_ARG = new io.vertx.lang.rx.TypeArg<>(    obj -> new TradingService((de.kkottke.stocktrading.trading.TradingService) obj),
    TradingService::getDelegate
  );

  private final de.kkottke.stocktrading.trading.TradingService delegate;
  
  public TradingService(de.kkottke.stocktrading.trading.TradingService delegate) {
    this.delegate = delegate;
  }

  public de.kkottke.stocktrading.trading.TradingService getDelegate() {
    return delegate;
  }

  public de.kkottke.stocktrading.trading.reactivex.TradingService buyStock(int amount, JsonObject quote, Handler<AsyncResult<Portfolio>> resultHandler) { 
    delegate.buyStock(amount, quote, resultHandler);
    return this;
  }

  public Single<Portfolio> rxBuyStock(int amount, JsonObject quote) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      buyStock(amount, quote, handler);
    });
  }

  public de.kkottke.stocktrading.trading.reactivex.TradingService sellStock(int amount, JsonObject quote, Handler<AsyncResult<Portfolio>> resultHandler) { 
    delegate.sellStock(amount, quote, resultHandler);
    return this;
  }

  public Single<Portfolio> rxSellStock(int amount, JsonObject quote) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      sellStock(amount, quote, handler);
    });
  }

  public de.kkottke.stocktrading.trading.reactivex.TradingService evaluatePortfolie(Handler<AsyncResult<Portfolio>> resultHandler) { 
    delegate.evaluatePortfolie(resultHandler);
    return this;
  }

  public Single<Portfolio> rxEvaluatePortfolie() { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      evaluatePortfolie(handler);
    });
  }

  public static final String SERVICE_ADDRESS = de.kkottke.stocktrading.trading.TradingService.SERVICE_ADDRESS;

  public static  TradingService newInstance(de.kkottke.stocktrading.trading.TradingService arg) {
    return arg != null ? new TradingService(arg) : null;
  }
}
