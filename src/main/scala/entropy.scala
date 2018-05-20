package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Entropy {

    def VonNewmann(doc: Array[Double]): Double = {
        var n = doc.size
        var entropy = 0.00
        var numberwords = doc.sum
        var degree = numberwords - 1
        var out = 0.00
        var E = degree*(degree+1)/2.00 // # of edges (sum of n = n*(n+1)/2) 
        if (E != 0){
            var c = 1.00/(2*E)
            var L = scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]]()
            for(i <- 0 to (n-1)){
              L(i) =  scala.collection.mutable.Map[Int,Double]()
            }    
            
            for(i <- 0 to (n-1)){
                for(j <- i to (n-1)){
                   if(i == j){
                     L(i)(j) = c*degree*doc(i)
                   } else if(doc(i) >= 1.00 && doc(j) >= 1.00){
                       L(i)(j) = -c
                       L(j)(i) = -c
                   } 
                }
            }
            
            val eigen = Decomposition.eigenValues(L)
            for(s <- eigen){
                entropy += -s*math.log(s)
            }
        }
        return entropy
    }

    def relative(tdm: Array[Array[Double]]): Double = {
       var n = tdm.size - 1
       var H = tdm.map(doc => VonNewmann(doc)).reduce((x,y) => x + y)
       return H/(n+1)
    }

    def GlobalQuality(layers: Array[Array[Double]], hA: Double): Double = {
       var q = 1.00 - relative(layers)/hA
       return q
    }

}
