    package com.example.dopefits.DataModel;

    public class Category {
        private Integer id;
        private String title;

        public Category() {
        }

        public Category(Integer id, String title ) {
            this.id = id;
            this.title = title;
        }

        // Getters and Setters
        public Integer getId() {
            return id;
        }
        public String getTitle() {
            return title;
        }


        public void setTitle(String title) {
            this.title = title;
        }
        public void setId(Integer id) {
            this.id = id;
        }


    }