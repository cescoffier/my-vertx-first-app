package io.vertx.blog.first;

import io.vertx.core.json.JsonObject;

public class Whisky {

  private String id;

  private String name;

  private String origin;

  public Whisky(String name, String origin) {
    this.name = name;
    this.origin = origin;
    this.id = "";
  }

  public Whisky(JsonObject json) {
    this.name = json.getString("name");
    this.origin = json.getString("origin");
    this.id = json.getString("_id");
  }

  public Whisky() {
    this.id = "";
  }

  public Whisky(String id, String name, String origin) {
    this.id = id;
    this.name = name;
    this.origin = origin;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject()
        .put("name", name)
        .put("origin", origin);
    if (id != null && !id.isEmpty()) {
      json.put("_id", id);
    }
    return json;
  }

  public String getName() {
    return name;
  }

  public String getOrigin() {
    return origin;
  }

  public String getId() {
    return id;
  }

  public Whisky setName(String name) {
    this.name = name;
    return this;
  }

  public Whisky setOrigin(String origin) {
    this.origin = origin;
    return this;
  }

  public Whisky setId(String id) {
    this.id = id;
    return this;
  }
}