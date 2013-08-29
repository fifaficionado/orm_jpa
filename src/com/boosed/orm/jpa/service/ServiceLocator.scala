package com.boosed.orm.jpa.service

import scala.reflect.Manifest

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

// use bean to inject static context
@Component("locator") class ServiceLocator extends ApplicationContextAware {
  override def setApplicationContext(ctx: ApplicationContext) =
    ServiceLocator.ctx = ctx
}

object ServiceLocator {

  @volatile var ctx: ApplicationContext = _

  def service[T: Manifest]: T = {
    //manifest[T]
    val clazz = manifest[T].erasure
    // this does the same thing
    //val clazz = implicitly[Manifest[T]].erasure
    val name = clazz.getSimpleName //.toList
    ctx.getBean(Character.toLowerCase(name.head) + name.tail, clazz).asInstanceOf[T]
  }
}