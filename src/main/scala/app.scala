package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.log4j.{Level, Logger}

object App {
  def main(args: Array[String]) {
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
    val filename = args(0)
    
    val conf = new SparkConf(true).setAppName("Distributed Hierarchical Clustering - "+filename)
    val sc = new SparkContext(conf)
    
    println("Reading s3 file ...")    
    val tweets = sc.textFile("s3n://"+sys.env("AWS_ACCESS_KEY_ID")+":"+sys.env("AWS_SECRET_ACCESS_KEY")+"@gibran-bucket/tweets/"+filename)
    
    println("Creating term document matrix ...")
    val tdm = TM.termDocumentMatrix(tweets, sc)
    
    println("Creating layers ...")
    var layers = sc.parallelize(0 to (tdm._1.size-1)).map(i => Graph.adjacencyMatrix(tdm._1(i))).collect
 
    println("Starting clustering ...")
    val t1 = System.nanoTime
    val results = Clusters.Hierarchical(layers, sc, tdm._2)
    val duration = (System.nanoTime - t1) / 1e9d
    print("Duration Time:",duration, "Numbers of Cores", sc.getExecutorStorageStatus.length)
    sc.parallelize(results).map(i => i.mkString(",")).coalesce(1).saveAsTextFile("s3n://"+sys.env("AWS_ACCESS_KEY_ID")+":"+sys.env("AWS_SECRET_ACCESS_KEY")+"@gibran-bucket/results"+layers.size)
    sc.stop()
  }
}
