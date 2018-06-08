package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Divergence extends Serializable {

  def JensenShannon(A: Array[Array[Double]], B: Array[Array[Double]], par: Array[Double]): Double = {
     var C = A
     var n = C.size
     for(i <- 0 to (n-1)){
       for(j <- i to (n-1)){
         C(i)(j) = 0.5*(A(i)(j)+B(i)(j))
         C(j)(i) = 0.5*(A(j)(i)+B(j)(i))
         if(C(i)(j) > 1){
           C(i)(j) = 1
         }
         if(C(j)(i) > 1){
           C(j)(i) = 1
         }
       }
     }
     var r = Entropy.VonNewmann2(C,par)-(1/2)*(Entropy.VonNewmann2(A,par)+Entropy.VonNewmann2(B,par))
     return r
  }

  def computeJSD(x: Array[Int], layers: Array[Array[Array[Double]]], par: Array[Double]) : Double = {
    var jsd = JensenShannon(layers(x(0)),layers(x(1)), par)
    return jsd
  }

}
