package com.zegates.vozco;


import com.zegates.vozco.services.CouchDBServiceImpl;
import com.zegates.vozco.verticles.CouchDBVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.io.IOException;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;


/**
 * Created by sandaruwann on 10/12/17.
 */
public class Main extends AbstractVerticle {

    private CouchDBServiceImpl couchDBService;

    public static void main(String[] args) {

        VertxOptions options = new VertxOptions();
//                .setEventBusOptions(new EventBusOptions()
//                                .setClusterPublicHost("localhost")
//                                .setClusterPublicPort(8080)

//                        .setTrustAll(true)
//                );

        Vertx vertx = Vertx.vertx(options);


        vertx.deployVerticle(Main.class.getName());
//        vertx.deployVerticle(CouchDBVerticle.class.getName());
    }


  public void start(Future<Void> fut) {

      couchDBService = new CouchDBServiceImpl();
      Router router = Router.router(vertx);
            router.route().handler(io.vertx.ext.web.handler.CorsHandler.create("http://localhost:8181")
              .allowedMethod(io.vertx.core.http.HttpMethod.GET)
              .allowedMethod(io.vertx.core.http.HttpMethod.POST)
              .allowedMethod(io.vertx.core.http.HttpMethod.PUT)
              .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
              .allowCredentials(true)
              .allowedHeader("Access-Control-Allow-Method")
              .allowedHeader("Access-Control-Allow-Origin")
              .allowedHeader("Access-Control-Allow-Credentials")
              .allowedHeader("Content-Type"));


      initRouter(router);
      // Allow events for the designated addresses in/out of the event bus bridge
      BridgeOptions opts = new BridgeOptions()
              .addOutboundPermitted(new PermittedOptions().setAddress("feed"));

      // Create the event bus bridge and add it to the router.
      SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);
      router.route("/eventbus/*").handler(ebHandler);

      // Create a router endpoint for the static content.
      router.route().handler(StaticHandler.create());

      // Start the web server and tell it to use the router to handle requests.
      vertx.createHttpServer().requestHandler(router::accept).listen(8080);

      EventBus eb = vertx.eventBus();

      vertx.setPeriodic(10000l, t -> {
          // Create a timestamp string
          String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
          eb.send("feed", new JsonObject().put("now", timestamp));
      });
  }

    public void initRouter(Router router) {
        router.get("/data/:doc").handler(this::getDocument);
        router.get("/view/:design/:view").handler(this::getView);
    }

    private void getDocument(RoutingContext routingContext) {
        String docName = routingContext.request().getParam("doc");
        try {
            String document = couchDBService.getDocument(docName);
            if (document != null) {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(document);
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end("");
            }
        } catch (IOException e) {
            routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end("");
        }
    }

    private void getView(RoutingContext routingContext) {
        String view = routingContext.request().getParam("view");
        String design = routingContext.request().getParam("design");
        try {
            String document = couchDBService.getView(design, view);
            if (document != null) {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(document);
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end("");
            }
        } catch (IOException e) {
            routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end("");
        }
    }
}
