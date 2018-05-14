package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object TM {
  def termDocumentMatrix(texts: org.apache.spark.rdd.RDD[String], sc: SparkContext): scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]] = {
    var tokens = texts.map(x => x.split(" "))
    var dwords = sc.broadcast(tokens.flatMap(token => token).distinct().collect)
    val n = dwords.value.size
    var tdm = scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int, Double]]()
    var documents = sc.broadcast(tokens.collect)
    var k = 0
    for(i <- documents.value){
      var x = scala.collection.mutable.Map[Int, Double]()
       for(j <- i){
         var index = dwords.value.indexOf(j)
         x(index) = 1.00
       }
       tdm(k) = x
       k = k + 1
    }
    return tdm
  }
}
