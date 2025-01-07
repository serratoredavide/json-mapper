// src/test/scala/MainSpec.scala
import org.scalatest.funsuite.AnyFunSuite
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import JsonMapper._

class JsonMapperTest extends AnyFunSuite {

  test("Json should be decoded correctly") {
    // Input
    val jsonInput: String =
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
      ),
      "hobbies.0" -> Json.fromString("reading"),
      "hobbies.1" -> Json.fromString("cycling")
    )

    // Check
    parse(jsonInput).flatMap(_.as[Map[String, Json]](flattenDecoder)) match {
      case Right(flattenedMap) =>
        assert(flattenedMap.equals(expectedFlattened))
      case _ => ???
    }
  }

  test("Nested Json should be decoded correctly") {
    // Input
    val jsonInput: String =
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
    parse(jsonInput).flatMap(_.as[Map[String, Json]](flattenDecoder)) match {
      case Right(flattenedMap) =>
        assert(flattenedMap.equals(expectedFlattened))
      case _ => ???
    }
  }

  test("Json with Array should be decoded correctly") {
    // Input
    val jsonInput: String =
      """
        {
          "name": "Pat",
          "metadata": [1,2,3],
          "metadata2": [
            {
              "name": "t1",
              "info": "t2"  
            },
            {
              "name": "t3",
              "info": "t4"  
            }
          ]
        }
      """

    // Expected
    val expectedFlattened: Map[String, Json] = Map(
      "name" -> Json.fromString("Pat"),
      "metadata" -> Json.arr(
        Json.fromInt(1),
        Json.fromInt(2),
        Json.fromInt(3)
      ),
      "metadata.0" -> Json.fromInt(1),
      "metadata.1" -> Json.fromInt(2),
      "metadata.2" -> Json.fromInt(3),
      "metadata2" -> Json.arr(
        Json.obj(
          "name" -> Json.fromString("t1"),
          "info" -> Json.fromString("t2")
        ),
        Json.obj(
          "name" -> Json.fromString("t3"),
          "info" -> Json.fromString("t4")
        )
      ),
      "metadata2.0" -> Json.obj(
        "name" -> Json.fromString("t1"),
        "info" -> Json.fromString("t2")
      ),
      "metadata2.0.name" -> Json.fromString("t1"),
      "metadata2.0.info" -> Json.fromString("t2"),
      "metadata2.1" -> Json.obj(
        "name" -> Json.fromString("t3"),
        "info" -> Json.fromString("t4")
      ),
      "metadata2.1.name" -> Json.fromString("t3"),
      "metadata2.1.info" -> Json.fromString("t4")
    )

    // Check
    parse(jsonInput).flatMap(_.as[Map[String, Json]](flattenDecoder)) match {
      case Right(flattenedMap) =>
        assert(flattenedMap.equals(expectedFlattened))
      case _ => ???
    }
  }

  test("JsonTemplate should be encoded correctly") {
    // Input
    val jsonInput: String =
      """
        {
          "name": "Pat",
          "metadata": [1,2,3],
          "metadata2": [
            {
              "name": "t1",
              "info": 256,
              "number": null  
            },
            {
              "name": "t3",
              "info": 133,
              "number": "3657483955"  
            }
          ]
        }
      """

    // Inputs Template
    val jsonTemplateString: String =
      """
        {
          "name": "MapTo.metadata2.0.name",
          "alternativeName": "MapTo.metadata2.1.name",
          "number": "MapTo.metadata2.0.number",
          "infos": [
            "MapTo.metadata2.0.info",
            "MapTo.metadata2.1.info" 
          ]
        }
      """

    val jsonTemplateWithOtherKeyMapString: String =
      """
        {
          "name": "MapKeyword.metadata2.0.name",
          "alternativeName": "MapKeyword.metadata2.1.name",
          "number": "MapKeyword.metadata2.0.number",
          "infos": [
            "MapKeyword.metadata2.0.info",
            "MapKeyword.metadata2.1.info" 
          ]
        }
      """

    // Expected Decoder
    val expectedFlattened: Map[String, Json] = Map(
      "name" -> Json.fromString("Pat"),
      "metadata" -> Json.arr(
        Json.fromInt(1),
        Json.fromInt(2),
        Json.fromInt(3)
      ),
      "metadata.0" -> Json.fromInt(1),
      "metadata.1" -> Json.fromInt(2),
      "metadata.2" -> Json.fromInt(3),
      "metadata2" -> Json.arr(
        Json.obj(
          "name" -> Json.fromString("t1"),
          "info" -> Json.fromInt(256),
          "number" -> Json.Null
        ),
        Json.obj(
          "name" -> Json.fromString("t3"),
          "info" -> Json.fromInt(133),
          "number" -> Json.fromString("3657483955")
        )
      ),
      "metadata2.0" -> Json.obj(
        "name" -> Json.fromString("t1"),
        "info" -> Json.fromInt(256),
        "number" -> Json.Null
      ),
      "metadata2.0.name" -> Json.fromString("t1"),
      "metadata2.0.info" -> Json.fromInt(256),
      "metadata2.0.number" -> Json.Null,
      "metadata2.1" -> Json.obj(
        "name" -> Json.fromString("t3"),
        "info" -> Json.fromInt(133),
        "number" -> Json.fromString("3657483955")
      ),
      "metadata2.1.name" -> Json.fromString("t3"),
      "metadata2.1.info" -> Json.fromInt(133),
      "metadata2.1.number" -> Json.fromString("3657483955")
    )

    // Output
    val expectedOutputString: String =
      """
        {
          "name": "t1",
          "alternativeName": "t3",
          "number": null,
          "infos": [
            256,
            133 
          ]
        }
      """

    // Check
    parse(jsonInput).flatMap(_.as[Map[String, Json]](flattenDecoder)) match {
      case Right(flattenedMap) =>
        assert(flattenedMap.equals(expectedFlattened))
        parse(expectedOutputString) match {
          case Right(j) => {
            // Standard Mapping Keyword
            assert(
              j.equals(
                mapTemplate(
                  parse(jsonTemplateString).getOrElse(Json.obj()),
                  flattenedMap
                )
              )
            )

            // Different Mapping Keyword
            assert(
              j.equals(
                mapTemplate(
                  parse(jsonTemplateWithOtherKeyMapString).getOrElse(
                    Json.obj()
                  ),
                  flattenedMap,
                  "MapKeyword."
                )
              )
            )
          }
          case Left(e) => ???
        }
      case _ => ???
    }
  }

}
