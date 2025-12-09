package com.goodhelp.landing.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a user's request to find a therapist.
 * Stores form submission data from the "Help me find a therapist" form.
 */
@Entity
@Table(name = "user_request_psiholog")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestTherapist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 500, nullable = false)
    private String name;

    @Column(name = "email", length = 500, nullable = false)
    private String email;

    @Column(name = "phone", length = 100)
    private String phone;

    @Column(name = "psiholog_id")
    private Long therapistId;

    @Column(name = "is_processed", nullable = false)
    private Integer isProcessed = 0;

    @Column(name = "lgbtq")
    private Integer lgbtq = 0;

    @Column(name = "channel", length = 500, nullable = false)
    private String channel;

    @Column(name = "problem", columnDefinition = "TEXT", nullable = false)
    private String problem;

    @Column(name = "consultation_type", length = 500, nullable = false)
    private String consultationType;

    @Column(name = "sex", length = 50, nullable = false)
    private String sex = "both";

    @Column(name = "price", length = 500, nullable = false)
    private String price;

    @Column(name = "promocode", length = 500)
    private String promocode;

    @Column(name = "additional_data", columnDefinition = "JSONB")
    private String additionalData = "{}";

    /**
     * Create a new user request for therapist.
     */
    public static UserRequestTherapist create(
            String name,
            String email,
            String phone,
            String channel,
            String problem,
            String consultationType,
            String sex,
            String price,
            String promocode,
            Long therapistId,
            Integer lgbtq) {
        UserRequestTherapist request = new UserRequestTherapist();
        request.setName(name);
        request.setEmail(email);
        request.setPhone(phone);
        request.setChannel(channel);
        request.setProblem(problem);
        request.setConsultationType(consultationType);
        request.setSex(sex);
        request.setPrice(price);
        request.setPromocode(promocode);
        request.setTherapistId(therapistId);
        request.setLgbtq(lgbtq != null ? lgbtq : 0);
        request.setIsProcessed(0);
        return request;
    }
}

