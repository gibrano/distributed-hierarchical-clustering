package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Graph extends Serializable {

    def adjacencyMatrix(v: scala.collection.mutable.Map[Int,Double]): Array[Array[Double]] = {
       var A = Array[Array[Double]]()
       for(i <- v){
         for(j <- v){
           if(i._1 < j._1){
             A = A ++ Array(Array(i._1.toDouble,j._1.toDouble,1.0))  
           }    
         }
       } 
       return A
    }

    def aggregate(A: Array[Array[Double]], B: Array[Array[Double]]): Array[Array[Double]] = {
      var C = A ++ B
      var n = C.size
      var i = 0  
      while(i < (C.size-1)){
        for(j <- (i+1) to (C.size-1)){
          if(C(i).deep == C(j).deep){
            C = C.filter(_ != C(j))
          }
        }
        i = i + 1  
      }
      return C
    }
    
    def degrees(A: Array[Array[Double]]): scala.collection.mutable.Map[Int,Double] = {
       var n = A.size
       var d = scala.collection.mutable.Map[Int,Double]()
       for(i <- 0 to (n-1)){
         var a = A(i)(0).toInt
         var b = A(i)(1).toInt
         d(a) = d.getOrElse(a, 0.00) + A(i)(2)
         d(b) = d.getOrElse(b, 0.00) + A(i)(2)
       }
       return d 
    }
    
}
