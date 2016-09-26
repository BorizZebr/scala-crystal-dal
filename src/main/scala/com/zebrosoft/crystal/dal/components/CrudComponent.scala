package com.zebrosoft.crystal.dal.components

import scala.concurrent.Future

/**
  * Created by borisbondarenko on 25.05.16.
  */
trait CrudComponent extends TypedComponent { self: DalConfig =>

  import driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  trait IdColumn[Entity] extends Table[Entity] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  }

  override type EntityTable <: IdColumn[Entity]

  def getAll: Future[Seq[Entity]] =
    db.run(table.sortBy(_.id).result)

  def getById(id: Long): Future[Option[Entity]] =
    db.run(table.filter(_.id === id).result.headOption)

  def count: Future[Int] =
    db.run(table.map(_.id).length.result)

  def insert(entity: Entity): Future[Long] =
    db.run((table returning table.map(_.id)) += entity)

  def insert(entities: Seq[Entity]): Future[Seq[Long]] =
    db.run((table returning table.map(_.id)) ++= entities)

  def update(entity: Entity): Future[Unit] =
    db.run(table.insertOrUpdate(entity)).map(_ => ())

  def delete(id: Long): Future[Unit] =
    db.run(table.filter(_.id === id).delete).map(_ => ())

  def contains(entity: Entity): Future[Boolean]
}