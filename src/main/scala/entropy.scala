package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Entropy extends Serializable {
    
    def VonNewmann1(layer: Array[Array[Double]]): Double = {
      var A = layer
      var n = layer.size
      var entropy = 0.00
      var sumall = layer.map(row => row.sum).reduce((x,y) => x+y)
      var dgr = layer.map(row => row.sum)
      if (sumall != 0){
        var c = 1.00/sumall
        var L = scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]]()
        for(i <- 0 to (n-1)){
          L(i) =  scala.collection.mutable.Map[Int,Double]()
        }    
         
        for(i <- 0 to (n-1)){
          for(j <- i to (n-1)){
            if(i == j){
              L(i)(j) = c*(dgr(i) - A(i)(j))
            } else {
              L(i)(j) = -c*A(i)(j)
              L(j)(i) = -c*A(j)(i)
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
    
    def VonNewmann2(layer: Array[Array[Double]], par: Array[Double], n: Int): Double = {
      var entropy = 0.00
      var TraceL1 = 0.00
      var TraceL2 = 0.00  
      var TraceL3 = 0.00
      var sumall = 2*layer.size
      var dgrees = layer.flatMap(row => row).map(i =>(i,1)).reduceByKey((x,y)=>x+y) 
      for(dgr <- degrees){ 
        TraceL1 = TraceL1 + dgr
        TraceL2 = TraceL2 + math.pow(dgr,2) + dgr
        TraceL3 = TraceL3 + math.pow(dgr,3) + math.pow(dgr,2) - dgr
      }
      var c = 1.00/sumall  
      TraceL1 = c*TraceL1
      TraceL2 = math.pow(c,2)*TraceL2
      TraceL3 = math.pow(c,3)*TraceL3  
      entropy = - par(0)*n - par(1)*TraceL1 - par(2)*TraceL2 - par(3)*TraceL3
      return entropy
    }
 
}
