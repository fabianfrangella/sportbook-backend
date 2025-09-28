package ar.edu.unq.ttip.sportbook.persistence.entity.event.football

import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.persistence.entity.exception.DuplicatePlayerException
import ar.edu.unq.ttip.sportbook.persistence.entity.exception.NotTeamMemberException
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Position
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import jakarta.persistence.*

@Entity
@Table(name = "FOOTBALL_LINEUP")
class FootballLineup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToMany
    @JoinTable(
        name = "football_lineup_positions",
        joinColumns = [JoinColumn(name = "lineup_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "position")
    var positionsByPlayer: MutableMap<Position, Player> = mutableMapOf()

    @ManyToMany
    @JoinTable(
        name = "football_lineup_bench",
        joinColumns = [JoinColumn(name = "lineup_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    var bench: MutableList<Player> = mutableListOf()

    @ManyToMany
    @JoinTable(
        name = "football_lineup_initial",
        joinColumns = [JoinColumn(name = "lineup_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    var initialLineup: MutableList<Player> = mutableListOf()

    @ManyToOne
    @JoinColumn(name = "event_id")
    lateinit var event: FootballEvent

    @ManyToOne
    @JoinColumn(name = "team_id")
    lateinit var team: Team


    fun addPlayerToPosition(player: Player, position: Position) {
        if (!team.hasPlayerId(player.id)) {
            throw NotTeamMemberException(player.name)
        }

        if (positionsByPlayer.containsKey(position)) {
            throw BusinessException("La posición $position ya está ocupada")
        }
        if (positionsByPlayer.containsValue(player)) {
            throw BusinessException("El jugador ${player.name} ya está en el campo")
        }
        positionsByPlayer[position] = player
        if (initialLineup.none { it.id == player.id })
            initialLineup.add(player)
        if (bench.any { it.id == player.id })
            bench.removeIf { it.id == player.id }
    }

    fun removePlayerFromPosition(position: Position) {
        val player = positionsByPlayer[position] ?: throw BusinessException("No hay ningún jugador en la posición $position")
        positionsByPlayer.remove(position)
        initialLineup.removeIf{ it.id == player.id}
        bench.add(player)
    }

    fun addPlayerToBench(player: Player) {
        if (initialLineup.contains(player) || bench.contains(player)) {
            throw DuplicatePlayerException(player.name)
        }
        bench.add(player)
    }

    fun removePlayer(player: Player) {
        positionsByPlayer.entries
            .find { it.value.id == player.id }
            ?.let { entry ->
                positionsByPlayer.remove(entry.key)
            }

        bench.removeIf { it.id == player.id }

        initialLineup.removeIf { it.id == player.id }
    }
}
