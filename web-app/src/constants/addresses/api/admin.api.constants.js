
const main = "/api/admin";

const AdminApiConstants = {
    create_course: `${main}/create/course`,
    delete_course: `${main}/delete/course`,
    update_course: `${main}/update/course`,
    recover_course: `${main}/recover/course`,
    delete_completely_course: `${main}/delete/completely/course`,
    
    delete_lesson: `${main}/delete/lesson`,
    update_lesson: `${main}/update/lesson`,
    add_lesson: `${main}/add/lesson`,
}

module.exports = AdminApiConstants;