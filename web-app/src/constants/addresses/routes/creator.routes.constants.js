export const main = '/creator';
export const content = '/content';

const CreatorRoutesConstants = {
    content_view: `${main}${content}/view`,
    content_add: `${main}${content}/add`,
    content_edit: `${main}${content}/edit`,
    content_archive: `${main}${content}/archive`,
    content_category: `${main}${content}/category`
};

export default CreatorRoutesConstants;