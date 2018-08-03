package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Divergence extends Serializable {
  
  def JensenShannon(A: Array[Array[Double]], B: Array[Array[Double]],par: Array[Double], n: Int): Double = {
    var C = Graph.aggregate(A,B, 0.5)
    var entropyA = Entropy.VonNewmann2(A,par,n)
    var entropyB = Entropy.VonNewmann2(B,par,n)
    var entropyC = Entropy.VonNewmann2(C,par,n)
    var r = math.sqrt(entropyC-(1/2)*(entropyA+entropyB))
    return r
  }  
  
  def computeJSD(x: Array[Int], layers: Array[Array[Array[Double]]], par: Array[Double], n: Int) : (Int, Int, Double) = {
    var min = JensenShannon(layers(x(0)),layers(x(1)),par,n)
    var doc1 = 0
    var doc2 = 1
    var l = x.size
    for( i <- 0 to l-2){
        for(j <- i+1 to l-1){
          var jsd = JensenShannon(layers(x(i)),layers(x(j)),par,n)
          println(i,j,jsd)
          if(min > jsd){
            min = jsd
            doc1 = i
            doc2 = j
          }  
        }
    }
    return (doc1,doc2,min)
  }

}
