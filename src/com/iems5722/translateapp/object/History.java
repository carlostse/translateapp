package com.iems5722.translateapp.object;

import java.util.List;
import com.iems5722.translateapp.util.Database;

public class History {

    public enum Type { Send, Receive };

    public History(String text, Type type) {
        super();
        this.text = text;
        this.type = type;
    }

    public History(int rowId, String text, int type) {
        super();
        this.rowId = rowId;
        this.text = text;
        this.type = Type.values()[type];
    }

    private int rowId;
    private String text;
    private Type type;

    public static List<History> getAll(Database db){
        return db.getHistory();
    }

    public void save(Database db){
        db.saveHistory(this);
    }

    // getters
    public int getRowId() {
        return rowId;
    }
    public String getText() {
        return text;
    }
    public Type getType() {
        return type;
    }
}
