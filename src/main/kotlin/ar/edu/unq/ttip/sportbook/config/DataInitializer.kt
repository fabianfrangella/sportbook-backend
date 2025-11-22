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
import ar.edu.unq.ttip.sportbook.service.LineupService
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
    private val lineupService: LineupService
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        if (userRepository.count() > 0) return

        // 1. Usuarios Base
        val users = createUsers()

        // 2. Lugares
        val locCentral = createLocation("-34.6037", "-58.3816", "Cancha Central")
        val locPadel = createLocation("-34.5900", "-58.4000", "Pista de Padel UNQ")
        val locGym = createLocation("-34.5800", "-58.3500", "Gimnasio Cubierto")
        val locPredio = createLocation("-34.7000", "-58.2500", "Predio El Porvenir")

        // 3. Eventos por Deporte (Total 10 eventos)
        createFootballEvents(users, locCentral, locPredio)
        createPaddleEvents(users, locPadel)
        createVolleyEvents(users, locGym)
    }

    // --- HELPERS ---

    private fun createLocation(x: String, y: String, name: String) = Location().apply {
        this.x = x
        this.y = y
        this.placeName = name
    }

    private fun createPlayer(user: SportUser? = null, name: String? = null): Player {
        val playerName = user?.name ?: name ?: "Jugador"
        val player = Player().apply {
            this.name = playerName
            this.user = user
        }
        user?.players?.add(player)
        return player
    }

    private fun createTeam(color: TeamColor, teamName: String): Team {
        return Team().apply {
            this.name = teamName
            this.color = color
            this.players = mutableListOf()
        }
    }

    private fun addPlayersToEvent(event: Event, count: Int, realUsers: List<SportUser> = emptyList()) {
        realUsers.forEach { user ->
            if (event.unnasignedPlayers.size < event.maxPlayers) {
                val alreadyJoined = event.unnasignedPlayers.any { it.user?.username == user.username }
                if (!alreadyJoined) {
                    event.join(createPlayer(user = user))
                }
            }
        }
        val currentSize = event.unnasignedPlayers.size
        for (i in 1..(count - currentSize)) {
            if (event.unnasignedPlayers.size < event.maxPlayers) {
                event.join(createPlayer(name = "Jugador Invitado $i"))
            }
        }
    }

    private fun assignTeams(event: Event) {
        val allPlayers = ArrayList(event.unnasignedPlayers)
        event.unnasignedPlayers.clear()
        // Importante: desvincular del evento para evitar duplicados en JPA
        allPlayers.forEach { it.event = null }

        if (allPlayers.isNotEmpty()) {
            val mid = allPlayers.size / 2
            event.teams[0].players.addAll(allPlayers.subList(0, mid))
            event.teams[1].players.addAll(allPlayers.subList(mid, allPlayers.size))
        }
    }

    // --- CREATE ENTITIES ---

    private fun createUsers(): List<SportUser> {
        val user1 = SportUser("1234", "admin", "messi@mail.com", "Lio", "Messi", LocalDate.of(1990, 1, 1), role = Role.ORGANIZER).apply {
            addProfile(SportProfile(this, Sport.FOOTBALL, FootballProfileDetail(mutableListOf("ST"), "ST", true, 10)))
            addProfile(SportProfile(this, Sport.PADDLE, PaddleProfileDetail("Izquierda", "Ofensivo", true, true, 9)))
        }
        val user2 = SportUser("pass2", "mari_star", "mari@mail.com", "Mariana", "Gomez", LocalDate.of(1995, 5, 10), role = Role.PLAYER).apply {
            addProfile(SportProfile(this, Sport.VOLLEY, VolleyProfileDetail(mutableListOf("Opuesto"), "Opuesto", 280, "Ataque", true, 8)))
            addProfile(SportProfile(this, Sport.FOOTBALL, FootballProfileDetail(mutableListOf("CM"), "CM", false, 7)))
        }
        val user3 = SportUser("pass3", "pablo_pro", "pablo@mail.com", "Pablo", "Perez", LocalDate.of(1985, 12, 20), role = Role.PLAYER).apply {
            addProfile(SportProfile(this, Sport.PADDLE, PaddleProfileDetail("Derecha", "Defensivo", false, true, 6)))
        }

        authService.register(user1)
        authService.register(user2)
        authService.register(user3)

        try {
            loadProfilePicture(user1, "/config/admin_profile.png", "admin_profile.png")
            loadProfilePicture(user2, "/config/julian_profile.png", "julian_profile.png")
        } catch (e: Exception) {
            // Ignorar
        }

        return listOf(user1, user2, user3)
    }

    // --- EVENTS CREATION ---

    private fun createFootballEvents(users: List<SportUser>, locCentral: Location, locPredio: Location) {
        // 1. FULL (5v5) - Con Lineups
        val ev1 = FootballEvent().apply {
            name = "Torneo Relámpago - Final"
            dateTime = LocalDateTime.now().plusDays(2).withHour(19).withMinute(0)
            minPlayers = 10
            maxPlayers = 10
            location = locCentral
            cost = BigDecimal("2500.00")
            organizer = users[0] // Lio
            pitchSize = 5
            teams.add(createTeam(TeamColor.BLUE, "Los Rayos"))
            teams.add(createTeam(TeamColor.RED, "Furia Roja"))
            transferData = TransferData().apply { cbu = "0000003100044888333322"; alias = "torneo.relampago.pago" }
        }
        addPlayersToEvent(ev1, 10, users)
        assignTeams(ev1)
        val savedEv1 = eventRepository.save(ev1)
        lineupService.createLineups(savedEv1)

        // 2. ALMOST FULL (5v5) - Sin Lineups
        val ev2 = FootballEvent().apply {
            name = "Fútbol 5 Amistoso"
            dateTime = LocalDateTime.now().plusDays(1).withHour(21).withMinute(0)
            minPlayers = 10
            maxPlayers = 10
            location = locCentral
            cost = BigDecimal("2000.00")
            organizer = users[1] // Mariana
            pitchSize = 5
            teams.add(createTeam(TeamColor.WHITE, "Real Bañil"))
            teams.add(createTeam(TeamColor.BLACK, "Inter de Mitre"))
            transferData = TransferData().apply { cbu = "1112223334445556667778"; alias = "mariana.futbol.alias" }
        }
        addPlayersToEvent(ev2, 9, listOf(users[1], users[2])) // Falta 1
        eventRepository.save(ev2)

        // 3. FULL (7v7) - Con Lineups
        val ev3 = FootballEvent().apply {
            name = "Clásico del Domingo (F7)"
            dateTime = LocalDateTime.now().plusDays(5).withHour(10).withMinute(0)
            minPlayers = 14
            maxPlayers = 14
            location = locPredio
            cost = BigDecimal("3000.00")
            organizer = users[2] // Pablo
            pitchSize = 7
            teams.add(createTeam(TeamColor.GREEN, "La Máquina Verde"))
            teams.add(createTeam(TeamColor.WHITE, "Los Galácticos"))
            transferData = TransferData().apply { cbu = "222200044448888"; alias = "pablo.domingo.f7" }
        }
        addPlayersToEvent(ev3, 14, users)
        assignTeams(ev3)
        val savedEv3 = eventRepository.save(ev3)
        lineupService.createLineups(savedEv3)

        // 4. VACÍO (Sin jugadores)
        val ev4 = FootballEvent().apply {
            name = "Entrenamiento Táctico"
            dateTime = LocalDateTime.now().plusDays(10).withHour(18).withMinute(0)
            minPlayers = 10
            maxPlayers = 10
            location = locCentral
            cost = BigDecimal("1000.00")
            organizer = users[0] // Lio
            pitchSize = 5
            teams.add(createTeam(TeamColor.BLUE, "Titulares"))
            teams.add(createTeam(TeamColor.RED, "Suplentes"))
            transferData = TransferData().apply { cbu = "0000000000000000000000"; alias = "sin.jugadores" }
        }
        // No agregamos jugadores
        eventRepository.save(ev4)
    }

    private fun createPaddleEvents(users: List<SportUser>, loc: Location) {
        // 5. FULL
        val ev5 = PaddleEvent().apply {
            name = "Pádel Competitivo"
            dateTime = LocalDateTime.now().plusDays(3).withHour(18).withMinute(30)
            minPlayers = 4
            maxPlayers = 4
            location = loc
            cost = BigDecimal("4500.00")
            organizer = users[2] // Pablo
            teams.add(createTeam(TeamColor.GREEN, "Los Smashers"))
            teams.add(createTeam(TeamColor.BLUE, "Volea Mágica"))
            transferData = TransferData().apply { cbu = "9876543210123456789012"; alias = "padel.unq.club" }
        }
        addPlayersToEvent(ev5, 4, users)
        assignTeams(ev5)
        eventRepository.save(ev5)

        // 6. ALMOST FULL
        val ev6 = PaddleEvent().apply {
            name = "Clase Abierta Pádel"
            dateTime = LocalDateTime.now().plusDays(4).withHour(10).withMinute(0)
            minPlayers = 4
            maxPlayers = 4
            location = loc
            cost = BigDecimal("3000.00")
            organizer = users[0] // Lio
            teams.add(createTeam(TeamColor.RED, "Dinamita"))
            teams.add(createTeam(TeamColor.WHITE, "Pura Clase"))
            transferData = TransferData().apply { cbu = "4561237890123456789012"; alias = "clase.padel.pago" }
        }
        addPlayersToEvent(ev6, 2, listOf(users[0]))
        eventRepository.save(ev6)

        // 7. VACÍO
        val ev7 = PaddleEvent().apply {
            name = "Torneo Americano (Inscripción)"
            dateTime = LocalDateTime.now().plusDays(20).withHour(9).withMinute(0)
            minPlayers = 4
            maxPlayers = 4
            location = loc
            cost = BigDecimal("5000.00")
            organizer = users[1] // Mariana
            teams.add(createTeam(TeamColor.BLACK, "Pareja 1"))
            teams.add(createTeam(TeamColor.GREEN, "Pareja 2"))
            transferData = TransferData().apply { cbu = "111122223333444455"; alias = "torneo.americano" }
        }
        eventRepository.save(ev7)
    }

    private fun createVolleyEvents(users: List<SportUser>, loc: Location) {
        // 8. FULL (6v6)
        val ev8 = VolleyEvent().apply {
            name = "Voley Mixto Pro"
            dateTime = LocalDateTime.now().plusDays(5).withHour(20).withMinute(0)
            minPlayers = 12
            maxPlayers = 12
            location = loc
            cost = BigDecimal("1500.00")
            organizer = users[1] // Mariana
            teams.add(createTeam(TeamColor.BLACK, "Bloqueo Total"))
            teams.add(createTeam(TeamColor.RED, "Saque Potencia"))
            transferData = TransferData().apply { cbu = "3216549870123456789012"; alias = "voley.mixto.2024" }
        }
        addPlayersToEvent(ev8, 12, users)
        assignTeams(ev8)
        eventRepository.save(ev8)

        // 9. ALMOST FULL (Beach 2v2)
        val ev9 = VolleyEvent().apply {
            name = "Beach Volley Training"
            dateTime = LocalDateTime.now().plusDays(6).withHour(17).withMinute(0)
            minPlayers = 4
            maxPlayers = 4
            location = loc
            cost = BigDecimal("2000.00")
            organizer = users[0] // Lio
            teams.add(createTeam(TeamColor.BLUE, "Arena y Sol"))
            teams.add(createTeam(TeamColor.GREEN, "Los Cangrejos"))
            transferData = TransferData().apply { cbu = "7894561230123456789012"; alias = "beach.volley.sol" }
        }
        addPlayersToEvent(ev9, 3, listOf(users[0], users[2]))
        eventRepository.save(ev9)

        // 10. FULL (Universitario)
        val ev10 = VolleyEvent().apply {
            name = "Voley Universitario"
            dateTime = LocalDateTime.now().plusDays(8).withHour(19).withMinute(30)
            minPlayers = 12
            maxPlayers = 12
            location = loc
            cost = BigDecimal("500.00")
            organizer = users[2] // Pablo
            teams.add(createTeam(TeamColor.WHITE, "Facultad A"))
            teams.add(createTeam(TeamColor.BLUE, "Facultad B"))
            transferData = TransferData().apply { cbu = "999888777666"; alias = "voley.uni" }
        }
        addPlayersToEvent(ev10, 12, users)
        assignTeams(ev10)
        eventRepository.save(ev10)
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
                override fun transferTo(dest: File) { dest.writeBytes(getBytes()) }
            }
            profilePictureService.uploadProfilePicture(multipartFile, user)
        }
    }
}