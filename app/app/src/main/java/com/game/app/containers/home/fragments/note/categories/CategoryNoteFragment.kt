package com.game.app.containers.home.fragments.note.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.adapters.CategoryHighAdapterV
import com.game.app.containers.home.fragments.home.models.CourseViewModel
import com.game.app.data.CategoryPreferences
import com.game.app.data.content.NoteCoursePreferences
import com.game.app.data.courses.CourseCategoryPreferences
import com.game.app.databinding.FragmentCategoryNoteBinding
import com.game.app.models.category.CategoryItemModel
import com.game.app.models.category.CategoryModel
import com.game.app.models.content.ContentDataModel
import com.game.app.models.course.CourseDataModel
import com.game.app.models.course.CourseItemModel
import com.game.app.models.course.CourseNoteDataModel
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.recycler.setAdapter
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CategoryNoteFragment(
    private val category: CategoryItemModel
) : BaseFragment<CourseViewModel, FragmentCategoryNoteBinding, UserRepository>() {
    private lateinit var categoryHighAdapter: CategoryHighAdapterV

    // Course for category preferences
    private lateinit var coursePreferences: CourseCategoryPreferences

    // Note preferences
    private lateinit var noteCoursePreferences: NoteCoursePreferences

    // Global home preferences
    private lateinit var courseHomePreferences: CourseCategoryPreferences

    // Category preferences
    private lateinit var categoryPreferences: CategoryPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация внутреннего хранилища
        coursePreferences = CourseCategoryPreferences(requireContext(), categoryId = category.id.toString())
        courseHomePreferences = CourseCategoryPreferences(requireContext(), categoryId = "0")
        categoryPreferences = CategoryPreferences(requireContext())
        noteCoursePreferences = NoteCoursePreferences(requireContext())
        categoryHighAdapter = CategoryHighAdapterV(requireContext(), categories = arrayListOf())

        binding.recyclerView.setAdapter(
            requireContext(),
            categoryHighAdapter,
            RecyclerView.VERTICAL
        )

        noteCoursePreferences.courses.asLiveData().observe(viewLifecycleOwner){
            if(it != null){
                val data = Gson().fromJson(
                    it,
                    CourseNoteDataModel::class.java
                )

                if((data == null) || (data.coursesNotes!!.isEmpty())){
                    return@observe
                }

                val coursesForCategory = runBlocking {
                    coursePreferences.courses.first()
                }

                val coursesHome = runBlocking {
                    courseHomePreferences.courses.first()
                }

                val coursesForCategoryData = Gson().fromJson(
                    coursesForCategory,
                    CourseDataModel::class.java
                )

                val coursesHomeData = Gson().fromJson(
                    coursesHome,
                    CourseDataModel::class.java
                )

                val courses = arrayListOf<CourseItemModel?>()

                if((coursesForCategoryData != null) && coursesForCategoryData.courses.isNotEmpty()){
                    courses.addAll(coursesForCategoryData.courses)
                }

                if((coursesHomeData != null) && coursesHomeData.courses.isNotEmpty()){
                    courses.addAll(coursesHomeData.courses)
                }

                if(courses.isNotEmpty()){
                    val categories = Gson().fromJson(
                        runBlocking {
                            categoryPreferences.categories.first()
                        },
                        CategoryModel::class.java
                    )

                    val coursesAdapter = data.coursesNotes!!.map{ it ->
                        val findObj = it
                        val index = courses.indexOfFirst { it ->
                            (it!!.id == findObj.coursesId) && (it.categoryId == category.id)
                        }

                        if(index >= 0){
                            val course = courses[index]

                            return@map ContentDataModel(
                                id = course!!.id,
                                categoryId = course.categoryId,
                                subcategoryId = course.subcategoryId,
                                title = course.title,
                                count = course.sounds.size,
                                subscription = course.subscribe,
                                description = course.description,
                                titleImgPath = course.titleImgPath,
                                type = course.type
                            )
                        }else{
                            null
                        }
                    } as ArrayList<ContentDataModel?>

                    coursesAdapter.removeIf { it ->
                        it == null
                    }

                    categoryHighAdapter.setCategories(
                        coursesAdapter.map { it ->
                            return@map ContentDataModel(
                                id = it!!.id,
                                categoryId = it.categoryId,
                                subcategoryId = it.subcategoryId,
                                title = it.title,
                                count = it.count,
                                subscription = it.subscription,
                                description = it.description,
                                titleImgPath = it.titleImgPath,
                                type = it.type
                            )
                        } as ArrayList<ContentDataModel>
                    )
                }else{
                    categoryHighAdapter.setCategories(arrayListOf())
                }
            }
        }
    }

    override fun getViewModel() = CourseViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCategoryNoteBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences))
    }
}