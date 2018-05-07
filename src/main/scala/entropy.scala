package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Entropy {

    def VonNewmann(A: Array[Array[Double]], sc: SparkContext): Double = {
        var entropy = 0.00
        var out = 0.00
        val E = A.map(i => i.sum).reduce((x,y) => x+y) // sum of edges
        if (E != 0){
            val c = 1.00/(2.00*E)
            val degr = A.map(r => r.sum).collect // degrees of nodes
            var A2 = A.collect
            val n = A2.size - 1
            var L = Array[Array[Double]]()
            for(i <- 0 to n){
                var x = Array.fill(n+1)(0.00)
                for(j <- 0 to n){
                   if(i == j){
                     x(j) = c*(degr(i) - A2(i)(j))
                   } else {
                     x(j) = -c*A2(i)(j)
                   } 
                }
                L = L ++ Array(x)
            }
            val eigen = Decomposition.eigenValues(L)
            for(s <- eigen){
                entropy += -s*math.log(s)
            }
        }
        return entropy
    }

    def relative(layers: Array[org.apache.spark.rdd.RDD[Array[Double]]], sc: SparkContext): Double = {
       var n = layers.size - 1
       var H = layers.map(C => VonNewmann(C, sc)).reduce((x,y) => x + y)
       return H/(n+1)
    }

    def GlobalQuality(layers: Array[org.apache.spark.rdd.RDD[Array[Double]]], hA: Double, sc: SparkContext): Double = {
       var q = 1.00 - relative(layers, sc)/hA
       return q
    }

}
