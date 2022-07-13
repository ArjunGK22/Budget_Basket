package com.example.budgetbasket;

public class ModelProduct {

    String productid,productname,productcategory,productdescription,productquantity,productprice,productImage,productstock;

    String uid = "K2DurlCBHBQx4ViFWEETcfdGZIY2";

    public ModelProduct() {
    }

    public ModelProduct(String productid, String productname, String productcategory, String productdescription, String productquantity, String productprice, String productImage, String uid,String productstock) {
        this.productid = productid;
        this.productname = productname;
        this.productcategory = productcategory;
        this.productdescription = productdescription;
        this.productquantity = productquantity;
        this.productprice = productprice;
        this.productImage = productImage;
        this.uid = uid;
        this.productstock = productstock;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductcategory() {
        return productcategory;
    }

    public void setProductcategory(String productcategory) {
        this.productcategory = productcategory;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription;
    }

    public String getProductquantity() {
        return productquantity;
    }

    public void setProductquantity(String productquantity) {
        this.productquantity = productquantity;
    }

    public String getProductprice() {
        return productprice;
    }

    public void setProductprice(String productprice) {
        this.productprice = productprice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProductstock() {
        return productstock;
    }

    public void setProductstock(String productstock) {
        this.productstock = productstock;
    }
}
