// src/test/scala/MainSpec.scala
import org.scalatest.funsuite.AnyFunSuite

class MainSpec extends AnyFunSuite {

  test("Addition should work correctly") {
    assert(1 + 1 == 2)
  }

  test("String length should return correct value") {
    assert("hello".length == 5)
  }
}
