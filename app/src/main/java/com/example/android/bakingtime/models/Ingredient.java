package com.example.android.bakingtime.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "ingredient", primaryKeys = {"id", "recipeId"})
public class Ingredient implements Parcelable {

    private int id;
    private double quantity;
    private String measure;
    @SerializedName("ingredient") private String ingredientName;
    private int recipeId;

    public Ingredient(double quantity, String measure, String ingredientName, int recipeId) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredientName = ingredientName;
        this.recipeId = recipeId;
    }

    @Ignore
    private Ingredient(Parcel in) {
        quantity = in.readDouble();
        measure = in.readString();
        ingredientName = in.readString();
        recipeId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(quantity);
        dest.writeString(measure);
        dest.writeString(ingredientName);
        dest.writeInt(recipeId);
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public double getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}
