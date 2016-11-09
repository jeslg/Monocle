---
layout: page
title:  "Lens"
section: "optics"
source: "core/src/main/scala/monocle/PLens.scala"
scaladoc: "#monocle.Lens"
---
# Lens

A `Lens` is an Optic used to zoom inside a `Product`, e.g. `case class`, `Tuple`, `HList` or even `Map`.

`Lenses` have two type parameters generally called `S` and `A`: `Lens[S, A]` where `S` represents the `Product` and `A` an element inside of `S`.

Let's take a simple case class with two fields:

```tut:silent
case class Address(streetNumber: Int, streetName: String)
```

We can create a `Lens[Address, Int]` which zoom from an `Address` to its field `streetNumber` by supplying a pair of functions:

*   `get: Address => Int`
*   `set: Int => Address => Address`

```tut:silent
import monocle.Lens
val _streetNumber = Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
```

Once we have a `Lens`, we can use the supplied `get` and `set` functions (nothing fancy!):

```tut
val address = Address(10, "High Street")

_streetNumber.get(address)
_streetNumber.set(5)(address)
```

We can also `modify` the target of `Lens` with a function, this equivalent to call `get` and then `set`:

```tut
_streetNumber.modify(_ + 1)(address)

val n = _streetNumber.get(address)
_streetNumber.set(n + 1)(address)
```

We can push push the idea even further, with `modifyF` we can update the target of a `Lens` in a context, cf `scalaz.Functor`:

```tut:silent
def neighbors(n: Int): List[Int] =
  if(n > 0) List(n - 1, n + 1) else List(n + 1)

import scalaz.std.list._ // to get Functor[List] instance
```

```tut
_streetNumber.modifyF(neighbors)(address)
_streetNumber.modifyF(neighbors)(Address(135, "High Street"))
```

This would work with any kind of `Functor` and is especially useful in conjunction with asynchronous APIs, where one has the task to update a deeply nested structure (see Lens Composition) with the result of an asynchronous computation:

```tut:silent
import scalaz.std.scalaFuture._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits._ // to get Future Functor instance
```

```tut
def updateNumber(n: Int) : Future[Int] = Future.successful ( n + 1)
_streetNumber.modifyF(updateNumber)(address)
```

Most importantly, `Lenses` compose to zoom deeper in a data structure

```tut:silent
case class Person(name: String, age: Int, address: Address)
val john = Person("John", 20, address)
```

```scala
val _address = Lens[Person, Address](_.address)(a => p => p.copy(address = a)) 

(_address composeLens _streetNumber).get(john)
(_address composeLens _streetNumber).set(2)(john)
```


## Lens Generation

`Lens` creation is rather boiler platy but we developed a few macros to generate them automatically. All macros
are defined in a separate module:

```scala
libraryDependencies += "com.github.julien-truffaut"  %%  "monocle-macro"  % ${version}
```

```tut:silent
import monocle.macros.GenLens
val _age = GenLens[Person](_.age)
```

`GenLens` can also be used to generate `Lens` several level deep:

```tut
GenLens[Person](_.address.streetName).set("Iffley Road")(john)
```

For those who want to push `Lenses` generation even further, we created `@Lenses` macro annotation which generate
`Lenses` for *all* fields of a case class. The generated `Lenses` are in the companion object of the case class:

```tut:silent
import monocle.macros.Lenses
@Lenses case class Point(x: Int, y: Int)
```

```tut
val p = Point(5, 3)
Point.x.get(p)
Point.y.set(0)(p)
```

## Laws

```tut:silent
class LensLaws[S, A](lens: Lens[S, A]) {

  def getSetLaw(s: S): Boolean =
    lens.set(lens.get(s)) == s

  def setGetLaw(s: S, a: A): Boolean =
    lens.get(lens.set(a)(s)) == a

}
```

`getSetLaw` states that if you `get` a value `A` from `S` and then `set` it back in, the result is an object identical to the original one.
A side effect of this law is that `set` is constraint to only update the `A` it points to, for example it cannot
increment a counter or modify another value of type `A`.

`setGetLaw` states that if you `set` a value, you always `get` the same value back. This law guarantees that `set` is
 actually updating a value of type `A`.
