package com.game.app.containers.home

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.game.app.R
import com.game.app.containers.home.fragments.calendar.CalendarFragment
import com.game.app.containers.home.fragments.home.HomeFragment
import com.game.app.containers.home.fragments.note.NoteFragment
import com.game.app.containers.home.fragments.profile.ProfileContainerFragment
import com.game.app.data.CoursePreferences
import com.game.app.databinding.ActivityHomeBinding
import com.game.app.utils.handleErrorMessage
import com.game.app.utils.handleMessage
import com.game.app.utils.handleWarningMessage
import com.game.app.utils.visible
import kotlinx.coroutines.runBlocking
import java.util.*


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var idDeque: ArrayDeque<Int> = ArrayDeque()
    private var flag: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val coursePreferences = CoursePreferences(this)
        runBlocking {
            coursePreferences.clear()
        }

        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.getStringExtra("message") != null){
            if(intent.getStringExtra("type") != null){
                when(intent.getStringExtra("type").toString()){
                    "error" -> {
                        handleErrorMessage(binding.root, intent.getStringExtra("message").toString())
                    }
                    "warning" -> {
                        handleWarningMessage(binding.root, intent.getStringExtra("message").toString())
                    }
                    else -> {
                        handleMessage(binding.root, intent.getStringExtra("message").toString())
                    }
                }
            }else{
                handleMessage(binding.root, intent.getStringExtra("message").toString())
            }
        }

        // Инициализация BottomNavigationView
        setupBottomNavigationView()

        // Имитация загрузки страницы
        binding.fcvActivityHome.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            binding.pbActivityHome.visible(
                false
            )
        }

        idDeque.add(R.id.itemBrowserMenu)

        // Запрашивание разрешения для работы с файловым хранилищем
        verifyStoragePermissions()
    }

    override fun onBackPressed() {
        idDeque.pop()

        if(!idDeque.isEmpty()){
            loadFragment(getFragment(idDeque.peek()!!))
        }else{
            finish()
        }
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fcvActivityHome.id, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    private fun getFragment(menuId: Int): Fragment{
        binding.pbActivityHome.visible(true)
        binding.bnvActivityHome.menu.findItem(menuId).isChecked = true

        when(menuId){
            R.id.itemBrowserMenu -> {
                return HomeFragment()
            }

            R.id.itemProfileMenu -> {
                return ProfileContainerFragment()
            }

            R.id.itemNoteMenu -> {
                return NoteFragment()
            }

            R.id.itemCalendarMenu -> {
                return CalendarFragment()
            }
        }

        return HomeFragment()
    }

    private fun setupBottomNavigationView(){
        binding.bnvActivityHome.setOnItemSelectedListener {
            if(idDeque.contains(it.itemId)){
                if(it.itemId == R.id.itemBrowserMenu){
                    if(idDeque.size != 1){
                        if(flag){
                            idDeque.addFirst(R.id.itemBrowserMenu)
                            flag = false
                        }
                    }
                }

                // Чтобы сохранять всю историю переходов нужно закомментировать данную строку
                idDeque.remove(it.itemId)
            }

            idDeque.push(it.itemId)
            loadFragment(getFragment(it.itemId))

            false
        }
    }

    private fun verifyStoragePermissions(){
        val permission = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                REQUEST_EXTERNAL_STORAGE_CODE
            )
        }
    }

    companion object{
        const val REQUEST_EXTERNAL_STORAGE_CODE = 1
    }
}