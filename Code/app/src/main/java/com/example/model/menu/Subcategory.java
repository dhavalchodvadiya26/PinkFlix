package com.example.model.menu;

import com.google.gson.annotations.SerializedName;

public class Subcategory {

    @SerializedName("category_id")
    private String mCategoryId;
    @SerializedName("id")
    private Long mId;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("subcategory_image")
    private Object mSubcategoryImage;
    @SerializedName("subcategory_name")
    private String mSubcategoryName;
    @SerializedName("subcategory_slug")
    private String mSubcategorySlug;

    public String getCategoryId() {
        return mCategoryId;
    }

    public Long getId() {
        return mId;
    }

    public String getStatus() {
        return mStatus;
    }

    public Object getSubcategoryImage() {
        return mSubcategoryImage;
    }

    public String getSubcategoryName() {
        return mSubcategoryName;
    }

    public String getSubcategorySlug() {
        return mSubcategorySlug;
    }

    public static class Builder {

        private String mCategoryId;
        private Long mId;
        private String mStatus;
        private Object mSubcategoryImage;
        private String mSubcategoryName;
        private String mSubcategorySlug;

        public Subcategory.Builder withCategoryId(String categoryId) {
            mCategoryId = categoryId;
            return this;
        }

        public Subcategory.Builder withId(Long id) {
            mId = id;
            return this;
        }

        public Subcategory.Builder withStatus(String status) {
            mStatus = status;
            return this;
        }

        public Subcategory.Builder withSubcategoryImage(Object subcategoryImage) {
            mSubcategoryImage = subcategoryImage;
            return this;
        }

        public Subcategory.Builder withSubcategoryName(String subcategoryName) {
            mSubcategoryName = subcategoryName;
            return this;
        }

        public Subcategory.Builder withSubcategorySlug(String subcategorySlug) {
            mSubcategorySlug = subcategorySlug;
            return this;
        }

        public Subcategory build() {
            Subcategory subcategory = new Subcategory();
            subcategory.mCategoryId = mCategoryId;
            subcategory.mId = mId;
            subcategory.mStatus = mStatus;
            subcategory.mSubcategoryImage = mSubcategoryImage;
            subcategory.mSubcategoryName = mSubcategoryName;
            subcategory.mSubcategorySlug = mSubcategorySlug;
            return subcategory;
        }

    }

}
