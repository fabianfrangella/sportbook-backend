package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.FootballEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.Location
import ar.edu.unq.ttip.sportbook.persistence.entity.PaddleEvent
import ar.edu.unq.ttip.sportbook.persistence.entity.Player
import ar.edu.unq.ttip.sportbook.persistence.entity.SportUser
import ar.edu.unq.ttip.sportbook.persistence.entity.Team
import ar.edu.unq.ttip.sportbook.persistence.entity.TransferData
import ar.edu.unq.ttip.sportbook.persistence.entity.VolleyEvent
import ar.edu.unq.ttip.sportbook.persistence.repository.EventJpaRepository
import jakarta.annotation.PostConstruct

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random


@Service
class DataInitializer(val eventService: EventService, val eventJpaRepository: EventJpaRepository) {

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
            player
        }

        val footballEvent = FootballEvent().apply {
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
            players = newPlayers.take(22)
            creator =  "Fabi"
            organizer = "Fabi"
            pitchSize = 11
            firstTeam = Team().apply {
                color = "Rojo"
                players = newPlayers
                    .take(11)
                    .toMutableList()
            }
            secondTeam = Team().apply {
                color = "Azul"
                players = newPlayers
                    .drop(11)
                    .take(11)
                    .toMutableList()
            }
        }

        val volleyEvent = VolleyEvent().apply {
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
            players = newPlayers.drop(22).take(5)
            creator =  "Fabi"
            organizer = "Fabi"
            teams = newPlayers.drop(22).take(2).map {
                Team().apply {
                    val colors = listOf("Rojo", "Azul", "Verde", "Negro", "Blanco")
                    val randomIndex = Random.nextInt(colors.size);
                    val randomColor = colors[randomIndex]
                    color = randomColor
                    players = listOf(it).toMutableList()
                }
            }
        }


        val paddleEvent = PaddleEvent().apply {
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
            players = newPlayers.drop(42).take(5)
            creator =  "Fabi"
            organizer = "Fabi"
            teams = newPlayers.drop(42).take(4).map {
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
        eventJpaRepository.saveAll(listOf(volleyEvent,paddleEvent))
    }
}