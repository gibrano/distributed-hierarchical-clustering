package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Graph {

    def adjacencyMatrix(v: Array[Double]): Array[Double] = {
       val n = v.size
       var A = Array.fill(n*n)(0.00)
       for(i <- 0 to (n-1)){
         for(j <- i to (n-1)){  
           if(i != j && v(i) >= 1.00 && v(j) >= 1.00){
             A(i*n+j) = 1.00
             A(j*n+i) = 1.00  
           } 
         }
       }
       return A
    }

    def aggregate(A:  Array[Double], B:  Array[Double]): Array[Double] = {
      var n = A.size
      var C = Array.fill(n)(0.00)
      for( i <- 0 to (n-1)){
        var cij = A(i) + B(i)
        if(cij > 0){  
          C(i) = cij/cij
        }    
      }
      return C
    }
}
