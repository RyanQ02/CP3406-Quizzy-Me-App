package au.edu.jcu.cp3406.educationalapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import au.edu.jcu.cp3406.educationalapp.QuizContract.QuestionsTable;
import au.edu.jcu.cp3406.educationalapp.QuizContract.CategoriesTable;

public class QuizDb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EducationalApp.db";
    private static final int DATABASE_VERSION = 1;

    private static QuizDb instance;

    private SQLiteDatabase db;

    private QuizDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Sets up SQLite Database with Columns, Question, Options.

    public static synchronized QuizDb getInstance(Context context){
        if (instance == null) {
            instance = new QuizDb(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
                CategoriesTable.TABLE_NAME + "( " +
                CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoriesTable.COLUMN_NAME + " TEXT " +
                ")";

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NUMBER + " INTEGER, " +
                QuestionsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY(" + QuestionsTable.COLUMN_CATEGORY_ID + ") REFERENCES " +
                CategoriesTable.TABLE_NAME + "(" + CategoriesTable._ID + ")" + "ON DELETE CASCADE" +
                ")";
        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillCategoriesTable();
        fillQuestionsTable();
    }
    // Any changes to the database need to be updated in onUpgrade by changing DATABASE_VERSION to a new number.
    // Changing the DATABASE_VERSION is useful when users use your app.
    // Alternatively, Uninstalling and reinstalling will reset the SQLite Database. Useful for Testing.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable() {

        Category c1 = new Category("IT");
        addCategory(c1);
        Category c2 = new Category("Science");
        addCategory(c2);
        Category c3 = new Category("Japanese");
        addCategory(c3);
    }

    private void addCategory(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoriesTable.COLUMN_NAME, category.getName());
        db.insert(CategoriesTable.TABLE_NAME, null, contentValues);
    }

    // This will fill the database with the Question, Question Categories, Options, Answer assigned to a Integer in correlation to the options.

    private void fillQuestionsTable() {

        // IT Questions

        Question question1_it = new Question("What does CPU stand for?",
                "A: Central Programming Unit", "B: Central Processing Unit",
                "C: Central Parsing Unit", 2, Category.IT);
        addQuestion(question1_it);

        Question question2_it = new Question("Which is a Static Typed Language?",
                "Java", "Python", "PHP", 1, Category.IT);
        addQuestion(question2_it);

        Question question3_it = new Question("How many bits are in a byte?",
                "A: 32", "B: 8", "C: 4", 2, Category.IT);
        addQuestion(question3_it);

        Question question4_it = new Question("What is the worst case time complexity of linear search algorithm?",
                "A: O(n^2)", "B: O(log n)", "C: O(n)", 1, Category.IT);
        addQuestion(question4_it);

        Question question5_it = new Question("Which data structure uses LIFO (Last In First Out)?",
                "A: Queues", "B: Array", "C: Stack", 3, Category.IT);
        addQuestion(question5_it);


        // Science Questions

        Question question1_science = new Question("In physics, for every action there is an equal and opposite what?",
                "A: Impaction", "B: Reaction", "C: Subtraction", 2, Category.SCIENCE);
        addQuestion(question1_science);

        Question question2_science = new Question("The only living reptiles that use a vertical limb posture in walking is:",
                "A: Turtle", "B: Lizard", "C: Crocodile", 3, Category.SCIENCE);
        addQuestion(question2_science);

        Question question3_science = new Question("Which of these chemicals help fruit to ripen?",
                "A: Ethylene", "B: Carbon Dioxide", "C: Nitrogen Oxide", 1, Category.SCIENCE);
        addQuestion(question3_science);

        Question question4_science = new Question("What is the study of fungi called?",
                "A: Virology", "B: Phycology", "C: Mycology", 3, Category.SCIENCE);
        addQuestion(question4_science);

        Question question5_science = new Question("Faraday is a unit of measurement for?",
                "A: Electricity", "B: Temperature", "C: Sound", 1, Category.SCIENCE);
        addQuestion(question5_science);


        // Japanese Questions

        Question question1_japanese = new Question("What is the Kanji for 'To Eat'? ",
                "A: 食べます", "B: 飲みます", "C: 転がします", 1, Category.JAPANESE);
        addQuestion(question1_japanese);

        Question question2_japanese = new Question("What is a traditional Japanese inn called?",
                "A: Minka", "B: Onsen", "C: Ryokan", 3, Category.JAPANESE);
        addQuestion(question2_japanese);

        Question question3_japanese = new Question("What is the Second Biggest City in Japan?",
                "A: Osaka", "B: Yokohama", "C: Kyoto", 2, Category.JAPANESE);
        addQuestion(question3_japanese);

        Question question4_japanese = new Question("How many islands does Japan have in total?",
                "A: 540 islands", "B: 160 islands", "C: 6852 islands", 3, Category.JAPANESE);
        addQuestion(question4_japanese);

        Question question5_japanese = new Question(" What is “Nice to Meet You!” in Japanese?",
                "A: Hajimemashite", "B: Sayonara", "C: Arigatou", 1, Category.JAPANESE);
        addQuestion(question5_japanese);


    }

    // This will add fillQuestionsTable to the Database.

    private void addQuestion(Question question) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        contentValues.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        contentValues.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        contentValues.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        contentValues.put(QuestionsTable.COLUMN_ANSWER_NUMBER, question.getAnswer_number());
        contentValues.put(QuestionsTable.COLUMN_CATEGORY_ID, question.getCategoryID());
        db.insert(QuestionsTable.TABLE_NAME, null, contentValues);
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + CategoriesTable.TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt(c.getColumnIndex(CategoriesTable._ID)));
                category.setName(c.getString(c.getColumnIndex(CategoriesTable.COLUMN_NAME)));
                categoryList.add(category);
            } while (c.moveToNext());
        }
        c.close();
        return categoryList;
    }

    // Read and Retrieves Questions, Options and AnswerNumber as list questionList.

    public ArrayList<Question> getQuestions(int categoryID) {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        String selection = QuestionsTable.COLUMN_CATEGORY_ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(categoryID)};
        Cursor cursor = db.query(QuestionsTable.TABLE_NAME, null, selection, selectionArgs,
                null, null, null
        );
        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswer_number(cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NUMBER)));
                question.setCategoryID(cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }
}