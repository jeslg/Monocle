package monocle.state

import monocle.{MonocleSuite, Optional}
import monocle.macros.GenLens

class StateExample extends MonocleSuite {

  case class Person(name: String, age: Int)
  val _age = GenLens[Person](_.age)
  val p = Person("John", 30)

  test("mod"){
    val increment = for {
      i <- _age mod (_ + 1)
    } yield i

    increment.run(p) shouldEqual ((Person("John", 31), 31))
  }

  test("modo"){
    val increment = for {
      i <- _age modo (_ + 1)
    } yield i

    increment.run(p) shouldEqual ((Person("John", 31), 30))
  }

  test("assign"){
    val set20 = for {
      i <- _age assign 20
    } yield i

    set20.run(p) shouldEqual ((Person("John", 20), 20))
  }

  test("assigno"){
    val set20 = for {
      i <- _age assigno 20
    } yield i

    set20.run(p) shouldEqual ((Person("John", 20), 30))
  }

  val _oldAge = Optional[Person, Int](p => if (p.age > 50) Some(p.age) else None){ a => _.copy(age = a) }
  val _coolGuy = Optional[Person, String](p => if (p.name.startsWith("C")) Some(p.name) else None){ n => _.copy(name = n) }

  test("modo for Optional (predicate is false)"){
    val youngPerson = Person("John", 30)
    val update = for {
      i <- _oldAge modo (_ + 1)
    } yield i

    update.run(youngPerson) shouldEqual ((Person("John", 30), None))
  }

  test("modo for Optional (predicate is true)"){
    val oldPerson = Person("John", 100)
    val update = for {
      i <- _oldAge modo (_ + 1)
    } yield i

    update.run(oldPerson) shouldEqual ((Person("John", 101), Some(100)))
  }

  test("modo for Optional (chaining modifications)"){
    val oldCoolPerson = Person("Chris", 100)
    val update = for {
      _ <- _oldAge modo (_ + 1)
      x <- _coolGuy modo (_.toLowerCase)
    } yield x

    update.run(oldCoolPerson) shouldEqual ((Person("chris", 101), Some("Chris")))
  }

  test("modo for Optional (only some of the modifications are applied)"){
    val oldCoolPerson = Person("Chris", 30)
    val update = for {
      _ <- _oldAge modo (_ + 1)
      x <- _coolGuy modo (_.toLowerCase)
    } yield x

    update.run(oldCoolPerson) shouldEqual ((Person("chris", 30), Some("Chris")))
  }

  case class Cat(name: String, age: Option[Int])
  val _safeAge = Optional[Cat, Int](_.age)(age =>_.copy(age = Option(age)))

  test("assigno for Optional (predicate is false)"){
    val noAgeAnimal = Cat("Nela", Option.empty)
    val update = for {
      i <- _safeAge assigno 5
    } yield i

    update.run(noAgeAnimal) shouldEqual (((Cat("Nela", Option(5))), Option.empty))
  }
}
