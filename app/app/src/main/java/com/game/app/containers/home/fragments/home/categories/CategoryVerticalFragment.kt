package com.game.app.containers.home.fragments.home.categories

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
import com.game.app.data.courses.CourseCategoryPreferences
import com.game.app.databinding.FragmentCategoryVerticalBinding
import com.game.app.models.category.CategoryItemModel
import com.game.app.models.content.ContentDataModel
import com.game.app.models.course.CourseDataModel
import com.game.app.models.error.ErrorModel
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.handleApiError
import com.game.app.utils.handleMessage
import com.game.app.utils.recycler.setAdapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch


class CategoryVerticalFragment(
    private val category: CategoryItemModel
) : BaseFragment<CourseViewModel, FragmentCategoryVerticalBinding, UserRepository>() {
    private lateinit var categoryHighAdapter: CategoryHighAdapterV
    private lateinit var coursePreferences: CourseCategoryPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация внутреннего хранилища
        coursePreferences = CourseCategoryPreferences(requireContext(), categoryId = category.id.toString())

        categoryHighAdapter = CategoryHighAdapterV(requireContext(), categories = arrayListOf())

        // Добавление Item Decoration к RecyclerView
        binding.recyclerView.setAdapter(
            requireContext(),
            categoryHighAdapter,
            RecyclerView.VERTICAL
        )

        // При обновлении курсов в локальном хранилище осуществляется обновление их вывода на экран
        coursePreferences.courses.asLiveData().observe(requireActivity()) {
            if (it != null) {
                val courseData = Gson().fromJson(
                    it,
                    CourseDataModel::class.java
                )

                if ((courseData != null) && (courseData.courses.isNotEmpty())) {
                    categoryHighAdapter.setCategories(
                        courseData.courses.map { it ->

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

        // Определение поведения в случае обновления списка курсов (полная перезапись всех курсов
        // в данной категории)
        viewModel.courses.observe(viewLifecycleOwner) {
            // binding.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    if (it.value.isSuccessful) {
                        lifecycleScope.launch {
                            coursePreferences.saveCourses(
                                Gson().toJson(
                                    JsonParser.parseString(
                                        it.value.body()?.string()
                                    )
                                )
                            )
                        }
                    } else {
                        handleMessage(
                            Gson().fromJson(
                                it.value.errorBody()?.string().toString(),
                                ErrorModel::class.java
                            ).message!!
                        )
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it) {
                        viewModel.getCoursesByTitle(CategoryTitleConstants.CATEGORY_MONEY_TITLE)
                    }
                }
                else -> {}
            }
        }

        // Получение всех курсов по определённой категории (генерация запроса на сервер)
        viewModel.getCoursesByTitle(category.title.toString())
    }

    override fun getViewModel() = CourseViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCategoryVerticalBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(
            remoteDataSource.buildApi(
                UserApi::class.java,
                userPreferences,
                false
            )
        )
    }
}