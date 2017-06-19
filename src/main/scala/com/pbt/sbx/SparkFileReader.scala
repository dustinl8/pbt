package com.pbt.sbx

/**
  * Created by gcrowell on 2017-06-18.
  */


/**
  * create case class to represent a row of data
  * same as header of stocks.csv
  */
case class StockCsv(symbol: String, name: String, id: String)



object SparkDemo extends App {

  override def main(args: Array[String]): Unit = {


    /**
      * SparkSession is required to do anything
      * SparkSession is a singleton object we carry around our app and send it work
      * see https://jaceklaskowski.gitbooks.io/mastering-apache-spark/spark-sql-SparkSession.html
      */

    import org.apache.spark.sql.SparkSession

    val spark: SparkSession = SparkSession.builder
      .appName("My Spark Application") // optional and will be autogenerated if not specified
      .master("local[*]") // avoid hardcoding the deployment environment
      //      .enableHiveSupport() // self-explanatory, isn't it?
      .config("spark.sql.warehouse.dir", "target/spark-warehouse")
      .getOrCreate

    import spark.implicits._


    /**
      * logical chain: (spark session)->(generic reader)->(csv reader)(csv file path)->(convert into legacy data frame)->(assign field names)->(convert in data set)
      * https://jaceklaskowski.gitbooks.io/mastering-apache-spark/spark-sql-Dataset.html
      */
    val stockReader = spark.read.csv("/Users/gcrowell/Documents/git/pbt/src/main/scala/com/pbt/stocks.csv").toDF("symbol", "name", "id").as[StockCsv]

    stockReader.show(10)


    
    //    val schemaTyped = new StructType()
    //      .add("a", IntegerType)
    //      .add("b", StringType)



  }

}