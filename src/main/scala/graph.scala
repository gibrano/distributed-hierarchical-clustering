package dhclust

object Graph {

    def adjacencyMatrix(v: Array[Int]): org.apache.spark.rdd.RDD[Array[Int]] = {
       val n = v.size - 1
       var A = Array(Array.fill(n+1)(0))
       for( k <- 1 to n){
          A =  A ++ Array(Array.fill(n+1)(0))
       }

       var index = Array[Int]()
       for(i <- 0 to n){
          if (v(i) >= 1){
             index = index ++ Array(i)
          }
       }

       for(i <- index){
          for(j <- index){
             if (i < j){
                A(i)(j) = 1
                A(j)(i) = 1
             }
          }
       }
       var B = sc.parallelize(A)
       return B
    }

    def aggregate(A: org.apache.spark.rdd.RDD[Array[Int]], B: org.apache.spark.rdd.RDD[Array[Int]]): org.apache.spark.rdd.RDD[Array[Int]] = {
        var out = Array[Array[Int]]()
        var A2 = A.collect
        var B2 = B.collect
        var n = A2.size
        for( i <- 0 to (n-1)){
           var x = Array.fill(n)(0)
           for(j <- 0 to (n-1)){
               x(j) = A2(i)(j) + B2(i)(j)
               if(x(j) > 1){
                  x(j) = 1
               }
           }
           out = out ++ Array(x)
        }
        var out2 = sc.parallelize(out)
        return out2
    }
}
