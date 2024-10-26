package com.example.dopefits.DataModel;

public class RecentTransact {
        private String customerName;
        private String itemName;
        private int price;
        private int qty;

        public RecentTransact(String customerName, String itemName,int price, int qty) {
            this.customerName = customerName;
            this.itemName = itemName;
            this.price = price;
            this.qty = qty;
        }

        public String getCustomerName() {
            return customerName;
        }
        public String getItemName() {
            return itemName;
        }
        public int getPrice() {
            return price;
        }
        public int getQty() { return qty; }

}
