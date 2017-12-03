package com.zegates.vozco.verticles;

import com.zegates.vozco.services.CouchDBService;
import com.zegates.vozco.services.CouchDBServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;

/**
 * Created by sandaruwann on 10/16/17.
 */
public class CouchDBVerticle extends AbstractVerticle{

    private CouchDBServiceImpl couchDBService;

    public void start(Future<Void> fut) {
        couchDBService = new CouchDBServiceImpl();
        initRouter();
    }

    public void initRouter() {
        Router router = Router.router(vertx);
//        router.get("/data/:doc").handler(this::getDocument);
    }

//    private void getDocument(RoutingContext routingContext) {
//        String docName = routingContext.request().getParam("doc");
//        try {
//            String document = couchDBService.getDocument(docName);
//            if (document != null) {
//                routingContext.response()
//                        .putHeader("content-type", "application/json; charset=utf-8")
//                        .end(document);
//            } else {
//                routingContext.response()
//                        .putHeader("content-type", "application/json; charset=utf-8")
//                        .end("");
//            }
//        } catch (IOException e) {
//            routingContext.response()
//                    .putHeader("content-type", "application/json; charset=utf-8")
//                    .end("");
//        }
//    }
}
