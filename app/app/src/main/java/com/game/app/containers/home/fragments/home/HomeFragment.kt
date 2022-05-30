package com.game.app.containers.home.fragments.home

import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.adapters.CategoryAdapter
import com.game.app.containers.home.adapters.FragmentDynamicAdapter
import com.game.app.containers.home.fragments.home.categories.*
import com.game.app.containers.home.models.HomeViewModel
import com.game.app.data.CategoryPreferences
import com.game.app.data.courses.CourseCategoryPreferences
import com.game.app.databinding.FragmentHomeBinding
import com.game.app.models.category.CategoryModel
import com.game.app.models.content.ContentDataModel
import com.game.app.models.course.CourseDataModel
import com.game.app.models.error.ErrorModel
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.*
import com.game.app.utils.recycler.setAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding, UserRepository>(){
    private lateinit var categoryPreferences: CategoryPreferences
    private lateinit var adapter: FragmentDynamicAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var coursePreferences: CourseCategoryPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация внутреннего хранилища
        categoryPreferences = CategoryPreferences(requireContext())

        // For default home category has id 0
        coursePreferences = CourseCategoryPreferences(requireContext(), categoryId = "0")

        categoryAdapter = CategoryAdapter(
            requireContext(),
            categories = arrayListOf()
        )

        binding.recyclerViewPopularHome.setAdapter(
            requireContext(),
            categoryAdapter
        )

        binding.progressBarHome.visible(false)

        val fm: FragmentManager = requireActivity().supportFragmentManager
        val pager2 = binding.viewPager2
        val tabLayout = binding.tabLayout

        viewModel.categories.observe(viewLifecycleOwner) {
            binding.progressBarHome.visible(it is Resource.Loading)
            hideKeyboard()

            when(it){
                is Resource.Success -> {
                    if(it.value.isSuccessful){
                        lifecycleScope.launch {
                            // Сохранение информации о категориях
                            categoryPreferences.saveCategories(
                                Gson().toJson(
                                    JsonParser.parseString(
                                        it.value.body()?.string()
                                    )
                                )
                            )
                        }
                    }else{
                        //handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it) {
                        viewModel.getAllCategory()
                    }
                }
                else -> {}
            }
        }

        // При обновлении курсов в локальном хранилище осуществляется обновление их вывода на экран
        coursePreferences.courses.asLiveData().observe(requireActivity()){
            if(it != null){
                val courseData = Gson().fromJson(
                    it,
                    CourseDataModel::class.java
                )

                if((courseData != null) && (courseData.courses.isNotEmpty())){
                    categoryAdapter.setCategories(
                        courseData.courses.map{ it ->
                            return@map ContentDataModel(
                                id = it.id,
                                categoryId = it.categoryId,
                                subcategoryId = it.subcategoryId,
                                title = it.title,
                                count = it.sounds.size,
                                subscription = it.subscribe,
                                description = it.description,
                                titleImgPath = it.titleImgPath,
                                sounds = it.sounds,
                                type = it.type
                            )
                        } as ArrayList<ContentDataModel>
                    )
                }
            }
        }

        // Определение поведения в случае обновления списка курсов (полная перезапись всех курсов
        // в данной категории)
        viewModel.courses.observe(viewLifecycleOwner){
            // binding.visible(it is Resource.Loading)
            when(it){
                is Resource.Success -> {
                    if(it.value.isSuccessful){
                        lifecycleScope.launch {
                            coursePreferences.saveCourses(
                                Gson().toJson(
                                    JsonParser.parseString(
                                        it.value.body()?.string()
                                    )
                                )
                            )
                        }
                    }else{
                        handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it) {
                        val categoryData = Gson().fromJson(
                            runBlocking { categoryPreferences.categories.first() },
                            CategoryModel::class.java
                        )

                        viewModel.getCoursesByTitle(categoryData.categories.first().title.toString())
                    }
                }
                else -> {}
            }
        }

        // Определение поведения в случае загрузки изображения курса
        /*viewModel.courseImage.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    if(it.value.isSuccessful){
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO){
                                val data = runBlocking {
                                    coursePreferences.coursesImg.first()
                                }

                                if(data == null){
                                    // Запись файла на локальное хранилище
                                    val filePath = writeResponseBodyToStorage(it.value.body()!!,
                                        it.value.headers()["filename"].toString()
                                    ) ?: return@withContext

                                    // В случае, если до этого момента не было загружено ни одного
                                    // изображения курса, то загрузить первое изображение
                                    coursePreferences.saveCoursesImg(
                                        Gson().toJson(
                                            CourseImgDataModel(
                                                titleImages = arrayListOf(
                                                    CourseImgItemModel(
                                                        coursesId = it.value.headers()["courses_id"].toString().toInt(),
                                                        filename = it.value.headers()["filename"].toString(),
                                                        filePath = filePath,
                                                    )
                                                )
                                            )
                                        )
                                    )
                                }else{
                                    val dataCourseImg = Gson().fromJson(data, CourseImgDataModel::class.java)
                                    if(!existsElementById(
                                            it.value.headers()["courses_id"].toString().toInt(),
                                            dataCourseImg
                                        )
                                    ){
                                        val filePath = writeResponseBodyToStorage(it.value.body()!!,
                                            it.value.headers()["filename"].toString()
                                        ) ?: return@withContext

                                        dataCourseImg.titleImages.add(
                                            CourseImgItemModel(
                                                coursesId = it.value.headers()["courses_id"].toString().toInt(),
                                                filename = it.value.headers()["filename"].toString(),
                                                filePath = filePath,
                                            )
                                        )

                                        coursePreferences.saveCoursesImg(
                                            Gson().toJson(dataCourseImg)
                                        )
                                    }
                                }
                            }
                        }
                    }else{
                        handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it)
                }
                else -> {}
            }
        }*/

        categoryPreferences.categories.asLiveData().observe(requireActivity()){
            if(it != null){
                val categoryData = Gson().fromJson(
                    it,
                    CategoryModel::class.java
                )

                viewModel.getCoursesByTitle(categoryData.categories.first().title.toString())

                val fragments = ArrayMap<Int, BaseFragment<*, *, *>>()
                tabLayout.removeAllTabs()

                for(i in 0 until categoryData.categories.size){
                    tabLayout.addTab(tabLayout.newTab().setText(categoryData.categories[i].title), i)
                    //fragments.add(CategoryVerticalFragment(it))

                    if(categoryData.categories[i].subCategories.size > 0){
                        // add linear layout
                        fragments[i] = CategoryLinearFragment(categoryData.categories[i])
                    }else{
                        // add nested scroll view with recycler view
                        fragments[i] = CategoryVerticalFragment(categoryData.categories[i])
                    }
                }

                adapter = FragmentDynamicAdapter(fm, lifecycle, fragments)

                pager2.adapter = adapter
                pager2.isUserInputEnabled = false

                // tabLayout.setupWithViewPager(pager2)

                tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        pager2.setCurrentItem(tab.position, true)
                        adapter.notifyItemChanged(tab.position)
                        //pager2.currentItem = tab.position

                        /*requireActivity().supportFragmentManager.beginTransaction()
                            .replace(binding.viewPager2.id, CategoryChangeFragment()).commit()*/
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {}
                    override fun onTabReselected(tab: TabLayout.Tab) {}
                })

                pager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        tabLayout.selectTab(tabLayout.getTabAt(position))
                        super.onPageSelected(position)
                    }
                })
            }
        }

        // Get information
        viewModel.getAllCategory()
    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences, false))
    }
}