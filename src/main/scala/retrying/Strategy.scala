package retrying

import java.time
import java.lang.Thread.sleep

trait RetryStrategy {
    /** Counter to track how many times evaluation/check happened */
    protected var tried = 0

    def check: Boolean
}

/**
  * Default strategy providing timeout, max re-tries and wait time between re-tries.
  * @param timeout
  * @param tryMax
  * @param interval
  */
class DefaultStrategy(timeout: Int, tryMax: Int, interval: => Int = 0) extends RetryStrategy {
    require(timeout > 0, "timeout argument must be greater than 0")
    require(tryMax > 0, "tryMax argument must be greater than 0")

    // We want this to evaluate on first invocation of check method.
    lazy val startTime = time.Instant.now.getEpochSecond

    def check: Boolean = {
        if (tried < tryMax && time.Instant.now.getEpochSecond - startTime < timeout) {
            tried += 1
            sleep(interval)
            println(interval)
            true
        } else false
    }
}

/**
  * Another strategy incrementing wait time exponentially.
  * @param startTime
  * @param maxWaitTime
  */
class ExponentialStrategy(startTime: Int, maxWaitTime: Int) extends RetryStrategy {
    require(startTime > 0, "startTime argument must be greater than 0")
    require(maxWaitTime > 0 && startTime < maxWaitTime, "endTime argument must be greater than 0 and bigger than startTime")

    var sleepTime: Int = 0

    def check: Boolean = {
        if (sleepTime < maxWaitTime) {
            tried += 1
            sleepTime = (math.pow(2, tried) * startTime).toInt
            sleep(sleepTime)
            true
        } else false
    }
}
