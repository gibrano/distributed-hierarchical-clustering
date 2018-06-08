package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Entropy extends Serializable {
    
    def Coef(a: Double): Array[Double] = {
      var C = -(a/2) 
      var B = math.log(a)
      var A = 1/(2*a)
      return Array(C,B,A)
    }
    
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
    
    def VonNewmann2(layer: Array[Array[Double]]): Double = {
      var A = layer
      var n = layer.size
      var entropy = 0.00
      var sumall = layer.map(row => row.sum).reduce((x,y) => x+y)
      var K = sumall/2
      var dgr = layer.map(row => row.sum)
      var maxdgr = dgr.reduce((x,y) => math.max(x,y))
      var upperbound = maxdgr / K
      var a = upperbound/2.00
      var par = Coef(a)
      if (sumall != 0){
        var c = 1.00/sumall
        var TraceL1 = 0.00
        var TraceL2 = 0.00  
        for(i <- 0 to (n-1)){
          for(j <- i to (n-1)){
            if(i == j){
              TraceL1 = TraceL1 + c*(dgr(i) - A(i)(j))
              TraceL2 = TraceL2 + c*c*(dgr(i) - A(i)(j))*(dgr(i) - A(i)(j))
            } else {
              TraceL2 = TraceL2 + c*c*A(i)(j)*A(i)(j) + c*c*A(j)(i)*A(j)(i)
            } 
          }
        }
        entropy = - par(0)*n - par(1)*TraceL1 - par(2)*TraceL2
      }
      return entropy
    }

    def TracePowMatrix(A: Array[Array[Double]], pow: Int): Double = {
      var n = A.size
      var trace = 0.0
      if(pow == 1){  
        for(i <- 0 to (n-1)){
          trace = trace + A(i)(i)
        }
      } else if(pow == 2){
        for(i <- 0 to (n-1)){
          trace = trace + vectorProd(A(i),A(i))
        }
      }    
      return trace  
    }
         
    def vectorProd(x: Array[Double], y: Array[Double]): Double = {
       var n = x.size
       var sum = 0.00 
       for(i <- 0 to (n-1)){
          sum = sum + x(i)*y(i)
       }
       return sum 
    }     
}
