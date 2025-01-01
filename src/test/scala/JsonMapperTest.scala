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
        assert(flattenedMap.equals(expectedFlattened))
      case _ => ???
    }
  }

}
