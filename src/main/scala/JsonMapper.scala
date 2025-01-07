import io.circe._
import io.circe.syntax._
import io.circe.parser._

object JsonMapper {

  // map jsonTemplate using values
  def mapTemplate(
      jsonTemplate: Json,
      values: Map[String, Json],
      mappingKeyword: String = "MapTo."
  ): Json = {
    def updateTemplate(template: Json): Json = template
      .mapObject(_.mapValues(updateTemplate))
      .mapArray(_.map(updateTemplate))
      .withString(x =>
        if (x.startsWith(mappingKeyword))
          values.get(x.substring(mappingKeyword.length())).get
        else Json.fromString(x)
      )

    updateTemplate(jsonTemplate)
  }

  // Flatten Json function for decoder
  def flattenJson(json: Json, parentKey: String = ""): Map[String, Json] = {
    json.asObject match {
      case Some(jsonObject) =>
        jsonObject.toMap.flatMap { case (key, value) =>
          val newKey = if (parentKey.isEmpty) key else s"$parentKey.$key"

          Map(newKey -> value) ++ flattenJson(value, newKey)
        }
      case None =>
        json.asArray match {
          case Some(ar) => {
            ar.zipWithIndex.flatMap {
              case (value, index) => {
                val newKey =
                  if (parentKey.isEmpty) index.toString()
                  else s"$parentKey.$index"
                Map(newKey -> value) ++ flattenJson(value, newKey)
              }
            }.toMap
          }
          case None => Map(parentKey -> json)
        }
    }
  }

  // Flatten Decoder
  val flattenDecoder: Decoder[Map[String, Json]] = Decoder.instance { cursor =>
    // Decode Json
    cursor.value.as[Json].map { json =>
      flattenJson(json)
    }
  }

}
