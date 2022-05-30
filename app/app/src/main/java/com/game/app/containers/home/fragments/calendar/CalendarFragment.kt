package com.game.app.containers.home.fragments.calendar

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.game.app.constants.data.value.CategoryTitleConstants
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.adapters.CalendarAdapter
import com.game.app.containers.home.adapters.CategoryHighAdapterCalendar
import com.game.app.containers.home.adapters.CategoryHighAdapterV
import com.game.app.containers.home.models.CalendarViewModel
import com.game.app.data.CategoryPreferences
import com.game.app.data.courses.*
import com.game.app.databinding.FragmentCalendarBinding
import com.game.app.models.calendar.CalendarItemDataModel
import com.game.app.models.content.ContentDataModel
import com.game.app.models.course.CourseDataModel
import com.game.app.models.error.ErrorModel
import com.game.app.models.user.UserDataModel
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.date.getMonthOfYear
import com.game.app.utils.date.getWeekOfDay
import com.game.app.utils.getImageByCourse
import com.game.app.utils.handleApiError
import com.game.app.utils.handleMessage
import com.game.app.utils.recycler.RecyclerItemClickListener
import com.game.app.utils.recycler.setAdapter
import com.game.app.utils.visible
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class CalendarFragment : BaseFragment<CalendarViewModel, FragmentCalendarBinding, UserRepository>(){
    private lateinit var adapter: CalendarAdapter
    private lateinit var adapterCategoryHigh: CategoryHighAdapterCalendar
    private var adapterList: ArrayList<CalendarItemDataModel> = arrayListOf()

    // References to DataStores
    private lateinit var categoryPreferences: CategoryPreferences
    private lateinit var categoryChangePreferences: CourseChangePreferences
    private lateinit var categoryChildPreferences: CourseChildPreferences
    private lateinit var categoryDialogPreferences: CourseDialogPreferences
    private lateinit var categoryMoneyPreferences: CourseMoneyPreferences
    private lateinit var categoryResourcePreferences: CourseResourcePreferences
    private lateinit var categoryTimePreferences: CourseTimePreferences

    // Init HashMap for fast access to define Data Store
    //private var mapPreferences: HashMap<String, Any> = mapOf<String, Any>() as HashMap<String, Any>

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This code is bad, because he is too big and no optimization
        categoryPreferences = CategoryPreferences(requireContext())
        categoryChangePreferences = CourseChangePreferences(requireContext())
        categoryChildPreferences = CourseChildPreferences(requireContext())
        categoryDialogPreferences = CourseDialogPreferences(requireContext())
        categoryMoneyPreferences = CourseMoneyPreferences(requireContext())
        categoryResourcePreferences = CourseResourcePreferences(requireContext())
        categoryTimePreferences = CourseTimePreferences(requireContext())

        /*mapPreferences["Ресурс"] = categoryResourcePreferences
        mapPreferences["Детское"] = categoryChildPreferences
        mapPreferences["Внутренний диалог"] = categoryDialogPreferences
        mapPreferences["Деньги"] = categoryMoneyPreferences
        mapPreferences["По времени суток"] = categoryTimePreferences
        mapPreferences["Изменение реальности"] = categoryChangePreferences*/

        val dateNow = LocalDate.now()
        val date = LocalDate.of(dateNow.year, dateNow.month, 1)

        binding.progressBarCalendar.visible(false)
        binding.textViewMonthCalendar.text = getMonthOfYear(date) + " ${date.year}"

        adapterList = arrayListOf()
        adapterCategoryHigh = CategoryHighAdapterCalendar(requireContext(), categories = arrayListOf())

        binding.recyclerViewCourseCalendar.setAdapter(
            requireContext(),
            adapterCategoryHigh,
            RecyclerView.VERTICAL,
            false
        )

        val dayOfMonthLength = date.lengthOfMonth()
        for(i in 1..dayOfMonthLength){
            adapterList.add(
                CalendarItemDataModel(
                    number = i,
                    dayWeek = getWeekOfDay(date.plusDays(i.toLong() - 1)),
                    ""
                )
            )
        }

        viewModel.setPosition(0)

        adapter = CalendarAdapter(requireContext(), days = adapterList)
        binding.recyclerViewCalendar.setAdapter(
            requireContext(),
            adapter
        )

        binding.recyclerViewCalendar.addOnItemTouchListener(
            RecyclerItemClickListener(context,
                binding.recyclerViewCalendar,
                object: RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        viewModel.setPosition(position)
                        val day = adapterList[position].number

                        val currentDateArray = dateNow.format(DateTimeFormatter.ISO_DATE)
                            .toString()
                            .split("-") as MutableList<String>

                        currentDateArray[2] = if(day < 10) "0$day" else day.toString()

                        val currentDate = currentDateArray.joinToString {
                            return@joinToString "$it-"
                        }.dropLast(1).replace(", ", "")

                        viewModel.getCourses(currentDate)
                    }

                    override fun onLongItemClick(view: View?, position: Int) {}

                    override fun onItemClick(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                    }
                })
        )

        viewModel.currentPosition.observe(viewLifecycleOwner) {
            adapter.clearPosition()
            adapter.setCurrentPosition(it)
        }

        viewModel.currentCourses.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    when {
                        it.value.isSuccessful -> {
                            lifecycleScope.launch {
                                adapterCategoryHigh.setCategories(
                                    Gson().fromJson(
                                        Gson().toJson(
                                            JsonParser.parseString(
                                                it.value.body()?.string()
                                            )
                                        ),
                                        CourseDataModel::class.java
                                    ).courses.map{ it ->
                                        return@map ContentDataModel(
                                            id = it.id,
                                            categoryId = it.categoryId,
                                            subcategoryId = it.subcategoryId,
                                            title = it.title,
                                            count = it.sounds.size,
                                            subscription = it.subscribe,
                                            description = it.description,
                                            titleImgPath = it.titleImgPath
                                        )
                                    } as ArrayList<ContentDataModel>
                                )
                            }
                        }
                        it.value.code() == 401 -> {
                            toAuth("Для просмотра информации о профиле необходимо авторизоваться")
                        }
                        else -> {
                            handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                        }
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it) {
                        viewModel.getCourses(dateNow.format(DateTimeFormatter.ISO_DATE).toString())
                    }
                }
                else -> {}
            }
        }

        /* viewModel.user.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    updateUI(it.value)
                    binding.progressBarHome.visible(false)
                }
                is Resource.Loading -> {
                    binding.progressBarHome.visible(true)
                }
                else -> {}
            }
        })*/

        /*binding.buttonLogoutHome.setOnClickListener {
            logout()
        }*/

        val gameList: ArrayList<ContentDataModel> = arrayListOf()
        gameList.add(
            ContentDataModel(
                title = "Title1",
                count = 12,
                subscription = false
            )
        )

        gameList.add(
            ContentDataModel(
                title = "Title2",
                count = 12,
                subscription = false
            )
        )

        gameList.add(
            ContentDataModel(
                title = "Title3",
                count = 12,
                subscription = false
            )
        )

        gameList.add(
            ContentDataModel(
                title = "Title4",
                count = 12,
                subscription = false
            )
        )


    }

    private fun updateUI(it: Response<ResponseBody>) {
        /*with(binding){
            var gson: Gson = Gson()
            var data = gson.fromJson(gson.toJson(JsonParser.parseString(it.body()?.string())), UserInfoTestModel::class.java)
            textViewHome.text = data.data
        }*/
    }

    override fun getViewModel() = CalendarViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCalendarBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences, false))
    }
}