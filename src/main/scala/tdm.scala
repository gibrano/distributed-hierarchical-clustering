package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object TM {
  def termDocumentMatrix(texts: org.apache.spark.rdd.RDD[String], sc: SparkContext): Array[Array[Int]] = {
    var tokens = texts.map(x => x.split(" "))
    var dwords = sc.broadcast(tokens.flatMap(token => token).distinct().collect)
    val n = dwords.value.size
    var tdm = Array[Array[Int]]()
    var documents = sc.broadcast(tokens.collect)
    for( i <- documents.value){
       var x = Array.fill(n)(0)
       for( j <- i){
         var index = dwords.value.indexOf(j)
         x(index) = 1
       }
       tdm = tdm ++ Array(x)
    }
    return tdm
  }
}
