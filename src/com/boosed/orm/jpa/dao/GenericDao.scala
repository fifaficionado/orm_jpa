package com.boosed.orm.jpa.dao

import java.io.Serializable
import java.lang.Boolean
import java.lang.Long
import java.util.List

import com.boosed.orm.jpa.dao.crit.Criterion
import com.boosed.orm.jpa.db.struct.Tuple

// generic dao IF declaration
trait GenericDao[T, ID <: Serializable] {
  def count: Long
  def delete(id: ID): Unit
  def delete(ts: T*): Unit
  def find(id: ID): Option[T]
  def find(start: Int, length: Int, sort: Tuple[String, Boolean], criteria: Criterion*): List[T]
  def findAll(orders: String*): List[T]
  /* attach the entity back into session */
  def merge(t: T): T
  def persist(t: T): T
  
  //@Transactional(propagation = Propagation.REQUIRES_NEW)
  def transact(tx: GenericDao[T, ID] => Unit)
}