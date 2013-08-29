package com.boosed.orm.jpa.dao.impl

import java.lang.Boolean
import java.lang.Long
import java.util.List
import java.io.Serializable
import com.boosed.orm.jpa.dao._
import com.boosed.orm.jpa.dao.crit._
import com.boosed.orm.jpa.db.struct._
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path

// generic implementation
abstract class GenericDaoImpl[T, ID <: Serializable](clazz: Class[T]) extends GenericDao[T, ID] {

  // persistence manager
  @PersistenceContext protected var manager: EntityManager = _

  // logger
  //val log = LogFactory.getLog(getClass)

  override def count = manager.createQuery("SELECT count(t) FROM " + clazz.getSimpleName + " t")
    .getSingleResult.asInstanceOf[Long]

  override def delete(id: ID) = find(id) match {
    case Some(x) => manager.remove(x)
    case None => // do nothing
  }

  override def delete(ts: T*) = manager.remove(ts)

  override def find(id: ID): Option[T] = Option(manager.find(clazz, id))

  override def find(start: Int, length: Int, sort: Tuple[String, Boolean], criteria: Criterion*): List[T] = {
    // create query
    val cb = manager.getCriteriaBuilder
    val query = cb.createQuery(clazz)
    val root = query.from(clazz)

    // ordering
    query.orderBy(if (sort.b.booleanValue) cb.asc(root.get(sort.a)) else cb.desc(root.get(sort.a)))

    //cb.between((root get "hi").asInstanceOf[Expression[Comparable[_]]], 1, 10)
    // believe this is conjunctive
    query.where(
      criteria.map {
        case Eq(name, value) => name.exists('.'==) match {
          case true =>
            // create path through a series of "get"s
            // this works to resolve path arguments such as user.address.city when
            // "address" is another managed entity
            val path = ((root: Path[T]) /: name.split('.'))(_ get _)
            //val path2 = name.split('.')./:(root: Path[T])(_ get _)
            cb.equal(path, value)
          case false => cb.equal(root get name, value)
        }
        case Ge(name, value) => cb.ge(root get name, value)
        case Gt(name, value) => cb.gt(root get name, value)
        case Le(name, value) => cb.le(root get name, value)
        case Lt(name, value) => cb.lt(root get name, value)
        case Like(name, value) => cb.like((root get name).asInstanceOf[Expression[String]], value)
        case Between(name, start, end) => cb.between[Long](root get name, start, end)
      }: _*)

    doQuery(start, length, query)
  }

  //    override def find(start: Int, length: Int, sort: Tuple[String, Boolean]): List[T] = {
  //      // create query
  //      val cb = manager.getCriteriaBuilder
  //      val query = cb.createQuery(clazz)
  //      val root = query.from(clazz)
  //
  //      // ordering
  //      query.orderBy(if (sort.b.booleanValue) cb.asc(root.get(sort.a)) else cb.desc(root.get(sort.a)))
  //
  //      // execute
  //      doQuery(start, length, query)
  //    }

  override def findAll(orders: String*): List[T] = { //manager.createQuery("select e from " concat clazz.getSimpleName
    //concat " e order by e.id").getResultList.asInstanceOf[List[T]]
    val cb = manager.getCriteriaBuilder
    val query = cb.createQuery(clazz)
    // create the root!
    val root = query.from(clazz)

    // ordering
    orders.foreach(order => query.orderBy(cb.asc(root get order)))

    manager.createQuery(query).getResultList
  }

  override def merge(t: T) = manager.merge(t)
  
  override def persist(t: T): T = { manager.persist(t); t }

  override def transact(tx: GenericDao[T, ID] => Unit): Unit = while (true) {
    // create transaction
    //val t = manager.getTransaction

    // continue until success
    try {
      tx(this)
      //t.commit
      // create new dao to perform task within
      //new GenericDaoImpl[E](clazz, new ObjectifyOpts().setBeginTransaction(true)) {
      // executes the task in the transactional context of this DAO/ofy
      //  def doTransaction(task: (GenericDao[E]) => Unit) = 

      // if no error, just return (break out of while loop)
      return
    } catch {
      case e =>
        //t.rollback
        e.printStackTrace
      //warning("could not perform transaction: " + e.getClass.getName)
    }
  }

  def doQuery(start: Int, length: Int, query: CriteriaQuery[T]) = manager.createQuery(query)
    .setFirstResult(start)
    .setMaxResults(length)
    .getResultList
}