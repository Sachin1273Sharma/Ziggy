package com.ziggy.utils

import com.typesafe.config.{Config, ConfigFactory}

 object AppConfig  {
  private val conf: Config = ConfigFactory.load().resolve()
  def getString(key : String) : String = {
        conf.getString(key)
  }
  def getInt(key : String) : Int = {
    conf.getInt(key)
  }
}