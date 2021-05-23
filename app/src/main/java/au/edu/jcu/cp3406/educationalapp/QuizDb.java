package au.edu.jcu.cp3406.educationalapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import au.edu.jcu.cp3406.educationalapp.QuizContract.QuestionsTable;

public class QuizDb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EducationalApp.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;

    public QuizDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Sets up SQLite Database with Columns, Question, Options.

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NUMBER + " INTEGER" +
                ")";
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillQuestionsTable();
    }
    // If we make changes in onCreate, for example add a extra option's column.
    // We need to delete our old tables and call onCreate to establish a new database.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        onCreate(db);
    }
    // This will fill the database with the Question, options and our answer assigned to a Integer in correlation to the options.

    private void fillQuestionsTable() {
        Question q1 = new Question("Easy: A is correct",
                "A", "B", "C", 1);
        addQuestion(q1);
        Question q2 = new Question("Medium: B is correct",
                "A", "B", "C", 2);
        addQuestion(q2);
        Question q3 = new Question("Medium: C is correct",
                "A", "B", "C", 3);
        addQuestion(q3);
        Question q4 = new Question("Hard: A is correct",
                "A", "B", "C", 1);
        addQuestion(q4);
        Question q5 = new Question("Hard: B is correct",
                "A", "B", "C", 2);
        addQuestion(q5);
        Question q6 = new Question("Hard: C is correct",
                "A", "B", "C", 3);
        addQuestion(q6);
    }

    // This will add fillQuestionsTable to the Database.

    private void addQuestion(Question question) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        contentValues.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        contentValues.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        contentValues.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        contentValues.put(QuestionsTable.COLUMN_ANSWER_NUMBER, question.getAnswer_number());
        db.insert(QuestionsTable.TABLE_NAME, null, contentValues);
    }

    // Read and Retrieves Questions, Options and AnswerNumber as list questionList.

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswer_number(cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NUMBER)));
                questionList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }
}