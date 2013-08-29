package com.boosed.orm.jpa.db.struct;

import java.io.Serializable;

import com.boosed.orm.jpa.db.enums.FilterType;

/**
 * Captures a serializable <code>FilterType</code> which should be applied as a
 * <code>Criteria</code>.
 * 
 * @author dsumera
 */
public class Filter implements Serializable {

	/* type of filter */
	public FilterType type;

	/* name of field */
	public String name;

	/* primary filtering value */
	public String value1;

	/* secondary filtering value */
	public String value2;

	public Filter() {
		// default no-arg constructor
	}

	public Filter(FilterType type, String name, String value1) {
		super();
		this.type = type;
		this.name = name;
		this.value1 = value1;
	}

	public Filter(FilterType type, String name, String value1, String value2) {
		super();
		this.type = type;
		this.name = name;
		this.value1 = value1;
		this.value2 = value2;
	}
}