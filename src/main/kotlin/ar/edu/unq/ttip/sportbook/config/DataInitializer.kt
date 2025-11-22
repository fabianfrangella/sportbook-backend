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
        if (userRepository.count() > 0) return


        val users = createUsers()


        val locCentral = createLocation("-34.6037", "-58.3816", "Cancha Central")
        val locPadel = createLocation("-34.5900", "-58.4000", "Pista de Padel UNQ")
        val locGym = createLocation("-34.5800", "-58.3500", "Gimnasio Cubierto")


        createFootballEvents(users, locCentral)
        createPaddleEvents(users, locPadel)
        createVolleyEvents(users, locGym)
    }



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

    private fun createTeam(color: TeamColor): Team {
        return Team().apply {
            this.name = "El Equipo ${color.name.lowercase().replaceFirstChar { it.uppercase() }}"
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

        val playersToAssign = ArrayList(event.unnasignedPlayers)

        if (playersToAssign.isNotEmpty()) {
            val mid = playersToAssign.size / 2


            event.teams[0].players.addAll(playersToAssign.subList(0, mid))
            event.teams[1].players.addAll(playersToAssign.subList(mid, playersToAssign.size))


            event.unnasignedPlayers.clear()




            playersToAssign.forEach { it.event = null }
        }
    }



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

        }

        return listOf(user1, user2, user3)
    }



    private fun createFootballEvents(users: List<SportUser>, loc: Location) {

        val evFull = FootballEvent().apply {
            name = "Torneo Relámpago - Final"
            dateTime = LocalDateTime.now().plusDays(2).withHour(19).withMinute(0)
            minPlayers = 10
            maxPlayers = 10
            location = loc
            cost = BigDecimal("2500.00")
            organizer = users[0]
            pitchSize = 5
            teams.add(createTeam(TeamColor.BLUE))
            teams.add(createTeam(TeamColor.RED))
            transferData = TransferData().apply {
                cbu = "0000003100044888333322"
                alias = "torneo.relampago.pago"
            }
        }
        addPlayersToEvent(evFull, 10, users)
        assignTeams(evFull)


        val evAlmost = FootballEvent().apply {
            name = "Fútbol 5 Amistoso"
            dateTime = LocalDateTime.now().plusDays(1).withHour(21).withMinute(0)
            minPlayers = 10
            maxPlayers = 10
            location = loc
            cost = BigDecimal("2000.00")
            organizer = users[1]
            pitchSize = 5
            teams.add(createTeam(TeamColor.WHITE))
            teams.add(createTeam(TeamColor.BLACK))
            transferData = TransferData().apply {
                cbu = "1112223334445556667778"
                alias = "mariana.futbol.alias"
            }
        }
        addPlayersToEvent(evAlmost, 9, listOf(users[1], users[2]))

        eventRepository.saveAll(listOf(evFull, evAlmost))
    }

    private fun createPaddleEvents(users: List<SportUser>, loc: Location) {

        val evFull = PaddleEvent().apply {
            name = "Pádel Competitivo"
            dateTime = LocalDateTime.now().plusDays(3).withHour(18).withMinute(30)
            minPlayers = 4
            maxPlayers = 4
            location = loc
            cost = BigDecimal("4500.00")
            organizer = users[2]
            teams.add(createTeam(TeamColor.GREEN))
            teams.add(createTeam(TeamColor.BLUE))
            transferData = TransferData().apply {
                cbu = "9876543210123456789012"
                alias = "padel.unq.club"
            }
        }
        addPlayersToEvent(evFull, 4, users)
        assignTeams(evFull)


        val evAlmost = PaddleEvent().apply {
            name = "Clase Abierta Pádel"
            dateTime = LocalDateTime.now().plusDays(4).withHour(10).withMinute(0)
            minPlayers = 4
            maxPlayers = 4
            location = loc
            cost = BigDecimal("3000.00")
            organizer = users[0]
            teams.add(createTeam(TeamColor.RED))
            teams.add(createTeam(TeamColor.WHITE))
            transferData = TransferData().apply {
                cbu = "4561237890123456789012"
                alias = "clase.padel.pago"
            }
        }
        addPlayersToEvent(evAlmost, 2, listOf(users[0]))

        eventRepository.saveAll(listOf(evFull, evAlmost))
    }

    private fun createVolleyEvents(users: List<SportUser>, loc: Location) {

        val evFull = VolleyEvent().apply {
            name = "Voley Mixto Pro"
            dateTime = LocalDateTime.now().plusDays(5).withHour(20).withMinute(0)
            minPlayers = 12
            maxPlayers = 12
            location = loc
            cost = BigDecimal("1500.00")
            organizer = users[1]
            teams.add(createTeam(TeamColor.BLACK))
            teams.add(createTeam(TeamColor.RED))
            transferData = TransferData().apply {
                cbu = "3216549870123456789012"
                alias = "voley.mixto.2024"
            }
        }
        addPlayersToEvent(evFull, 12, users)
        assignTeams(evFull)


        val evAlmost = VolleyEvent().apply {
            name = "Beach Volley Training"
            dateTime = LocalDateTime.now().plusDays(6).withHour(17).withMinute(0)
            minPlayers = 4
            maxPlayers = 4
            location = loc
            cost = BigDecimal("2000.00")
            organizer = users[0]
            teams.add(createTeam(TeamColor.BLUE))
            teams.add(createTeam(TeamColor.GREEN))
            transferData = TransferData().apply {
                cbu = "7894561230123456789012"
                alias = "beach.volley.sol"
            }
        }
        addPlayersToEvent(evAlmost, 3, listOf(users[0], users[2]))

        eventRepository.saveAll(listOf(evFull, evAlmost))
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