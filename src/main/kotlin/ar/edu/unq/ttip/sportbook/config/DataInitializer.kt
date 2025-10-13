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
            creator =  "Fabi"
            organizer = "Fabi"
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
            creator =  "Fabi"
            organizer = "Fabi"
            teams = volleyPlayers.map {
                Team().apply {
                    val colors = listOf("Rojo", "Azul", "Verde", "Negro", "Blanco")
                    val randomIndex = Random.nextInt(colors.size);
                    val randomColor = colors[randomIndex]
                    color = randomColor
                    players = listOf(it).toMutableList()
                }
            }
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
            creator =  "Fabi"
            organizer = "Fabi"
            teams = paddlePlayers.take(4).map {
                Team().apply {
                    val colors = listOf("Rojo", "Azul", "Verde", "Negro", "Blanco")
                    val randomIndex = Random.nextInt(colors.size);
                    val randomColor = colors[randomIndex]
                    color = randomColor
                    players = listOf(it).toMutableList()
                }
            }
        }

        eventService.createEvent(footballEvent)
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
            creator =  "Fabi"
            organizer = "Fabi"
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
        eventService.createEvent(papiFutbolEvent)
        eventJpaRepository.saveAll(listOf(volleyEvent,paddleEvent))
        val user = SportUser().apply {
            username = "admin"
            password = "1234"
            name = "Lionel"
            lastName = "Messi"
            email = "lio87kpo@hotmail.com"
        }
        user.profiles = mutableListOf(
            SportProfile(user, Sport.FOOTBALL, FootballProfileDetail().apply {
                ability = 10
                playsOften = true
            }),
            SportProfile(user, Sport.PADDLE, PaddleProfileDetail().apply {
                ability = 10
                playsOften = true
            }),
            SportProfile(user, Sport.VOLLEY, VolleyProfileDetail().apply {
                    ability = 10
                    playsOften = true
            }))
        authService.register(user)

        // Cargamos la foto de perfil para el usuario admin
        val imageResource = this::class.java.getResourceAsStream("/config/admin_profile.png")
        if (imageResource != null) {
            val multipartFile = object : MultipartFile {
                override fun getInputStream() = imageResource
                override fun getName() = "admin_profile.png"
                override fun getOriginalFilename() = "admin_profile.png"
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