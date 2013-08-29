package com.boosed.orm.jpa.dao.crit

// criteria builder for jpa
sealed trait Criterion
case class Eq(name: String, value: Any) extends Criterion
case class Ge(name: String, value: Number) extends Criterion
case class Gt(name: String, value: Number) extends Criterion
case class Le(name: String, value: Number) extends Criterion
case class Lt(name: String, value: Number) extends Criterion
case class Like(name: String, value: String) extends Criterion
case class Between(name: String, start: Long, end: Long) extends Criterion