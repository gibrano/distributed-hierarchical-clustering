package dhclust

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Divergence extends Serializable {
  
  def JensenShannon(A: Array[Array[Double]], B: Array[Array[Double]],par: Array[Double]): Double = {
    var n = A.size
    var entropyA = 0.00
    var entropyB = 0.00
    var entropyC = 0.00
    var TraceAL1 = 0.00
    var TraceAL2 = 0.00  
    var TraceAL3 = 0.00
    var TraceBL1 = 0.00
    var TraceBL2 = 0.00  
    var TraceBL3 = 0.00
    var TraceCL1 = 0.00
    var TraceCL2 = 0.00  
    var TraceCL3 = 0.00
    var sumallA = 0.00
    var sumallB = 0.00
    var sumallC = 0.00
    var dgrA = Array[Double]()
    var dgrB = Array[Double]()  
    var dgrC = Array[Double]()  
    for(i <- 0 to (n-1)){
      var sumA = A(i).sum
      var sumB = B(i).sum
      dgrA = dgrA ++ Array(sumA)
      dgrB = dgrB ++ Array(sumB)
      dgrC = dgrC ++ Array(0.5*(sumA + sumB))
      sumallA = sumallA + dgrA(i)
      sumallB = sumallB + dgrB(i)
      sumallC = sumallC + dgrC(i)
      TraceAL1 = TraceAL1 + dgrA(i)
      TraceAL2 = TraceAL2 + math.pow(dgrA(i),2) + dgrA(i)
      TraceAL3 = TraceAL3 + math.pow(dgrA(i),3) + math.pow(dgrA(i),2) + dgrA(i)
      TraceBL1 = TraceBL1 + dgrB(i)
      TraceBL2 = TraceBL2 + math.pow(dgrB(i),2) + dgrB(i)
      TraceBL3 = TraceBL3 + math.pow(dgrB(i),3) + math.pow(dgrB(i),2) + dgrB(i)
      TraceCL1 = TraceCL1 + dgrC(i)
      TraceCL2 = TraceCL2 + math.pow(dgrC(i),2) + dgrC(i)
      TraceCL3 = TraceCL3 + math.pow(dgrC(i),3) + math.pow(dgrC(i),2) + dgrC(i)
    }
    var cA = 1.00/sumallA
    var cB = 1.00/sumallB
    var cC = 1.00/sumallC
    TraceAL1 = cA*TraceAL1
    TraceBL1 = cB*TraceBL1
    TraceCL1 = cC*TraceCL1
    TraceAL2 = math.pow(cA,2)*TraceAL2
    TraceBL2 = math.pow(cB,2)*TraceBL2
    TraceCL2 = math.pow(cC,2)*TraceCL2
    TraceAL3 = math.pow(cA,3)*TraceAL3
    TraceBL3 = math.pow(cB,3)*TraceBL3
    TraceCL3 = math.pow(cC,3)*TraceCL3
    entropyA = - par(0)*n - par(1)*TraceAL1 - par(2)*TraceAL2 - par(3)*TraceAL3
    entropyB = - par(0)*n - par(1)*TraceBL1 - par(2)*TraceBL2 - par(3)*TraceBL3
    entropyC = - par(0)*n - par(1)*TraceCL1 - par(2)*TraceCL2 - par(3)*TraceCL3
    var r = entropyC-(1/2)*(entropyA+entropyB)
    return r
  }  
  
  def computeJSD(x: Array[Int], layers: Array[Array[Array[Double]]], par: Array[Double]) : Double = {
    var jsd = JensenShannon(layers(x(0)),layers(x(1)),par)
    return jsd
  }

}
