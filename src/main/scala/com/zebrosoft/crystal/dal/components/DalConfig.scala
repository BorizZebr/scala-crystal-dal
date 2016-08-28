package com.zebrosoft.crystal.dal.components

import slick.driver.JdbcProfile

/**
  * Created by borisbondarenko on 26.05.16.
  */
trait DalConfig {
  val driver: JdbcProfile
  val db: JdbcProfile#Backend#Database
}