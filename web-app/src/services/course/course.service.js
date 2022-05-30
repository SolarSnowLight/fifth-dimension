import AdminApiConstants from "../../constants/addresses/api/admin.api.constants";

class CourseService {
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

    async createCourse(courseData) {
        try {
            if (!courseData) {
                return null;
            }

            const formData = new FormData();

            formData.append("title_img", courseData.img);

            for (let i = 0; i < courseData.lessons.length; i++) {
                formData.append("title_img_sounds", courseData.lessons[i].img[0].file, courseData.lessons[i].num);
                formData.append("file_sounds", courseData.lessons[i].audio, courseData.lessons[i].num);
            }

            formData.append("data", JSON.stringify({
                users_id: courseData.users_id,
                roles_id: courseData.roles_id,
                date_open: courseData.date_open,
                category: courseData.category,
                subcategory: courseData.subcategory,
                subscribe: courseData.subscription,
                description: courseData.description,
                title: courseData.title,
                type: courseData.type,
                img: undefined,
                lessons: courseData.lessons.map((index) => {
                    return {
                        num: index.num,
                        title: index.title,
                        description: index.description,
                        type: index.type
                    }
                })
            }));

            const data = await this.request(AdminApiConstants.create_course, 'POST', formData, {}, true);

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

            this.message("Новый курс создан!", "success");
        } catch (e) {
            console.log(e);
        }
    }

    async updateCourse(courseData) {
        try {
            /*console.log(courseData);
            return null;*/
            
            if (!courseData) {
                return null;
            }

            const formData = new FormData();

            if (courseData.img) {
                formData.append("title_img", courseData.img);
            }

            for (let i = 0; i < courseData.lessons.length; i++) {
                if (courseData.lessons[i].img) {
                    formData.append(
                        "title_img_sounds",
                        courseData.lessons[i].img.file,
                        courseData.lessons[i].id ?? courseData.lessons[i].num
                    );
                }

                if (courseData.lessons[i].audio) {
                    formData.append("file_sounds",
                        courseData.lessons[i].audio,
                        courseData.lessons[i].id ?? courseData.lessons[i].num
                    );
                }
            }

            formData.append("data", JSON.stringify({
                courses_id: courseData.courses_id,
                users_id: courseData.users_id,
                roles_id: courseData.roles_id,
                date_open: courseData.date_open,
                category: courseData.category,
                subcategory: courseData.subcategory,
                type: courseData.type,
                subscribe: courseData.subscription,
                description: courseData.description,
                title: courseData.title,
                lessons: courseData.lessons.map((index) => {
                    return {
                        id: index.id,
                        num: index.num,
                        title: index.title,
                        description: index.description,
                        type: index.type
                    }
                })
            }));

            const data = await this.request(AdminApiConstants.update_course, 'POST', formData, {}, true);

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

            this.message("Курс был обновлён!", "success");
        } catch (e) {
            console.log(e);
        }
    }

    async archiveCourse(courseData) {
        try {
            if (!courseData) {
                return null;
            }

            const data = await this.request(AdminApiConstants.delete_course, 'POST', courseData);

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

            this.message("Курс был добавлен в архив!", "success");
        } catch (e) {
            console.log(e);
        }
    }

    async recoverCourse(courseData) {
        try {
            if (!courseData) {
                return null;
            }

            const data = await this.request(AdminApiConstants.recover_course, 'POST', courseData);

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

            this.message("Курс был восстановлен!", "success");
        } catch (e) {
            console.log(e);
        }
    }

    async deleteCompletelyCourse(courseData) {
        try {
            if (!courseData) {
                return null;
            }

            const data = await this.request(AdminApiConstants.delete_completely_course, 'POST', courseData);

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

            this.message("Курс был полностью удалён!", "success");
        } catch (e) {
            console.log(e);
        }
    }
}

export default CourseService;