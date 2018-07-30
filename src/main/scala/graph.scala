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

    def aggregate(A: Array[Array[Double]], B: Array[Array[Double]], v: Double): Array[Array[Double]] = {
      var C = A ++ B
      var out = Array[Array[Double]]()
      var encountered = scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Boolean]]()
      var n = C.size
      for(i <- 0 to (n-1)){
        if( !encountered(C(i)(0))(C(i)(1)) ){
          out = out ++ C(i)
        } 
      }
      return out
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
