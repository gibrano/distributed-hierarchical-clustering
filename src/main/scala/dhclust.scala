package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Clusters {

  def Hierarchical(C: Array[Array[Double]], sc: SparkContext): Array[Array[Int]] = {
    var layers = C
    var n = layers.size - 1
    var m = layers(0).size
    var A = Array.fill(m)(1.00)
    val hA = Entropy.VonNewmann(A)
    var clusters = sc.parallelize(0 to n).map(i => Array(i)).collect
    var aux = clusters
    
    var globalquality = Entropy.GlobalQuality(layers, hA)
    var max = globalquality
    var q = Array[Double](globalquality)

    while(layers.size > 1){
       println("Layers size", layers.size)
       var n = layers.size
       var coords = Array[Array[Int]](Array[Int]())
       for( i <- 0 to n-2){
          for(j <- i+1 to n-1){
             coords = coords ++ Array(Array(i,j))
          }
       }
       coords = coords.filter(_.size > 0)
       t2 = System.nanoTime
       var jsdMatrix = coords.map(x => Divergence.computeJSD(x, layers))
       duration2 = (System.nanoTime - t2) / 1e9d
       println("Duration time div JS:",duration2)
       var minimum = jsdMatrix.zipWithIndex.min
       var a = coords(minimum._2)(0)
       var b = coords(minimum._2)(1)
       var Cx = layers(a)
       var Cy = layers(b)
       var newlayer = Graph.aggregate(Cx,Cy)
       layers = layers.filter(_ != Cx)
       layers = layers.filter(_ != Cy)
       layers = layers ++ Array(newlayer)

       var v1 = aux(a)
       var v2 = aux(b)
       aux = aux.filter(_ != v1)
       aux = aux.filter(_ != v2)
       aux = aux.union(Array(v1.union(v2)))
  
       globalquality = Entropy.GlobalQuality(layers, hA)
       println("Global quality:",globalquality)

       if(globalquality >= max){
         max = globalquality
         clusters = aux
       }
       q = q ++ Array(globalquality)
    }
    return clusters
  }
}
