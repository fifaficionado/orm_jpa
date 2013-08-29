package com.boosed.orm.jpa.service

import java.io.Serializable
import java.lang.Boolean
import java.lang.Long
import java.util.List
import org.springframework.transaction.annotation.Transactional
import com.boosed.orm.jpa.db.struct._
import com.boosed.orm.jpa.dao.GenericDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate

trait GenericService[T, ID <: Serializable] { self: impl.GenericServiceImpl[T, ID] =>
  
  @Autowired private[service] var transaction: TransactionTemplate = _
  
  def count(filters: Filter*): Int
  @Transactional def drop(id: ID): Unit
  @Transactional def drop(ts: T*): Unit
  def get(id: ID): Option[T]
  def get(start: Int, length: Int, sort: Tuple[String, Boolean]): List[T]
  def getAll(orders: String*): List[T]
  def getByCriteria(start: Int, length: Int, sort: Tuple[String, Boolean], filters: Filter*): List[T]
  @Transactional def merge(t: T): T
  @Transactional def save(t: T): T
  
  /**
   * Save from a map and return an id.
   */
  @Transactional def save(id: => Option[String], values: Map[String, String]): T
  
  /**
   * Persistent transactional context for spawned threads.
   */
  @Transactional def transact(tx: GenericDao[T, ID] => Unit)
}