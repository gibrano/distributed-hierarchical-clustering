package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Divergence {

  def JensenShannon(A: org.apache.spark.rdd.RDD[Array[Int]], B: org.apache.spark.rdd.RDD[Array[Int]], sc: SparkContext): Double = {
     var A2 = A.collect
     var B2 = B.collect
     var n = A2.size - 1
     var C = Array[Array[Double]]()
     for(i <- 0 to n){
        var x = Array.fill(n+1)(0.00)
        for(j <- 0 to n){
           x(j) = 0.5*(A2(i)(j) - B2(i)(j))
        }
        C = C ++ Array(x)
     }
     var C2 = sc.parallelize(C)
     var r = Entropy.VonNewmann(C2, sc)-(1/2)*(Entropy.VonNewmann(A, sc)+Entropy.VonNewmann(B, sc))
     return r
  }

  def computeJSD(x: Array[Int], layers: Array[org.apache.spark.rdd.RDD[Array[Int]]], sc: SparkContext) : Double = {
    var jsd = JensenShannon(layers(x(0)),layers(x(1)), sc)
    return jsd
  }

}
