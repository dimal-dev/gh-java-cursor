package com.goodhelp.landing.application.service;

import com.goodhelp.landing.application.dto.TherapistProfileData;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for managing topics/specialties.
 */
@Service
public class TopicService {

    /**
     * Get all available topics grouped by category.
     */
    public Map<String, List<String>> getAllTopicsGrouped() {
        List<String> allTopics = List.of(
                TherapistProfileData.STRESS,
                TherapistProfileData.ANXIETY,
                TherapistProfileData.DEPRESSION,
                TherapistProfileData.PANIC_ATTACKS,
                TherapistProfileData.DECLINE_OF_STRENGTH,
                TherapistProfileData.LOW_SELF_ESTEEM,
                TherapistProfileData.MOOD_SWINGS,
                TherapistProfileData.IRRITABILITY,
                TherapistProfileData.AGGRESSION,
                TherapistProfileData.SLEEP_PROBLEMS,
                TherapistProfileData.A_FEELING_OF_LONELINESS,
                TherapistProfileData.EMOTIONAL_DEPENDENCE,
                TherapistProfileData.RELATIONSHIP_WITH_A_PARTNER,
                TherapistProfileData.RELATIONS_WITH_PARENTS,
                TherapistProfileData.RELATIONSHIP_WITH_CHILDREN,
                TherapistProfileData.RELATIONSHIPS_IN_GENERAL,
                TherapistProfileData.SEXUAL_PROBLEMS,
                TherapistProfileData.DIFFICULTIES_WITH_ORIENTATION,
                TherapistProfileData.BURNOUT,
                TherapistProfileData.LACK_OF_MOTIVATION,
                TherapistProfileData.PROCRASTINATION,
                TherapistProfileData.I_DONT_KNOW_WHAT_I_WANT,
                TherapistProfileData.CHANGE_JOB_LOSS,
                TherapistProfileData.WAR,
                TherapistProfileData.MOVING_EMIGRATING,
                TherapistProfileData.PREGNANCY,
                TherapistProfileData.BIRTH_OF_A_CHILD,
                TherapistProfileData.FINANCIAL_CHANGES,
                TherapistProfileData.BREAKUP_OF_RELATIONS,
                TherapistProfileData.TREASON,
                TherapistProfileData.LOSS_OF_LOVED_ONE
        );
        return TherapistProfileData.groupTopics(allTopics);
    }

    /**
     * Get all topics as a flat list.
     */
    public List<String> getAllTopics() {
        return List.of(
                TherapistProfileData.STRESS,
                TherapistProfileData.ANXIETY,
                TherapistProfileData.DEPRESSION,
                TherapistProfileData.PANIC_ATTACKS,
                TherapistProfileData.DECLINE_OF_STRENGTH,
                TherapistProfileData.LOW_SELF_ESTEEM,
                TherapistProfileData.MOOD_SWINGS,
                TherapistProfileData.IRRITABILITY,
                TherapistProfileData.AGGRESSION,
                TherapistProfileData.SLEEP_PROBLEMS,
                TherapistProfileData.A_FEELING_OF_LONELINESS,
                TherapistProfileData.EMOTIONAL_DEPENDENCE,
                TherapistProfileData.RELATIONSHIP_WITH_A_PARTNER,
                TherapistProfileData.RELATIONS_WITH_PARENTS,
                TherapistProfileData.RELATIONSHIP_WITH_CHILDREN,
                TherapistProfileData.RELATIONSHIPS_IN_GENERAL,
                TherapistProfileData.SEXUAL_PROBLEMS,
                TherapistProfileData.DIFFICULTIES_WITH_ORIENTATION,
                TherapistProfileData.BURNOUT,
                TherapistProfileData.LACK_OF_MOTIVATION,
                TherapistProfileData.PROCRASTINATION,
                TherapistProfileData.I_DONT_KNOW_WHAT_I_WANT,
                TherapistProfileData.CHANGE_JOB_LOSS,
                TherapistProfileData.WAR,
                TherapistProfileData.MOVING_EMIGRATING,
                TherapistProfileData.PREGNANCY,
                TherapistProfileData.BIRTH_OF_A_CHILD,
                TherapistProfileData.FINANCIAL_CHANGES,
                TherapistProfileData.BREAKUP_OF_RELATIONS,
                TherapistProfileData.TREASON,
                TherapistProfileData.LOSS_OF_LOVED_ONE
        );
    }
}

