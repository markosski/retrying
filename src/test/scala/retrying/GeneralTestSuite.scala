package retrying

import org.scalatest.{FunSuite}

/**
  * Created by marcin1 on 8/30/17.
  */
class GeneralTestSuite extends FunSuite {
    test("for tryMax=1 DefaultStrategy should evaluate `check` to true first time and false second time") {
        val st = new DefaultStrategy(10000, 1, 0)
        assert(st.check == true)
        assert(st.check == false)
    }

    test("retry will return value evaluated by input expression") {
        def foo(a: Int, b: Int): Int = a + b

        assert(Retry.once(foo(1,2)) == foo(1,2))
    }

    test("Retry should re-throw original exception") {
        class MyException extends Exception

        def foo: Unit = throw new MyException

        assertThrows[MyException] {
            Retry.once(foo)
        }
    }
}
