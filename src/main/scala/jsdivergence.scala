package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Divergence extends Serializable {
  
  def JensenShannon(A: Array[Array[Double]], B: Array[Array[Double]],par: Array[Double], n: Int): Double = {
    var C = Graph.aggregate(A,B)
    var n = C.size
    for(i <- 0 to (n-1)){
        C(i)(2) = 0.5*C(i)(2)
    }
    var entropyA = Entropy.VonNewmann2(A,par,n)
    var entropyB = Entropy.VonNewmann2(B,par,n)
    var entropyC = Entropy.VonNewmann2(C,par,n)
    var r = math.sqrt(entropyC-(1/2)*(entropyA+entropyB))
    return r
  }  
  
  def computeJSD(x: Array[Int], layers: Array[Array[Array[Double]]], par: Array[Double], n: Int) : Double = {
    var jsd = JensenShannon(layers(x(0)),layers(x(1)),par,n)
    return jsd
  }

}
