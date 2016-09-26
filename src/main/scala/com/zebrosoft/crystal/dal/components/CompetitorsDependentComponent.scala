package com.zebrosoft.crystal.dal.components

import org.joda.time.LocalDate

import scala.concurrent.Future

/**
  * Created by borisbondarenko on 27.05.16.
  */
trait CompetitorsDependentComponent extends TypedComponent
    { self: DalConfig =>

  import driver.api._

  trait CompetitorDependantColumns[B] extends Table[Entity] {
    def competitorId = column[Long]("competitor_id")
    def date = column[LocalDate]("date")
  }

  override type EntityTable <: CompetitorDependantColumns[Entity]

  def getByCompetitor(competitorId: Long, skip: Int, take: Int): Future[Seq[Entity]] =
    db.run(table
      .filter(_.competitorId === competitorId)
      .sortBy(_.date.desc)
      .drop(skip)
      .take(take)
      .result)
}
