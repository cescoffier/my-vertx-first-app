package io.vertx.blog.first;

import java.util.LinkedHashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class MyFirstVerticle extends AbstractVerticle {

	private Map<Integer, Whiskey> products = new LinkedHashMap<>();

	private void createSomeData() {
		Whiskey auchentoshan = new Whiskey("Auchentoshn 8 Years American Oak", "Scotland");
		products.put(auchentoshan.getId(), auchentoshan);
		Whiskey soccerShoe = new Whiskey("SoccerShoe", "Ireland");
		products.put(soccerShoe.getId(), soccerShoe);
	}
	
	@Override
	public void start(Future<Void> fut) {
		createSomeData();
		// Create a router object.
		Router router = Router.router(vertx);

		// Bind "/" to our hello message - so we are still compatible
		router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.putHeader("content-type", "text/html").end("<h1>Hello from my first Vert.x 3 application</h1>");
		});

		// Serve static resources from the /assets directory
		router.route("/assets/*").handler(StaticHandler.create("assets"));
		router.get("/api/whiskies").handler(this::getAll);

		// Create the HTTP server and pas the "accept" method to the request
		// handler
		vertx.createHttpServer().requestHandler(router::accept).listen(
				// Retrieve the port from the config
				// default to 8080
				config().getInteger("http.port", 8080), result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(result.cause());
					}
				});
	}
	
	private void getAll(RoutingContext routingContext) {
		routingContext.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.end(Json.encodePrettily(products.values()));
	}
}