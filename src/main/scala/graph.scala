package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Graph extends Serializable {

    def adjacencyMatrix(v: scala.collection.mutable.Map[Int,Double]): scala.collection.mutable.Map[Int,scala.collection.mutable.Map[Int,Double]] = {
       var A = scala.collection.mutable.Map[Int,scala.collection.mutable.Map[Int,Double]]()
       for(i <- v){
         A(i._1) = scala.collection.mutable.Map[Int,Double]() 
         for(j <- v){
           if(i._1 != j._1){  
             A(i._1)(j._1) = 1.00
           }    
         }
       } 
       return A
    }

    def aggregate(A: scala.collection.mutable.Map[Int,scala.collection.mutable.Map[Int,Double]], B: scala.collection.mutable.Map[Int,scala.collection.mutable.Map[Int,Double]]): scala.collection.mutable.Map[Int,scala.collection.mutable.Map[Int,Double]] = {
        var C = scala.collection.mutable.Map[Int,scala.collection.mutable.Map[Int,Double]]()
        var keys = A.keys ++ B.keys
        var e = keys.toArray.distinct
        for(i <- e){
          C(i) = scala.collection.mutable.Map[Int,Double]()
          for(j <- e){
            var cij = A.getOrElse(i,scala.collection.mutable.Map[Int,Double]()).getOrElse(j,0.00) + B.getOrElse(i,scala.collection.mutable.Map[Int,Double]()).getOrElse(j,0.00)
            if(cij >= 1 && i != j){
              C(i)(j) = 1.00
            }
          }
        }
        return C
    }

}
