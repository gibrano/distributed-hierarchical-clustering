package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Graph {

    def adjacencyMatrix(v: scala.collection.mutable.Map[Int, Double]): scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]] = {
       val n = v.size
       var A = scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]]()
       for(i <- 0 to (n-1)){
         for(j <- i to (n-1)){  
           if(i != j && v(i) >= 1.00 && v(j) >= 1.00){
             A.update(i,scala.collection.mutable.Map[Int,Double](j -> 1.00))
             A.update(j,scala.collection.mutable.Map[Int,Double](i -> 1.00))
           }  
         }
       }
       return A
    }

    def aggregate(A:  scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]], B:  scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]]):  scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Double]] = {
      var n = A.size
      for( i <- 0 to (n-1)){
        for(j <- i to (n-1)){
          var aij = A.getOrElse(i,scala.collection.mutable.Map[Int,Double](j -> 0.00)).getOrElse(j, 0.00)
          var bij = B.getOrElse(i,scala.collection.mutable.Map[Int,Double](j -> 0.00)).getOrElse(j, 0.00)
          A.update(i, scala.collection.mutable.Map[Int,Double](j -> (aij+bij)))
          A.update(j, scala.collection.mutable.Map[Int,Double](i -> (aij+bij)))
        }
      }
      return A
    }
}
