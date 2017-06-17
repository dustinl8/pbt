package com.pbt.dsl

/**
  * Created by gcrowell on 2017-06-16.
  */

trait Subject {
  def name: String
}
sealed trait DataPoint {
  def dateId: Int
}
case class PricePoint(val dateId: Int, val openPrice: Double)

trait Process[A<: Subject] {

}

trait PriceProcess {

}

trait TradeProcess {

}

