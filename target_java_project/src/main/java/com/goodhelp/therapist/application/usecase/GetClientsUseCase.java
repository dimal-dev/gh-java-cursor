package com.goodhelp.therapist.application.usecase;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.application.dto.ClientDto;
import com.goodhelp.therapist.application.query.GetClientsQuery;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for retrieving clients (users) who have had consultations with a therapist.
 * Returns paginated list with consultation statistics and notes status.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class GetClientsUseCase {

    private final TherapistRepository therapistRepository;
    private final EntityManager entityManager;

    public GetClientsUseCase(
            TherapistRepository therapistRepository,
            EntityManager entityManager) {
        this.therapistRepository = therapistRepository;
        this.entityManager = entityManager;
    }

    /**
     * Result containing clients list and pagination info.
     */
    public record Result(
        List<ClientDto> clients,
        int totalCount,
        int totalPages,
        int currentPage,
        int pageSize
    ) {
        public static Result empty(int page, int size) {
            return new Result(List.of(), 0, 0, page, size);
        }
    }

    /**
     * Execute the query to get clients for a therapist.
     */
    @SuppressWarnings("unchecked")
    public Result execute(@Valid GetClientsQuery query) {
        // Verify therapist exists
        if (!therapistRepository.existsById(query.therapistId())) {
            throw new ResourceNotFoundException("Therapist", query.therapistId());
        }

        int offset = query.page() * query.size();

        // Build the query with optional search filter
        String searchFilter = "";
        if (query.hasSearch()) {
            searchFilter = """
                AND (LOWER(u.full_name) LIKE LOWER(:search) 
                     OR LOWER(u.email) LIKE LOWER(:search)
                     OR LOWER(pun.name) LIKE LOWER(:search))
                """;
        }

        // Native query to get clients with consultation stats
        // Gets distinct users who have had consultations with this therapist
        String sql = """
            SELECT 
                u.id as user_id,
                COALESCE(u.full_name, 'клиент не указал') as full_name,
                u.email,
                COUNT(DISTINCT uc.id) as sessions_amount,
                MAX(ps.available_at) as next_session_at,
                CASE WHEN pun.id IS NOT NULL AND (pun.name IS NOT NULL OR pun.notes IS NOT NULL) THEN 1 ELSE 0 END as has_notes
            FROM user_consultation uc
            INNER JOIN app_user u ON uc.user_id = u.id
            INNER JOIN user_consultation_psiholog_schedule ucps ON uc.id = ucps.user_consultation_id
            INNER JOIN psiholog_schedule ps ON ucps.psiholog_schedule_id = ps.id
            LEFT JOIN psiholog_user_notes pun ON ps.psiholog_id = pun.psiholog_id AND u.id = pun.user_id
            WHERE ps.psiholog_id = :therapistId
            AND ps.state IN ('booked', 'done')
            %s
            GROUP BY u.id, u.full_name, u.email, pun.id, pun.name, pun.notes
            ORDER BY next_session_at DESC
            LIMIT :limit OFFSET :offset
            """.formatted(searchFilter);

        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter("therapistId", query.therapistId());
        nativeQuery.setParameter("limit", query.size());
        nativeQuery.setParameter("offset", offset);
        
        if (query.hasSearch()) {
            nativeQuery.setParameter("search", "%" + query.search() + "%");
        }

        List<Object[]> results = nativeQuery.getResultList();

        List<ClientDto> clients = results.stream()
            .map(this::mapToDto)
            .toList();

        // Get total count
        String countSql = """
            SELECT COUNT(DISTINCT u.id)
            FROM user_consultation uc
            INNER JOIN app_user u ON uc.user_id = u.id
            INNER JOIN user_consultation_psiholog_schedule ucps ON uc.id = ucps.user_consultation_id
            INNER JOIN psiholog_schedule ps ON ucps.psiholog_schedule_id = ps.id
            LEFT JOIN psiholog_user_notes pun ON ps.psiholog_id = pun.psiholog_id AND u.id = pun.user_id
            WHERE ps.psiholog_id = :therapistId
            AND ps.state IN ('booked', 'done')
            %s
            """.formatted(searchFilter);

        Query countQuery = entityManager.createNativeQuery(countSql);
        countQuery.setParameter("therapistId", query.therapistId());
        
        if (query.hasSearch()) {
            countQuery.setParameter("search", "%" + query.search() + "%");
        }
        
        int totalCount = ((Number) countQuery.getSingleResult()).intValue();
        int totalPages = totalCount > 0 ? (int) Math.ceil((double) totalCount / query.size()) : 0;

        return new Result(clients, totalCount, totalPages, query.page(), query.size());
    }

    private ClientDto mapToDto(Object[] row) {
        Long userId = ((Number) row[0]).longValue();
        String fullName = (String) row[1];
        String email = (String) row[2];
        int consultationCount = ((Number) row[3]).intValue();
        LocalDateTime lastConsultation = convertToLocalDateTime(row[4]);
        boolean hasNotes = row[5] != null && ((Number) row[5]).intValue() == 1;

        return new ClientDto(userId, fullName, email, consultationCount, lastConsultation, hasNotes);
    }

    private LocalDateTime convertToLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.sql.Timestamp ts) {
            return ts.toLocalDateTime();
        } else if (value instanceof LocalDateTime ldt) {
            return ldt;
        }
        return LocalDateTime.parse(value.toString().replace(" ", "T"));
    }
}

