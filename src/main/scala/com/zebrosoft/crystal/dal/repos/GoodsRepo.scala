package com.zebrosoft.crystal.dal.repos

import javax.inject.Inject

import com.zebrosoft.crystal.dal.components.{CompetitorsDependentComponent, CrudComponent, DalConfig}
import com.zebrosoft.crystal.model.Good

import scala.concurrent.Future

/**
  * Created by borisbondarenko on 27.05.16.
  */
class GoodsRepo @Inject() (dalConfig: DalConfig)
  extends RepoBase(dalConfig)
  with GoodsDao {
}

trait GoodsDao
    extends CrudComponent
    with CompetitorsDependentComponent { self: DalConfig =>

  import driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  class GoodsTable(tag: Tag) extends Table[Good](tag, tableName)
    with IdColumn[Good]
    with CompetitorDependantColumns[Good] {

    def extId = column[Long]("ext_id")
    def name = column[String]("name")
    def price = column[Double]("price")
    def imgUrl = column[String]("img_url")
    def url = column[String]("url")
    override def * = (id.?, competitorId.?, extId, name, price, imgUrl, url, date) <>(Good.tupled, Good.unapply)
  }

  override type Entity = Good
  override type EntityTable = GoodsTable
  override val table = TableQuery[GoodsTable]
  override val tableName = "good"

  override def getByCompetitor(competitorId: Long, skip: Int, take: Int): Future[Seq[Good]] =
    db.run(table
      .filter(_.competitorId === competitorId)
      .sortBy(_.extId.desc)
      .drop(skip)
      .take(take)
      .result)

  override def contains(entity: Good): Future[Boolean] =
    db.run {
      table.filter { en =>
        en.competitorId === entity.competitorId &&
          en.extId === entity.extId
      }.result.headOption
    }.map(_.isDefined)
}