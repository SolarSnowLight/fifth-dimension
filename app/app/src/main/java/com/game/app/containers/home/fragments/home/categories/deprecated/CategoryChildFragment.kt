package com.game.app.containers.home.fragments.home.categories.deprecated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.game.app.constants.data.value.CategoryTitleConstants
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.adapters.CategoryHighAdapterV
import com.game.app.containers.home.fragments.home.models.CourseViewModel
import com.game.app.data.courses.CourseChildPreferences
import com.game.app.databinding.FragmentCategoryChildBinding
import com.game.app.models.content.ContentDataModel
import com.game.app.models.course.CourseDataModel
import com.game.app.models.error.ErrorModel
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.*
import com.game.app.utils.recycler.setAdapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch


class CategoryChildFragment : BaseFragment<CourseViewModel, FragmentCategoryChildBinding, UserRepository>(){
    private lateinit var categoryHighAdapter: CategoryHighAdapterV
    private lateinit var coursePreferences: CourseChildPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация внутреннего хранилища
        coursePreferences = CourseChildPreferences(requireContext())

        categoryHighAdapter = CategoryHighAdapterV(requireContext(), categories = arrayListOf())

        // Добавление Item Decoration к RecyclerView
        // binding.recyclerViewRelax.addItemDecoration(ItemDecoration(800, 800))
        binding.recyclerViewRelax.setAdapter(
            requireContext(),
            categoryHighAdapter,
            RecyclerView.VERTICAL
        )

        // При обновлении курсов в локальном хранилище осуществляется обновление их вывода на экран
        coursePreferences.courses.asLiveData().observe(requireActivity()){
            if(it != null){
                val courseData = Gson().fromJson(
                    it,
                    CourseDataModel::class.java
                )

                /*val courseImages = runBlocking {
                    coursePreferences.coursesImg.first()
                }

                val courseImagesData = if(courseImages != null)
                    Gson().fromJson(
                        courseImages,
                        CourseImgDataModel::class.java
                    )
                else
                    null*/

                if((courseData != null) && (courseData.courses.isNotEmpty())){
                    categoryHighAdapter.setCategories(
                        courseData.courses.map{ it ->
                            /*val images = getImageByCourse(it.id, courseImagesData)

                            if(images == null){
                                viewModel.getCourseTitleImage(it.id)
                            }*/

                            return@map ContentDataModel(
                                id = it.id,
                                categoryId = it.categoryId,
                                subcategoryId = it.subcategoryId,
                                title = it.title,
                                count = it.sounds.size,
                                subscription = it.subscribe,
                                description = it.description,
                                titleImgPath = it.titleImgPath,
                                type = it.type
                            )
                        } as ArrayList<ContentDataModel>
                    )
                }
            }
        }

        // Получение всех курсов по определённой категории (генерация запроса на сервер)
        viewModel.getCoursesByTitle(CategoryTitleConstants.CATEGORY_CHILD_TITLE)

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
                        viewModel.getCoursesByTitle(CategoryTitleConstants.CATEGORY_CHILD_TITLE)
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
    }

    override fun getViewModel() = CourseViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCategoryChildBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences, false))
    }

}