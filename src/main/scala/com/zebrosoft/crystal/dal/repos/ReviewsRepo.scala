package com.zebrosoft.crystal.dal.repos

import javax.inject.Inject

import com.zebrosoft.crystal.dal.components.{CompetitorsDependentComponent, CrudComponent, DalConfig}
import com.zebrosoft.crystal.model.Review

import scala.concurrent.Future

/**
  * Created by borisbondarenko on 27.05.16.
  */
class ReviewsRepo @Inject() (dalConfig: DalConfig)
  extends RepoBase(dalConfig)
  with ReviewsDao {
}

trait ReviewsDao
    extends CrudComponent
    with CompetitorsDependentComponent { self: DalConfig =>

  import driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  class ReviewsTable(tag: Tag) extends Table[Review](tag, tableName)
    with IdColumn[Review]
    with CompetitorDependantColumns[Review] {

    def author = column[String]("AUTHOR")
    def text = column[String]("TEXT")
    override def * = (id.?, competitorId.?, author, text, date) <> (Review.tupled, Review.unapply)
  }

  override type Entity = Review
  override type EntityTable = ReviewsTable
  override val table = TableQuery[ReviewsTable]
  override val tableName = "REVIEW"

  override def contains(entity: Review): Future[Boolean] =
    db.run {
      table.filter { en =>
        en.competitorId === entity.competitorId &&
          en.author === entity.author &&
          en.text === entity.text &&
          en.date === entity.date
      }.result.headOption
    }.map(_.isDefined)
}