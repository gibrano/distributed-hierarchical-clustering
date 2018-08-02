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
  
  def Combine(x: Array[Int]): Array[Array[Int]] = {
      var pairs = Array[Array[Int]](Array[Int]())
      var l = x.size
      for( i <- 0 to l-2){
        for(j <- i+1 to l-1){
          pairs = pairs ++ Array(Array(x(i),x(j)))
        }
      }
      pairs = pairs.filter(_.size > 0)
      return pairs
  }
  
  def getPairs(layers: Array[Array[Array[Double]]], par: Array[Double], n: Int, sc: SparkContext): Array[Array[Int]] = {
      var C = layers
      var pairs = Array[Array[Int]](Array[Int]())
      var c = 100
      while(C.size > 0){
        var l = C.size
        if(l < 120){
          c = l
        }
        var t = System.nanoTime
        var jsdMatrix = Array[Double]()
        var index = (1 to (l-1))
        for(i <- index){
          jsdMatrix = jsdMatrix ++ Array(Divergence.computeJSD(Array(0,i),C,par,n))
        }
        var jsdWithIndex = jsdMatrix.zipWithIndex
        println("getPairs - jsdMatrix - time:",(System.nanoTime - t) / 1e9d)
        t = System.nanoTime
        var x = jsdWithIndex.sorted.take(c)
        println("getPairs - sort - time:",(System.nanoTime - t) / 1e9d)
        t = System.nanoTime
        var y = x.map(i => index(i._2.toInt)) ++ Array(0)
        pairs = pairs ++ Combine(y)
        println("getPairs - Combine - time:",(System.nanoTime - t) / 1e9d)
        t = System.nanoTime
        var comp = index.toBuffer -- y
        //for(i <- comp){
        //  
        //}
        C = sc.parallelize(comp).map(i => C(i)).collect
        println("getPairs - Filter C - time:",(System.nanoTime - t) / 1e9d)
      }
      pairs = pairs.filter(_.size > 0)
      return pairs
  }
  
  def Hierarchical(layers: Array[Array[Array[Double]]], sc: SparkContext, n: Int): Array[Array[Double]] = {
    var C = layers
    var linkages = Array[Array[Double]]()
    var l = C.size
    println("Aggregating complete layers ...")
    //var A = layers(0)
    //for(i <- 1 to (l-1)){
    //   A = Graph.aggregate(A,C(i), 0.5)
    //}
    var All = sc.parallelize(C).flatMap(i => i).collect
    var A = Array[Array[Double]]()
    var encountered = scala.collection.mutable.Map[Int, scala.collection.mutable.Map[Int,Boolean]]()
    var m = All.size
    for(i <- 0 to (m-1)){
      if( !encountered.contains(All(i)(0).toInt) ){
        encountered(All(i)(0).toInt) = scala.collection.mutable.Map[Int,Boolean](All(i)(1).toInt -> false)
      }    
      if(!encountered(All(i)(0).toInt).getOrElse(All(i)(1).toInt, false)){
        A = A ++ Array(All(i))
        encountered(All(i)(0).toInt) = scala.collection.mutable.Map[Int,Boolean](All(i)(1).toInt -> true)
      } 
    }

    println("Computing taylor coefficients  ...")
    var K = A.size
    var sumall = 2*K
    var maxdgr = Graph.degrees(A).values.max
    var upperbound = maxdgr / K
    var center = upperbound/2.00
    var par = Coef(center)
    
    println("Computing Von Newmann entropy of A ...")
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
      l = C.size
      println("Layers size", l)
      t2 = System.nanoTime      
      var coords = getPairs(C,par,n, sc)
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
      var newlayer = Graph.aggregate(Cx,Cy,0.5)
      C = C.filter(_ != Cx)
      C = C.filter(_ != Cy)
      C = C ++ Array(newlayer)

      t2 = System.nanoTime
      println("Computing global quality ...")
      println("Number of Layers" + C.size)
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
