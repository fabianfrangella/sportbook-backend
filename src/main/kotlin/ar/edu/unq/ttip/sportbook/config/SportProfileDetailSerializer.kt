package ar.edu.unq.ttip.sportbook.config

import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfileDetail
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import com.fasterxml.jackson.databind.node.ObjectNode

class SportProfileDetailSerializer : JsonSerializer<SportProfileDetail>(), ContextualSerializer {
    private var sport: Sport? = null

    override fun serialize(value: SportProfileDetail?, gen: JsonGenerator, serializers: SerializerProvider) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeStartObject()

        // Escribir propiedades comunes
        gen.writeNumberField("id", value.id)
        gen.writeBooleanField("playsOften", value.playsOften)
        value.ability?.let { gen.writeNumberField("ability", it) }

        // Escribir propiedades específicas según el tipo
        when (value) {
            is FootballProfileDetail -> {
                gen.writeArrayFieldStart("positions")
                value.positions.forEach { gen.writeString(it) }
                gen.writeEndArray()
                value.favoritePosition?.let { gen.writeStringField("favoritePosition", it) }
            }
            is VolleyProfileDetail -> {
                gen.writeArrayFieldStart("positions")
                value.positions.forEach { gen.writeString(it) }
                gen.writeEndArray()
                value.favoritePosition?.let { gen.writeStringField("favoritePosition", it) }
                value.blockHeight?.let { gen.writeNumberField("blockHeight", it) }
                value.rolePreference?.let { gen.writeStringField("rolePreference", it) }
            }
            is PaddleProfileDetail -> {
                value.preferredSide?.let { gen.writeStringField("preferredSide", it) }
                value.playStyle?.let { gen.writeStringField("playStyle", it) }
                value.playedTournaments?.let { gen.writeBooleanField("playedTournaments", it) }
            }
        }

        gen.writeEndObject()
    }

    override fun createContextual(prov: SerializerProvider, property: BeanProperty?): JsonSerializer<*> {
        return this
    }
}

class SportProfileDetailDeserializer : JsonDeserializer<SportProfileDetail>(), ContextualDeserializer {
    private var sport: Sport? = null

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): SportProfileDetail? {
        val node = p.codec.readTree<ObjectNode>(p)

        // Si no tenemos el contexto del sport, intentar inferirlo del contenido
        val effectiveSport = sport ?: return null

        val playsOften = node.get("playsOften")?.asBoolean() ?: false
        val ability = node.get("ability")?.asInt()

        return when (effectiveSport) {
            Sport.FOOTBALL -> {
                val positions = node.get("positions")?.map { it.asText() }?.toMutableList() ?: mutableListOf()
                val favoritePosition = node.get("favoritePosition")?.asText()
                FootballProfileDetail(positions, favoritePosition, playsOften, ability)
            }
            Sport.VOLLEY -> {
                val positions = node.get("positions")?.map { it.asText() }?.toMutableList() ?: mutableListOf()
                val favoritePosition = node.get("favoritePosition")?.asText()
                val blockHeight = node.get("blockHeight")?.asInt()
                val rolePreference = node.get("rolePreference")?.asText()
                VolleyProfileDetail(positions, favoritePosition, blockHeight, rolePreference, playsOften, ability)
            }
            Sport.PADDLE -> {
                val preferredSide = node.get("preferredSide")?.asText()
                val playStyle = node.get("playStyle")?.asText()
                val playedTournaments = node.get("playedTournaments")?.asBoolean()
                PaddleProfileDetail(preferredSide, playStyle, playedTournaments, playsOften, ability)
            }
        }
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> {
        // Intentar obtener el sport del contexto de la clase padre
        val contextClass = ctxt.contextualType?.rawClass
        if (contextClass != null) {
            // Este deserializador mantendrá la lógica de inferencia
        }
        return this
    }
}
