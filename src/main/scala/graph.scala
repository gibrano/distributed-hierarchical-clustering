package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Graph {

    def adjacencyMatrix(v: Array[Int]): org.apache.spark.rdd.RDD[Array[Double]] = {
       val n = v.size - 1
       var A = Array(Array.fill(n+1)(0.00))
       for( k <- 1 to n){
          A =  A ++ Array(Array.fill(n+1)(0.00))
       }

       var index = Array[Int]()
       for(i <- 0 to n){
          if (v(i) >= 1.00){
             index = index ++ Array(i)
          }
       }

       for(i <- index){
          for(j <- index){
             if (i < j){
                A(i)(j) = 1.00
                A(j)(i) = 1.00
             }
          }
       }
       var B = sc.parallelize(A)
       return B
    }

    def aggregate(A: org.apache.spark.rdd.RDD[Array[Double]], B: org.apache.spark.rdd.RDD[Array[Double]]): org.apache.spark.rdd.RDD[Array[Double]] = {
        var out = Array[Array[Double]]()
        var A2 = A.collect
        var B2 = B.collect
        var n = A2.size
        for( i <- 0 to (n-1)){
           var x = Array.fill(n)(0.00)
           for(j <- 0 to (n-1)){
               x(j) = A2(i)(j) + B2(i)(j)
               if(x(j) > 1){
                  x(j) = 1.00
               }
           }
           out = out ++ Array(x)
        }
        var out2 = sc.parallelize(out)
        return out2
    }
}
