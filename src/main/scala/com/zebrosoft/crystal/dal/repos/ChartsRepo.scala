package com.zebrosoft.crystal.dal.repos

import javax.inject.Inject

import com.zebrosoft.crystal.dal.components.{CompetitorsDependentComponent, CrudComponent, DalConfig}
import com.zebrosoft.crystal.model.{Chart, ChartPoint}
import org.joda.time.LocalDate

import scala.concurrent.Future

/**
  * Created by borisbondarenko on 27.05.16.
  */
class ChartsRepo @Inject() (dalConfig: DalConfig)
  extends RepoBase(dalConfig)
  with ChartsDao {
}

trait ChartsDao
    extends CrudComponent
    with CompetitorsDependentComponent { self: DalConfig =>

  import driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  class ChartsTable(tag: Tag) extends Table[Chart](tag, tableName)
    with IdColumn[Chart]
    with CompetitorDependantColumns[Chart] {

    def amount = column[Int]("amount")

    override def * = (id.?, competitorId.?, amount, date) <>(Chart.tupled, Chart.unapply)
  }

  override type EntityTable = ChartsTable
  override type Entity = Chart
  override val table: driver.api.TableQuery[EntityTable] = TableQuery[ChartsTable]
  override val tableName = "chart"

  def getPoints(competitorId: Long, skip: Int, take: Int): Future[Seq[ChartPoint]] = {
    getByCompetitor(competitorId, skip, take).map {
      case a if a.isEmpty => Nil
      case a =>
        val seq = a.reverse
        seq.tail.foldLeft(List(ChartPoint(seq.head.date, seq.head.amount, 0))) { (a, b) =>
          ChartPoint(b.date, b.amount, b.amount - a.head.amount) :: a
        }.reverse
    }
  }

  def getByCompetitorAndDate(competitorId: Long, date: LocalDate): Future[Option[Chart]] =
    db.run(
      table
        .filter(x => x.competitorId === competitorId && x.date === date)
        .result
        .headOption)

  override def contains(entity: Chart): Future[Boolean] =
    db.run {
      table.filter { en =>
        en.competitorId === entity.competitorId &&
          en.date === entity.date
      }.result.headOption
    }.map(_.isDefined)
}