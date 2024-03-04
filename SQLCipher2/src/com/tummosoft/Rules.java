package com.tummosoft;

import anywheresoftware.b4a.BA;

@BA.ShortName("Rules")
public class Rules {
    public static int DB_CREATE_NEW = 11;
    public static int DB_COPY = 12;
    public static int DB_BACKUP = 13;        
    public static int R_UPGRADE_DELETE = 13;
    private static Rules obRule;
    
    static public boolean Equal(Rules object) {
        return obRule.equals(object);
    }
}
