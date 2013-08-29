package com.boosed.orm.jpa.dialect

import org.hibernate.Hibernate
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.`type`.Type
import org.hibernate.dialect.MySQL5InnoDBDialect
import org.hibernate.dialect.function.SQLFunctionTemplate
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.engine.SessionFactoryImplementor

class MySQLDialect extends MySQL5InnoDBDialect {

  // logger.info("Creating MySQLDialect instance");
  registerFunction("bitwise_and", new MySQLBitwiseAndSQLFunction("bitwise_and", Hibernate.INTEGER));
  //registerFunction("bitwise_and", new SQLFunctionTemplate(StandardBasicTypes.LONG, "(?1 & ?2)"));
  registerFunction("hasflags", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "(?1 & ?2 = ?2)"));
}

class MySQLBitwiseAndSQLFunction(name: String, _type: Type) extends StandardSQLFunction(name, _type) {

  override def render(t: Type, args: java.util.List[_], sfi: SessionFactoryImplementor): String = args.size match {
    case size if size != 2 => throw new IllegalArgumentException("MySQLBitwiseAndSQLFunction requires 2 arguments!")
    case size =>
      val buffer = new StringBuffer(args.get(0).toString)
      buffer.append(" & ").append(args.get(1))
      buffer.toString
  }
}