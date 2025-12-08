package com.goodhelp.therapist.application.usecase;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.application.dto.ConsultationDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Use case for retrieving upcoming consultations for a therapist.
 * Returns booked consultations that haven't happened yet.
 */
@Service
@Transactional(readOnly = true)
public class GetUpcomingConsultationsUseCase {

    private final TherapistRepository therapistRepository;
    private final EntityManager entityManager;

    public GetUpcomingConsultationsUseCase(
            TherapistRepository therapistRepository,
            EntityManager entityManager) {
        this.therapistRepository = therapistRepository;
        this.entityManager = entityManager;
    }

    /**
     * Query parameters for pagination.
     */
    public record QueryParams(
        Long therapistId,
        int offset,
        int limit
    ) {
        public static QueryParams of(Long therapistId, int page, int pageSize) {
            return new QueryParams(therapistId, page * pageSize, pageSize);
        }
    }

    /**
     * Result containing consultations and pagination info.
     */
    public record Result(
        List<ConsultationDto> consultations,
        int total,
        int offset,
        int limit
    ) {}

    /**
     * Execute the query to get upcoming consultations.
     */
    @SuppressWarnings("unchecked")
    public Result execute(QueryParams params) {
        Therapist therapist = therapistRepository.findById(params.therapistId())
            .orElseThrow(() -> new ResourceNotFoundException("Therapist", params.therapistId()));

        String timezone = therapist.getTimezone();

        // Native query to match PHP logic - get booked consultations with schedule slots
        String sql = """
            SELECT 
                uc.id,
                uc.consultation_type as type,
                COALESCE(u.full_name, 'клиент не указал') as full_name,
                u.is_full_name_set_by_user,
                u.id as user_id,
                MIN(ps.available_at) as available_at,
                MIN(ps.id) as ps_id,
                COALESCE(pun.name, '') as psiholog_user_notes_name
            FROM psiholog_schedule ps
            INNER JOIN user_consultation_psiholog_schedule ucps ON ps.id = ucps.psiholog_schedule_id
            INNER JOIN user_consultation uc ON ucps.user_consultation_id = uc.id
            INNER JOIN app_user u ON uc.user_id = u.id
            LEFT JOIN psiholog_user_notes pun ON ps.psiholog_id = pun.psiholog_id AND uc.user_id = pun.user_id
            WHERE ps.state = 'booked'
            AND uc.state = 'created'
            AND ps.psiholog_id = :therapistId
            AND ps.available_at > :fromTime
            GROUP BY uc.id, uc.consultation_type, u.full_name, u.is_full_name_set_by_user, u.id, pun.name
            ORDER BY available_at ASC
            LIMIT :limit OFFSET :offset
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("therapistId", params.therapistId());
        query.setParameter("fromTime", LocalDateTime.now());
        query.setParameter("limit", params.limit());
        query.setParameter("offset", params.offset());

        List<Object[]> results = query.getResultList();

        List<ConsultationDto> consultations = results.stream()
            .map(row -> mapToDto(row, timezone))
            .toList();

        // Get total count
        String countSql = """
            SELECT COUNT(DISTINCT uc.id)
            FROM psiholog_schedule ps
            INNER JOIN user_consultation_psiholog_schedule ucps ON ps.id = ucps.psiholog_schedule_id
            INNER JOIN user_consultation uc ON ucps.user_consultation_id = uc.id
            WHERE ps.state = 'booked'
            AND uc.state = 'created'
            AND ps.psiholog_id = :therapistId
            AND ps.available_at > :fromTime
            """;

        Query countQuery = entityManager.createNativeQuery(countSql);
        countQuery.setParameter("therapistId", params.therapistId());
        countQuery.setParameter("fromTime", LocalDateTime.now());
        int total = ((Number) countQuery.getSingleResult()).intValue();

        return new Result(consultations, total, params.offset(), params.limit());
    }

    private ConsultationDto mapToDto(Object[] row, String timezone) {
        Long id = ((Number) row[0]).longValue();
        String type = (String) row[1];
        String fullName = (String) row[2];
        boolean isFullNameSetByUser = row[3] != null && ((Number) row[3]).intValue() == 1;
        Long userId = ((Number) row[4]).longValue();
        LocalDateTime availableAt = convertToLocalDateTime(row[5]);
        String notesName = (String) row[7];

        ZonedDateTime localTime = availableAt.atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of(timezone));

        String label = formatDateTimeLabel(localTime);
        long timestampUtc = availableAt.atZone(ZoneId.of("UTC")).toEpochSecond();

        return new ConsultationDto(
            id,
            userId,
            fullName,
            notesName,
            isFullNameSetByUser,
            type,
            availableAt,
            localTime,
            label,
            timestampUtc
        );
    }

    private LocalDateTime convertToLocalDateTime(Object value) {
        if (value instanceof java.sql.Timestamp ts) {
            return ts.toLocalDateTime();
        } else if (value instanceof LocalDateTime ldt) {
            return ldt;
        }
        return LocalDateTime.parse(value.toString().replace(" ", "T"));
    }

    private String formatDateTimeLabel(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "d MMMM yyyy, HH:mm", Locale.forLanguageTag("uk")
        );
        return dateTime.format(formatter);
    }
}

