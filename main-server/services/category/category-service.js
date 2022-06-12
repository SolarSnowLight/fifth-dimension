
const {
    CategoriesModel, SubcategoriesModel,
    CategoriesSubcategoriesModel, sequelize, 
    CoursesCategoriesModel, CoursesModel, CoursesSoundsModel, SoundsModel,
    DeletedCourses
} = require('../../data/index');
const ApiError = require('../../exceptions/api-error');
const fs = require("fs");

class CategoryService {
    findOneDataValues(key, value, data) {
        for (let i = 0; i < data.length; i++) {
            if (data[i].dataValues[key] === value) {
                return data[i].dataValues;
            }
        }

        return null;
    };

    findAnyDataValues(key, value, data) {
        const result = [];
        for (let i = 0; i < data.length; i++) {
            if (data[i].dataValues[key] === value) {
                result.push(data[i].dataValues);
            }
        }

        return result;
    };

    // Получение информации обо всех категориях с их подкатегориями
    async getAllCategories() {
        const categories = await CategoriesModel.findAll();
        const subCategories = await SubcategoriesModel.findAll();
        const categoriesSubCategories = await CategoriesSubcategoriesModel.findAll();

        const result = [];
        for (let i = 0; i < categories.length; i++) {
            const catSubCat = this.findAnyDataValues("categories_id", categories[i].dataValues.id, categoriesSubCategories);
            categories[i].dataValues.users_id = undefined;
            categories[i].dataValues.sub_categories = [];

            for (let j = 0; j < catSubCat.length; j++) {
                const data = this.findOneDataValues("id", catSubCat[j].subcategories_id, subCategories);
                data.users_id = undefined;

                categories[i].dataValues.sub_categories.push(data);
            }

            result.push(categories[i].dataValues);
        }

        result.sort((a, b) => {
            if (a.id > b.id) {
                return 1;
            } else if (a.id < b.id) {
                return -1;
            } else {
                return 0;
            }
        });

        /*const formatterFunction = (item) => {
            return {
                ...item.dataValues,
                users_id: undefined
            }
        };*/

        return result;
    }

    async deleteCourseByCategory(category, transaction) {
        const deleteFiles = [];

        const coursesCategories = await CoursesCategoriesModel.findAll({
            where: {
                categories_id: category.id
            }
        });

        await CoursesCategoriesModel.destroy({
            where: {
                categories_id: category.id
            }
        }, {
            transaction: transaction
        });

        for (let j = 0; j < coursesCategories.length; j++) {
            const course = await CoursesModel.findOne({
                where: {
                    id: coursesCategories[j].courses_id
                }
            });

            deleteFiles.push(course.title_img_path);

            const sounds = await CoursesSoundsModel.findAll({
                where: {
                    courses_id: course.id
                }
            });

            for (let k = 0; k < sounds.length; k++) {
                const sound = await SoundsModel.findOne({
                    where: {
                        id: sounds[k].sounds_id
                    }
                });

                deleteFiles.push(sound.title_img_path);
                deleteFiles.push(sound.file_sound_path);

                await SoundsModel.destroy({
                    where: {
                        id: sound.id
                    }
                }, {
                    transaction: transaction
                });
            }

            await CoursesSoundsModel.destroy({
                where: {
                    courses_id: course.id
                }
            }, {
                transaction: transaction
            });

            await CoursesModel.destroy({
                where: {
                    id: course.id
                }
            }, { transaction: transaction });
        }

        return deleteFiles;
    }

    async deleteCourseBySubcategory(subcategory, transaction) {
        const deleteFiles = [];

        const coursesCategories = await CoursesCategoriesModel.findAll({
            where: {
                categories_subcategories_id: subcategory.id
            }
        });

        await CategoriesSubcategoriesModel.destroy({
            where: {
                subcategories_id: subcategory.subcategories_id
            }
        }, {
            transaction: transaction
        });

        await CoursesCategoriesModel.destroy({
            where: {
                categories_subcategories_id: subcategory.id
            }
        }, {
            transaction: transaction
        });

        for (let j = 0; j < coursesCategories.length; j++) {
            const course = await CoursesModel.findOne({
                where: {
                    id: coursesCategories[j].courses_id
                }
            });

            deleteFiles.push(course.title_img_path);

            const sounds = await CoursesSoundsModel.findAll({
                where: {
                    courses_id: course.id
                }
            });

            for (let k = 0; k < sounds.length; k++) {
                const sound = await SoundsModel.findOne({
                    where: {
                        id: sounds[k].sounds_id
                    }
                });

                deleteFiles.push(sound.title_img_path);
                deleteFiles.push(sound.file_sound_path);

                await SoundsModel.destroy({
                    where: {
                        id: sound.id
                    }
                }, {
                    transaction: transaction
                });
            }

            await DeletedCourses.destroy({
                where: {
                    courses_id: course.id
                }
            }, {
                transaction: transaction
            });

            await CoursesSoundsModel.destroy({
                where: {
                    courses_id: course.id
                }
            }, {
                transaction: transaction
            });

            await CoursesModel.destroy({
                where: {
                    id: course.id
                }
            }, { transaction: transaction });
        }

        return deleteFiles;
    }

