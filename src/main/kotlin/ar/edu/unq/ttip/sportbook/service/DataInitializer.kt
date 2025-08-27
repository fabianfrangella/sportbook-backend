package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.FootballEventJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.LocationJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.PaddelEventJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.PlayerJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.SportUserJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.TeamJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.TransferDataJPA
import ar.edu.unq.ttip.sportbook.persistence.entity.VolleyEventJPA
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
class DataInitializer(val eventJpaRepository: EventJpaRepository) {

    @PostConstruct
    @Transactional
    fun initialize() {
        val generateData = System.getenv()["GENERATE_DATA"].toBoolean()
        if (!generateData) {
            println("WILL NOT GENERATE DATA")
            return
        }
        val newPlayers = (1..22).map {
            val player = PlayerJPA()
            player.name = "Player $it"
            player.user = SportUserJPA(
                username = "playerusername$it",
                password = "",
                name = "Player $it",
                lastName = "Last Name $it",
                email = "player$it@gmail.com",
                dateOfBirth = LocalDate.of(1994,9,20))
            player
        }

        val footballEvent = FootballEventJPA().apply {
            minPlayers = 10
            maxPlayers = 20
            dateTime = LocalDateTime.now().plus(10, ChronoUnit.DAYS)
            location = LocationJPA().apply {
                x = "-34.713390223118736"
                y = "-58.28190778950768"
                placeName = "ABC Ateneo Bernal"
            }
            cost = BigDecimal(10000)
            transferData = TransferDataJPA().apply {
                cbu = "1095432198059"
                alias = "carpincho.torre.bici"
            }
            players = newPlayers.take(10)
            creator =  "Fabi"
            organizer = "Fabi"
            pitchSize = 5
            firstTeam = TeamJPA().apply {
                color = "Rojo"
                players = newPlayers
                    .take(5)
                    .toMutableList()
            }
            secondTeam = TeamJPA().apply {
                color = "Azul"
                players = newPlayers
                    .drop(5)
                    .take(5)
                    .toMutableList()
            }
        }

        val volleyEvent = VolleyEventJPA().apply {
            minPlayers = 10
            maxPlayers = 20
            dateTime = LocalDateTime.now().plus(10, ChronoUnit.DAYS)
            location = LocationJPA().apply {
                x = "-34.713390223118736"
                y = "-58.28190778950768"
                placeName = "ABC Ateneo Bernal"
            }
            cost = BigDecimal(10000)
            transferData = TransferDataJPA().apply {
                cbu = "1231243124132"
                alias = "pez.roto.cuero"
            }
            players = newPlayers.take(5)
            creator =  "Fabi"
            organizer = "Fabi"
            teams = newPlayers.take(2).map {
                TeamJPA().apply {
                    val colors = listOf("Rojo", "Azul", "Verde", "Amarillo", "Negro", "Blanco")
                    val randomIndex = Random.nextInt(colors.size);
                    val randomColor = colors[randomIndex]
                    color = randomColor
                    players = newPlayers
                        .take(6)
                        .toMutableList()
                }
            }
        }


        val paddelEvent = PaddelEventJPA().apply {
            minPlayers = 10
            maxPlayers = 20
            dateTime = LocalDateTime.now().plus(10, ChronoUnit.DAYS)
            location = LocationJPA().apply {
                x = "-34.713390223118736"
                y = "-58.28190778950768"
                placeName = "ABC Ateneo Bernal"
            }
            cost = BigDecimal(10000)
            transferData = TransferDataJPA().apply {
                cbu = "12312312312"
                alias = "obi.juan.kenobi"
            }
            players = newPlayers.take(5)
            creator =  "Fabi"
            organizer = "Fabi"
            teams = newPlayers.take(4).map {
                TeamJPA().apply {
                    val colors = listOf("Rojo", "Azul", "Verde", "Amarillo", "Negro", "Blanco")
                    val randomIndex = Random.nextInt(colors.size);
                    val randomColor = colors[randomIndex]
                    color = randomColor
                    players = newPlayers
                        .take(2)
                        .toMutableList()
                }
            }
        }

        eventJpaRepository.saveAll(listOf(footballEvent, volleyEvent,paddelEvent))
    }
}