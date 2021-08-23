package com.cs.lms.uimodel;

import io.swagger.annotations.ApiModelProperty;

public class BookUiModel {

    public static class TitleIsbn{
        private String isbn;
        private String title;

        @ApiModelProperty(required = true, value = "ISBN number of the book")
        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        @ApiModelProperty(required = true, value = "title of the book")
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "TitleIsbn{" +
                    "isbn='" + isbn + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public static class Base{
        private String exact;
        @ApiModelProperty(value = "wether search should be based on exact string. allowed values (true, false)")
        public String getExact() {
            return exact;
        }
        public void setExact(String exact) {
            this.exact = exact;
        }
    }

    public static class Isbn extends Base{
        private String isbn;

        @ApiModelProperty(required = true, value = "ISBN number of the book")
        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

    }

    public static class Title extends Base{
        public String title;

        @ApiModelProperty(required = true, value = "title of the book")
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class Keyword{
        public String keyword;

        @ApiModelProperty(required = true, value = "keyword to search for book (can be isbn or title)")
        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }
    }
}
