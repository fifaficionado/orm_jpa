package com.boosed.orm.jpa.db.struct;

import java.io.Serializable;

/**
 * Specialized <code>Tuple</code> that is serializable wrt GWT.
 * 
 * @param <A>
 * @param <B>
 */
public class Tuple<A, B> implements Serializable {

	public A a;
	public B b;

	public Tuple(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public Tuple() {
		// default no-arg constructor
	}
}