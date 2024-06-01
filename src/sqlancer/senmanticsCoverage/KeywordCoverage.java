package sqlancer.senmanticsCoverage;

import sqlancer.Main;

import java.util.Iterator;

public class KeywordCoverage {
    public static void initKeywordMap(){
        Main.Keyword.add("lpad");
        Main.Keyword.add("left");
        Main.Keyword.add("right");
        Main.Keyword.add("strcmp");
        Main.Keyword.add("repeat");
        Main.Keyword.add("position");
        Main.Keyword.add("substring");
        Main.Keyword.add("power");
        Main.Keyword.add("coalesce");
        Main.Keyword.add("format");
        Main.Keyword.add("instr");
        Main.Keyword.add("locate");
        Main.Keyword.add("rpad");
        Main.Keyword.add("replace");
        Main.Keyword.add("substring_index");
        Main.Keyword.add("case");
        Main.Keyword.add("if");
        Main.Keyword.add("insert");
        Main.Keyword.add("and");
        Main.Keyword.add("ceil");
        Main.Keyword.add("ceiling");
        Main.Keyword.add("floor");
        Main.Keyword.add("bin");
        Main.Keyword.add("oct");
        Main.Keyword.add("sign");
        Main.Keyword.add("sqrt");
        Main.Keyword.add("square");
        Main.Keyword.add("abs");
        Main.Keyword.add("cos");
        Main.Keyword.add("sin");
        Main.Keyword.add("acos");
        Main.Keyword.add("asin");
        Main.Keyword.add("atan");
        Main.Keyword.add("degree");
        Main.Keyword.add("ln");
        Main.Keyword.add("radians");
        Main.Keyword.add("atan");
        Main.Keyword.add("&&");
        Main.Keyword.add("concat");
        Main.Keyword.add("concat_ws");
        Main.Keyword.add("find_in_set");
        Main.Keyword.add("or");
        Main.Keyword.add("xor");
        Main.Keyword.add(">");
        Main.Keyword.add(">=");
        Main.Keyword.add("<");
        Main.Keyword.add("<=");
        Main.Keyword.add("=");
        Main.Keyword.add("!=");
        Main.Keyword.add("+");
        Main.Keyword.add("-");
        Main.Keyword.add("*");
        Main.Keyword.add("/");
        Main.Keyword.add("%");
        Main.Keyword.add("&");
        Main.Keyword.add("round");
        Main.Keyword.add("^");
        Main.Keyword.add("<<");
        Main.Keyword.add(">>");
        Main.Keyword.add("not");
        Main.Keyword.add("!");
        Main.Keyword.add("between");
        Main.Keyword.add("exists");
        Main.Keyword.add("is");
        Main.Keyword.add("ascii");
        Main.Keyword.add("upper");
        Main.Keyword.add("lcase");
        Main.Keyword.add("ucase");
        Main.Keyword.add("lower");
        Main.Keyword.add("char_length");
        Main.Keyword.add("character_length");
        Main.Keyword.add("length");
        Main.Keyword.add("reverse");
        Main.Keyword.add("char");
        Main.Keyword.add("avg");
        Main.Keyword.add("sign");
        Main.Keyword.add("square");
        Main.Keyword.add("left");
        Main.Keyword.add("strcmp");
        Main.Keyword.add("%");
        Main.Keyword.add("position");
        Main.Keyword.add("mod");
        Main.Keyword.add("|");
        Main.Keyword.add("in");
        Main.Keyword.add("field");
        Main.Keyword.add("concat");
        Main.Keyword.add("elt");
        Main.Keyword.add("substring");
        Main.Keyword.add("least");
        Main.Keyword.add("power");
        Main.Keyword.add("%");
        Main.Keyword.add("9223372036854775807");
        Main.Keyword.add("REPEAT");
    }
    public static boolean keywordcov(String sql){
        sql = sql.toLowerCase();
        Iterator<String> iterator1 = Main.Keyword.iterator();
        String keyword = "";
        boolean flag = false;

        while (iterator1.hasNext()){
            String elemant = iterator1.next();
            if(sql.contains(elemant)){
                keyword = elemant;
                if(!Main.KeywordCover.contains(keyword)){
                    Main.KeywordCover.add(keyword);
                    flag = true;
                }
            }
        }
        return flag;
    }
}
