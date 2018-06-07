package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Clusters {

  def Hierarchical(layers: Array[Array[Array[Double]]], sc: SparkContext): Array[Array[Int]] = {
    var linkages = Array[Array[Double]]()
    var l = layers.size
    var m = layers(0).size
    
    var A = layers(0)
    for(i <- 1 to (l-1)){
       A = Graph.aggregate(A,layers(i))
    }
    
    val hA = Entropy.VonNewmann(A)
        
    var globalquality = Entropy.GlobalQuality(layers, hA)
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
       var t2 = System.nanoTime
       var jsdMatrix = sc.parallelize(coords).map(x => Divergence.computeJSD(x, layers))
       var duration2 = (System.nanoTime - t2) / 1e9d
       println("Duration time div JS:",duration2)
       var minimum = jsdMatrix.zipWithIndex.min
       var a = coords(minimum._2.toInt)(0)
       var b = coords(minimum._2.toInt)(1)
       var Cx = layers(a)
       var Cy = layers(b)
       var newlayer = Graph.aggregate(Cx,Cy)
       layers = layers.filter(_ != Cx)
       layers = layers.filter(_ != Cy)
       layers = layers ++ Array(newlayer)

  
       globalquality = Entropy.GlobalQuality(layers, hA)
       println("Global quality:",globalquality)
       q = q ++ Array(globalquality)
       linkages = linkages ++ Array(Array(a,b,globalquality))
    }
    return linkages
  }
}
