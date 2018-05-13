package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Graph {

    def adjacencyMatrix(v: scala.collection.mutable.Map[Int, Double], sc: SparkContext): scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]] = {
       val n = v.size
       var A = scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]]()
       for( k <- 0 to (n-1)){
          A(k) = TM.zeros(n)
       }

       for(i <- 0 to (n-2)){
          if(v(i) >= 1.00){ 
            for(j <- (i+1) to (n-1)){ 
              if(v(j) >= 1.00){
                A(i)(j) = 1.00
                A(j)(i) = 1.00  
              }
            }    
          }
       }
       return A
    }

    def aggregate(A:  scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]], B:  scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]]):  scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]] = {
      var n = A.size
      for( i <- 0 to (n-1)){
        for(j <- i to (n-1)){
          A(i)(j) = A(i)(j) + B(i)(j)
          A(j)(i) = A(j)(i) + B(j)(i)
        }
      }
      return A
    }
}
