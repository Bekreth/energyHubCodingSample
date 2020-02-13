package com.energyHub.interview.thermostat

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonNode, JsonSerializer, SerializerProvider}
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}

@JsonSerialize(using = classOf[ModeSerializer])
@JsonDeserialize(using = classOf[ModeDeserializer])
sealed trait Mode

object OFF extends Mode
object AUTO extends Mode
object COOL extends Mode
object HEAT extends Mode

class ModeSerializer extends JsonSerializer[Mode] {
  override def serialize(value: Mode, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
    gen.writeString(
      value match {
        case OFF => "OFF"
        case AUTO => "AUTO"
        case COOL => "COOL"
        case HEAT => "HEAT"
      }
    )
  }
}

class ModeDeserializer extends JsonDeserializer[Mode] {
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): Mode = {
    val node: JsonNode = p.getCodec.readTree(p)
    node.asText() match {
      case "OFF" => OFF
      case "AUTO" => AUTO
      case "COOL" => COOL
      case "HEAT" => HEAT
    }
  }

}
