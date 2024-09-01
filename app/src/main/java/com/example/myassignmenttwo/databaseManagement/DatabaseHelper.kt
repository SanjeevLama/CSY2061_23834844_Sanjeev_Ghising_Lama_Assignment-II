package com.example.myassignmenttwo.databaseManagement

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "socialApp.db"
        private const val DATABASE_VERSION = 1  // Use an integer for the version

        // POST TABLE
        private const val TABLE_POST = "post"
        private const val COLUMN_POSTID = "postId"
        private const val COLUMN_CAPTION = "caption"
        private const val COLUMN_POSTEDATE = "postedDate"
        private const val COLUMN_IMAGE_URI = "imageUri"

        // USER TABLE
        private const val TABLE_USER = "user"
        private const val COLUMN_USERID = "userId"
        private const val COLUMN_FULLNAME = "fullName"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_DOB = "dob"
        private const val COLUMN_DATECREATED = "dateCreated"
        private const val COLUMN_DATEUPDATED = "dateUpdated"
        private const val COLUMN_PASSWORD = "password"

        //ADMIN CLASS
        private const val TABLE_ADMIN = "admin"
        private const val COLUMN_ADMIN_ID = "adminId"
        private const val COLUMN_ADMIN_NAME = "fullName"
        private const val COLUMN_ADMIN_EMAIL = "email"
        private const val COLUMN_ADMIN_PASSWORD = "password"

        // LIKE_POST TABLE
        private const val TABLE_LIKE_POST = "like_post"
        private const val COLUMN_LIKE_ID = "likeId"
        private const val COLUMN_LIKE_POST_ID = "postId"
        private const val COLUMN_LIKE_USER_ID = "userId"

        // COMMENT_POST TABLE
        private const val TABLE_COMMENT_POST = "comment_post"
        private const val COLUMN_COMMENT_ID = "commentId"
        private const val COLUMN_COMMENT_POST_ID = "postId"
        private const val COLUMN_COMMENT_USER_ID = "userId"
        private const val COLUMN_COMMENT_TEXT = "commentText"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // SQL statement to create the USER table
        val createTableUser = ("CREATE TABLE " + TABLE_USER + " ("
                + COLUMN_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FULLNAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_DOB + " DATE, "
                + COLUMN_DATECREATED + " DATE, "
                + COLUMN_DATEUPDATED + " DATE, "
                + COLUMN_PASSWORD + " TEXT)")

        //SQL statement to create the ADMIN TABLE
        val createTableAdmin = ("CREATE TABLE " + TABLE_ADMIN + " ("
                + COLUMN_ADMIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ADMIN_NAME + " TEXT, "
                + COLUMN_ADMIN_EMAIL + " TEXT, "
                + COLUMN_ADMIN_PASSWORD + " TEXT)")

        // SQL statement to create the POST table
        val createTablePost = ("CREATE TABLE " + TABLE_POST + " ("
                + COLUMN_POSTID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CAPTION + " TEXT, "
                + COLUMN_POSTEDATE + " DATE, "
                + COLUMN_USERID + " INTEGER, "
                + COLUMN_IMAGE_URI + " TEXT, "
                + "FOREIGN KEY (" + COLUMN_USERID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USERID + ")"
                + " ON DELETE CASCADE ON UPDATE CASCADE)")

        // SQL statement to create the LIKE_POST table
        val createTableLikePost = ("CREATE TABLE " + TABLE_LIKE_POST + " ("
                + COLUMN_LIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LIKE_POST_ID + " INTEGER, "
                + COLUMN_LIKE_USER_ID + " INTEGER, "
                + "FOREIGN KEY (" + COLUMN_LIKE_POST_ID + ") REFERENCES " + TABLE_POST + "(" + COLUMN_POSTID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY (" + COLUMN_LIKE_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USERID + ") ON DELETE CASCADE)")

        // SQL statement to create the COMMENT_POST table
        val createTableCommentPost = ("CREATE TABLE " + TABLE_COMMENT_POST + " ("
                + COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_COMMENT_POST_ID + " INTEGER, "
                + COLUMN_COMMENT_USER_ID + " INTEGER, "
                + COLUMN_COMMENT_TEXT + " TEXT, "
                + "FOREIGN KEY (" + COLUMN_COMMENT_POST_ID + ") REFERENCES " + TABLE_POST + "(" + COLUMN_POSTID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY (" + COLUMN_COMMENT_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USERID + ") ON DELETE CASCADE)")

        db?.execSQL(createTableUser)
        db?.execSQL(createTableAdmin)
        db?.execSQL(createTablePost)
        db?.execSQL(createTableLikePost)
        db?.execSQL(createTableCommentPost)

        // Insert a default admin row
        val insertAdmin = ("INSERT INTO " + TABLE_ADMIN + " ("
                + COLUMN_ADMIN_NAME + ", "
                + COLUMN_ADMIN_EMAIL + ", "
                + COLUMN_ADMIN_PASSWORD + ") VALUES (?, ?, ?)")

        db?.execSQL(insertAdmin, arrayOf("admin", "admin@.com", "admin"))
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop the LIKE_POST and COMMENT_POST tables first due to foreign key constraints
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LIKE_POST")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_COMMENT_POST")

        // Then drop the POST and USER tables
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_POST")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ADMIN")

        // Recreate all tables
        onCreate(db)
    }

    //function to get current date
    @SuppressLint("DefaultLocale")
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
        val day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
        return "$year-$month-$day"
    }
    //function to get date difference
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeAgo(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val postedDate = LocalDateTime.parse(dateString, formatter)
        val now = LocalDateTime.now()

        val minutesAgo = ChronoUnit.MINUTES.between(postedDate, now)
        val hoursAgo = ChronoUnit.HOURS.between(postedDate, now)
        val daysAgo = ChronoUnit.DAYS.between(postedDate, now)

        return when {
            minutesAgo < 1 -> "just now"
            minutesAgo < 60 -> "$minutesAgo min ago"
            hoursAgo < 24 -> "$hoursAgo hours ago"
            daysAgo < 7 -> "$daysAgo days ago"
            else -> postedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        }
    }

    //function to get current time with date
    @SuppressLint("DefaultLocale")
    private fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
        val day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
        val hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
        val minute = String.format("%02d", calendar.get(Calendar.MINUTE))

        return "$year-$month-$day $hour:$minute"
    }

    //DATABASE OPERATION
    // Add a new user
    fun addUser(fullName:String, email:String, dob:String,password:String): Long {
        val db = this.writableDatabase
        val currentDate = getCurrentDate()  // Get current dat
        val values = ContentValues().apply {
            put(COLUMN_FULLNAME,fullName)
            put(COLUMN_EMAIL, email)
            put(COLUMN_DOB, dob)
            put(COLUMN_DATECREATED, currentDate)
            put(COLUMN_DATEUPDATED, currentDate)
            put(COLUMN_PASSWORD, password)
        }
        return db.insert(TABLE_USER, null, values)
    }
    //Check to see if email already exist for register
    fun emailExists(email: String): Boolean {
        val db = this.readableDatabase

        // Check email in the user table
        val userCursor = db.query(
            TABLE_USER,
            arrayOf(COLUMN_EMAIL),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        val userExists = userCursor.count > 0
        userCursor.close()

        // Check email in the admin table if not found in user table
        if (!userExists) {
            val adminCursor = db.query(
                TABLE_ADMIN,
                arrayOf(COLUMN_ADMIN_EMAIL),
                "$COLUMN_ADMIN_EMAIL = ?",
                arrayOf(email),
                null,
                null,
                null
            )

            val adminExists = adminCursor.count > 0
            adminCursor.close()

            return adminExists
        }

        return true
    }

    fun updateEmailExists(email: String, userId: Int): Boolean {
        val db = this.readableDatabase

        // Check email in the user table excluding the given userId
        val userCursor = db.query(
            TABLE_USER,
            arrayOf(COLUMN_EMAIL),
            "$COLUMN_EMAIL = ? AND $COLUMN_USERID != ?",
            arrayOf(email, userId.toString()),
            null,
            null,
            null
        )

        val userExists = userCursor.count > 0
        userCursor.close()

        // Check email in the admin table for all rows
        if (!userExists) {
            val adminCursor = db.query(
                TABLE_ADMIN,
                arrayOf(COLUMN_ADMIN_EMAIL),
                "$COLUMN_ADMIN_EMAIL = ?",
                arrayOf(email),
                null,
                null,
                null
            )

            val adminExists = adminCursor.count > 0
            adminCursor.close()

            return adminExists
        }

        return true
    }

    //for login
    fun authenticateUser(email: String, password: String): Pair<Boolean, Long?> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(COLUMN_USERID),
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )

        val isAuthenticated = cursor.moveToFirst()
        val userId = if (isAuthenticated) {
            val columnIndex = cursor.getColumnIndex(COLUMN_USERID)
            if (columnIndex != -1) {
                cursor.getLong(columnIndex)
            } else {
                // Column index is invalid; handle this case
                null
            }
        } else {
            null
        }

        cursor.close()
        return Pair(isAuthenticated, userId)
    }

    // Get a user by ID
    fun getUserNameById(userId: Int): String {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(COLUMN_FULLNAME),
            "$COLUMN_USERID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        var userName = "Unknown User"
        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME))
        }
        cursor.close()
        return userName
    }

    //Get all the post from the database in list in recent post order
    fun getAllPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_POST,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_POSTEDATE DESC" // Order by posted date, most recent first
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_POSTID))
                val userId = getInt(getColumnIndexOrThrow(COLUMN_USERID))
                val caption = getString(getColumnIndexOrThrow(COLUMN_CAPTION))
                val imageUri = getString(getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                val postedDate = getString(getColumnIndexOrThrow(COLUMN_POSTEDATE))

                Log.d("DatabaseHelper", "Image URI for post $id: $imageUri")
                posts.add(Post(id, userId, caption, imageUri, postedDate))
            }
        }
        cursor.close()
        return posts
    }
    //Get post related to given user
    fun getPostsByUserId(userId: Int): List<Post> {
        val posts = mutableListOf<Post>()
        val db = this.readableDatabase
        val selection = "$COLUMN_USERID = ?"
        val selectionArgs = arrayOf(userId.toString())

        val cursor = db.query(
            TABLE_POST,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_POSTEDATE DESC" // Order by posted date, most recent first
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_POSTID))
                val caption = getString(getColumnIndexOrThrow(COLUMN_CAPTION))
                val imageUri = getString(getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                val postedDate = getString(getColumnIndexOrThrow(COLUMN_POSTEDATE))

                Log.d("DatabaseHelper", "Image URI for post $id: $imageUri")
                posts.add(Post(id, userId, caption, imageUri, postedDate))
            }
        }
        cursor.close()
        return posts
    }


    // Add a new post
    fun addPost(caption: String, userId: Long, imageUri: String?): Long {
        val db = this.writableDatabase
        val currentTime = getCurrentDateTime()
        val values = ContentValues().apply {
            put(COLUMN_CAPTION, caption)
            put(COLUMN_POSTEDATE, currentTime)
            put(COLUMN_IMAGE_URI, imageUri)
            put(COLUMN_USERID, userId)
        }
        return db.insert(TABLE_POST, null, values)
    }

    // Like a post
    fun likePost(postId: Int, userId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LIKE_POST_ID, postId)
            put(COLUMN_LIKE_USER_ID, userId)
        }
        return db.insert(TABLE_LIKE_POST, null, values)
    }

    // Unlike a post
    fun unlikePost(postId: Int, userId: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_LIKE_POST, "$COLUMN_LIKE_POST_ID = ? AND $COLUMN_LIKE_USER_ID = ?", arrayOf(postId.toString(), userId.toString()))
    }

    // Check if a user has liked a post
    fun isPostLikedByUser(postId: Int, userId: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_LIKE_POST,
            null,
            "$COLUMN_LIKE_POST_ID = ? AND $COLUMN_LIKE_USER_ID = ?",
            arrayOf(postId.toString(), userId.toString()),
            null,
            null,
            null
        )
        val isLiked = cursor.count > 0
        cursor.close()
        return isLiked
    }

    // Get like count for a post
    fun getLikeCountForPost(postId: Int): Int {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_LIKE_POST,
            arrayOf("COUNT(*)"),
            "$COLUMN_LIKE_POST_ID = ?",
            arrayOf(postId.toString()),
            null,
            null,
            null
        )
        var likeCount = 0
        if (cursor.moveToFirst()) {
            likeCount = cursor.getInt(0)
        }
        cursor.close()
        return likeCount
    }

    // Add a comment to a post
    fun addComment(postId: Long, userId: Long, commentText: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_COMMENT_POST_ID, postId)
            put(COLUMN_COMMENT_USER_ID, userId)
            put(COLUMN_COMMENT_TEXT, commentText)
        }
        return db.insert(TABLE_COMMENT_POST, null, values)
    }

    // Get comments for a post
    fun getAllCommentsForPost(postId: Long): List<Comment> {
        val comments = mutableListOf<Comment>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_COMMENT_POST,
            null,
            "$COLUMN_COMMENT_POST_ID = ?",
            arrayOf(postId.toString()),
            null,
            null,
            null // No ordering since there’s no date column
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_COMMENT_ID))
                val userId = getLong(getColumnIndexOrThrow(COLUMN_COMMENT_USER_ID))
                val text = getString(getColumnIndexOrThrow(COLUMN_COMMENT_TEXT))

                comments.add(Comment(id, postId, userId, text))
            }
        }
        cursor.close()
        return comments
    }
    //Get total comment number for a post
    fun getCommentCountForPost(postId: Int): Int {
        val db = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_COMMENT_POST WHERE $COLUMN_COMMENT_POST_ID = ?"
        val cursor = db.rawQuery(countQuery, arrayOf(postId.toString()))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0) // The count will be in the first column of the result
        }
        cursor.close()

        return count
    }
    // Get total post of a user
    fun getPostCountByUserId(userId: Int): Int {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_POST WHERE $COLUMN_USERID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        return count
    }

    // Function to login for admin
    fun authenticateAdmin(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_ADMIN WHERE $COLUMN_ADMIN_EMAIL = ? AND $COLUMN_ADMIN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        val isAuthenticated = cursor.count > 0
        cursor.close()

        return isAuthenticated
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(COLUMN_USERID, COLUMN_FULLNAME, COLUMN_EMAIL, COLUMN_DOB, COLUMN_DATECREATED, COLUMN_DATEUPDATED, COLUMN_PASSWORD),
            null, // No selection criteria, so it will get all rows
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_USERID))
                val fullName = getString(getColumnIndexOrThrow(COLUMN_FULLNAME))
                val email = getString(getColumnIndexOrThrow(COLUMN_EMAIL))
                val dob = getString(getColumnIndexOrThrow(COLUMN_DOB))
                val dateCreated = getString(getColumnIndexOrThrow(COLUMN_DATECREATED))
                val dateUpdated = getString(getColumnIndexOrThrow(COLUMN_DATEUPDATED))
                val password = getString(getColumnIndexOrThrow(COLUMN_PASSWORD))

                val user = User(id.toInt(), fullName, email, dob, dateCreated, dateUpdated, password)
                users.add(user)
            }
        }
        cursor.close()
        return users
    }


    // Update a user's details
    fun updateUser(
        userId: Long,
        fullName: String,
        dateOfBirth: String,
        email: String,
        password: String
    ): Int {
        val currentDate = getCurrentDate()
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_FULLNAME, fullName)
            put(COLUMN_DOB, dateOfBirth)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_DATEUPDATED, currentDate)
        }
        return db.update(
            TABLE_USER,
            contentValues,
            "$COLUMN_USERID = ?",
            arrayOf(userId.toString())
        )
    }
    // Delete a user
    fun deleteUser(userId: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_USER, "$COLUMN_USERID = ?", arrayOf(userId.toString()))
    }

    fun getUserById(userId: Long): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USER,
            arrayOf(
                COLUMN_USERID,
                COLUMN_FULLNAME,
                COLUMN_EMAIL,
                COLUMN_DOB,
                COLUMN_DATECREATED,
                COLUMN_DATEUPDATED,
                COLUMN_PASSWORD
            ),
            "$COLUMN_USERID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USERID)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                dob = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB)),
                dateCreated = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATECREATED)),
                dateUpdated = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATEUPDATED)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
        }
        cursor.close()
        return user
    }
    //get post detail of specific post
    fun getPostById(postId: Int): Post? {
        val db = this.readableDatabase
        var post: Post? = null

        val query = "SELECT * FROM $TABLE_POST WHERE $COLUMN_POSTID = ?"
        val cursor = db.rawQuery(query, arrayOf(postId.toString()))

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POSTID))
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USERID))
            val caption = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAPTION))
            val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
            val postedDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTEDATE))

            post = Post(id, userId, caption, imageUri, postedDate)
        }

        cursor.close()
        return post
    }

    fun updatePost(postId: Int, newCaption: String?, newImageUri: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_CAPTION, newCaption)
            put(COLUMN_IMAGE_URI, newImageUri)
        }

        val whereClause = "$COLUMN_POSTID = ?"
        val whereArgs = arrayOf(postId.toString())

        val rowsAffected = db.update(TABLE_POST, contentValues, whereClause, whereArgs)

        db.close()
        return rowsAffected > 0
    }

    // Delete a post
    fun deletePost(userId: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_POST, "$COLUMN_USERID = ?", arrayOf(userId.toString()))
    }
}
