<?php

namespace App\Landing\Service;

class TopicList
{
    public const STRESS = 'stress';
    public const ANXIETY = 'anxiety';
    public const DEPRESSION = 'depression';
    public const PANIC_ATTACKS = 'panic_attacks';
    public const DECLINE_OF_STRENGTH = 'decline_of_strength';
    public const LOW_SELF_ESTEEM = 'low_self_esteem';
    public const MOOD_SWINGS = 'mood_swings';
    public const IRRITABILITY = 'irritability';
    public const AGGRESSION = 'aggression';
    public const SLEEP_PROBLEMS = 'sleep_problems';
    public const A_FEELING_OF_LONELINESS = 'a_feeling_of_loneliness';
    public const EMOTIONAL_DEPENDENCE = 'emotional_dependence';
    public const HYPOCHONDRIA = 'hypochondria';
    public const EATING_DISORDER = 'eating_disorder';
    public const RELATIONSHIP_WITH_A_PARTNER = 'relationship_with_a_partner';
    public const RELATIONS_WITH_PARENTS = 'relations_with_parents';
    public const RELATIONSHIP_WITH_CHILDREN = 'relationship_with_children';
    public const RELATIONSHIPS_IN_GENERAL_WITH_OTHERS = 'relationships_in_general_with_others';
    public const SEXUAL_PROBLEMS_DISORDERS = 'sexual_problems_disorders';
    public const DIFFICULTIES_WITH_ORIENTATION_ITS_SEARCH = 'difficulties_with_orientation_its_search';
    public const BURNOUT = 'burnout';
    public const LACK_OF_MOTIVATION = 'lack_of_motivation';
    public const PROCRASTINATION = 'procrastination';
    public const I_DON_T_KNOW_WHAT_I_WANT_TO_DO = 'i_don_t_know_what_i_want_to_do';
    public const CHANGE_JOB_LOSS = 'change_job_loss';
    public const WAR = 'war';
    public const LOSS_OF_A_LOVED_ONE = 'loss_of_a_loved_one';
    public const MOVING_EMIGRATING = 'moving_emigrating';
    public const PREGNANCY = 'pregnancy';
    public const BIRTH_OF_A_CHILD = 'birth_of_a_child';
    public const FINANCIAL_CHANGES = 'financial_changes';
    public const BREAKUP_OF_RELATIONS_DIVORCE = 'breakup_of_relations_divorce';
    public const TREASON = 'treason';
    public const VIOLENCE = 'violence';
    public const ILLNESS_RELATIVES = 'illness_relatives';
    public const PSYCHOSOMATICS = 'psychosomatics';

    public const ALL_TOPICS = [
        self::STRESS,
        self::ANXIETY,
        self::DEPRESSION,
        self::PANIC_ATTACKS,
        self::DECLINE_OF_STRENGTH,
        self::LOW_SELF_ESTEEM,
        self::MOOD_SWINGS,
        self::IRRITABILITY,
        self::AGGRESSION,
        self::SLEEP_PROBLEMS,
        self::A_FEELING_OF_LONELINESS,
        self::EMOTIONAL_DEPENDENCE,
        self::HYPOCHONDRIA,
        self::EATING_DISORDER,
        self::RELATIONSHIP_WITH_A_PARTNER,
        self::RELATIONS_WITH_PARENTS,
        self::RELATIONSHIP_WITH_CHILDREN,
        self::RELATIONSHIPS_IN_GENERAL_WITH_OTHERS,
        self::SEXUAL_PROBLEMS_DISORDERS,
        self::DIFFICULTIES_WITH_ORIENTATION_ITS_SEARCH,
        self::BURNOUT,
        self::LACK_OF_MOTIVATION,
        self::PROCRASTINATION,
        self::I_DON_T_KNOW_WHAT_I_WANT_TO_DO,
        self::CHANGE_JOB_LOSS,
        self::WAR,
        self::LOSS_OF_A_LOVED_ONE,
        self::MOVING_EMIGRATING,
        self::PREGNANCY,
        self::BIRTH_OF_A_CHILD,
        self::FINANCIAL_CHANGES,
        self::BREAKUP_OF_RELATIONS_DIVORCE,
        self::TREASON,
        self::VIOLENCE,
        self::ILLNESS_RELATIVES,
        self::PSYCHOSOMATICS,
    ];

