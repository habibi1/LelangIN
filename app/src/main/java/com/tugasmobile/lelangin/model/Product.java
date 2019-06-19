package com.tugasmobile.lelangin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    String id;
    public String name;
    public String image;
    public int price;
    public String owner;
    public String idOwner;
    public String description;
    public String idMember;
    public String nameMember;

    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        image = in.readString();
        price = in.readInt();
        owner = in.readString();
        idOwner = in.readString();
        description = in.readString();
        idMember = in.readString();
        nameMember = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(String idOwner) {
        this.idOwner = idOwner;
    }

    public String getIdMember() {
        return idMember;
    }

    public void setIdMember(String idMember) {
        this.idMember = idMember;
    }

    public String getNameMember() {
        return nameMember;
    }

    public void setNameMember(String nameMember) {
        this.nameMember = nameMember;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Product() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Product(String id,String name, String image , int price, String owner, String idOwner, String description, String idMember, String nameMember) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.owner = owner;
        this.idOwner = idOwner;
        this.description = description;
        this.idMember = idMember;
        this.nameMember = nameMember;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeInt(price);
        parcel.writeString(owner);
        parcel.writeString(idOwner);
        parcel.writeString(description);
    }
}
