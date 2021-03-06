package au.edu.jcu.cp3406.educationalapp;

import android.provider.BaseColumns;

public final class QuizContract {
    private QuizContract() {
    }

    public static class CategoriesTable implements BaseColumns {
        public static final String TABLE_NAME = "quiz_categories";
        public static final String COLUMN_NAME = "name";
    }

    // Sets Table ID's (_ID) using Base Columns for use in the Quiz Database.
    public static class QuestionsTable implements BaseColumns {
        public static final String TABLE_NAME = "quiz_questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_ANSWER_NUMBER = "answer_number";
        public static final String COLUMN_CATEGORY_ID = "category_id";
    }
}
