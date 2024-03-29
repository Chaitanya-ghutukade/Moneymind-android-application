package com.example.moneymind;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.moneymind.models.Transaction;
import com.example.moneymind.models.Userdata;
import com.example.moneymind.utils.Constants;
import com.example.moneymind.utils.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MainDB";
    private static final int DATABASE_VERSION =6;

    // User table
    private static final String USER_TABLE_NAME = "Users";
    private static final String USER_COLUMN_ID = "UserID";
    private static final String USER_COLUMN_FULL_NAME = "Full_name";
    private static final String USER_COLUMN_USERNAME = "Username";
    private static final String USER_COLUMN_EMAIL = "Email";
    private static final String USER_COLUMN_PASSWORD = "Password";

    // Account table
    private static final String ACCOUNT_TABLE_NAME = "Accounts";
    private static final String ACCOUNT_COLUMN_ID = "AccountID";
    private static final String ACCOUNT_COLUMN_USER_ID = "UserID";
    private static final String ACCOUNT_COLUMN_NAME = "AccountName";


    //Transaction table

    private static final String TRANSACTION_TABLE_NAME = "Transactions";
    private static final String TRANSACTION_COLUMN_TYPE = "TransactionType";
    private static final String TRANSACTION_COLUMN_CATEGORY = "TransactionCategory";
    private static final String TRANSACTION_COLUMN_ACCOUNT = "TransactionAccount";
    private static final String TRANSACTION_COLUMN_AMOUNT = "TransactionAmount";
    private static final String TRANSACTION_COLUMN_NOTE = "TransactionNote";
    private static final String TRANSACTION_COLUMN_DATE = "TransactionDate";
    private static final String TRANSACTION_COLUMN_ID = "TransactionId";
   // private static final String TRANSACTION_COLUMN_ACCOUNT_ID = "TransactionAccountId";


    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create User table
        String createUserTable = "CREATE TABLE " + USER_TABLE_NAME + " (" +
                USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_COLUMN_FULL_NAME + " TEXT, " +
                USER_COLUMN_USERNAME + " TEXT, " +
                USER_COLUMN_EMAIL + " TEXT, " +
                USER_COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUserTable);

        // Create Account table
        String createAccountTable = "CREATE TABLE " + ACCOUNT_TABLE_NAME + " (" +
                ACCOUNT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACCOUNT_COLUMN_USER_ID + " INTEGER, " +
                ACCOUNT_COLUMN_NAME + " TEXT, " +
                "FOREIGN KEY(" + ACCOUNT_COLUMN_USER_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_COLUMN_ID + "))";
        db.execSQL(createAccountTable);


        // Create transaction table
        String createTransactionTable = "CREATE TABLE " + TRANSACTION_TABLE_NAME + " (" +
                TRANSACTION_COLUMN_ID + " LONG PRIMARY KEY, " +
                //  TRANSACTION_COLUMN_ACCOUNT_ID + " INTEGER, " +
                TRANSACTION_COLUMN_TYPE + " TEXT, " +
                TRANSACTION_COLUMN_CATEGORY + " TEXT, " +
                TRANSACTION_COLUMN_ACCOUNT + " TEXT, " +
                TRANSACTION_COLUMN_AMOUNT + " DOUBLE, " +
                TRANSACTION_COLUMN_NOTE + " TEXT, " +
                TRANSACTION_COLUMN_DATE + " TEXT " +
                // "FOREIGN KEY(" + TRANSACTION_COLUMN_ACCOUNT_ID + ") REFERENCES " + ACCOUNT_TABLE_NAME + "(" + ACCOUNT_COLUMN_ID +
                ")";
        db.execSQL(createTransactionTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE_NAME);
        db.execSQL(("DROP TABLE IF EXISTS " + TRANSACTION_TABLE_NAME));

        onCreate(db);

    }

    public Boolean createUser(String full_name, String email, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_FULL_NAME, full_name);
        values.put(USER_COLUMN_USERNAME, username);
        values.put(USER_COLUMN_EMAIL, email);
        values.put(USER_COLUMN_PASSWORD, password);

        long result = db.insert(USER_TABLE_NAME, null, values);
        if (result == -1) return false;
        else
            return true;
    }

    public long createAccount(int userID, String accountName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_COLUMN_USER_ID, userID);
        values.put(ACCOUNT_COLUMN_NAME, accountName);

        return db.insert(ACCOUNT_TABLE_NAME, null, values);
    }

    public long addtransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String formattedDate = Helper.format_date(transaction.getDate());
        values.put(TRANSACTION_COLUMN_ID, transaction.getId());
        //  values.put(TRANSACTION_COLUMN_ACCOUNT_ID, transactionAccountId);
        values.put(TRANSACTION_COLUMN_TYPE, transaction.getType());
        values.put(TRANSACTION_COLUMN_CATEGORY, transaction.getCategory());
        values.put(TRANSACTION_COLUMN_ACCOUNT, transaction.getAccount());
        values.put(TRANSACTION_COLUMN_AMOUNT, transaction.getAmount());
        values.put(TRANSACTION_COLUMN_NOTE, transaction.getNote());
        values.put(TRANSACTION_COLUMN_DATE, formattedDate);

        return db.insert(TRANSACTION_TABLE_NAME, null, values);
    }



    public void deleteTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        long transactionId = transaction.getId();
        String[] whereArgs = new String[]{String.valueOf(transactionId)};
        String sql = "DELETE FROM " + TRANSACTION_TABLE_NAME + " WHERE " + TRANSACTION_COLUMN_ID + " = ?";
        db.execSQL(sql, whereArgs);

    }

    public boolean checkUsername(String Username) {
        SQLiteDatabase userdb = this.getWritableDatabase();
        Cursor cursor = userdb.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_USERNAME + "=?", new String[]{Username});
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;

    }

    public Boolean checkusernamepassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_USERNAME + "=?" + " AND " + USER_COLUMN_PASSWORD + "=?", new String[]{username, password});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean checkAccountname(String Accountname, int userid) {
        SQLiteDatabase userdb = this.getWritableDatabase();
        Cursor cursor = userdb.rawQuery("SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " + ACCOUNT_COLUMN_NAME + "=? And " + ACCOUNT_COLUMN_USER_ID + "=?", new String[]{Accountname, String.valueOf(userid)});
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;

    }


    public Userdata getuserdetails(String full_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + USER_COLUMN_FULL_NAME + "," + USER_COLUMN_EMAIL + "  FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_USERNAME + "=?", new String[]{full_name});
        Userdata userinfo = new Userdata();

        if (cursor != null && cursor.moveToFirst()) { // Check if the cursor is not null and if it moves to the first row

            int fullNameIndex = cursor.getColumnIndex(USER_COLUMN_FULL_NAME);
            int emailIndex = cursor.getColumnIndex(USER_COLUMN_EMAIL);

            // Check if column indices are valid before retrieving data
            if (fullNameIndex >= 0) {
                userinfo.user_name = cursor.getString(fullNameIndex);
            }

            if (emailIndex >= 0) {
                userinfo.user_email = cursor.getString(emailIndex);
            }


        }
        return userinfo;
    }


    public int getuserid(String full_name) {
        int user_id = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + USER_COLUMN_ID + "  FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_USERNAME + "=?", new String[]{full_name});

        if (cursor != null && cursor.moveToFirst()) {

            int userid_Index = cursor.getColumnIndex(USER_COLUMN_ID);

            // Check if column indices are valid before retrieving data
            if (userid_Index >= 0) {
                user_id = cursor.getInt(userid_Index);
            }

        }
        return user_id;
    }

    public int get_account_id(String acc_name, int userid) {
        int acc_id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        String u_id = String.valueOf(userid);

        Cursor cursor = db.rawQuery("SELECT " + ACCOUNT_COLUMN_ID + "  FROM " + ACCOUNT_TABLE_NAME + " WHERE " + ACCOUNT_COLUMN_NAME + "=?" + " AND " + ACCOUNT_COLUMN_USER_ID + "=?", new String[]{acc_name, u_id});

        if (cursor != null && cursor.moveToFirst()) {

            int acc_id_Index = cursor.getColumnIndex(ACCOUNT_COLUMN_ID);

            // Check if column indices are valid before retrieving data
            if (acc_id_Index >= 0) {
                acc_id = cursor.getInt(acc_id_Index);
            }

        }
        return acc_id;
    }


    public ArrayList<String> fetchAccountNames(int userid) {
        ArrayList<String> accountNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selectionArgs = {String.valueOf(userid)};
        Cursor cursor = db.rawQuery("SELECT " + ACCOUNT_COLUMN_NAME + "  FROM " + ACCOUNT_TABLE_NAME + " WHERE " + USER_COLUMN_ID + "=?", selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int accname = cursor.getColumnIndex(ACCOUNT_COLUMN_NAME);


                if (accname >= 0) {
                    String accountName = cursor.getString(accname);
                    accountNames.add(accountName);
                }

            } while (cursor.moveToNext());
        }

        return accountNames;
    }

    public Boolean delete_account(String acc_name, int usr_id) {

        SQLiteDatabase db = this.getReadableDatabase();
        int rowdeleted = db.delete(ACCOUNT_TABLE_NAME, ACCOUNT_COLUMN_USER_ID + "=? And " + ACCOUNT_COLUMN_NAME + "=? ", new String[]{String.valueOf(usr_id), acc_name});

        if (rowdeleted > 0) {
            return true;
        }
        return false;

    }

    public Boolean edit_account(int acc_id, int usr_id, String newacc_name) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_COLUMN_NAME, newacc_name);
        int rowedited = db.update(ACCOUNT_TABLE_NAME, values, ACCOUNT_COLUMN_ID + " = ? And " + ACCOUNT_COLUMN_USER_ID + "= ?", new String[]{String.valueOf(acc_id), String.valueOf(usr_id)});
        if (rowedited > 0) {
            return true;
        }
        return false;


    }

    // Method to fetch transaction details for a specific account ID and return as a list of Transaction objects
    public ArrayList<Transaction> getTransactionDetailsForAccount(Calendar calendar) {

        String formattedDate=Helper.format_date(calendar.getTime());
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String rawQuery = "SELECT * FROM " + TRANSACTION_TABLE_NAME +" WHERE "+ TRANSACTION_COLUMN_DATE +" = ? ";
        String[] selectionArgs = {formattedDate};
        Cursor cursor = db.rawQuery(rawQuery, selectionArgs);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = -1;
                String type = null;
                String category = null;
                String account = null;
                double amount = 0.0;
                String note = null;
                Date date = null;

                int idIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_ID);
                if (idIndex >= 0) {
                    id = cursor.getLong(idIndex);
                }

                int typeIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_TYPE);
                if (typeIndex >= 0) {
                    type = cursor.getString(typeIndex);
                }

                int categoryIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_CATEGORY);
                if (categoryIndex >= 0) {
                    category = cursor.getString(categoryIndex);
                }

                int accountIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_ACCOUNT);
                if (accountIndex >= 0) {
                    account = cursor.getString(accountIndex);
                }

                int amountIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_AMOUNT);
                if (amountIndex >= 0) {
                    amount = cursor.getDouble(amountIndex);
                }

                int noteIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_NOTE);
                if (noteIndex >= 0) {
                    note = cursor.getString(noteIndex);
                }

                int dateIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_DATE);
                if (dateIndex >= 0) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM,yy", Locale.getDefault());
                        date = dateFormat.parse(cursor.getString(dateIndex));

                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Handle parsing exception
                    }


                }

                Transaction transaction = new Transaction(type, category, note, account, date, amount, id);

                transactions.add(transaction);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return transactions;
    }

    public ArrayList<Transaction> getTransactionDetailsOfDayChart(Calendar calendar,String Type) {

        String formattedDate=Helper.format_date(calendar.getTime());
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String rawQuery = "SELECT * FROM " + TRANSACTION_TABLE_NAME +" WHERE "+ TRANSACTION_COLUMN_DATE +" = ? AND " + TRANSACTION_COLUMN_TYPE + " = ?";
        String[] selectionArgs = {formattedDate,Type};
        Cursor cursor = db.rawQuery(rawQuery, selectionArgs);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = -1;
                String type = null;
                String category = null;
                String account = null;
                double amount = 0.0;
                String note = null;
                Date date = null;

                int idIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_ID);
                if (idIndex >= 0) {
                    id = cursor.getLong(idIndex);
                }

                int typeIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_TYPE);
                if (typeIndex >= 0) {
                    type = cursor.getString(typeIndex);
                }

                int categoryIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_CATEGORY);
                if (categoryIndex >= 0) {
                    category = cursor.getString(categoryIndex);
                }

                int accountIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_ACCOUNT);
                if (accountIndex >= 0) {
                    account = cursor.getString(accountIndex);
                }

                int amountIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_AMOUNT);
                if (amountIndex >= 0) {
                    amount = cursor.getDouble(amountIndex);
                }

                int noteIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_NOTE);
                if (noteIndex >= 0) {
                    note = cursor.getString(noteIndex);
                }

                int dateIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_DATE);
                if (dateIndex >= 0) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM,yy", Locale.getDefault());
                        date = dateFormat.parse(cursor.getString(dateIndex));

                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Handle parsing exception
                    }


                }

                Transaction transaction = new Transaction(type, category, note, account, date, amount, id);

                transactions.add(transaction);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return transactions;
    }

    public ArrayList<Transaction> getTransactionDetailsOfMonths(Calendar calendar) {

        String formattedDate=Helper.format_date_month(calendar.getTime());
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Assuming tableName and dateColumnName are String variables representing the table name and date column respectively

        String query = "SELECT * FROM " + TRANSACTION_TABLE_NAME +
                " WHERE " + TRANSACTION_COLUMN_DATE + " LIKE ?";


        Cursor cursor = db.rawQuery(query, new String[]{"% " + formattedDate});


        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = -1;
                String type = null;
                String category = null;
                String account = null;
                double amount = 0.0;
                String note = null;
                Date date = null;

                int idIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_ID);
                if (idIndex >= 0) {
                    id = cursor.getLong(idIndex);
                }

                int typeIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_TYPE);
                if (typeIndex >= 0) {
                    type = cursor.getString(typeIndex);
                }

                int categoryIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_CATEGORY);
                if (categoryIndex >= 0) {
                    category = cursor.getString(categoryIndex);
                }

                int accountIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_ACCOUNT);
                if (accountIndex >= 0) {
                    account = cursor.getString(accountIndex);
                }

                int amountIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_AMOUNT);
                if (amountIndex >= 0) {
                    amount = cursor.getDouble(amountIndex);
                }

                int noteIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_NOTE);
                if (noteIndex >= 0) {
                    note = cursor.getString(noteIndex);
                }

                int dateIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_DATE);
                if (dateIndex >= 0) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM,yy", Locale.getDefault());
                        date = dateFormat.parse(cursor.getString(dateIndex));

                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Handle parsing exception
                    }


                }

                Transaction transaction = new Transaction(type, category, note, account, date, amount, id);

                transactions.add(transaction);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return transactions;
    }

    public ArrayList<Transaction> getTransactionDetailsOfMonthsChart(Calendar calendar,String Type) {

        String formattedDate=Helper.format_date_month(calendar.getTime());
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Assuming tableName and dateColumnName are String variables representing the table name and date column respectively

        String query = "SELECT * FROM " + TRANSACTION_TABLE_NAME +
                " WHERE " + TRANSACTION_COLUMN_DATE + " LIKE ? AND " + TRANSACTION_COLUMN_TYPE + " = ?";


        Cursor cursor = db.rawQuery(query, new String[]{"% " + formattedDate,Type});


        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = -1;
                String type = null;
                String category = null;
                String account = null;
                double amount = 0.0;
                String note = null;
                Date date = null;

                int idIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_ID);
                if (idIndex >= 0) {
                    id = cursor.getLong(idIndex);
                }

                int typeIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_TYPE);
                if (typeIndex >= 0) {
                    type = cursor.getString(typeIndex);
                }

                int categoryIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_CATEGORY);
                if (categoryIndex >= 0) {
                    category = cursor.getString(categoryIndex);
                }

                int accountIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_ACCOUNT);
                if (accountIndex >= 0) {
                    account = cursor.getString(accountIndex);
                }

                int amountIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_AMOUNT);
                if (amountIndex >= 0) {
                    amount = cursor.getDouble(amountIndex);
                }

                int noteIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_NOTE);
                if (noteIndex >= 0) {
                    note = cursor.getString(noteIndex);
                }

                int dateIndex = cursor.getColumnIndex(TRANSACTION_COLUMN_DATE);
                if (dateIndex >= 0) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM,yy", Locale.getDefault());
                        date = dateFormat.parse(cursor.getString(dateIndex));

                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Handle parsing exception
                    }


                }

                Transaction transaction = new Transaction(type, category, note, account, date, amount, id);

                transactions.add(transaction);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return transactions;
    }

    public double getTotalIncomeForDate(Calendar calendar) {
        double totalIncome = 0;
        String formattedDate = Helper.format_date(calendar.getTime());

        // Create your SQLite query to fetch total income for the given date
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TRANSACTION_COLUMN_AMOUNT + ") AS total_income FROM " + TRANSACTION_TABLE_NAME + " WHERE " + TRANSACTION_COLUMN_DATE + " = ? AND " + TRANSACTION_COLUMN_TYPE + " = ?";
        String[] selectionArgs = {formattedDate, "INCOME"};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Retrieve the sum of income from the cursor
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("total_income");
            if (columnIndex >= 0) {
                totalIncome = cursor.getDouble(columnIndex);
            }
            cursor.close();
        }

        // Close the database connection
        db.close();

        return totalIncome;
    }

    public double getTotalIncomeForMonth(Calendar calendar) {
        double totalIncome = 0;
        String formattedDate = Helper.format_date_month(calendar.getTime());

        // Create your SQLite query to fetch total income for the given date
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(" + TRANSACTION_COLUMN_AMOUNT + ") AS total_income FROM " +
                TRANSACTION_TABLE_NAME + " WHERE " +
                TRANSACTION_COLUMN_DATE + " LIKE ? AND " +
                TRANSACTION_COLUMN_TYPE + " = ?";

        String[] selectionArgs = {"% " + formattedDate, "INCOME"};


        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Retrieve the sum of income from the cursor
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("total_income");
            if (columnIndex >= 0) {
                totalIncome = cursor.getDouble(columnIndex);
            }
            cursor.close();
        }

        // Close the database connection
        db.close();

        return totalIncome;
    }

    public double getTotalExpenseForDate(Calendar calendar) {
        double totalExpense= 0;
        String formattedDate = Helper.format_date(calendar.getTime());

        // Create your SQLite query to fetch total income for the given date
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TRANSACTION_COLUMN_AMOUNT + ") AS total_expense FROM " + TRANSACTION_TABLE_NAME + " WHERE " + TRANSACTION_COLUMN_DATE + " = ? AND " + TRANSACTION_COLUMN_TYPE + " = ?";
        String[] selectionArgs = {formattedDate, "EXPENSE"};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Retrieve the sum of income from the cursor
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("total_expense");
            if (columnIndex >= 0) {
                totalExpense = cursor.getDouble(columnIndex);
            }
            cursor.close();
        }

        // Close the database connection
        db.close();

        return totalExpense;
    }

    public double getTotalExpenseForMonth(Calendar calendar) {
        double totalExpense= 0;
        String formattedDate = Helper.format_date_month(calendar.getTime());

        // Create your SQLite query to fetch total income for the given date
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(" + TRANSACTION_COLUMN_AMOUNT + ") AS total_expense FROM " +
                TRANSACTION_TABLE_NAME + " WHERE " +
                TRANSACTION_COLUMN_DATE + " LIKE ? AND " +
                TRANSACTION_COLUMN_TYPE + " = ?";

        String[] selectionArgs = {"% " + formattedDate, "EXPENSE"};


        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Retrieve the sum of income from the cursor
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("total_expense");
            if (columnIndex >= 0) {
                totalExpense = cursor.getDouble(columnIndex);
            }
            cursor.close();
        }

        // Close the database connection
        db.close();

        return totalExpense;
    }

    public double getTotalAccountForDate(Calendar calendar) {
        double totalAccount= 0;
        String formattedDate = Helper.format_date(calendar.getTime());

        // Create your SQLite query to fetch total income for the given date
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TRANSACTION_COLUMN_AMOUNT + ") AS total_expense FROM " + TRANSACTION_TABLE_NAME + " WHERE " + TRANSACTION_COLUMN_DATE + " = ?  " ;
        String[] selectionArgs = {formattedDate};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Retrieve the sum of income from the cursor
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("total_expense");
            if (columnIndex >= 0) {
                totalAccount = cursor.getDouble(columnIndex);
            }
            cursor.close();
        }

        // Close the database connection
        db.close();

        return totalAccount;
    }

    public double getTotalAccountForMonth(Calendar calendar) {
        double totalAccount= 0;
        String formattedDate = Helper.format_date_month(calendar.getTime());

        // Create your SQLite query to fetch total income for the given date
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(" + TRANSACTION_COLUMN_AMOUNT + ") AS total_expense FROM " +
                TRANSACTION_TABLE_NAME + " WHERE " +
                TRANSACTION_COLUMN_DATE + " LIKE ?  ";


        String[] selectionArgs = {"% " + formattedDate};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Retrieve the sum of income from the cursor
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("total_expense");
            if (columnIndex >= 0) {
                totalAccount = cursor.getDouble(columnIndex);
            }
            cursor.close();
        }

        // Close the database connection
        db.close();

        return totalAccount;
    }



}



