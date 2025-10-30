package ar.edu.unq.ttip.sportbook.config

import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Location
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.entity.team.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.event.TransferData
import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Role
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfile
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import ar.edu.unq.ttip.sportbook.service.EventService
import ar.edu.unq.ttip.sportbook.service.ProfilePictureService
import ar.edu.unq.ttip.sportbook.service.auth.AuthService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class DataInitializer(val eventService: EventService,
                      val eventJpaRepository: EventJpaRepository,
                      val authService: AuthService,
                      val profilePictureService: ProfilePictureService) {

    @PostConstruct
    @Transactional
    fun initialize() {
        val generateData = System.getenv()["GENERATE_DATA"].toBoolean()
        if (!generateData) {
            println("WILL NOT GENERATE DATA")
            return
        }
        val messi = SportUser().apply {
            username = "admin"
            password = "1234"
            name = "Lionel"
            lastName = "Messi"
            email = "lio87kpo@hotmail.com"
            role = Role.ORGANIZER
            dateOfBirth = LocalDate.of(1987,6,24)
        }
        messi.profiles = mutableListOf(
            SportProfile(messi, Sport.FOOTBALL, FootballProfileDetail().apply {
                ability = 10
                playsOften = true
            }),
            SportProfile(messi, Sport.PADDLE, PaddleProfileDetail().apply {
                ability = 10
                playsOften = true
            }),
            SportProfile(messi, Sport.VOLLEY, VolleyProfileDetail().apply {
                ability = 10
                playsOften = true
            }))

        val julian = SportUser().apply {
            username = "juli"
            password = "1234"
            name = "Julian"
            lastName = "Alvarez"
            email = "julikpo@hotmail.com"
            role = Role.ORGANIZER
            dateOfBirth = LocalDate.of(2000,1,31)
        }
        julian.profiles = mutableListOf(
            SportProfile(julian, Sport.FOOTBALL, FootballProfileDetail().apply {
                ability = 10
                playsOften = true
            }),
            SportProfile(julian, Sport.PADDLE, PaddleProfileDetail().apply {
                ability = 10
                playsOften = true
            }),
            SportProfile(julian, Sport.VOLLEY, VolleyProfileDetail().apply {
                ability = 10
                playsOften = true
            }))
        authService.register(messi)
        authService.register(julian)
        val newPlayers = (1..35).map {
            val names = listOf("Fabi",
                "Aaron",
                "Margo",
                "Tobi",
                "Tom",
                "Fran",
                "Valentin",
                "Juanma",
                "Fer",
                "Elias",
                "Diego",
                "Male",
                "Ale",
                "Emi",
                "Lu",
                "Abi",
                "Brian",
                "Santi",
                "Guido",
                "Luqui",
                "Mateo",
                "Yoel",
                "Marcos",
                "Miguel",
                "Ricardo",
                "Nico",
                "Agus",
                "Pablo",
                "Gonza",
                "Juli",
                "Jose",
                "Seba",
                "Pedro",
                "Matias",
                "Pepe",
                "Lautaro",)
            val playerName = names[it - 1]
            val player = Player()
            player.name = playerName
            player.user = SportUser(
                username = playerName,
                password = "",
                name = playerName,
                lastName = "Last Name $it",
                email = "$playerName@gmail.com",
                dateOfBirth = LocalDate.of(1994,9,20))
            player.user.profiles = mutableListOf(
                SportProfile(player.user, Sport.FOOTBALL, FootballProfileDetail().apply {
                    ability = Random.nextInt(1, 5)
                    playsOften = Random.nextBoolean()
                }),
                SportProfile(player.user, Sport.PADDLE, PaddleProfileDetail().apply {
                    ability = Random.nextInt(1, 5)
                    playsOften = Random.nextBoolean()
                }),
                SportProfile(player.user, Sport.VOLLEY, VolleyProfileDetail().apply {
                    ability = Random.nextInt(1, 5)
                    playsOften = Random.nextBoolean()
                }))
            player
        }

        val footballEvent = FootballEvent()
        val footballPlayers = newPlayers.take(22).map { it.event = footballEvent; it }
        footballEvent.apply {
            minPlayers = 22
            maxPlayers = 24
            dateTime = LocalDateTime.now().plus(10, ChronoUnit.DAYS)
            location = Location().apply {
                x = "-34.713390223118736"
                y = "-58.28190778950768"
                placeName = "ABC Ateneo Bernal"
            }
            cost = BigDecimal(10000)
            transferData = TransferData().apply {
                cbu = "1095432198059"
                alias = "carpincho.torre.bici"
            }
            players = footballPlayers
            pitchSize = 11
            firstTeam = Team().apply {
                color = "Rojo"
                players = footballPlayers
                    .take(11)
                    .toMutableList()
            }
            secondTeam = Team().apply {
                color = "Azul"
                players = footballPlayers
                    .drop(11)
                    .take(11)
                    .toMutableList()
            }
        }

        val volleyEvent = VolleyEvent()
        val volleyPlayers = newPlayers.drop(22).take(5).map { it.event = volleyEvent; it }
        volleyEvent.apply {
            minPlayers = 10
            maxPlayers = 20
            dateTime = LocalDateTime.now().plus(10, ChronoUnit.DAYS)
            location = Location().apply {
                x = "-34.713390223118736"
                y = "-58.28190778950768"
                placeName = "ABC Ateneo Bernal"
            }
            cost = BigDecimal(10000)
            transferData = TransferData().apply {
                cbu = "1231243124132"
                alias = "pez.roto.cuero"
            }
            players = volleyPlayers
            teams = volleyPlayers.map {
                Team().apply {
                    val colors = listOf("Rojo", "Azul", "Verde", "Negro", "Blanco")
                    val randomIndex = Random.nextInt(colors.size);
                    val randomColor = colors[randomIndex]
                    color = randomColor
                    players = listOf(it).toMutableList()
                }
            }
            organizer = julian
        }


        val paddleEvent = PaddleEvent()
        val paddlePlayers = newPlayers.drop(42).take(5).map { it.event = paddleEvent; it }
        paddleEvent.apply {
            minPlayers = 10
            maxPlayers = 20
            dateTime = LocalDateTime.now().plus(10, ChronoUnit.DAYS)
            location = Location().apply {
                x = "-34.713390223118736"
                y = "-58.28190778950768"
                placeName = "ABC Ateneo Bernal"
            }
            cost = BigDecimal(10000)
            transferData = TransferData().apply {
                cbu = "12312312312"
                alias = "obi.juan.kenobi"
            }
            players = paddlePlayers
            teams = paddlePlayers.take(4).map {
                Team().apply {
                    val colors = listOf("Rojo", "Azul", "Verde", "Negro", "Blanco")
                    val randomIndex = Random.nextInt(colors.size);
                    val randomColor = colors[randomIndex]
                    color = randomColor
                    players = listOf(it).toMutableList()
                }
            }
            organizer = messi
        }

        eventService.createEvent(footballEvent, messi)
        val papiFutbolEvent = FootballEvent()
        val papiFutbolPlayers = newPlayers.drop(27).take(5).map { it.event = papiFutbolEvent; it }
        papiFutbolEvent.apply {
            minPlayers = 10
            maxPlayers = 10
            dateTime = LocalDateTime.now().plus(10, ChronoUnit.DAYS)
            location = Location().apply {
                x = "-34.713390223118736"
                y = "-58.28190778950768"
                placeName = "ABC Ateneo Bernal"
            }
            cost = BigDecimal(10000)
            transferData = TransferData().apply {
                cbu = "1095432198059"
                alias = "carpincho.torre.bici"
            }
            players = papiFutbolPlayers
            pitchSize = 5
            firstTeam = Team().apply {
                color = "Rojo"
                players = papiFutbolPlayers
                    .take(3)
                    .toMutableList()
            }
            secondTeam = Team().apply {
                color = "Azul"
                players = papiFutbolPlayers
                    .drop(3)
                    .take(2)
                    .toMutableList()
            }
        }
        eventService.createEvent(papiFutbolEvent, messi)
        eventJpaRepository.saveAll(listOf(volleyEvent,paddleEvent))

        loadProfilePicture(messi, "/config/admin_profile.png", "admin_profile.png")
        loadProfilePicture(julian, "/config/julian_profile.png", "julian_profile.png")
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