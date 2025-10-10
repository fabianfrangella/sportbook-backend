package ar.edu.unq.ttip.sportbook.persistence.entity.event

import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "EVENT")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "sport"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = FootballEvent::class, name = "FOOTBALL"),
    JsonSubTypes.Type(value = PaddleEvent::class, name = "PADDLE"),
    JsonSubTypes.Type(value = VolleyEvent::class, name = "VOLLEY")
)
abstract class Event() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    var minPlayers: Int = 0
    var maxPlayers: Int = 0
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    lateinit var dateTime: LocalDateTime
    @ManyToOne(cascade = [CascadeType.ALL])
    lateinit var location: Location
    var cost: BigDecimal? = null
    @OneToOne(cascade = [CascadeType.ALL])
    var transferData: TransferData? = null
    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL], targetEntity = Player::class)
    var players: List<Player> = listOf()
    lateinit var creator: String
    lateinit var organizer: String

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    lateinit var sport: Sport

    var isFinished: Boolean = false

    @OneToOne(mappedBy = "event", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var finishedStats: FinishedEventStats? = null

    fun canJoin(username: String) : Boolean {
        if (isFull()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El evento está completo")
        return players.find { player -> player.user.username == username } == null
    }

    private fun isFull() = players.size >= maxPlayers
    fun join(player: Player) {
        if (canJoin(player.user.username!!)) {
            players = players.plus(player)
            player.event = this
        } else
            throw BusinessException("Ya sos parte de este evento!")
    }

    fun leave(player: Player) {
        if (!players!!.contains(player)) {
            throw BusinessException("No estás registrado en el evento")
        }

        removePlayerFromTeams(player)
        players = players!!.filter { it != player }
        player.event = null
    }

    protected abstract fun removePlayerFromTeams(player: Player)

    fun updateBasicFields(cost: BigDecimal?, creator: String?, organizer: String?) {
        cost?.let { this.cost = it }
        creator?.let { this.creator = it }
        organizer?.let { this.organizer = it }
    }

    fun updateLocation(x: String?, y: String?, placeName: String?) {
        x?.let { location.x = it }
        y?.let { location.y = it }
        placeName?.let { location.placeName = it }
    }

    fun updateTransferData(cbu: String?, alias: String?) {
        if (transferData == null && (cbu != null || alias != null)) {
            transferData = TransferData()
        }

        transferData?.let { data ->
            cbu?.let { data.cbu = it }
            alias?.let { data.alias = it }
        }
    }

    abstract fun createLineups() : List<Lineup>

    abstract fun updatePitchSize(size: Int?)

    fun finish() {
        if (isFinished) {
            throw BusinessException("El evento $id ya fue finalizado")
        }
        isFinished = true
    }

    fun getPlayer(playerId: Long): Player {
        return players?.find { it.id == playerId }
            ?: throw BusinessException("Jugador $playerId no encontrado en el evento")
    }

    abstract fun getTeam(teamId: Long) : Team

    abstract fun getFairnessScore(): Double
}