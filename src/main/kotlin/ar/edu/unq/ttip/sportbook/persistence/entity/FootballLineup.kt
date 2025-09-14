package ar.edu.unq.ttip.sportbook.persistence.entity

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
        if (positionsByPlayer.containsKey(position)) {
            throw IllegalArgumentException("La posición $position ya está ocupada")
        }
        if (positionsByPlayer.containsValue(player)) {
            throw IllegalArgumentException("El jugador ${player.name} ya está en el campo")
        }
        positionsByPlayer.put(position, player)
        if (initialLineup.none { it.id == player.id })
            initialLineup.add(player)
        if (bench.any { it.id == player.id })
            bench.removeIf { it.id == player.id }
    }

    fun removePlayerFromPosition(position: Position) {
        val player = positionsByPlayer[position] ?: throw IllegalArgumentException("No hay ningún jugador en la posición $position")
        positionsByPlayer.remove(position)
        initialLineup.removeIf{ it.id == player.id}
        bench.add(player)
    }

    fun addPlayerToBench(player: Player) {
        if (initialLineup.contains(player) || bench.contains(player)) {
            throw IllegalArgumentException("El jugador ${player.name} ya está en el banco")
        }
        bench.add(player)
    }
}

enum class Position {
    GK, // Arquero
    RB, // Lateral derecho
    LB, // Lateral izquierdo
    CB, // Defensa central
    CM, // Mediocampista central
    RM, // Mediocampista derecho
    LM, // Mediocampista izquierdo
    ST, // Delantero
    CT, // Centro delantero
    RW, // Extremo derecho
    LW // Extremo izquierdo
}
