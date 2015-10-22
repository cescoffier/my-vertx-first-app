package io.vertx.blog.first;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This is our JUnit test for our verticle. The test uses vertx-unit, so we declare a custom runner.
 */
@RunWith(VertxUnitRunner.class)
public class MyFirstVerticleTest {

  private Vertx vertx;
  private Integer port;
  private static MongodProcess MONGO;
  private static int MONGO_PORT = 12345;

  @BeforeClass
  public static void initialize() throws IOException {
    MongodStarter starter = MongodStarter.getDefaultInstance();

    IMongodConfig mongodConfig = new MongodConfigBuilder()
        .version(Version.Main.PRODUCTION)
        .net(new Net(MONGO_PORT, Network.localhostIsIPv6()))
        .build();

    MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
    MONGO = mongodExecutable.start();
  }

  @AfterClass
  public static void shutdown() {
    MONGO.stop();
  }

  /**
   * Before executing our test, let's deploy our verticle.
   * <p/>
   * This method instantiates a new Vertx and deploy the verticle. Then, it waits in the verticle has successfully
   * completed its start sequence (thanks to `context.asyncAssertSuccess`).
   *
   * @param context the test context.
   */
  @Before
  public void setUp(TestContext context) throws IOException {
    vertx = Vertx.vertx();

    // Let's configure the verticle to listen on the 'test' port (randomly picked).
    // We create deployment options and set the _configuration_ json object:
    ServerSocket socket = new ServerSocket(0);
    port = socket.getLocalPort();
    socket.close();

    DeploymentOptions options = new DeploymentOptions()
        .setConfig(new JsonObject()
            .put("http.port", port)
            .put("db_name", "whiskies-test")
            .put("connection_string", "mongodb://localhost:" + MONGO_PORT)
        );

    // We pass the options as the second parameter of the deployVerticle method.
    vertx.deployVerticle(MyFirstVerticle.class.getName(), options, context.asyncAssertSuccess());
  }

  /**
   * This method, called after our test, just cleanup everything by closing the vert.x instance
   *
   * @param context the test context
   */
  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  /**
   * Let's ensure that our application behaves correctly.
   *
   * @param context the test context
   */
  @Test
  public void testMyApplication(TestContext context) {
    // This test is asynchronous, so get an async handler to inform the test when we are done.
    final Async async = context.async();

    // We create a HTTP client and query our application. When we get the response we check it contains the 'Hello'
    // message. Then, we call the `complete` method on the async handler to declare this async (and here the test) done.
    // Notice that the assertions are made on the 'context' object and are not Junit assert. This ways it manage the
    // async aspect of the test the right way.
    vertx.createHttpClient().getNow(port, "localhost", "/", response -> {
      response.handler(body -> {
        context.assertTrue(body.toString().contains("Hello"));
        async.complete();
      });
    });
  }

  @Test
  public void checkThatTheIndexPageIsServed(TestContext context) {
    Async async = context.async();
    vertx.createHttpClient().getNow(port, "localhost", "/assets/index.html", response -> {
      context.assertEquals(response.statusCode(), 200);
      context.assertEquals(response.headers().get("content-type"), "text/html");
      response.bodyHandler(body -> {
        context.assertTrue(body.toString().contains("<title>My Whisky Collection</title>"));
        async.complete();
      });
    });
  }

  @Test
  public void checkThatWeCanAdd(TestContext context) {
    Async async = context.async();
    final String json = Json.encodePrettily(new Whisky("Jameson", "Ireland"));
    vertx.createHttpClient().post(port, "localhost", "/api/whiskies")
        .putHeader("content-type", "application/json")
        .putHeader("content-length", Integer.toString(json.length()))
        .handler(response -> {
          context.assertEquals(response.statusCode(), 201);
          context.assertTrue(response.headers().get("content-type").contains("application/json"));
          response.bodyHandler(body -> {
            final Whisky whisky = Json.decodeValue(body.toString(), Whisky.class);
            context.assertEquals(whisky.getName(), "Jameson");
            context.assertEquals(whisky.getOrigin(), "Ireland");
            context.assertNotNull(whisky.getId());
            async.complete();
          });
        })
        .write(json)
        .end();
  }
}
