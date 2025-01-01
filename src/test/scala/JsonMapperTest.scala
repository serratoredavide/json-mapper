// src/test/scala/MainSpec.scala
import org.scalatest.funsuite.AnyFunSuite
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import JsonMapper._

class JsonMapperTest extends AnyFunSuite {

  test("Json should be decoded correctly") {
    // Input
    val jsonInput =
      """
        {
          "name": "Alice",
          "age": 30,
          "address": {
            "city": "Wonderland",
            "zip": "12345"
          },
          "hobbies": ["reading", "cycling"]
        }
      """

    // Expected
    val expectedFlattened: Map[String, Json] = Map(
      "name" -> Json.fromString("Alice"),
      "age" -> Json.fromInt(30),
      "address" -> Json.obj(
        "city" -> Json.fromString("Wonderland"),
        "zip" -> Json.fromString("12345")
      ),
      "address.city" -> Json.fromString("Wonderland"),
      "address.zip" -> Json.fromString("12345"),
      "hobbies" -> Json.arr(
        Json.fromString("reading"),
        Json.fromString("cycling")
      )
    )

    // Check
    parse(jsonInput).flatMap(_.as[Map[String, Json]]) match {
      case Right(flattenedMap) =>
        println(flattenedMap)
        assert(flattenedMap.equals(expectedFlattened))
      case _ => ???
    }
  }

  test("Nested Json should be decoded correctly") {
    // Input
    val jsonInput =
      """
        {
          "name": "Bob",
          "metadata": {
            "t1": 10,
            "t2": {
              "t3": "test",
              "t4": 100.5
            }  
          }
        }
      """

    // Expected
    val expectedFlattened: Map[String, Json] = Map(
      "name" -> Json.fromString("Bob"),
      "metadata" -> Json.obj(
        "t1" -> Json.fromInt(10),
        "t2" -> Json.obj(
          "t3" -> Json.fromString("test"),
          "t4" -> Json.fromDoubleOrNull(100.5)
        )
      ),
      "metadata.t1" -> Json.fromInt(10),
      "metadata.t2" -> Json.obj(
        "t3" -> Json.fromString("test"),
        "t4" -> Json.fromDoubleOrNull(100.5)
      ),
      "metadata.t2.t3" -> Json.fromString("test"),
      "metadata.t2.t4" -> Json.fromDoubleOrNull(100.5)
    )

    // Check
    parse(jsonInput).flatMap(_.as[Map[String, Json]]) match {
      case Right(flattenedMap) =>
        assert(flattenedMap.equals(expectedFlattened))
      case _ => ???
    }
  }

}