    public const TOPIC_GROUP_MY_STATE = 'My_state';
    public const TOPIC_GROUP_RELATIONS = 'Relations';
    public const TOPIC_GROUP_WORK_CAREER_STUDY = 'Work_career_study';
    public const TOPIC_GROUP_LIFE_EVENTS = 'Life_events';

    public const TOPIC_TO_TOPIC_GROUP = [
        self::STRESS => self::TOPIC_GROUP_MY_STATE,
        self::ANXIETY => self::TOPIC_GROUP_MY_STATE,
        self::DEPRESSION => self::TOPIC_GROUP_MY_STATE,
        self::PANIC_ATTACKS => self::TOPIC_GROUP_MY_STATE,
        self::DECLINE_OF_STRENGTH => self::TOPIC_GROUP_MY_STATE,
        self::LOW_SELF_ESTEEM => self::TOPIC_GROUP_MY_STATE,
        self::MOOD_SWINGS => self::TOPIC_GROUP_MY_STATE,
        self::IRRITABILITY => self::TOPIC_GROUP_MY_STATE,
        self::AGGRESSION => self::TOPIC_GROUP_MY_STATE,
        self::SLEEP_PROBLEMS => self::TOPIC_GROUP_MY_STATE,
        self::A_FEELING_OF_LONELINESS => self::TOPIC_GROUP_MY_STATE,
        self::HYPOCHONDRIA => self::TOPIC_GROUP_MY_STATE,
        self::EATING_DISORDER => self::TOPIC_GROUP_MY_STATE,
        self::PSYCHOSOMATICS => self::TOPIC_GROUP_MY_STATE,

        self::EMOTIONAL_DEPENDENCE => self::TOPIC_GROUP_RELATIONS,
        self::RELATIONSHIP_WITH_A_PARTNER => self::TOPIC_GROUP_RELATIONS,
        self::RELATIONS_WITH_PARENTS => self::TOPIC_GROUP_RELATIONS,
        self::RELATIONSHIP_WITH_CHILDREN => self::TOPIC_GROUP_RELATIONS,
        self::RELATIONSHIPS_IN_GENERAL_WITH_OTHERS => self::TOPIC_GROUP_RELATIONS,
        self::SEXUAL_PROBLEMS_DISORDERS => self::TOPIC_GROUP_RELATIONS,
        self::DIFFICULTIES_WITH_ORIENTATION_ITS_SEARCH => self::TOPIC_GROUP_RELATIONS,

        self::BURNOUT => self::TOPIC_GROUP_WORK_CAREER_STUDY,
        self::LACK_OF_MOTIVATION => self::TOPIC_GROUP_WORK_CAREER_STUDY,
        self::PROCRASTINATION => self::TOPIC_GROUP_WORK_CAREER_STUDY,
        self::I_DON_T_KNOW_WHAT_I_WANT_TO_DO => self::TOPIC_GROUP_WORK_CAREER_STUDY,
        self::CHANGE_JOB_LOSS => self::TOPIC_GROUP_WORK_CAREER_STUDY,

        self::WAR => self::TOPIC_GROUP_LIFE_EVENTS,
        self::LOSS_OF_A_LOVED_ONE => self::TOPIC_GROUP_LIFE_EVENTS,
        self::MOVING_EMIGRATING => self::TOPIC_GROUP_LIFE_EVENTS,
        self::PREGNANCY => self::TOPIC_GROUP_LIFE_EVENTS,
        self::BIRTH_OF_A_CHILD => self::TOPIC_GROUP_LIFE_EVENTS,
        self::FINANCIAL_CHANGES => self::TOPIC_GROUP_LIFE_EVENTS,
        self::BREAKUP_OF_RELATIONS_DIVORCE => self::TOPIC_GROUP_LIFE_EVENTS,
        self::TREASON => self::TOPIC_GROUP_LIFE_EVENTS,
        self::VIOLENCE => self::TOPIC_GROUP_LIFE_EVENTS,
        self::ILLNESS_RELATIVES => self::TOPIC_GROUP_LIFE_EVENTS,
    ];

    public function groupTopics(array $worksWith): array
    {
        $grouped = [];
        foreach ($worksWith as $topic) {
            $group = TopicList::TOPIC_TO_TOPIC_GROUP[$topic];
            $grouped[$group][] = $topic;
        }

        return $grouped;
    }
}
