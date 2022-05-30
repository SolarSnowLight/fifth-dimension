package com.game.app.containers.home.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.game.app.constants.data.value.TargetSizeImageConstants
import com.game.app.constants.network.main.MainNetworkConstants
import com.game.app.containers.base.BaseAdapter
import com.game.app.containers.course.CourseActivity
import com.game.app.databinding.AdapterCategoryItemBinding
import com.game.app.models.content.ContentDataModel
import com.game.app.utils.startStdActivity
import com.game.app.utils.visible
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import eightbitlab.com.blurview.RenderScriptBlur
import java.lang.Exception

class CategoryAdapter(
    private val context: Context,
    private var categories: ArrayList<ContentDataModel>
) : BaseAdapter<ContentDataModel, AdapterCategoryItemBinding>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setCategories(categoriesInput: ArrayList<ContentDataModel>){
        categories.clear()
        categories = categoriesInput
        notifyDataSetChanged()
    }

    override fun getAdapterBinding(parent: ViewGroup, viewType: Int): BaseViewHolder<AdapterCategoryItemBinding> {
        val binding = AdapterCategoryItemBinding.inflate(LayoutInflater.from(context), parent, false)

        binding.blurView.setupWith(parent)
            .setFrameClearDrawable(parent.background)
            .setBlurAlgorithm(RenderScriptBlur(context))
            .setBlurRadius(5f)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(false)

        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder<AdapterCategoryItemBinding>, @SuppressLint(
        "RecyclerView"
    ) position: Int) {
        val category = categories[position]

        holder.binding.textViewFirstText.text = category.type
        holder.binding.textViewSecondText.text = category.title
        holder.binding.imageViewLockV.visible(category.subscription)

        val imageView = ImageView(context)
        Picasso.get()
            .load((MainNetworkConstants.SERVER_MAIN_ADDRESS + "//" + category.titleImgPath)
                .replace("\\", "/"))
            .resize(TargetSizeImageConstants.WIDTH_MIN, TargetSizeImageConstants.HEIGHT_MAX)
            .centerCrop()
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    holder.binding.progressBarCategory.visible(false)
                    holder.binding.constraintLayoutItemAdapter.background = imageView.drawable

                    holder.binding.cardView.setOnClickListener {
                        context.startStdActivity(
                            CourseActivity::class.java,
                            Gson().toJson(categories[position])
                        )
                    }
                }

                override fun onError(e: Exception?) {}
            })

        /*val conditionNotNull = (category != null) && (category.filePath != null)

        if(conditionNotNull){
            holder.binding.constraintLayoutItemAdapter.background = Drawable.createFromPath(categories[position].filePath)
            holder.binding.progressBarCategory.visible(false)

            holder.binding.cardView.setOnClickListener {
                context.startStdActivity(
                    CourseActivity::class.java,
                    Gson().toJson(categories[position])
                )
            }
        }*/
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}