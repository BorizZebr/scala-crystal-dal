package com.zebrosoft.crystal.dal.components

import java.sql.Timestamp

import org.joda.time.{DateTime, LocalDate}

/**
  * Created by borisbondarenko on 25.05.16.
  */
trait DateColumnMapper { self: DalConfig =>

  import driver.api._

  implicit val jodaDateColumnType = MappedColumnType.base[LocalDate, Timestamp](
    jodaTime => new Timestamp(jodaTime.toDateTimeAtStartOfDay.getMillis),
    sqlTime => new LocalDate(sqlTime)
  )

  implicit val jodaTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    jodaTime => new Timestamp(jodaTime.getMillis),
    sqlTime => new DateTime(sqlTime)
  )
}
