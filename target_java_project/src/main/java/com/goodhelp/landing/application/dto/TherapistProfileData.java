package com.goodhelp.landing.application.dto;

import java.util.*;

/**
 * Static data provider for therapist profile information.
 * In a production system, this would come from a database or external service.
 * 
 * This class stores extended profile information that doesn't fit in the main 
 * database schema (multi-language content, detailed education, methods, etc.)
 */
public final class TherapistProfileData {

    private TherapistProfileData() {}

    // Topic list constants
    public static final String STRESS = "stress";
    public static final String ANXIETY = "anxiety";
    public static final String DEPRESSION = "depression";
    public static final String PANIC_ATTACKS = "panic_attacks";
    public static final String DECLINE_OF_STRENGTH = "decline_of_strength";
    public static final String LOW_SELF_ESTEEM = "low_self_esteem";
    public static final String MOOD_SWINGS = "mood_swings";
    public static final String IRRITABILITY = "irritability";
    public static final String AGGRESSION = "aggression";
    public static final String SLEEP_PROBLEMS = "sleep_problems";
    public static final String A_FEELING_OF_LONELINESS = "a_feeling_of_loneliness";
    public static final String EMOTIONAL_DEPENDENCE = "emotional_dependence";
    public static final String RELATIONSHIP_WITH_A_PARTNER = "relationship_with_a_partner";
    public static final String RELATIONS_WITH_PARENTS = "relations_with_parents";
    public static final String RELATIONSHIP_WITH_CHILDREN = "relationship_with_children";
    public static final String RELATIONSHIPS_IN_GENERAL = "relationships_in_general_with_others";
    public static final String SEXUAL_PROBLEMS = "sexual_problems_disorders";
    public static final String DIFFICULTIES_WITH_ORIENTATION = "difficulties_with_orientation_its_search";
    public static final String BURNOUT = "burnout";
    public static final String LACK_OF_MOTIVATION = "lack_of_motivation";
    public static final String PROCRASTINATION = "procrastination";
    public static final String I_DONT_KNOW_WHAT_I_WANT = "i_don_t_know_what_i_want_to_do";
    public static final String CHANGE_JOB_LOSS = "change_job_loss";
    public static final String WAR = "war";
    public static final String MOVING_EMIGRATING = "moving_emigrating";
    public static final String PREGNANCY = "pregnancy";
    public static final String BIRTH_OF_A_CHILD = "birth_of_a_child";
    public static final String FINANCIAL_CHANGES = "financial_changes";
    public static final String BREAKUP_OF_RELATIONS = "breakup_of_relations_divorce";
    public static final String TREASON = "treason";
    public static final String LOSS_OF_LOVED_ONE = "loss_of_loved_one";

    // Topic groups
    public static final String GROUP_MY_STATE = "My_state";
    public static final String GROUP_RELATIONS = "Relations";
    public static final String GROUP_WORK_CAREER = "Work_career_study";
    public static final String GROUP_LIFE_EVENTS = "Life_events";

    // Topic to group mapping
    private static final Map<String, String> TOPIC_TO_GROUP = Map.ofEntries(
        Map.entry(STRESS, GROUP_MY_STATE),
        Map.entry(ANXIETY, GROUP_MY_STATE),
        Map.entry(DEPRESSION, GROUP_MY_STATE),
        Map.entry(PANIC_ATTACKS, GROUP_MY_STATE),
        Map.entry(DECLINE_OF_STRENGTH, GROUP_MY_STATE),
        Map.entry(LOW_SELF_ESTEEM, GROUP_MY_STATE),
        Map.entry(MOOD_SWINGS, GROUP_MY_STATE),
        Map.entry(IRRITABILITY, GROUP_MY_STATE),
        Map.entry(AGGRESSION, GROUP_MY_STATE),
        Map.entry(SLEEP_PROBLEMS, GROUP_MY_STATE),
        Map.entry(A_FEELING_OF_LONELINESS, GROUP_MY_STATE),
        Map.entry(EMOTIONAL_DEPENDENCE, GROUP_MY_STATE),
        Map.entry(RELATIONSHIP_WITH_A_PARTNER, GROUP_RELATIONS),
        Map.entry(RELATIONS_WITH_PARENTS, GROUP_RELATIONS),
        Map.entry(RELATIONSHIP_WITH_CHILDREN, GROUP_RELATIONS),
        Map.entry(RELATIONSHIPS_IN_GENERAL, GROUP_RELATIONS),
        Map.entry(SEXUAL_PROBLEMS, GROUP_RELATIONS),
        Map.entry(DIFFICULTIES_WITH_ORIENTATION, GROUP_RELATIONS),
        Map.entry(BURNOUT, GROUP_WORK_CAREER),
        Map.entry(LACK_OF_MOTIVATION, GROUP_WORK_CAREER),
        Map.entry(PROCRASTINATION, GROUP_WORK_CAREER),
        Map.entry(I_DONT_KNOW_WHAT_I_WANT, GROUP_WORK_CAREER),
        Map.entry(CHANGE_JOB_LOSS, GROUP_WORK_CAREER),
        Map.entry(WAR, GROUP_LIFE_EVENTS),
        Map.entry(MOVING_EMIGRATING, GROUP_LIFE_EVENTS),
        Map.entry(PREGNANCY, GROUP_LIFE_EVENTS),
        Map.entry(BIRTH_OF_A_CHILD, GROUP_LIFE_EVENTS),
        Map.entry(FINANCIAL_CHANGES, GROUP_LIFE_EVENTS),
        Map.entry(BREAKUP_OF_RELATIONS, GROUP_LIFE_EVENTS),
        Map.entry(TREASON, GROUP_LIFE_EVENTS),
        Map.entry(LOSS_OF_LOVED_ONE, GROUP_LIFE_EVENTS)
    );

