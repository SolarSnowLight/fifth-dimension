package com.game.app.containers.home.fragments.home.categories.deprecated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.game.app.constants.data.value.CategoryTitleConstants
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.adapters.CategoryHighAdapter
import com.game.app.containers.home.models.HomeViewModel
import com.game.app.data.courses.CourseTimePreferences
import com.game.app.models.content.ContentDataModel
import com.game.app.models.course.CourseDataModel
import com.game.app.models.course.CourseImgDataModel
import com.game.app.models.course.CourseItemModel
import com.game.app.models.error.ErrorModel
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.*
import com.game.app.utils.recycler.setAdapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

class CategoryTimeFragment : BaseFragment<HomeViewModel, com.game.app.databinding.FragmentCategoryTimeBinding, UserRepository>(){
    private lateinit var morningAdapter: CategoryHighAdapter
    private lateinit var eveningAdapter: CategoryHighAdapter
    private lateinit var coursePreferences: CourseTimePreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coursePreferences = CourseTimePreferences(requireContext())
        morningAdapter = CategoryHighAdapter(requireContext(), categories = arrayListOf())
        eveningAdapter = CategoryHighAdapter(requireContext(), categories = arrayListOf())

        binding.recyclerViewMorningTime.setAdapter(
            requireContext(),
            morningAdapter
        )

        binding.recyclerViewEveningTime.setAdapter(
            requireContext(),
            eveningAdapter
        )

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
                    var dataAdapters = arrayListOf<ArrayList<ContentDataModel>>()
                    for(i in 5..7){
                        dataAdapters.add(
                            courseData.courses.filter { it ->
                                it.subcategoryId == i
                            }.map { it ->
                                return@map getDefineContentData(
                                    it
                                )
                            } as ArrayList<ContentDataModel>
                        )
                    }

                    morningAdapter.setCategories(
                        dataAdapters[0]
                    )

                    eveningAdapter.setCategories(
                        dataAdapters[1]
                    )
                }
            }
        }

        viewModel.courses.observe(viewLifecycleOwner){
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
                        viewModel.getCoursesByTitle(CategoryTitleConstants.CATEGORY_TIME_TITLE)
                    }
                }
                else -> {}
            }
        }

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

        viewModel.getCoursesByTitle(CategoryTitleConstants.CATEGORY_TIME_TITLE)
    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = com.game.app.databinding.FragmentCategoryTimeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences, false))
    }

    private fun getDefineContentData(item: CourseItemModel, courseImages: CourseImgDataModel? = null): ContentDataModel{
        /*val images = getImageByCourse(item.id, courseImages)

        if(images == null){
            viewModel.getCourseTitleImage(item.id)
        }*/

        return ContentDataModel(
            id = item.id,
            categoryId = item.categoryId,
            subcategoryId = item.subcategoryId,
            title = item.title,
            count = item.sounds.size,
            subscription = item.subscribe,
            description = item.description,
            titleImgPath = item.titleImgPath,
            type = item.type
        )
    }
}