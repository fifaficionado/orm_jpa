package com.boosed.orm.jpa.db.enums;

/**
 * Filter types, these should match the Criteria types. Created to make filters passable to rpc endpoint.
 * 
 * @author dsumera
 */
public enum FilterType {
	EQUAL, GE, GT, LE, LT, LIKE, BETWEEN
}