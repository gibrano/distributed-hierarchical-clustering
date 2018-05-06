package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.log4j.{Level, Logger}

object App {
  def main(args: Array[String]) {
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    val conf = new SparkConf(true).setAppName("Distributed Hierarchical Clustering")
    val sc = new SparkContext(conf)
    
    println("Reading s3 file ...")
    val filename = args(0)
    val tweets = sc.textFile("s3n://AKIAJ3WA6NVC2KBLWPKQ:wBcSpSmfm1uYy1mrvUfRU0m+JyXK3O0FcAMFZyjc@gibran-bucket/tweets/"+filename)
    val texts = tweets.collect
    println(texts.mkString(" "))
    
    println("Creating term document matrix ...")
    val t1 = System.nanoTime
    val tdm = TM.termDocumentMatrix(texts, sc)
    println(tdm.mkString(" "))
    println("Creating layers ...")
    var layers = tdm.map(doc => Graph.adjacencyMatrix(doc))
    println(layers.mkString(" "))
    println("Starting clustering ...")
    val clusters = Clusters.Hierarchical(layers, sc)
    println(clusters.mkString(" "))
    val duration = (System.nanoTime - t1) / 1e9d
    print("Duration Time:",duration, "Numbers of Cores", sc.getExecutorStorageStatus.length)

    sc.stop()
  }
}
