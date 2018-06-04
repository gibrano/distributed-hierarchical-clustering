package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Divergence extends Serializable {

  def JensenShannon(A: Array[Double], B: Array[Double]): Double = {
     var n = A.size
     var C = Array.fill(n)(0.00)
     for(i <- 0 to (n-1)){
       C(i) = 0.5*(A(i)+B(i))
     }
     var r = Entropy.VonNewmann(C)-(1/2)*(Entropy.VonNewmann(A)+Entropy.VonNewmann(B))
     return r
  }

  def computeJSD(x: Array[Int], tdm: Array[Array[Double]]) : Double = {
    var jsd = JensenShannon(tdm(x(0)),tdm(x(1)))
    return jsd
  }

}
