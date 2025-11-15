package ar.edu.unq.ttip.sportbook.config

import ar.edu.unq.ttip.sportbook.persistence.entity.event.*
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.team.TeamColor
import ar.edu.unq.ttip.sportbook.persistence.entity.user.*
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository
import ar.edu.unq.ttip.sportbook.service.ProfilePictureService
import ar.edu.unq.ttip.sportbook.service.auth.AuthService
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class SportbookDataInitializer(
    private val authService: AuthService,
    private val userRepository: SportUserJpaRepository,
    private val eventRepository: EventJpaRepository,
    private val profilePictureService: ProfilePictureService,
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        if (userRepository.count() > 0) return // Solo inicializar si la DB está vacía

        val users = createUsers()
        val loc1 = createLocation("34.6037", "-58.3816", "Cancha Central")
        val loc2 = createLocation("34.5900", "-58.4000", "Pista de Padel UNQ")
        val loc3 = createLocation("34.5800", "-58.3500", "Gimnasio Cubierto")

        createFootballEvents(users, loc1)
        createPaddleEvents(users, loc2)
        createVolleyEvents(users, loc3)
    }

    // --- UTILS ---

    private fun createLocation(x: String, y: String, name: String): Location {
        return Location().apply {
            this.x = x
            this.y = y
            this.placeName = name
        }
    }

    private fun createPlayer(user: SportUser? = null, name: String? = null): Player {
        val playerName = user?.name ?: name!!
        val player = Player().apply {
            this.name = playerName
            this.user = user
        }
        // Si es un usuario, también agregamos el player a su colección (bidireccionalidad)
        user?.players?.add(player)
        return player
    }

    private fun createTeam(color: TeamColor, vararg players: Player): Team {
        return Team().apply {
            this.color = color
            this.players = players.toMutableList()
        }
    }

    // --- STEP 1: USERS AND PROFILES ---

    private fun createUsers(): List<SportUser> {
        val user1 = SportUser(
            "1234", "admin", "messi@mail.com", "Lio", "Messi", LocalDate.of(1990, 1, 1), role = Role.ORGANIZER
        ).apply {
            addProfile(SportProfile(this, Sport.FOOTBALL, FootballProfileDetail(mutableListOf("ST", "RW"), "ST", true, 9)))
            addProfile(SportProfile(this, Sport.PADDLE, PaddleProfileDetail("Izquierda", "Ofensivo", true, true, 10)))
        }

        val user2 = SportUser(
            "pass2", "mari_star", "mari@mail.com", "Mariana", "Gomez", LocalDate.of(1995, 5, 10), role = Role.PLAYER
        ).apply {
            addProfile(SportProfile(this, Sport.VOLLEY, VolleyProfileDetail(mutableListOf("Opuesto"), "Opuesto", 280, "Ataque", true, 8)))
            addProfile(SportProfile(this, Sport.FOOTBALL, FootballProfileDetail(mutableListOf("CM", "LB"), "CM", false, 6)))
        }

        val user3 = SportUser(
            "pass3", "pablo_pro", "pablo@mail.com", "Pablo", "Perez", LocalDate.of(1985, 12, 20), role = Role.PLAYER
        ).apply {
            addProfile(SportProfile(this, Sport.PADDLE, PaddleProfileDetail("Izquierda", "Defensivo", false, true, 7)))
        }
        authService.register(user1)
        authService.register(user2)
        authService.register(user3)

        loadProfilePicture(user1, "/config/admin_profile.png", "admin_profile.png")
        loadProfilePicture(user2, "/config/julian_profile.png", "julian_profile.png")
        userRepository.saveAll(listOf(user1, user2, user3))
        return listOf(user1, user2, user3)
    }

    // --- STEP 2: EVENTS ---

    private fun createFootballEvents(users: List<SportUser>, location: Location) {
        var lioUser = users.first()
        var marianaUser = users[1]
        val userPlayer1 = createPlayer(users[0])
        val userPlayer2 = createPlayer(users[1])

        // Evento 1: Fútbol 5 - Jugadores Registrados + Invitados
        val event1 = FootballEvent().apply {
            dateTime = LocalDateTime.now().plusDays(1)
            minPlayers = 10
            maxPlayers = 10
            this.location = location
            cost = BigDecimal("2000.00")
            transferData = TransferData().apply {
                cbu = "1095432198059"
                alias = "carpincho.torre.bici"
            }
            organizer = lioUser
            pitchSize = 11

            // Jugadores: 2 Registrados, 8 Invitados
            val initialPlayers = mutableListOf(
                userPlayer1, // Registrado
                userPlayer2, // Registrado
                createPlayer(name = "Invitado Leo"),
                createPlayer(name = "Invitado Emi"),
                createPlayer(name = "Invitado Juan"),
                createPlayer(name = "Invitado Facu"),
                createPlayer(name = "Invitado Pipo"),
                createPlayer(name = "Invitado Seba"),
                createPlayer(name = "Invitado Maxi"),
            )
            this.unnasignedPlayers = initialPlayers

            // Crear equipos iniciales (vacíos o desbalanceados)
            firstTeam = createTeam(TeamColor.BLUE)
            secondTeam = createTeam(TeamColor.RED)
        }

        // Evento 2: Fútbol 8 - Solo Registrados
        val event2 = FootballEvent().apply {
            dateTime = LocalDateTime.now().plusDays(5)
            minPlayers = 16
            maxPlayers = 16
            this.location = location
            cost = BigDecimal("1500.00")
            transferData = TransferData().apply {
                cbu = "1095432198059"
                alias = "carpincho.torre.bici"
            }
            organizer = marianaUser
            pitchSize = 11

            // Jugadores: 16 Registrados (usando placeholders para simplificar)
            this.unnasignedPlayers = users.map { createPlayer(it) }.toMutableList()
            // Agregar 13 jugadores más (asumiendo que hay más usuarios, o creándolos aquí para la demo)
            // Para simplificar, solo asignamos los 3 creados arriba
            // En una app real, aquí usarías 16 jugadores únicos.

            firstTeam = createTeam(TeamColor.GREEN)
            secondTeam = createTeam(TeamColor.WHITE)
        }

        eventRepository.saveAll(listOf(event1, event2))
    }

    private fun createPaddleEvents(users: List<SportUser>, location: Location) {
        var organizerUser = users.first()
        val userPlayer3 = createPlayer(users[2])

        // Evento 3: Pádel - 4 jugadores (2 equipos)
        val event3 = PaddleEvent().apply {
            dateTime = LocalDateTime.now().plusDays(2)
            minPlayers = 4
            maxPlayers = 4
            this.location = location
            cost = BigDecimal("4000.00")
            transferData = TransferData().apply {
                cbu = "1095432198059"
                alias = "carpincho.torre.bici"
            }
            organizer = organizerUser

            // Jugadores: 2 Registrados, 2 Invitados
            val initialPlayers = mutableListOf(
                createPlayer(users[0]), // Fede
                userPlayer3,             // Pablo
                createPlayer(name = "Invitado Tino"),
                createPlayer(name = "Invitado Lalo")
            )
            this.unnasignedPlayers = initialPlayers

            // Equipos pre-creados
            val teamA = createTeam(TeamColor.BLUE, initialPlayers[0], initialPlayers[3])
            val teamB = createTeam(TeamColor.RED, initialPlayers[1], initialPlayers[2])
            this.teams = mutableListOf(teamA, teamB) // lateinit var teams
        }

        // Evento 4: Pádel - 8 jugadores (4 equipos)
        val event4 = PaddleEvent().apply {
            dateTime = LocalDateTime.now().plusDays(7)
            minPlayers = 8
            maxPlayers = 8
            this.location = location
            cost = BigDecimal("6000.00")
            transferData = TransferData().apply {
                cbu = "1095432198059"
                alias = "carpincho.torre.bici"
            }
            organizer = organizerUser // Mariana organiza

            // Jugadores: 3 Registrados, 5 Invitados
            val initialPlayers = mutableListOf(
                createPlayer(users[0]), // Fede
                createPlayer(users[1]), // Mari
                createPlayer(users[2]), // Pablo
                createPlayer(name = "Invitado Ana"),
                createPlayer(name = "Invitado Sol"),
                createPlayer(name = "Invitado Gaby"),
                createPlayer(name = "Invitado Lupe"),
                createPlayer(name = "Invitado Rocio")
            )
            this.unnasignedPlayers = initialPlayers

            // Equipos (vacíos o incompletos)
            this.teams = mutableListOf(
                createTeam(TeamColor.WHITE),
                createTeam(TeamColor.BLACK),
                createTeam(TeamColor.GREEN),
                createTeam(TeamColor.RED)
            )
        }

        eventRepository.saveAll(listOf(event3, event4))
    }

    private fun createVolleyEvents(users: List<SportUser>, location: Location) {
        var organizerUser = users[1] // Mariana organiza

        // Evento 5: Vóley - 6 jugadores (3 vs 3)
        val event5 = VolleyEvent().apply {
            dateTime = LocalDateTime.now().plusDays(3)
            minPlayers = 6
            maxPlayers = 6
            this.location = location
            cost = BigDecimal("1000.00")
            transferData = TransferData().apply {
                cbu = "1095432198059"
                alias = "carpincho.torre.bici"
            }
            organizer = organizerUser

            // Jugadores: 1 Registrado, 5 Invitados
            val initialPlayers = mutableListOf(
                createPlayer(users[1]), // Mariana
                createPlayer(name = "I-Voley-1"),
                createPlayer(name = "I-Voley-2"),
                createPlayer(name = "I-Voley-3"),
                createPlayer(name = "I-Voley-4"),
                createPlayer(name = "I-Voley-5")
            )
            this.unnasignedPlayers = initialPlayers

            // Equipos
            this.teams = mutableListOf(
                createTeam(TeamColor.BLUE, initialPlayers[0], initialPlayers[2], initialPlayers[4]),
                createTeam(TeamColor.GREEN, initialPlayers[1], initialPlayers[3], initialPlayers[5])
            )
        }

        // Evento 6: Vóley - 12 jugadores (6 vs 6)
        val event6 = VolleyEvent().apply {
            dateTime = LocalDateTime.now().plusDays(10)
            minPlayers = 12
            maxPlayers = 12
            this.location = location
            cost = BigDecimal("1200.00")
            transferData = TransferData().apply {
                cbu = "1095432198059"
                alias = "carpincho.torre.bici"
            }
            organizer = organizerUser // Fede organiza

            // Jugadores: 3 Registrados, 9 Invitados (solo 6 para el ejemplo)
            val initialPlayers = mutableListOf(
                createPlayer(users[0]),
                createPlayer(users[1]),
                createPlayer(users[2]),
                createPlayer(name = "Inv-A"),
                createPlayer(name = "Inv-B"),
                createPlayer(name = "Inv-C")
            )
            this.unnasignedPlayers = initialPlayers

            // Equipos (vacíos)
            this.teams = mutableListOf(
                createTeam(TeamColor.RED),
                createTeam(TeamColor.WHITE)
            )
        }

        eventRepository.saveAll(listOf(event5, event6))
    }

    private fun loadProfilePicture(user: SportUser, path: String, name: String) {
        val imageResource = this::class.java.getResourceAsStream(path)
        if (imageResource != null) {
            val multipartFile = object : MultipartFile {
                override fun getInputStream() = imageResource
                override fun getName() = name
                override fun getOriginalFilename() = name
                override fun getContentType() = "image/png"
                override fun isEmpty() = false
                override fun getSize() = imageResource.available().toLong()
                override fun getBytes() = imageResource.readAllBytes()
                override fun transferTo(dest: File) {
                    dest.writeBytes(getBytes())
                }
            }
            profilePictureService.uploadProfilePicture(multipartFile, user)
        }
    }

}