package com.pbt.io

import java.util.Calendar
import java.text.SimpleDateFormat

import com.pbt.dsl.Constants

/**
  * Created by gcrowell on 2017-06-17.
  */
case class PriceDownloadRequest(val symbol: String, val startDate: Option[Calendar] = None) extends DataDownloadRequest {
  override def toUrlString: String = {
    s"https://www.google.com/finance/historical?output=csv&q=$symbol&startdate=" + (startDate match {
      case dt: Some[Calendar] => PriceDownloadRequest.toDateArg(dt.get)
      case _ => PriceDownloadRequest.toDateArg(Constants.epoch)
    })
  }
}


object PriceDownloadRequest {

  val MMM_fmt = new SimpleDateFormat("MMM")

  def toDateArg(date: Calendar): String = {
    s"${MMM_fmt.format(date.getTime)}+%2C+${date.get(Calendar.DAY_OF_MONTH)}+${date.get(Calendar.YEAR)}"
  }
}
