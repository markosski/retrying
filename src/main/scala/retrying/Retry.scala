package retrying

/**
  * Retry object contains main functionality for the user.
  *
  * ==Overview==
  * {{{
  *     val res = Retry.once { 1+1 }
  *
  *     val res = Retry.times(3) { thisFunctionMayFail() }
  *
  *     val res = Retry.exponential(100, 1000) { thisFunctionMayFail() }
  *
  *     val res = Retry.withStrategy(new DefaultStrategy(5000, 1, 100)) {thisFunctionMayFail}
  * }}}
  *
  * ==Example==
  * {{{
  *     def foo: Unit = { println("try me"); throw new Exception("foo") }
  *
  *     Retry.once(foo)
  *
  *     ```
  *     try me
  *     try me
  *     java.lang.Exception: foo
  *     ```
  * }}}
  *
  * Example of using random re-try wait time.
  * {{{
  *     val res = Retry.withStrategy(
  *         new DefaultStrategy(5000, 5, math.abs(Random.nextInt) % 1000)
  *     ) {thisFunctionMayFail}
  * }}}
  */

import scala.util._

object Retry {
    /**
      * Retry class is for internal use only.
      * @param strategy
      */
    protected class Retry(strategy: RetryStrategy) {
        def apply[A](x: => A): A = {
            Try(x) match {
                case Success(x) => x
                case Failure(err) => {
                    if (strategy.check) {
                        apply(x)
                    }
                    else throw err
                }
            }
        }
    }

    /**
      * Retry expression only once.
      * @return
      */
    def once[A](x: => A): A = (new Retry(new DefaultStrategy(1, 1, 0)))(x)

    /**
      * Retry expression N number of times. Optionally provide timeout in seconds.
      * @param n how many times to re-try
      * @param timeout try until timeout (seconds) is reached
      * @param x some computation of type A
      * @tparam A
      * @return
      */
    def times[A](n: Int, timeout: Int = 5000)(x: => A): A = {
        (new Retry(new DefaultStrategy(timeout, n, 0)))(x)
    }

    /**
      * Retry with exponential strategy.
      * @param startTime first retry, consecutive (2**tries) * startTime
      * @param maxWaitTime try until wait time is less that maxWaitTime
      * @param x
      * @tparam A
      * @return
      */
    def exponential[A](startTime: Int, maxWaitTime: Int = 5000)(x: => A): A =
        (new Retry(new ExponentialStrategy(startTime, maxWaitTime)))(x)

    /**
      * Retry with providing strategy.
      * @param strategy
      * @param x some computetion of type A
      * @tparam A
      * @return
      */
    def withStrategy[A](strategy: RetryStrategy)(x: => A): A = (new Retry(strategy))(x)
}
