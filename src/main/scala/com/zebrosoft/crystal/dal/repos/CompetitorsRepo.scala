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

    def name = column[String]("NAME")
    def url = column[String]("URL")
    def lastCrawlStart = column[Option[DateTime]]("LAST_CRAWL_START")
    def lastCrawlFinish = column[Option[DateTime]]("LAST_CRAWL_FINISH")
    def crawledGoodsPages = column[Int]("CRAWLED_GOODS_PAGES")
    def crawledReviewsPages = column[Int]("CRAWLED_REVIEWS_PAGES")
    override def * = (id.?, name, url, lastCrawlStart, lastCrawlFinish, crawledGoodsPages, crawledReviewsPages) <>
      (Competitor.tupled, Competitor.unapply)
  }

  override type Entity = Competitor
  override type EntityTable = CompetitorsTable
  override val table = TableQuery[CompetitorsTable]
  override val tableName = "COMPETITOR"

  def getByUrl(url: String): Future[Option[Competitor]] =
    db.run(table.filter(_.url === url).result.headOption)

  override def contains(entity: Competitor): Future[Boolean] =
    db.run {
      table.filter { en =>
        en.name === entity.name &&
          en.url === entity.url
      }.result.headOption
    }.map(_.isDefined)
}
