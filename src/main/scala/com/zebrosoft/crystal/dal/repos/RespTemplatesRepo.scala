package com.zebrosoft.crystal.dal.repos

import javax.inject.Inject

import com.zebrosoft.crystal.dal.components.{CrudComponent, DalConfig}
import com.zebrosoft.crystal.model.ResponseTemplate

import scala.concurrent.Future

/**
  * Created by borisbondarenko on 22.08.16.
  */
class RespTemplatesRepo @Inject() (dalConfig: DalConfig)
  extends RepoBase(dalConfig)
    with RespTemplatesDao {
}

trait RespTemplatesDao
    extends CrudComponent { self: DalConfig =>

  import driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  class RespTemplatesTable(tag: Tag) extends Table[ResponseTemplate](tag, tableName)
    with IdColumn[ResponseTemplate] {

    def name = column[String]("NAME")
    def text = column[String]("TEXT")
    override def * = (id.?, name, text) <> (ResponseTemplate.tupled, ResponseTemplate.unapply)
  }

  override type Entity = ResponseTemplate
  override type EntityTable = RespTemplatesTable
  override val table = TableQuery[RespTemplatesTable]
  override val tableName = "RESTEMPLATES"

  override def contains(entity: Entity): Future[Boolean] =
    db.run {
      table.filter { en =>
        en.name === entity.name &&
        en.text === entity.text
      }.result.headOption
    }.map(_.isDefined)
}
