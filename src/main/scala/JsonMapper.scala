import io.circe._
import io.circe.syntax._
import io.circe.parser._

object JsonMapper {

  // Flatten Json for decoder
  def flattenJson(json: Json, parentKey: String = ""): Map[String, Json] = {
    json.asObject match {
      case Some(jsonObject) =>
        jsonObject.toMap.flatMap { case (key, value) =>
          val newKey = if (parentKey.isEmpty) key else s"$parentKey.$key"
          
          Map(newKey -> value) ++ flattenJson(value, newKey)
        }
      case None =>
        // Se non è un oggetto, restituisci il valore
        Map(parentKey -> json)
    }
  }

  // Decoder personalizzato per appiattire un JSON stringa in Map[String, Json]
  implicit val flattenDecoder: Decoder[Map[String, Json]] = Decoder.instance {
    cursor =>
      // Decodifica l'intero JSON
      cursor.value.as[Json].map { json =>
        flattenJson(json)
      }
  }

  // Encoder personalizzato per ricostruire il JSON da Map[String, Json]
  // implicit val customEncoder: Encoder[Map[String, Json]] = Encoder.instance {
  //   map =>
  //     // Ricostruisce il JSON nidificato partendo dalla mappa
  //     def unflattenJson(map: Map[String, Json]): Json = {
  //       map.foldLeft(Json.obj()) { case (acc, (key, value)) =>
  //         val keys = key.split("\\.")
  //         val nestedJson = keys.reverse.foldLeft(value) { (acc, k) =>
  //           Json.obj(k -> acc)
  //         }
  //         acc.deepMerge(nestedJson)
  //       }
  //     }

  //     unflattenJson(map)
  // }

//   def main(args: Array[String]): Unit = {
//     val jsonInput =
//       """
//         {
//           "name": "Alice",
//           "age": 30,
//           "address": {
//             "city": "Wonderland",
//             "zip": "12345"
//           },
//           "hobbies": ["reading", "cycling"]
//         }
//       """

//     // Utilizzo del Decoder personalizzato
//     parse(jsonInput).flatMap(_.as[Map[String, Json]]) match {
//       case Right(flattenedMap) =>
//         println("Flattened JSON:")
//         flattenedMap.foreach { case (key, value) =>
//           println(s"$key -> ${value.noSpaces}")
//         }

//         // Utilizzo dell'Encoder personalizzato
//         val reencodedJson = flattenedMap.asJson
//         println("\nReconstructed JSON:")
//         println(reencodedJson.spaces2)

//       case Left(error) =>
//         println(s"Failed to parse and flatten JSON: ${error.getMessage}")
//     }
//   }
}
