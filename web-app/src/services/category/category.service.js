import CategoryApiConstants from "../../constants/addresses/api/category.api.constants";

class CategoryService {
    message;
    loading;
    request;
    error;
    clearError;

    constructor(message, loading, request, error, clearError) {
        this.message = message;
        this.loading = loading;
        this.request = request;
        this.error = error;
        this.clearError = clearError;
    }

    async getAllCategory() {
        try {
            const data = await this.request(CategoryApiConstants.get_all_categories, 'POST');
            if (data.message) {
                this.message(data.message, "error");

                const errors = data.errors;
                if (errors) {
                    errors.forEach((item) => {
                        this.message(item.msg, "error");
                    });
                }
                return null;
            }

            return data;
        } catch (e) {
            console.log(e);
        }
    }

    async updateCategory(categories) {
        try {
            if ((!categories) || (categories.length == 0)) {
                this.message("Необходимо создать хотя бы одну категорию!", "error");
                return;
            }

            for (const i of categories) {
                if ((i.title == null) || (i.description == null)) {
                    this.message("Необходимо указать название категории!", "error");
                    return;
                }

                for (const j of i.sub_categories) {
                    if ((j.title == null) || (j.description == null)) {
                        this.message("Необходимо указать название подкатегории!", "error");
                        return;
                    }
                }
            }

            const data = await this.request(CategoryApiConstants.update_categories, 'POST', {
                categories: categories
            });
            
            if (data.message) {
                this.message(data.message, "error");

                const errors = data.errors;
                if (errors) {
                    errors.forEach((item) => {
                        this.message(item.msg, "error");
                    });
                }
                return null;
            }

            return data;
        } catch (e) {
            console.log(e);
        }
    }
}

export default CategoryService;