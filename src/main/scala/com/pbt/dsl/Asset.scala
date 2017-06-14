package com.pbt.dsl

import java.util.Calendar

/**
  * Created by gcrowell on 2017-06-13.
  */


//TODO StockExchange has a Currency
object StockExchange extends Enumeration {
  val NYSE, AMEX, TSX, NASDAQ = Value
}


//TODO add forex data
object Currency extends Enumeration {
  val USD, CAD = Value
}


sealed trait Asset {
  def ticker: String
  def currency: Currency.Value
}

trait ExchangeTradedAsset extends Asset {
  def stockExchange: StockExchange.Value
}

trait Cash extends Asset {
  def quantity: Double
}

// TODO
trait Portfolio

trait Etf extends Asset with Portfolio

//TODO convert to custom enum (see here http://underscore.io/blog/posts/2014/09/03/enumerations.html)
//TODO StockExchange has a Currency
object OrderType extends Enumeration {
  val MARKET, LIMIT, STOP = Value
}

trait TradeOrder {
  def asset: Asset
  def orderDateTime: Calendar
  def orderType: OrderType.Value
}




case class Stock(val ticker: String, val currency: Currency.Value, val stockExchange: StockExchange.Value) extends ExchangeTradedAsset



//TODO convert Scala enums to custom (see here http://underscore.io/blog/posts/2014/09/03/enumerations.html)
