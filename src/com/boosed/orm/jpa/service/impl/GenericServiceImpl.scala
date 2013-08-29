package com.boosed.orm.jpa.service.impl

import java.lang.Boolean
import java.io.Serializable
import com.boosed.orm.jpa.dao._
import com.boosed.orm.jpa.dao.crit._
import com.boosed.orm.jpa.db.enums._
import com.boosed.orm.jpa.db.struct._
import com.boosed.orm.jpa.service.GenericService
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback

abstract class GenericServiceImpl[T, ID <: Serializable] extends GenericService[T, ID] {

  protected var dao: GenericDao[T, ID] = _

  implicit def filters2Criteria(filters: Filter*): Seq[Criterion] = filters map { filter =>
    filter.`type` match {
      case FilterType.EQUAL if filter.name == "ids" => Eq("ids", filter.value1.toInt)
      case FilterType.EQUAL => Eq(filter.name, filter.value1)
      case FilterType.GE => Ge(filter.name, filter.value1.toLong)
      case FilterType.GT => Gt(filter.name, filter.value1.toLong)
      case FilterType.LE => Le(filter.name, filter.value1.toLong)
      case FilterType.LT => Lt(filter.name, filter.value1.toLong)
      case FilterType.LIKE => Like(filter.name, "%" concat filter.value1 concat "%")
      case FilterType.BETWEEN => Between(filter.name, filter.value1.toLong, filter.value2.toLong)
    }
  }

  // how do we get implicit to work?!?!
  override def count(filters: Filter*) =
    dao.find(0, Int.MaxValue, new Tuple("id", Boolean.TRUE), filters2Criteria(filters: _*): _*).size

  override def drop(id: ID) = dao.delete(id)

  override def drop(ts: T*) = dao.delete(ts: _*)

  override def get(id: ID) = dao.find(id)

  override def get(start: Int, length: Int, sort: String Tuple Boolean) = dao.find(start, length, sort)

  override def getAll(orders: String*) = dao.findAll(orders: _*)

  override def getByCriteria(start: Int, length: Int, sort: String Tuple Boolean, filters: Filter*) =
    dao.find(start, length, sort, filters2Criteria(filters: _*): _*)

  override def merge(t: T) = dao.merge(t)

  override def save(t: T) = dao.persist(t)

  // use this to provide transactional context inside a worker thread
  override def transact(tx: GenericDao[T, ID] => Unit) = transaction.execute(
    new TransactionCallback[Unit] {
      override def doInTransaction(status: TransactionStatus) = tx(dao)
    })

  //dao.transact(tx)
}