package com.zebrosoft.crystal.dal.repos

import javax.inject.Inject

import com.zebrosoft.crystal.dal.components.{CrudComponent, DalConfig}
import com.zebrosoft.crystal.model.Competitor
import org.joda.time.DateTime

import scala.concurrent.Future

/**
  * Created by borisbondarenko on 26.05.16.
  */
class CompetitorsRepo @Inject() (dalConfig: DalConfig)
    extends RepoBase(dalConfig)
    with CompetitorsDao {
}

trait CompetitorsDao extends CrudComponent { self: DalConfig =>

  import driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  class CompetitorsTable(tag: Tag) extends Table[Competitor](tag, tableName)
    with IdColumn[Competitor] {

    def name = column[String]("name")
    def url = column[String]("url")
    def lastCrawlStart = column[Option[DateTime]]("last_crawl_start")
    def lastCrawlFinish = column[Option[DateTime]]("last_crawl_finish")
    def crawledGoodsPages = column[Int]("crawled_goods_pages")
    def crawledReviewsPages = column[Int]("crawled_reviews_pages")
    override def * = (id.?, name, url, lastCrawlStart, lastCrawlFinish, crawledGoodsPages, crawledReviewsPages) <>
      (Competitor.tupled, Competitor.unapply)
  }

  override type Entity = Competitor
  override type EntityTable = CompetitorsTable
  override val table = TableQuery[CompetitorsTable]
  override val tableName = "competitor"

  def getByUrl(url: String): Future[Option[Competitor]] =
    db.run(table.filter(_.url === url).result.headOption)

  override def contains(entity: Competitor): Future[Boolean] =
    db.run {
      table.filter { en =>
        en.name === entity.name &&
          en.url === entity.url
      }.result.headOption
    }.map(_.isDefined)

  def updateName(url: String, name: String) = {
    getByUrl(url).map {
      // in case of we already have this comp, check the name
      // update if we need to
      case Some(comInDb) => if (comInDb.name != name) {
        val comToUpdate = comInDb.updateNameAndUrl(name, url)
        update(comToUpdate)
      }
      // in case of none -- create in DB
      case None =>
        val comToCreate = Competitor(None, name, url, None, None, 0, 0)
        insert(comToCreate)
    }
  }
}
