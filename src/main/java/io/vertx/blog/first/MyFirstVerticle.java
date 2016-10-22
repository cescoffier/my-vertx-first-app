package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class MyFirstVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> fut) {
	// Create a router object.
	Router router = Router.router(vertx);
	
	// Bind "/" to our hello message - so we are still compatible
	router.route("/").handler(routingContext -> {
		HttpServerResponse response = routingContext.response();
		response
			.putHeader("content-type", "text/html")
			.end("<h1>Hello from my first Vert.x 3 application</h1>");
	});
	
	// Create the HTTP server and pas the "accept" method to the request handler
	vertx
		.createHttpServer()
		.requestHandler(router::accept)
		.listen(
				// Retrieve the port from the config
				// default to 8080
				config().getInteger("http.port", 8080),
				result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(result.cause());
					}
				}
				);
  } 
}