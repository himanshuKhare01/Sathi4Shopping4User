package com.sathi4shopping.Choices;

import java.util.ArrayList;

public class UserOptionsInChat {
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> category1 = new ArrayList<>();
    private ArrayList<String> category2 = new ArrayList<>();
    private ArrayList<String> category3 = new ArrayList<>();
    private ArrayList<String> category4 = new ArrayList<>();
    private ArrayList<String> category5 = new ArrayList<>();
    private ArrayList<String> category6 = new ArrayList<>();
    private ArrayList<String> category7 = new ArrayList<>();

    public ArrayList[] list = new ArrayList[]{getCategory1(), getCategory2(), getCategory3(), getCategory4(), getCategory4(), getCategory5(), getCategory6(), getCategory7()};

    private ArrayList<String> getCategory1() {
        category1.add("Mobiles");
        category1.add("Mobile Accessories");
        category1.add("Smart Wearable Tech");
        category1.add("Health Care Appliances");
        category1.add("Laptops");
        category1.add("Desktop PCs");
        category1.add("Gaming & Accessories");
        category1.add("Computer Accessories");
        category1.add("Computer Peripherals");
        category1.add("Tablets");
        category1.add("Home Entertainment");
        category1.add("Smart Home Automation");
        category1.add("Camera");
        category1.add("Camera Accessories");
        category1.add("Network Components");
        category1.add("Others");
        return category1;
    }

    private ArrayList<String> getCategory2() {
        category2.add("Television");
        category2.add("Official Android TVs");
        category2.add("Smart & Ultra HD");
        category2.add("TVs by brand");
        category2.add("TVs by screen size");
        category2.add("Washing Machine");
        category2.add("Shop By Brand");
        category2.add("Refrigerators");
        category2.add("Air Conditioners");
        category2.add("Kitchen Appliances");
        category2.add("Winter Special");
        category2.add("Healthy Living Appliances");
        category2.add("Small Home Appliances");
        category2.add("Top Brands");
        category2.add("Buying Guides");
        category2.add("Others");
        return category2;
    }

    private ArrayList<String> getCategory3() {
        category3.add("Footwear");
        category3.add("Men's Grooming");
        category3.add("Clothing");
        category3.add("Top wear");
        category3.add("Bottom wear");
        category3.add("Ties, Socks, Caps & more");
        category3.add("Kurta, Pyjama & others");
        category3.add("Fabrics");
        category3.add("Winter wear");
        category3.add("Sports wear");
        category3.add("Innerwear & Sleepwear");
        category3.add("Watches");
        category3.add("Accessories");
        category3.add("Sports & Fitness Store");
        category3.add("Smart Watches");
        category3.add("Smart Bands");
        category3.add("Personal Care Appliances");
        category3.add("Others");
        return category3;
    }

    private ArrayList<String> getCategory4() {
        category4.add("Clothing");
        category4.add("Winter & Seasonal Wear");
        category4.add("Western Wear");
        category4.add("Lingerie & Sleepwear");
        category4.add("Ethnic Wear");
        category4.add("Sports Wear");
        category4.add("Swim & Beachwear");
        category4.add("International Brands");
        category4.add("New arrivals !");
        category4.add("Exclusive Collection");
        category4.add("Under 299");
        category4.add("Footwear");
        category4.add("Sandals");
        category4.add("Shoes");
        category4.add("Ballerinas");
        category4.add("Slippers & Flip- Flop's");
        category4.add("Watches");
        category4.add("Smart Watches");
        category4.add("Personal Care Appliances");
        category4.add("Beauty & Grooming");
        category4.add("Jewellery");
        category4.add("Accessories");
        category4.add("Trending today!");
        category4.add("Others");
        return category4;
    }

    private ArrayList<String> getCategory5() {
        category5.add("Kids Clothing");
        category5.add("Boys' Clothing");
        category5.add("Girls' Clothing");
        category5.add("Baby Boy Clothing");
        category5.add("Baby Girl Clothing");
        category5.add("Kids Footwear");
        category5.add("Boys' Footwear");
        category5.add("Girls' Footwear");
        category5.add("Baby Footwear");
        category5.add("Character Shoes");
        category5.add("Kids Winter Wear");
        category5.add("Toys");
        category5.add("School Supplies");
        category5.add("Baby Care");
        category5.add("Featured brands");
        category5.add("Others");
        return category5;
    }

    private ArrayList<String> getCategory6() {
        category6.add("Kitchen & Dining");
        category6.add("Dining & Serving");
        category6.add("Kitchen Storage");
        category6.add("Furniture");
        category6.add("Offers on Furniture");
        category6.add("House Keeping & Laundry");
        category6.add("Furnishing");
        category6.add("Smart Home Automation");
        category6.add("Tools & Hardware");
        category6.add("Home Decor");
        category6.add("Lighting");
        category6.add("Pet Supplies");
        category6.add("Durability Certified Furniture");
        category6.add("Gardening Store");
        category6.add("Bakeware store");
        category6.add("Perfect Home Store");
        category6.add("Others");
        return category6;
    }

    private ArrayList<String> getCategory7() {
        category7.add("Books");
        category7.add("Stationery");
        category7.add("Gaming");
        category7.add("Music");
        category7.add("Movies & TV Shows");
        category7.add("Musical Instruments");
        category7.add("The Hobby Store");
        category7.add("Sports");
        category7.add("Exercise & Fitness");
        category7.add("Car & Bike Accessories");
        category7.add("Car Electronics & Appliances");
        category7.add("Helmets & Riding Gear");
        category7.add("Health & Nutrition");
        category7.add("Gourmet & Specialty Foods");
        category7.add("Others");
        return category7;
    }


    public ArrayList<String> getCategories() {
        categories.add("Electronics");
        categories.add("TV & Appliances");
        categories.add("Men");
        categories.add("Women");
        categories.add("Baby & Kids");
        categories.add("Home & Furniture");
        categories.add("Sports,Books & Others");
        return categories;
    }
}
