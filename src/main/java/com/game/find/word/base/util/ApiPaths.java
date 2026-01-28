package com.game.find.word.base.util;

public class ApiPaths {
    public static final String BASE_PATH_V1 = "/api/v1";
    private static final String FIND_WORD_PATH = "/scrambled-word";
    private static final String ADMIN_PATH = "/admin";
    private static final String SENTENCE_PATH = "/sentence";
    private static final String SENTENCE_BUILDER_PATH = "/sentence-builder";
    private static final String KEYWORD_QUIZ_PATH = "/keyword-quiz";
    private static final String GLOBAL_PATH = "/global";
    private static final String VERSION_PATH = "/version";
    private static final String MATCH_SENTENCE_PATH = "/match-sentence";
    private static final String GRID_CHALLANGE_PATH = "/grid-challange";
    private static final String LIST_WORD_PATH = "/list-word";

    public static final class GridChallangeCtrl {
        public static final String CTRL = BASE_PATH_V1 + GRID_CHALLANGE_PATH;
    }
    public static final class MatchSentenceAdminCtrl {
        public static final String CTRL = BASE_PATH_V1 +  ADMIN_PATH + MATCH_SENTENCE_PATH;
    }

    public static final class MatchSentencehCtrl {
        public static final String CTRL = BASE_PATH_V1 + MATCH_SENTENCE_PATH;
    }

    public static final class VersionAdminCtrl {
        public static final String CTRL = BASE_PATH_V1 +  ADMIN_PATH + VERSION_PATH;
    }

    public static final class VersionCtrl {
        public static final String CTRL = BASE_PATH_V1 + VERSION_PATH;
    }

    public static final class GlobalCtrl {
        public static final String CTRL = BASE_PATH_V1 + GLOBAL_PATH;
    }

    public static final class ScrambledWordCtrl {
        public static final String CTRL = BASE_PATH_V1 + FIND_WORD_PATH;
    }

    public static final class ScrambledWordAdminCtrl {
        public static final String CTRL = BASE_PATH_V1 + ADMIN_PATH + FIND_WORD_PATH;
    }
    public static final class ListenWordCtrl {
        public static final String CTRL = BASE_PATH_V1 + LIST_WORD_PATH;
    }

    public static final class ListenWordAdminCtrl {
        public static final String CTRL = BASE_PATH_V1 + ADMIN_PATH + LIST_WORD_PATH;
    }
    public static final class KeywordQuizCtrl {
        public static final String CTRL = BASE_PATH_V1 + KEYWORD_QUIZ_PATH;
    }

    public static final class KeywordQuizAdminCtrl {
        public static final String CTRL = BASE_PATH_V1 + ADMIN_PATH + KEYWORD_QUIZ_PATH;
    }

    public static final class SentenceCtrl {
        public static final String CTRL = BASE_PATH_V1 + SENTENCE_PATH;
    }

    public static final class SentenceAdminCtrl {
        public static final String CTRL = BASE_PATH_V1 + ADMIN_PATH + SENTENCE_PATH;
    }
    public static final class SentenceBuilderCtrl {
        public static final String CTRL = BASE_PATH_V1 + SENTENCE_BUILDER_PATH;
    }

    public static final class SentenceBuilderAdminCtrl {
        public static final String CTRL = BASE_PATH_V1 + ADMIN_PATH + SENTENCE_BUILDER_PATH;
    }
}
