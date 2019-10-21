package com.yordan.finance.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.yordan.finance.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

@Entity (tableName = "items")
public class Item {

    @PrimaryKey(autoGenerate = true)
    private int _id;
    private String name;
    private double price;
    private int purchaseId;
    private int timestamp;

    public Item() {}

    @Ignore
    public Item(String name, double price){
        this.name = name;
        this.price = price;
        this.timestamp = DateUtils.currentDate();
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Item{" +
                "_id " + _id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", purchaseId=" + purchaseId +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if((obj != null && ((Item) obj).get_id() == this._id)){
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 72;
    }

    public String toJsonString() {
        JSONObject itemJson = new JSONObject();
        try{
            itemJson.put("id", get_id());
            itemJson.put("purchaseId", getPurchaseId());
            itemJson.put("name", getName());
            itemJson.put("price", getPrice());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return itemJson.toString();
    }

    public static Item fromJsonString(JSONObject jsonObject) {
        try{
            String name  = jsonObject.getString("name");
            double price = jsonObject.getDouble("price");

            return new Item(name, price);
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }
}
