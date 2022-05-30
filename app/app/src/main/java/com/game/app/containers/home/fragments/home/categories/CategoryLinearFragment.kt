package com.game.app.containers.home.fragments.home.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.game.app.R
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.adapters.CategoryHighAdapter
import com.game.app.containers.home.fragments.home.models.CourseViewModel
import com.game.app.data.courses.CourseCategoryPreferences
import com.game.app.databinding.FragmentCategoryLinearBinding
import com.game.app.models.category.CategoryItemModel
import com.game.app.models.content.ContentDataModel
import com.game.app.models.course.CourseDataModel
import com.game.app.models.course.CourseItemModel
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

class CategoryLinearFragment(
    private val category: CategoryItemModel
) : BaseFragment<CourseViewModel, FragmentCategoryLinearBinding, UserRepository>() {

    private var scaleFactor: Int = 3
    private lateinit var coursePreferences: CourseCategoryPreferences
    private var adapters: ArrayList<CategoryHighAdapter> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация внутреннего хранилища
        coursePreferences = CourseCategoryPreferences(requireContext(), categoryId = category.id.toString())

        /*updateViewContent(runBlocking {
            coursePreferences.courses.first()
        })*/

        updateView()

        coursePreferences.courses.asLiveData().observe(requireActivity()){
            updateViewContent(it)
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
                        viewModel.getCoursesByTitle(category.title.toString())
                    }
                }
                else -> {}
            }
        }

        viewModel.getCoursesByTitle(category.title.toString())
    }

    override fun getViewModel() = CourseViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCategoryLinearBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        return UserRepository(
            remoteDataSource.buildApi(
                UserApi::class.java,
                userPreferences,
                false
            )
        )
    }

    private fun getDefineContentData(item: CourseItemModel): ContentDataModel{
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

    private fun updateViewContent(value: String?){
        if(value != null){
            val courseData = Gson().fromJson(
                value,
                CourseDataModel::class.java
            )

            if((courseData != null) && (courseData.courses.isNotEmpty())){
                val dataAdapters = arrayListOf<ArrayList<ContentDataModel>>()

                for(i in 0 until category.subCategories.size){
                    dataAdapters.add(
                        courseData.courses.filter { it ->
                            val subcategoryId = it.subcategoryId
                            category.subCategories[i].id == subcategoryId
                        }.map { it ->
                            return@map getDefineContentData(
                                it
                            )
                        } as ArrayList<ContentDataModel>
                    )
                }

                for(i in 0 until dataAdapters.size){
                    adapters[i].setCategories(dataAdapters[i])
                    /*recyclerViews[i].setAdapter(
                        requireContext(),
                        adapter = CategoryHighAdapter(requireContext(), categories = dataAdapters[i])
                    )*/
                    //recyclerViews[i].invalidate()
                }
            }
        }
    }

    private fun updateView(){
        adapters.clear()
        binding.linearLayout.removeAllViews()

        category.subCategories.forEach{
            val textView = TextView(requireContext())
            textView.text = it.title
            textView.setTextAppearance(R.style.TitleTextView)
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(16 * scaleFactor, 16 * scaleFactor, 0, 0)

            binding.linearLayout.addView(textView, layoutParams)

            adapters.add(CategoryHighAdapter(requireContext(), categories = arrayListOf()))

            val recyclerView = RecyclerView(requireContext())
            recyclerView.setAdapter(
                requireContext(),
                adapters.last()
            )

            val layoutParams2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams2.setMargins(16, 16, 0, 0)

            binding.linearLayout.addView(recyclerView, layoutParams2)
        }
    }
}