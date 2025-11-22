package ar.edu.unq.ttip.sportbook.persistence.entity.event

import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.*
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
    var name: String? = null
    var minPlayers: Int = 0
    var maxPlayers: Int = 0

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    lateinit var dateTime: LocalDateTime

    @ManyToOne(cascade = [CascadeType.ALL])
    lateinit var location: Location
    var cost: BigDecimal? = null

    @OneToOne(cascade = [CascadeType.ALL])
    var transferData: TransferData? = null

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "event_id")
    var unnasignedPlayers: MutableList<Player> = mutableListOf()

    @ManyToOne(targetEntity = SportUser::class)
    var organizer: SportUser? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    final lateinit var sport: Sport

    var isFinished: Boolean = false

    @OneToOne(mappedBy = "event", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var finishedStats: FinishedEventStats? = null

    @OneToMany(targetEntity = Team::class, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "event_teams",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    var teams: MutableList<Team> = mutableListOf()

    @PrePersist
    @PreUpdate
    private fun validateTeamLimit() {
        if (teams.size > 2) {
            throw BusinessException("Un evento no puede tener más de 2 equipos.")
        }
    }

    private fun isFull() = unnasignedPlayers.size >= maxPlayers

    fun join(player: Player) {

        if (isFull()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El evento está completo")
        }

        // 2. Verificar duplicados SOLO si es un usuario registrado
        val username = player.user?.username
        if (username != null) {
            val isDuplicate = unnasignedPlayers.any { it.user?.username == username } ||
                    teams.flatMap { it.players }.any { it.user?.username == username }

            if (isDuplicate) {
                throw BusinessException("Ya sos parte de este evento!")
            }
        }

        unnasignedPlayers.add(player)
        player.event = this
    }

    fun updateBasicFields(
        cost: BigDecimal?,
        organizer: SportUser?,
        name: String?,
        newDateTime: LocalDateTime?,
        minPlayers: Int?,
        maxPlayers: Int?
    ) {
        cost?.let { this.cost = it }
        organizer?.let { this.organizer = it }
        name?.let { this.name = it }


        newDateTime?.let { this.dateTime = it }
        minPlayers?.let { this.minPlayers = it }
        maxPlayers?.let { this.maxPlayers = it }
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

    fun finish() {
        if (isFinished) {
            throw BusinessException("El evento $id ya fue finalizado")
        }
        isFinished = true
    }

    fun getPlayer(playerId: Long): Player {
        unnasignedPlayers.find { it.id == playerId }?.let { return it }

        teams.flatMap { it.players }
            .find { it.id == playerId }
            ?.let { return it }

        throw BusinessException("Jugador $playerId no encontrado en el evento")
    }

    fun getTeam(teamId: Long): Team {
        return teams.find { it.id == teamId }
            ?: throw IllegalArgumentException("El equipo con id $teamId no pertenece a este evento")
    }

    fun getFairnessScore(): Double {
        if (teams.isEmpty()) {
            return 0.0
        }

        val teamScores = teams.map { team ->
            team.players.map { it.calculateScore(sport) }.average()
        }

        var maxDifference = 0.0
        for (i in teamScores.indices) {
            for (j in i + 1 until teamScores.size) {
                val difference = kotlin.math.abs(teamScores[i] - teamScores[j])
                if (difference > maxDifference) {
                    maxDifference = difference
                }
            }
        }

        return kotlin.math.max(10.0 - maxDifference, 0.0)
    }

    fun balanceTeams() {
        if (teams.isEmpty()) return



        val allPlayers = (teams.flatMap { it.players } + unnasignedPlayers)
            .distinctBy { it.id }
            .toMutableList()


        teams.forEach { it.clear() }
        unnasignedPlayers.clear()


        allPlayers.sortByDescending { it.calculateScore(sport) }



        val limit = if (maxPlayers > 0) maxPlayers else allPlayers.size

        val playersToPlay = allPlayers.take(limit)
        val playersSurplus = allPlayers.drop(limit)


        unnasignedPlayers.addAll(playersSurplus)



        val teamCount = teams.size

        playersToPlay.forEachIndexed { index, player ->

            val round = index / teamCount


            val teamIndex = if (round % 2 == 0) {
                index % teamCount
            } else {
                teamCount - 1 - (index % teamCount)
            }

            teams[teamIndex].players.add(player)
        }
    }

    fun addTeam(team: Team) {
        if (teams.size >= 2) {
            throw BusinessException("El evento ya tiene los 2 equipos máximos permitidos.")
        }
        teams.add(team)
    }

    fun removeTeam(team: Team) {
        team.clear()
        teams.remove(team)
    }

    fun leave(user: SportUser): Player {

        var player = teams.flatMap { it.players }.find { it.user?.id == user.id }

        if (player == null) {
            player = unnasignedPlayers.find { it.user?.id == user.id }
        }

        if (player == null) throw NotFoundException("No estás en este evento.")



        teams.forEach { team ->
            team.players.removeIf { it.id == player.id }
        }




        unnasignedPlayers.removeIf { it.id == player.id }


        player.event = null

        return player
    }

    abstract fun updatePitchSize(size: Int)
}