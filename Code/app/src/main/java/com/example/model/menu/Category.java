package com.example.model.menu;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {

    @SerializedName("category_id")
    private Long mCategoryId;
    @SerializedName("category_name")
    private String mCategoryName;
    @SerializedName("subcategory")
    private List<Subcategory> mSubcategory;

    public Long getCategoryId() {
        return mCategoryId;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public List<Subcategory> getSubcategory() {
        return mSubcategory;
    }

    public static class Builder {

        private Long mCategoryId;
        private String mCategoryName;
        private List<Subcategory> mSubcategory;

        public Category.Builder withCategoryId(Long categoryId) {
            mCategoryId = categoryId;
            return this;
        }

        public Category.Builder withCategoryName(String categoryName) {
            mCategoryName = categoryName;
            return this;
        }

        public Category.Builder withSubcategory(List<Subcategory> subcategory) {
            mSubcategory = subcategory;
            return this;
        }

        public Category build() {
            Category category = new Category();
            category.mCategoryId = mCategoryId;
            category.mCategoryName = mCategoryName;
            category.mSubcategory = mSubcategory;
            return category;
        }

    }

}
