package com.firebase.keskustelu;

public class Keskustelu {

    private String message;
    private String author;

    @SuppressWarnings("kayttamaton")
    private Keskustelu() {
    }

    Keskustelu(String message, String author) {
        this.message = message;
        this.author = author;
    }

    public String getMessage() {
        return message;
    }
	
	public String getAuthor() {
        return author;
    }
}
