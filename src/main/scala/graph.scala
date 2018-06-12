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
             A = A ++ Array(Array(i._1,j._1))  
           }    
         }
       } 
       return A
    }

    def aggregate(A: Array[Array[Double]], B: Array[Array[Double]]): Array[Array[Double]] = {
      var C = Array[Array[Double]]()
      var keys = A.keys ++ B.keys
      var C = A ++ B
      C = C.distinct()
      return C
    }

}
