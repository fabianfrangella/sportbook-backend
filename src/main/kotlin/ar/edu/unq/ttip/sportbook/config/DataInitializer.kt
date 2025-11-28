package ar.edu.unq.ttip.sportbook.config

import ar.edu.unq.ttip.sportbook.dto.request.FinishEventRequest
import ar.edu.unq.ttip.sportbook.dto.request.SetResultRequest
import ar.edu.unq.ttip.sportbook.dto.request.TeamGoalRequest
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
import ar.edu.unq.ttip.sportbook.persistence.repository.FinishedEventStatsRepository
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
import kotlin.random.Random

@Component
class SportbookDataInitializer(
    private val authService: AuthService,
    private val userRepository: SportUserJpaRepository,
    private val eventRepository: EventJpaRepository,
    private val finishedEventStatsRepository: FinishedEventStatsRepository,
    private val profilePictureService: ProfilePictureService,
    private val lineupService: LineupService
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        if (userRepository.count() > 0) return

        val locations = createLocations()
        val users = createAllUsers()
        generateRandomHistory(users, locations)
        createActiveEvents(users, locations)

    }


    private fun createLocation(x: String, y: String, name: String) = Location().apply {
        this.x = x; this.y = y; this.placeName = name
    }

    private fun createPlayer(user: SportUser? = null, name: String? = null): Player {
        val playerName = user?.name ?: name ?: "Jugador"
        val player = Player().apply { this.name = playerName; this.user = user }
        user?.players?.add(player)
        return player
    }

    private fun createTeam(color: TeamColor, teamName: String): Team {
        return Team().apply { this.name = teamName; this.color = color; this.players = mutableListOf() }
    }

    private fun addPlayersToEvent(event: Event, count: Int, poolOfUsers: List<SportUser>) {
        val shuffledUsers = poolOfUsers.shuffled()

        for (user in shuffledUsers) {
            if (event.unnasignedPlayers.size >= event.maxPlayers) break
            if (event.unnasignedPlayers.none { it.user?.id == user.id }) {
                event.join(createPlayer(user = user))
            }
        }

        val currentCount = event.unnasignedPlayers.size
        if (currentCount < count) {
            for (i in 1..(count - currentCount)) {
                event.join(createPlayer(name = "Invitado $i"))
            }
        }
    }

    private fun assignTeams(event: Event) {
        val allPlayers = ArrayList(event.unnasignedPlayers)
        event.unnasignedPlayers.clear()
        allPlayers.forEach { it.event = null }
        if (allPlayers.isNotEmpty()) {
            val mid = allPlayers.size / 2
            event.teams[0].players.addAll(allPlayers.subList(0, mid))
            event.teams[1].players.addAll(allPlayers.subList(mid, allPlayers.size))
        }
    }

    // --- LOCATIONS ---
    private fun createLocations(): List<Location> {
        return listOf(
            createLocation("-34.6037", "-58.3816", "Cancha Central"),
            createLocation("-34.5900", "-58.4000", "Pista de Padel UNQ"),
            createLocation("-34.5800", "-58.3500", "Gimnasio Cubierto"),
            createLocation("-34.7000", "-58.2500", "Predio El Porvenir")
        )
    }

    // --- USUARIOS Y PERFILES ---

    private fun createAllUsers(): List<SportUser> {
        val coreUsers = createCoreUsers()
        val randomUsers = generateRandomUsers(20)

        val allUsers = coreUsers + randomUsers

        allUsers.forEachIndexed { index, user ->
            authService.register(user)
            val photoName = if (index % 2 == 0) "admin_profile.png" else "julian_profile.png"
            try {
                loadProfilePicture(user, "/config/$photoName", photoName)
            } catch (e: Exception) {}
        }

        return allUsers
    }

    private fun createCoreUsers(): List<SportUser> {
        // ADMIN (Lio)
        val user1 = SportUser("1234", "admin", "messi@mail.com", "Lio", "Messi", LocalDate.of(1987, 6, 24), Role.ORGANIZER).apply {
            addProfile(SportProfile(this, Sport.FOOTBALL, FootballProfileDetail(mutableListOf("ST", "RW"), "ST", PlayFrequency.VERY_OFTEN, 10)))
            addProfile(SportProfile(this, Sport.PADDLE, PaddleProfileDetail("Izquierda", "Ofensivo", true, PlayFrequency.OFTEN, 9)))
            addProfile(SportProfile(this, Sport.VOLLEY, VolleyProfileDetail(mutableListOf("Punta"), "Punta", 300, "Ataque", "jump", 8, 7, PlayFrequency.OFTEN, 8)))
        }
        val user2 = SportUser("pass2", "mari_star", "mari@mail.com", "Mariana", "Gomez", LocalDate.of(1995, 5, 10), Role.PLAYER).apply {
            addProfile(SportProfile(this, Sport.VOLLEY, VolleyProfileDetail(mutableListOf("Opuesto"), "Opuesto", 280, "Ataque", "jump", 9, 6, PlayFrequency.VERY_OFTEN, 8)))
            addProfile(SportProfile(this, Sport.FOOTBALL, FootballProfileDetail(mutableListOf("CM"), "CM", PlayFrequency.RARELY, 7)))
        }
        val user3 = SportUser("pass3", "pablo_pro", "pablo@mail.com", "Pablo", "Perez", LocalDate.of(1985, 12, 20), Role.PLAYER).apply {
            addProfile(SportProfile(this, Sport.PADDLE, PaddleProfileDetail("Derecha", "Defensivo", false, PlayFrequency.OFTEN, 6)))
        }
        val user4 = SportUser("pass4", "julian_sp", "juli@mail.com", "Julian", "Alvarez", LocalDate.of(2000, 1, 31), Role.PLAYER).apply {
            addProfile(SportProfile(this, Sport.FOOTBALL, FootballProfileDetail(mutableListOf("ST"), "ST", PlayFrequency.VERY_OFTEN, 9)))
        }
        return listOf(user1, user2, user3, user4)
    }

    private fun generateRandomUsers(count: Int): List<SportUser> {
        val names = listOf("Lucas", "Mateo", "Sofia", "Camila", "Nicolas", "Valentina", "Tomas", "Martina", "Agustin", "Lucia", "Federico", "Paula")
        val lastNames = listOf("Garcia", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Perez", "Sanchez", "Romero", "Diaz")

        val generated = mutableListOf<SportUser>()

        for (i in 1..count) {
            val name = names.random()
            val lastName = lastNames.random()
            val username = "${name.lowercase()}_${lastName.lowercase()}$i"

            val user = SportUser(
                "password",
                username,
                "$username@example.com",
                name,
                lastName,
                LocalDate.of(1990 + Random.nextInt(0, 10), 1, 1),
                Role.PLAYER
            )

            user.addProfile(SportProfile(user, Sport.FOOTBALL, FootballProfileDetail(
                positions = mutableListOf(listOf("GK", "DF", "MD", "ST").random()),
                favoritePosition = listOf("GK", "DF", "MD", "ST").random(),
                playsOften = PlayFrequency.values().random(),
                ability = Random.nextInt(4, 10)
            )))

            user.addProfile(SportProfile(user, Sport.PADDLE, PaddleProfileDetail(
                preferredSide = if (Random.nextBoolean()) "Derecha" else "Izquierda",
                playStyle = if (Random.nextBoolean()) "Ofensivo" else "Defensivo",
                playedTournaments = Random.nextBoolean(),
                playsOften = PlayFrequency.values().random(),
                ability = Random.nextInt(3, 9)
            )))

            user.addProfile(SportProfile(user, Sport.VOLLEY, VolleyProfileDetail(
                positions = mutableListOf(listOf("Armador", "Libero", "Punta", "Central").random()),
                favoritePosition = "Punta",
                blockHeight = Random.nextInt(240, 310),
                rolePreference = if(Random.nextBoolean()) "Defensa" else "Ataque",
                serveType = if(Random.nextBoolean()) "jump" else "float",
                offensiveLevel = Random.nextInt(3, 10),
                defensiveLevel = Random.nextInt(3, 10),
                playsOften = PlayFrequency.values().random(),
                ability = Random.nextInt(3, 9)
            )))

            generated.add(user)
        }
        return generated
    }

    // --- HISTORIAL GENERATOR ---
    private fun generateRandomHistory(users: List<SportUser>, locations: List<Location>) {
        for (i in 1..6) {
            createFinishedFootballMatch("Liga Histórica - Fecha $i", users, locations[0], i * 5)
        }
        for (i in 1..4) {
            createFinishedPaddleMatch("Torneo Pádel - Ronda $i", users, locations[1], i * 3)
        }
        for (i in 1..3) {
            createFinishedVolleyMatch("Liga Vóley - Juego $i", users, locations[2], i * 4)
        }
    }

    private fun createFinishedFootballMatch(name: String, pool: List<SportUser>, loc: Location, daysAgo: Int) {
        val ev = FootballEvent().apply {
            this.name = name
            dateTime = LocalDateTime.now().minusDays(daysAgo.toLong())
            minPlayers = 10; maxPlayers = 10; location = loc; cost = BigDecimal(2000); organizer = pool[0]
            pitchSize = 5
            teams.add(createTeam(TeamColor.BLUE, "Azules"))
            teams.add(createTeam(TeamColor.RED, "Rojos"))
            isFinished = true
        }
        addPlayersToEvent(ev, 10, pool)
        assignTeams(ev)
        val savedEv = eventRepository.save(ev)

        val teamA = savedEv.teams[0]
        val teamB = savedEv.teams[1]
        val goalsA = Random.nextInt(1, 6)
        val goalsB = Random.nextInt(1, 6)

        val goalsRequest = mutableListOf<TeamGoalRequest>()
        repeat(goalsA) {
            val scorer = teamA.players.random()
            goalsRequest.add(TeamGoalRequest(teamA.id, scorer.id))
        }
        repeat(goalsB) {
            val scorer = teamB.players.random()
            goalsRequest.add(TeamGoalRequest(teamB.id, scorer.id))
        }

        val winnerId = if (goalsA > goalsB) teamA.id else if (goalsB > goalsA) teamB.id else null
        val mvp = if (winnerId != null) {
            if (winnerId == teamA.id) teamA.players.random() else teamB.players.random()
        } else teamA.players.random()

        finishedEventStatsRepository.save(FinishedEventStats(
            savedEv, FinishEventRequest(winnerId, goalsRequest, emptyList(), mvp.id, null)
        ))
    }

    private fun createFinishedPaddleMatch(name: String, pool: List<SportUser>, loc: Location, daysAgo: Int) {
        val ev = PaddleEvent().apply {
            this.name = name
            dateTime = LocalDateTime.now().minusDays(daysAgo.toLong())
            minPlayers = 4; maxPlayers = 4; location = loc; cost = BigDecimal(4000); organizer = pool[2]
            teams.add(createTeam(TeamColor.GREEN, "Pareja 1"))
            teams.add(createTeam(TeamColor.WHITE, "Pareja 2"))
            isFinished = true
        }
        addPlayersToEvent(ev, 4, pool)
        assignTeams(ev)
        val savedEv = eventRepository.save(ev)

        val t1 = savedEv.teams[0]
        val s1 = SetResultRequest(6, Random.nextInt(0, 5))
        val s2 = SetResultRequest(Random.nextInt(0, 5), 6)
        val s3 = SetResultRequest(6, Random.nextInt(0, 5))

        finishedEventStatsRepository.save(FinishedEventStats(
            savedEv, FinishEventRequest(
                t1.id, emptyList(), emptyList(), t1.players.random().id, listOf(s1, s2, s3)
            )
        ))
    }

    private fun createFinishedVolleyMatch(name: String, pool: List<SportUser>, loc: Location, daysAgo: Int) {
        val ev = VolleyEvent().apply {
            this.name = name
            dateTime = LocalDateTime.now().minusDays(daysAgo.toLong())
            minPlayers = 12; maxPlayers = 12; location = loc; cost = BigDecimal(1500); organizer = pool[1]
            teams.add(createTeam(TeamColor.BLACK, "Negros"))
            teams.add(createTeam(TeamColor.RED, "Rojos"))
            isFinished = true
        }
        addPlayersToEvent(ev, 12, pool)
        assignTeams(ev)
        val savedEv = eventRepository.save(ev)

        val t1 = savedEv.teams[0]
        val s1 = SetResultRequest(25, 20)
        val s2 = SetResultRequest(25, 22)

        finishedEventStatsRepository.save(FinishedEventStats(
            savedEv, FinishEventRequest(
                t1.id, emptyList(), emptyList(), t1.players.random().id, listOf(s1, s2)
            )
        ))
    }

    // --- ACTIVE EVENTS (Futuros) ---

    private fun createActiveEvents(users: List<SportUser>, locations: List<Location>) {
        val (lio, mari, pablo, juli) = users


        // 1. FÚTBOL ADMIN: Lleno con Lineups (Listo para jugar/finalizar)
        val evF1 = FootballEvent().apply {
            name = "Torneo Relámpago - La Final"
            dateTime = LocalDateTime.now().plusDays(2).withHour(20)
            minPlayers = 10; maxPlayers = 10; location = locations[0]; cost = BigDecimal(3000); organizer = lio
            pitchSize = 5
            teams.add(createTeam(TeamColor.BLUE, "Los Galácticos"))
            teams.add(createTeam(TeamColor.RED, "La Scaloneta"))
            transferData = TransferData().apply { cbu = "0000003100044888333322"; alias = "final.torneo" }
        }
        addPlayersToEvent(evF1, 10, users)
        assignTeams(evF1)
        val savedF1 = eventRepository.save(evF1)
        lineupService.createLineups(savedF1)

        // 2. FÚTBOL ADMIN: Vacío (Para probar invitación y auto-tactica desde cero)
        val evF2 = FootballEvent().apply {
            name = "Entrenamiento Táctico Semanal"
            dateTime = LocalDateTime.now().plusDays(10).withHour(19)
            minPlayers = 10; maxPlayers = 10; location = locations[0]; cost = BigDecimal(1000); organizer = lio
            pitchSize = 5
            teams.add(createTeam(TeamColor.BLACK, "Titulares"))
            teams.add(createTeam(TeamColor.WHITE, "Suplentes"))
        }
        eventRepository.save(evF2)

        // 3. FÚTBOL MARIANA: Casi Lleno (Falta 1)
        val evF3 = FootballEvent().apply {
            name = "Fútbol 5 Amistoso"
            dateTime = LocalDateTime.now().plusDays(1).withHour(22)
            minPlayers = 10; maxPlayers = 10; location = locations[0]; cost = BigDecimal(2500); organizer = mari
            pitchSize = 5
            teams.add(createTeam(TeamColor.GREEN, "Verdes"))
            teams.add(createTeam(TeamColor.BLACK, "Negros"))
        }
        addPlayersToEvent(evF3, 9, listOf(mari, juli))
        eventRepository.save(evF3)

        // 4. PADEL ADMIN: Lleno
        val evP1 = PaddleEvent().apply {
            name = "Desafío de Parejas"
            dateTime = LocalDateTime.now().plusDays(3).withHour(18)
            minPlayers = 4; maxPlayers = 4; location = locations[1]; cost = BigDecimal(5000); organizer = lio
            teams.add(createTeam(TeamColor.BLUE, "Los Magos"))
            teams.add(createTeam(TeamColor.RED, "Los Potentes"))
        }
        addPlayersToEvent(evP1, 4, listOf(lio, pablo))
        assignTeams(evP1)
        eventRepository.save(evP1)

        // 5. PADEL ADMIN: Casi Lleno (Falta 1)
        val evP2 = PaddleEvent().apply {
            name = "Clase Abierta Nivel Medio"
            dateTime = LocalDateTime.now().plusDays(5).withHour(10)
            minPlayers = 4; maxPlayers = 4; location = locations[1]; cost = BigDecimal(3000); organizer = lio
            teams.add(createTeam(TeamColor.WHITE, "Alumnos A"))
            teams.add(createTeam(TeamColor.GREEN, "Alumnos B"))
        }
        addPlayersToEvent(evP2, 3, listOf(lio))
        eventRepository.save(evP2)

        // 6. PADEL PABLO: Vacío
        val evP3 = PaddleEvent().apply {
            name = "Torneo Americano"
            dateTime = LocalDateTime.now().plusDays(20).withHour(9)
            minPlayers = 4; maxPlayers = 8; location = locations[1]; cost = BigDecimal(6000); organizer = pablo
            teams.add(createTeam(TeamColor.BLACK, "Grupo 1"))
            teams.add(createTeam(TeamColor.RED, "Grupo 2"))
        }
        eventRepository.save(evP3)

        // 7. VOLEY ADMIN: Lleno (12 jugadores)
        val evV1 = VolleyEvent().apply {
            name = "Entrenamiento Sub-21"
            dateTime = LocalDateTime.now().plusDays(6).withHour(17)
            minPlayers = 12; maxPlayers = 12; location = locations[2]; cost = BigDecimal(1000); organizer = lio
            teams.add(createTeam(TeamColor.BLUE, "Equipo A"))
            teams.add(createTeam(TeamColor.WHITE, "Equipo B"))
        }
        addPlayersToEvent(evV1, 12, users)
        assignTeams(evV1)
        eventRepository.save(evV1)

        // 8. VOLEY ADMIN: Vacío (Beach Volley)
        val evV2 = VolleyEvent().apply {
            name = "Beach Volley Verano"
            dateTime = LocalDateTime.now().plusDays(15).withHour(16)
            minPlayers = 4; maxPlayers = 4; location = locations[3]; cost = BigDecimal(2000); organizer = lio
            teams.add(createTeam(TeamColor.GREEN, "Dupla 1"))
            teams.add(createTeam(TeamColor.RED, "Dupla 2"))
        }
        eventRepository.save(evV2)

        // 9. VOLEY MARIANA: Casi Lleno
        val evV3 = VolleyEvent().apply {
            name = "Voley Mixto Amistoso"
            dateTime = LocalDateTime.now().plusDays(8).withHour(20)
            minPlayers = 12; maxPlayers = 12; location = locations[2]; cost = BigDecimal(1500); organizer = mari
            teams.add(createTeam(TeamColor.BLACK, "Norte"))
            teams.add(createTeam(TeamColor.BLUE, "Sur"))
        }
        addPlayersToEvent(evV3, 10, listOf(mari))
        eventRepository.save(evV3)

        val evF7 = FootballEvent().apply {
            name = "Fútbol 7 - Domingo"
            dateTime = LocalDateTime.now().plusDays(7).withHour(11)
            minPlayers = 14; maxPlayers = 14; location = locations[3]; cost = BigDecimal(3500); organizer = juli
            pitchSize = 7
            teams.add(createTeam(TeamColor.WHITE, "Local"))
            teams.add(createTeam(TeamColor.BLUE, "Visitante"))
        }
        addPlayersToEvent(evF7, 7, listOf(juli))
        eventRepository.save(evF7)
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