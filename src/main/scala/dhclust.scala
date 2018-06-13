package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Clusters extends Serializable {
  
  def Coef(a: Double): Array[Double] = {
      var D = -a/3
      var C = math.log(a) - 0.5
      var B = 1/a
      var A = -1/(6*math.pow(a,2))
      return Array(D,C,B,A)
  }
  
  def Hierarchical(layers: Array[Array[Array[Double]]], sc: SparkContext, n: Int): Array[Array[Double]] = {
    var C = layers
    var linkages = Array[Array[Double]]()
    var l = C.size
        
    var A = layers(0)
    for(i <- 1 to (l-1)){
       A = Graph.aggregate(A,C(i))
    }
    
    var K = A.size
    var sumall = 2*K
    var maxdgr = Graph.degrees(layer).values.max
    var upperbound = maxdgr / K
    var a = upperbound/2.00
    var par = Coef(a)
    
    val hA = Entropy.VonNewmann2(A,par,n)
    println("Von Newmann entropy:", hA)
    println("Computing global quality ...")
    var t2 = System.nanoTime
    var H = sc.parallelize(C).map(layer => Entropy.VonNewmann2(layer,par,n)).reduce((x,y) => x + y)
    println("Sum of Von Newmann entropy H =",H)
    var globalquality = 1.00 - ((H/l)/hA)
    var duration2 = (System.nanoTime - t2) / 1e9d
    println("Global quality:",globalquality,"Duration time:",duration2)
    
    var q = Array[Double](globalquality)

    while(C.size > 1){
      var n = C.size
      println("Layers size", n)
      t2 = System.nanoTime
      var coords = Array[Array[Int]](Array[Int]())
      for( i <- 0 to n-2){
        for(j <- i+1 to n-1){
          coords = coords ++ Array(Array(i,j))
        }
      }
      coords = coords.filter(_.size > 0)
      duration2 = (System.nanoTime - t2) / 1e9d
      println("Numbers of pairs",coords.size,"Duration time coords:",duration2)
      t2 = System.nanoTime
      var jsdMatrix = sc.parallelize(coords).map(x => Divergence.computeJSD(x,C,par,n)).cache()
      var minimum = jsdMatrix.zipWithIndex().reduce((x,y) => Array(x,y).min)
      duration2 = (System.nanoTime - t2) / 1e9d
      println("Duration time div JS:",duration2)
      var a = coords(minimum._2.toInt)(0)
      var b = coords(minimum._2.toInt)(1)
      println("Merging layers",a,b)
      var Cx = C(a)
      var Cy = C(b)
      var newlayer = Graph.aggregate(Cx,Cy)
      C = C.filter(_ != Cx)
      C = C.filter(_ != Cy)
      C = C ++ Array(newlayer)

      t2 = System.nanoTime
      println("Computing global quality ...")
      var H = sc.parallelize(C).map(layer => Entropy.VonNewmann2(layer,par,n)).reduce((x,y) => x + y)
      println("Sum of Von Newmann entropy H =",H)
      var globalquality = 1.00 - ((H/C.size)/hA)
      duration2 = (System.nanoTime - t2) / 1e9d
      println("Global quality:",globalquality,"Duration time:",duration2)
      q = q ++ Array(globalquality)
      linkages = linkages ++ Array(Array(a,b,globalquality))
    }
    return linkages
  }
}
