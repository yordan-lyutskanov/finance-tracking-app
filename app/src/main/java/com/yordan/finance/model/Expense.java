package com.yordan.finance.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.yordan.finance.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity (tableName = "expenses")
public class Expense{
    @PrimaryKey
    private int _id;
    private String name;
    private double amount;
    private int date;
    private int category;
    private int timestamp;

    @Ignore
    private List<Item> items;

    public Expense(){
        this.items = new ArrayList<>();
    }

    public int getId() { return _id; }

    public void setId(int _id) { this._id = _id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public double getAmount() { return amount; }

    public void setAmount(double price){ this.amount = price; }

    public void setDate(int date) { this.date = date; }

    public int getDate() { return date; }

    public void setCategory(int category) { this.category = category; }

    public int getCategory() { return category; }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<Item> getItems() { return items;}

    public void addItem(Item item){
        items.add(item);
        amount += item.getPrice();
        item.setPurchaseId(this._id);
    }

    public void removeItem(Item item){
        items.remove(item);
        amount -= item.getPrice();
    }

    @Override
    public String toString() {
        return _id + ", "
                + name + ", "
                 + amount + ", "
                 + DateUtils.intDateToString(date) + ", "
                + category;
    }

    public static Expense fromJsonString(JSONObject jsonObject){
        try{
            int id =  jsonObject.getInt("clientsideId");
            String name  = jsonObject.getString("name");
            double amount = jsonObject.getDouble("amount");
            int date = jsonObject.getInt("date");
            int category = jsonObject.getInt("category");
            int timestamp = jsonObject.getInt("timestamp");

            return new Builder(id, category)
                    .name(name)
                    .amount(amount)
                    .date(date)
                    .timeStamp(timestamp)
                    .build();
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public String toJsonString(){
        JSONObject userJson = new JSONObject();
        try{
            userJson.put("clientsideId", getId());
            userJson.put("name", getName());
            userJson.put("amount", getAmount());
            userJson.put("date", getDate());
            userJson.put("category", getCategory());
            userJson.put("timestamp", getTimestamp());
        }catch (JSONException e){
            e.printStackTrace();
        }

        return userJson.toString();
    }

    public static class Builder {
        private int id;
        private int category;

        private double amount;
        private String name;
        private int date;
        private int timeStamp;

        public Builder(int id, int category) {
            this.id = id;
            this.category = category;

            this.amount = 0;
            this.name = "Unnamed";
            this.date = DateUtils.currentDate();
            this.timeStamp = DateUtils.currentDate();
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder date(int date){
            this.date = date;
            return this;
        }

        public Builder timeStamp(int timeStamp){
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder amount(double amount){
            this.amount = amount;
            return this;
        }

        public Expense build(){
            Expense expense = new Expense();
            expense.setId(this.id);
            expense.setAmount(this.amount);
            expense.setCategory(this.category);
            expense.setName(this.name);
            expense.setDate(this.date);
            expense.setTimestamp(this.timeStamp);

            return expense;
        }
    }

}
