package ar.edu.unq.ttip.sportbook.service.event_stats

import ar.edu.unq.ttip.sportbook.dto.response.EventStatsResponse
import ar.edu.unq.ttip.sportbook.persistence.entity.event.Event
import org.springframework.stereotype.Component

@Component
class EventStatsMapper {
    fun toResponse(event: Event, calc: CalculatedStats): EventStatsResponse =
        EventStatsResponse(
            eventId = event.id,
            sport = event.sport,
            dateTime = event.dateTime,
            finished = event.isFinished,
            totalRegisteredPlayers = calc.registered,
            presentPlayers = calc.present,
            absentPlayers = calc.absent,
            attendanceRate = calc.attendanceRate,
            totalGoals = calc.totalGoals,
            scores = calc.scores,
            goalsDetail = calc.goalsDetail,
            winningTeam = calc.winningTeam,
            mvp = calc.mvp,
            missingPlayers = calc.missingPlayers,
            sets = calc.sets,
            insights = calc.insights,
        )
}