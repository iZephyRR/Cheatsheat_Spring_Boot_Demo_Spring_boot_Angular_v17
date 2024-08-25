package com.cheatsheet.dto;

public class CheatsheetNotification {
    private String creator;
    private String cheatsheetTitle;

    public CheatsheetNotification(String creator, String cheatsheetTitle) {
        this.creator = creator;
        this.cheatsheetTitle = cheatsheetTitle;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCheatsheetTitle() {
        return cheatsheetTitle;
    }

    public void setCheatsheetTitle(String cheatsheetTitle) {
        this.cheatsheetTitle = cheatsheetTitle;
    }
}
