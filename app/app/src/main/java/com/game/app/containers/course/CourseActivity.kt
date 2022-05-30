package com.game.app.containers.course

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.work.WorkManager
import com.game.app.R
import com.game.app.data.CoursePreferences
import com.game.app.data.UserPreferences
import com.game.app.data.content.NoteCoursePreferences
import com.game.app.databinding.ActivityCourseBinding
import com.game.app.models.content.ContentDataModel
import com.game.app.models.course.CourseNoteDataModel
import com.game.app.models.course.CourseNoteItemModel
import com.game.app.utils.checkAuth
import com.game.app.utils.handleMessage
import com.game.app.utils.makeStatusBarTransparent
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseBinding
    private lateinit var coursePreferences: CoursePreferences
    private lateinit var noteCoursePreferences: NoteCoursePreferences
    private lateinit var userPreferences: UserPreferences
    private var flagNoteIcon: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        coursePreferences = CoursePreferences(this)
        noteCoursePreferences = NoteCoursePreferences(this)
        userPreferences = UserPreferences(this)

        runBlocking {
            coursePreferences.saveCourse(intent.getStringExtra("data").toString())
        }

        super.onCreate(savedInstanceState)
        binding = ActivityCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeStatusBarTransparent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_course, menu)

        val course = runBlocking {
            coursePreferences.course.first()
        }

        if(course != null) {
            val data = Gson().fromJson(
                course,
                ContentDataModel::class.java
            )

            val notes = runBlocking {
                noteCoursePreferences.courses.first()
            }

            var dataNotes = Gson().fromJson(
                notes,
                CourseNoteDataModel::class.java
            )

            val index =
                if ((notes != null) && (dataNotes.coursesNotes != null)) dataNotes.coursesNotes!!.indexOfFirst {
                    it.coursesId == data.id
                } else (-1)

            if(index >= 0){
                menu!!.findItem(R.id.itemNoteMenuCourse).setIcon(R.drawable.ic_note)
            }
        }else{
            menu!!.findItem(R.id.itemNoteMenuCourse).setIcon(R.drawable.ic_note_off)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
            }

            R.id.itemNoteMenuCourse -> {
                if(!checkAuth(userPreferences, "Для сохранения или удаления курсов необходимо авторизоваться")){
                    return true
                }

                val course = runBlocking {
                    coursePreferences.course.first()
                }

                if(course != null){
                    val data = Gson().fromJson(
                        course,
                        ContentDataModel::class.java
                    )

                    val notes = runBlocking {
                        noteCoursePreferences.courses.first()
                    }

                    var dataNotes = Gson().fromJson(
                        notes,
                        CourseNoteDataModel::class.java
                    )

                    val index = if((notes != null) && (dataNotes.coursesNotes != null)) dataNotes.coursesNotes!!.indexOfFirst {
                        it.coursesId == data.id
                    } else (-1)


                    flagNoteIcon = if(index >= 0){
                        // Заметка с данным курсом уже существует, следовательно её нужно удалить
                        dataNotes.coursesNotes?.removeAt(index)
                        handleMessage(binding.root, "Курс удалён")
                        false
                    }else{
                        if((notes != null) && (dataNotes.coursesNotes != null)){
                            // Заметка с данным курсом не существует, следовательно её нужно добавить
                            dataNotes.coursesNotes?.add(
                                CourseNoteItemModel(
                                    coursesId = data.id!!,
                                    categoryId = data.categoryId!!,
                                    subcategoryId = data.subcategoryId,
                                    titleImgPath = data.filePath
                                )
                            )
                        }else{
                            dataNotes = CourseNoteDataModel(
                                coursesNotes = arrayListOf(
                                    CourseNoteItemModel(
                                        coursesId = data.id!!,
                                        categoryId = data.categoryId!!,
                                        subcategoryId = data.subcategoryId,
                                        titleImgPath = data.filePath
                                    )
                                )
                            )
                        }
                        handleMessage(binding.root, "Курс сохранён")
                        true
                    }

                    runBlocking {
                        noteCoursePreferences.saveCourses(Gson().toJson(dataNotes))
                    }

                    item.setIcon(if(flagNoteIcon) R.drawable.ic_note else R.drawable.ic_note_off)
                }
            }
        }

        return true
    }

    override fun onBackPressed() {
        when(binding.fragmentContainerView2.findNavController().currentDestination?.id){
            R.id.courseFragment -> {
                finish()
            }
            R.id.audioPlayerFragment -> {
                binding.fragmentContainerView2.findNavController().navigate(R.id.action_audioPlayerFragment_to_courseFragment)
            }
            else -> {
                finish()
            }
        }
    }

    override fun onDestroy() {
        runBlocking {
            coursePreferences.clear()
        }
        super.onDestroy()
    }
}