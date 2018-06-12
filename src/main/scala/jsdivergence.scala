package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Divergence extends Serializable {
  
  def JensenShannon(A: scala.collection.mutable.Map[Int,scala.collection.mutable.Map[Int,Double]], B: scala.collection.mutable.Map[Int,scala.collection.mutable.Map[Int,Double]],par: Array[Double]): Double = {
    var C = Graph.aggregate(A,B)
    for(i <- C.keys){
      for(j <- C(i).keys){
        C(i)(j) = 0.5*C(i)(j)
      }  
    }
    var entropyA = Entropy.VonNewmann2(A,par)
    var entropyB = Entropy.VonNewmann2(B,par)
    var entropyC = Entropy.VonNewmann2(C,par)
    var r = entropyC-(1/2)*(entropyA+entropyB)
    return r
  }  
  
  def computeJSD(x: Array[Int], layers: Array[scala.collection.mutable.Map[Int,scala.collection.mutable.Map[Int,Double]]], par: Array[Double]) : Double = {
    var jsd = JensenShannon(layers(x(0)),layers(x(1)),par)
    return jsd
  }

}
