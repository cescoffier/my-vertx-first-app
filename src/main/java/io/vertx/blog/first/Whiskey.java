/**
 * 
 */
package io.vertx.blog.first;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Willi
 *
 */
public class Whiskey {
	
	private static final AtomicInteger COUNTER = new AtomicInteger();
	
	private final int id;
	
	private String name;
	
	private String origin;
	
	public Whiskey(String name, String origin) {
		this.id = COUNTER.getAndIncrement();
		this.name = name;
		this.origin = origin;
	}

	public Whiskey() {
		this.id = COUNTER.getAndIncrement();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getOrigin() {
		return this.origin;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
}

