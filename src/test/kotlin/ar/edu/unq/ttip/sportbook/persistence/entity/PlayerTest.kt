package ar.edu.unq.ttip.sportbook.persistence.entity

import BusinessException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class PlayerTest {

    private fun createMockUser() = SportUser(
        username = "test",
        password = "test123",
        name = "Test",
        lastName = "User",
        email = "test@test.com",
        dateOfBirth = LocalDate.of(1990, 1, 1)
    )

    @Test
    fun `joinTeam successfully adds player to first team`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user)
        val team1 = Team().apply {
            id = 1L
            players = mutableListOf()
            color = "Red"
        }
        val team2 = Team().apply {
            id = 2L
            players = mutableListOf()
            color = "Blue"
        }
        val event = FootballEvent().apply {
            maxPlayers = 10
            firstTeam = team1
            secondTeam = team2
        }

        // Execute
        player.joinTeam(event, 1L)

        // Verify
        assertTrue(team1.players.contains(player))
        assertFalse(team2.players.contains(player))
    }

    @Test
    fun `joinTeam throws exception when team is full`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user)
        val fullTeam = Team().apply {
            id = 1L
            players = MutableList(5) { Player("Player$it", createMockUser()) }
            color = "Red"
        }
        val event = FootballEvent().apply {
            maxPlayers = 10
            firstTeam = fullTeam
            secondTeam = Team().apply {
                id = 2L
                players = mutableListOf()
                color = "Blue"
            }
        }

        // Execute & Verify
        val exception = assertThrows(BusinessException::class.java) {
            player.joinTeam(event, 1L)
        }
        assertEquals("El equipo ya tiene la cantidad maxima de jugadores", exception.message)
    }

    @Test
    fun `joinTeam throws exception when player is already in team`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user)
        val team = Team().apply {
            id = 1L
            players = mutableListOf(player)
            color = "Red"
        }
        val event = FootballEvent().apply {
            maxPlayers = 10
            firstTeam = team
            secondTeam = Team().apply {
                id = 2L
                players = mutableListOf()
                color = "Blue"
            }
        }

        // Execute & Verify
        val exception = assertThrows(BusinessException::class.java) {
            player.joinTeam(event, 1L)
        }
        assertEquals("El jugador ya esta es parte del equipo", exception.message)
    }

    @Test
    fun `joinTeam removes player from other team when switching teams`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user).apply { id = 1L }
        val team1 = Team().apply {
            id = 1L
            players = mutableListOf()
            color = "Red"
        }
        val team2 = Team().apply {
            id = 2L
            players = mutableListOf(player)
            color = "Blue"
        }
        val event = FootballEvent().apply {
            maxPlayers = 10
            firstTeam = team1
            secondTeam = team2
        }

        // Execute
        player.joinTeam(event, 1L)

        // Verify
        assertTrue(team1.players.contains(player))
        assertFalse(team2.players.contains(player))
    }

    @Test
    fun `joinTeam successfully adds player to paddle team`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user)
        val teams = listOf(
            Team().apply {
                id = 1L
                players = mutableListOf()
                color = "Red"
            },
            Team().apply {
                id = 2L
                players = mutableListOf()
                color = "Blue"
            }
        )
        val event = PaddleEvent().apply {
            maxPlayers = 4
            this.teams = teams
        }

        // Execute
        player.joinTeam(event, 1L)

        // Verify
        assertTrue(teams[0].players.contains(player))
        assertFalse(teams[1].players.contains(player))
    }

    @Test
    fun `joinTeam throws exception when paddle team is full`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user)
        val teams = listOf(
            Team().apply {
                id = 1L
                players = MutableList(2) { Player("Player$it", createMockUser()) }
                color = "Red"
            },
            Team().apply {
                id = 2L
                players = mutableListOf()
                color = "Blue"
            }
        )
        val event = PaddleEvent().apply {
            maxPlayers = 4
            this.teams = teams
        }

        // Execute & Verify
        val exception = assertThrows(BusinessException::class.java) {
            player.joinTeam(event, 1L)
        }
        assertEquals("El equipo ya tiene la cantidad maxima de jugadores", exception.message)
    }

    @Test
    fun `joinTeam successfully adds player to volley team`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user)
        val teams = listOf(
            Team().apply {
                id = 1L
                players = mutableListOf()
                color = "Red"
            },
            Team().apply {
                id = 2L
                players = mutableListOf()
                color = "Blue"
            }
        )
        val event = VolleyEvent().apply {
            maxPlayers = 12
            this.teams = teams
        }

        // Execute
        player.joinTeam(event, 1L)

        // Verify
        assertTrue(teams[0].players.contains(player))
        assertFalse(teams[1].players.contains(player))
    }

    @Test
    fun `joinTeam throws exception when volley team is full`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user)
        val teams = listOf(
            Team().apply {
                id = 1L
                players = MutableList(6) { Player("Player$it", createMockUser()) }
                color = "Red"
            },
            Team().apply {
                id = 2L
                players = mutableListOf()
                color = "Blue"
            }
        )
        val event = VolleyEvent().apply {
            maxPlayers = 12
            this.teams = teams
        }

        // Execute & Verify
        val exception = assertThrows(BusinessException::class.java) {
            player.joinTeam(event, 1L)
        }
        assertEquals("El equipo ya tiene la cantidad maxima de jugadores", exception.message)
    }

    @Test
    fun `joinTeam throws exception when team id does not exist in paddle event`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user)
        val teams = listOf(
            Team().apply {
                id = 1L
                players = mutableListOf()
                color = "Red"
            }
        )
        val event = PaddleEvent().apply {
            maxPlayers = 4
            this.teams = teams
        }

        // Execute & Verify
        val exception = assertThrows(BusinessException::class.java) {
            player.joinTeam(event, 999L)
        }
        assertEquals("El equipo no pertenece a este evento", exception.message)
    }

    @Test
    fun `joinTeam throws exception when team id does not exist in volley event`() {
        // Setup
        val user = createMockUser()
        val player = Player("TestPlayer", user)
        val teams = listOf(
            Team().apply {
                id = 1L
                players = mutableListOf()
                color = "Red"
            }
        )
        val event = VolleyEvent().apply {
            maxPlayers = 12
            this.teams = teams
        }

        // Execute & Verify
        val exception = assertThrows(BusinessException::class.java) {
            player.joinTeam(event, 999L)
        }
        assertEquals("El equipo no pertenece a este evento", exception.message)
    }
}