    /**
     * Group topics by category.
     */
    public static Map<String, List<String>> groupTopics(List<String> topics) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        
        // Initialize groups in specific order
        result.put(GROUP_MY_STATE, new ArrayList<>());
        result.put(GROUP_RELATIONS, new ArrayList<>());
        result.put(GROUP_WORK_CAREER, new ArrayList<>());
        result.put(GROUP_LIFE_EVENTS, new ArrayList<>());
        
        for (String topic : topics) {
            String group = TOPIC_TO_GROUP.getOrDefault(topic, GROUP_MY_STATE);
            result.get(group).add(topic);
        }
        
        // Remove empty groups
        result.entrySet().removeIf(e -> e.getValue().isEmpty());
        
        return result;
    }

    /**
     * Get profile data for a therapist by ID.
     * Returns null if not found.
     */
    public static ProfileInfo getProfileInfo(Long therapistId) {
        return PROFILES.get(therapistId);
    }

    /**
     * Profile information record.
     */
    public record ProfileInfo(
        String singlePhrase,
        List<String> specialties,
        int experienceYears,
        Integer profileCourses,
        boolean hasPsychologistDiploma,
        boolean isNotTherapist,
        List<String> therapyTypes,
        Map<String, String> about,
        List<String> worksWith,
        Education education,
        List<String> methods,
        List<Map<String, String>> associationAndSupervision,
        List<Review> reviews
    ) {}

    public record Education(
        int mainAmount,
        int additionalAmount,
        List<EducationItem> list
    ) {}

    public record EducationItem(
        String years,
        Map<String, String> name
    ) {}

    public record Review(
        String name,
        String body,
        String created
    ) {}

    // Static profile data (in production, this would come from database)
    private static final Map<Long, ProfileInfo> PROFILES = new HashMap<>();

    static {
        // Example therapist profile - ID 8 (Mariya Koroyed from PHP)
        PROFILES.put(8L, new ProfileInfo(
            "gestalt_therapist_and_lgbt_friendly_psiholog",
            List.of("Sexology", "Crisis_and_trauma", "Organizational_consulting", "Interpersonal_relations"),
            5,
            4,
            true,
            false,
            List.of("Individual", "Couple", "Teenager"),
            Map.of(
                "ru", """
                    <p>Я энергичная, внимательная, с уважением отношусь к инаковости другого. Я верю в уникальность каждого, и что эта уникальность имеет право проявляться во всех областях жизни. Готова быть рядом, когда трудно: слушать и слышать тебя, если надо выговориться; порадоваться вместе с тобой, твоим большим и маленьким успехам.</p>
                    <p>Темы с которыми точно ко мне:<br>
                    - Принятие себя.<br>
                    - Кризисы в браке и вообще в отношениях.<br>
                    - Сексуальность, женская идентичность.<br>
                    - Как не потерять себя как женщину, как личность, в семье и с ребенком.</p>
                    """,
                "ua", """
                    <p>Я енергійна, уважна, з повагою ставлюся до інаковості іншого. Я вірю в унікальність кожного, і що ця унікальність має право виявлятися у всіх сферах життя. Готова бути поряд, коли важко: слухати і чути тебе, якщо треба виговоритись; порадіти разом з тобою, твоїм великим та маленьким успіхам.</p>
                    <p>Теми з якими точно до мене:<br>
                    - Прийняття себе.<br>
                    - Кризи у шлюбі та взагалі в відносинах.<br>
                    - Сексуальність, жіноча ідентичність.<br>
                    - Як не втратити себе як жінку, як особистість, у сім'ї та з дитиною.</p>
                    """,
                "en", """
                    <p>I am energetic, attentive, and respectful of others' differences. I believe in the uniqueness of each person and that this uniqueness has the right to manifest in all areas of life. I am ready to be there when things are difficult: to listen and hear you if you need to talk; to rejoice with you in your big and small successes.</p>
                    <p>Topics to definitely bring to me:<br>
                    - Self-acceptance.<br>
                    - Crises in marriage and relationships in general.<br>
                    - Sexuality, female identity.<br>
                    - How not to lose yourself as a woman, as a person, in family and with children.</p>
                    """
            ),
            List.of(
                STRESS, ANXIETY, DEPRESSION, PANIC_ATTACKS, DECLINE_OF_STRENGTH,
                LOW_SELF_ESTEEM, MOOD_SWINGS, IRRITABILITY, AGGRESSION, SLEEP_PROBLEMS,
                A_FEELING_OF_LONELINESS, EMOTIONAL_DEPENDENCE, RELATIONSHIP_WITH_A_PARTNER,
                RELATIONS_WITH_PARENTS, RELATIONSHIP_WITH_CHILDREN, RELATIONSHIPS_IN_GENERAL,
                SEXUAL_PROBLEMS, DIFFICULTIES_WITH_ORIENTATION, BURNOUT, LACK_OF_MOTIVATION,
                PROCRASTINATION, I_DONT_KNOW_WHAT_I_WANT, CHANGE_JOB_LOSS, WAR,
                MOVING_EMIGRATING, PREGNANCY, BIRTH_OF_A_CHILD, FINANCIAL_CHANGES,
                BREAKUP_OF_RELATIONS, TREASON
            ),
            new Education(
                2,
                5,
                List.of(
                    new EducationItem("2020-2022", Map.of(
                        "ru", "Магистр психологии, «Межрегиональная академия управления персоналом»",
                        "ua", "Магістр психології, «Міжрегіональна академія управління персоналом»",
                        "en", "Master of Psychology, Interregional Academy of Personnel Management"
                    )),
                    new EducationItem("2007-2011", Map.of(
                        "ru", "Магистр социологии, «Академия труда и социальных отношений»",
                        "ua", "Магістр соціології, «Академія праці та соціальних відносин»",
                        "en", "Master of Sociology, Academy of Labor and Social Relations"
                    )),
                    new EducationItem("2018-2022", Map.of(
                        "ru", "Гештальт-терапевт, сертифицированный Московским Гештальт Институтом",
                        "ua", "Гештальт-терапевт, сертифікований Московським Гештальт Інститутом",
                        "en", "Gestalt therapist, certified by the Moscow Gestalt Institute"
                    ))
                )
            ),
            List.of(
                "Gestalt_therapy",
                "Systemic_family_therapy",
                "Art_therapy",
                "Body_therapy"
            ),
            List.of(
                Map.of(
                    "ru", "Европейская Ассоциация Гештальт Терапии (EAGT)",
                    "ua", "Європейська Асоціація Гештальт Терапії (EAGT)",
                    "en", "European Association for Gestalt Therapy (EAGT)"
                )
            ),
            List.of(
                new Review(
                    "Анна",
                    "Очень внимательный и понимающий специалист. Помогла мне разобраться в сложной жизненной ситуации.",
                    "2024-01-15"
                ),
                new Review(
                    "Марія",
                    "Рекомендую! Після декількох сесій почала краще розуміти себе та свої почуття.",
                    "2024-02-20"
                )
            )
        ));

        // Add more sample profiles for testing
        PROFILES.put(1L, new ProfileInfo(
            "clinical_psiholog",
            List.of("Crisis_and_trauma", "Psychosomatics", "Emotional_intelligence"),
            8,
            6,
            true,
            false,
            List.of("Individual", "Couple"),
            Map.of(
                "en", "<p>Experienced clinical psychologist specializing in trauma and crisis intervention. I use evidence-based approaches to help clients overcome difficult life situations.</p>",
                "ua", "<p>Досвідчений клінічний психолог, який спеціалізується на травмах та кризовій інтервенції. Використовую доказові підходи для допомоги клієнтам у подоланні складних життєвих ситуацій.</p>",
                "ru", "<p>Опытный клинический психолог, специализирующийся на травмах и кризисной интервенции. Использую доказательные подходы для помощи клиентам в преодолении сложных жизненных ситуаций.</p>"
            ),
            List.of(STRESS, ANXIETY, DEPRESSION, PANIC_ATTACKS, WAR, LOSS_OF_LOVED_ONE, BURNOUT),
            new Education(2, 3, List.of(
                new EducationItem("2015-2020", Map.of(
                    "en", "PhD in Clinical Psychology, National University",
                    "ua", "Кандидат психологічних наук, Національний університет",
                    "ru", "Кандидат психологических наук, Национальный университет"
                ))
            )),
            List.of("Cognitive_behavioral_therapy_cbt", "Psychoanalysis", "Existential_therapy"),
            List.of(),
            List.of()
        ));
    }
}