    async updateAllCategories(categories, users_id) {
        const t = await sequelize.transaction();

        try {
            const currentAllCategories = await CategoriesModel.findAll({
                include: {
                    model: CategoriesSubcategoriesModel,
                    required: false
                }
            });

            const deleteCategories = [];
            const deleteSubcategories = [];
            let deleteFiles = [];

            currentAllCategories.forEach((value) => {
                let indexCategory = null;
                const category = categories.find((number, index) => {
                    if (number.id == value.id) {
                        indexCategory = index;
                        return true;
                    }

                    return false;
                });

                if (!category) {
                    deleteCategories.push(value);
                } else if (value.categories_subcategories) {
                    value.categories_subcategories.forEach((sub_value) => {
                        const subcategory = categories[indexCategory].sub_categories.find((number) => {
                            return number.id == sub_value.subcategories_id;
                        })

                        if (!subcategory) {
                            deleteSubcategories.push(sub_value);
                        }
                    });
                }
            });

            // Delete categories
            for (let i = 0; i < deleteCategories.length; i++) {
                const deleted = await this.deleteCourseByCategory(deleteCategories[i], t);
                deleteFiles = deleted;
                await CategoriesModel.destroy({
                    where: {
                        id: deleteCategories[i].id
                    }
                }, { transaction: t });
            }

            for (let i = 0; i < deleteSubcategories.length; i++) {
                const deleted = await this.deleteCourseBySubcategory(deleteSubcategories[i], t);
                deleteFiles = deleted.concat(deleteFiles);
                await SubcategoriesModel.destroy({
                    where: {
                        id: deleteSubcategories[i].subcategories_id
                    }
                }, { transaction: t });
            }

            // Add new categories
            const newCategories = categories.filter((value) => {
                return value.id < 1;
            });

            for (let i = 0; i < newCategories.length; i++) {
                const newCategory = newCategories[i];
                const category = await CategoriesModel.create({
                    title: newCategory.title,
                    description: newCategory.description,
                    users_id: users_id
                }, {
                    transaction: t
                });

                if (newCategory.sub_categories.length > 0) {
                    for (let j = 0; j < newCategory.sub_categories.length; j++) {
                        const newSubcategory = newCategory.sub_categories[j];
                        const subcategory = await SubcategoriesModel.create({
                            title: newSubcategory.title,
                            description: newSubcategory.description,
                            users_id: users_id
                        }, {
                            transaction: t
                        });

                        await CategoriesSubcategoriesModel.create({
                            categories_id: category.id,
                            subcategories_id: subcategory.id
                        }, {
                            transaction: t
                        });
                    }
                }
            }

            // Update exists categories
            const updateCategories = categories.filter((value) => {
                return value.id >= 1;
            });

            for (let i = 0; i < updateCategories.length; i++) {
                const updateCategory = updateCategories[i];

                await CategoriesModel.update(
                    {
                        title: updateCategory.title,
                        description: updateCategory.description
                    },
                    {
                        where: {
                            id: updateCategory.id
                        }
                    },
                    {
                        transaction: t
                    }
                );

                if (updateCategory.sub_categories.length > 0) {
                    // added new subcategory
                    const newSubcategories = updateCategory.sub_categories.filter((value) => {
                        return value.id < 1;
                    });

                    const categorySubcategory = await CategoriesSubcategoriesModel.findOne({
                        where: {
                            categories_id: updateCategory.id
                        }
                    });

                    let begin = 0;
                    if (!categorySubcategory) {
                        const newSubcategory = newSubcategories[begin];
                        const subcategory = await SubcategoriesModel.create({
                            title: newSubcategory.title,
                            description: newSubcategory.description,
                            users_id: users_id
                        }, {
                            transaction: t
                        });

                        const csValue = await CategoriesSubcategoriesModel.create({
                            categories_id: updateCategory.id,
                            subcategories_id: subcategory.id
                        }, {
                            transaction: t
                        });

                        await CoursesCategoriesModel.update(
                            {
                                categories_subcategories_id: csValue.id
                            },
                            {
                                where: {
                                    categories_id: updateCategory.id
                                }
                            },
                            {
                                transaction: t
                            }
                        );

                        begin++;
                    }

                    for (let j = begin; j < newSubcategories.length; j++) {
                        const newSubcategory = newSubcategories[j];
                        const subcategory = await SubcategoriesModel.create({
                            title: newSubcategory.title,
                            description: newSubcategory.description,
                            users_id: users_id
                        }, {
                            transaction: t
                        });

                        await CategoriesSubcategoriesModel.create({
                            categories_id: updateCategory.id,
                            subcategories_id: subcategory.id
                        }, {
                            transaction: t
                        });
                    }

                    // update old subcategory
                    const updateSubcategories = updateCategory.sub_categories.filter((value) => {
                        return value.id >= 1;
                    });

                    for (let j = 0; j < updateSubcategories.length; j++) {
                        const updateSubcategory = updateSubcategories[j];
                        await SubcategoriesModel.update(
                            {
                                title: updateSubcategory.title,
                                description: updateSubcategory.description
                            },
                            {
                                where: {
                                    id: updateSubcategory.id
                                }
                            },
                            {
                                transaction: t
                            }
                        );
                    }
                }
            }

            deleteFiles.forEach((value) => {
                fs.unlinkSync(value);
            });

            await t.commit();

            return true;
        } catch (e) {
            await t.rollback();
            ApiError.BadRequest(e);
        }

        return false;
    }
}

module.exports = new CategoryService();