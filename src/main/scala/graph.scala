package dhclust

object Graph {

    def adjacencyMatrix(v: Array[Int]): Array[Array[Int]] = {
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
       return A
    }

    def sumAllEntries(A: Array[Array[Int]]): Double = {
       var sum1 = 0.00
       for( i <- A){
          for(j <- i){
              sum1 = sum1 + j
          }
       }
       return sum1
    }

    def degrees(A: Array[Array[Int]]): Array[Int] = {
        var n = A.size
        var out = Array.fill(n)(0)
        for( i <- A){
           for(j <- 0 to (i.size-1)){
               out(j) = out(j) + i(j)
           }
        }
       return out
    }

    def aggregate(A: Array[Array[Int]], B: Array[Array[Int]]): Array[Array[Int]] = {
        var n = A.size
        var out = Array[Array[Int]]()
        for( i <- 0 to (n-1)){
           var x = Array.fill(n)(0)
           for(j <- 0 to (n-1)){
               x(j) = A(i)(j) + B(i)(j)
               if(x(j) > 1){
                  x(j) = 1
               }
           }
           out = out ++ Array(x)
        }
       return out
    }

}
