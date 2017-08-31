# Retrying

```

val res: Int = Retry.once(1)

// Try to execute up to 3 more times
val res = Retry.times(3) { thisFunctionMayFail() }

// Same as above but with 5s timeout.
val res = Retry.times(3, 5000) { thisFunctionMayFail() }

// start at 100ms and stop when 1s wait time is reached.
val res = Retry.exponential(100, 1000) { thisFunctionMayFail() }

val res = Retry.withStrategy(new DefaultStrategy(5000, 1, 100)) {thisFunctionMayFail}
```


```
def foo: Unit = { println("try me"); throw new Exception("foo") }

Retry.once(foo)

> try me
> try me
> java.lang.Exception: foo ...
```

To get more flexibility `Retry.withStrategy` can be used providing implementation of `RetryStrategy`.
Below example uses random wait time for each retry.
```
val res = Retry.withStrategy(
     new DefaultStrategy(5000, 5, math.abs(Random.nextInt) % 1000)
) {thisFunctionMayFail}
```