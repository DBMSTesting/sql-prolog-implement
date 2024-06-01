package sqlancer.senmanticsCoverage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseRule {
    private static String intPattern = "^[a-zA-Z]+$";
    private static String alphanumericPattern = "^[a-zA-Z0-9]+$";
    private static String dotSeparatedPattern = "^[a-zA-Z]+\\.[a-zA-Z]+$";
    public static List<String> parseUnaryOP(String substring,String input){
        List<String> patterns = new ArrayList<>();
        int fromIndex = input.indexOf(substring);
        HashSet<Integer> indexes = new HashSet<>();

        while (fromIndex < input.length() && fromIndex != -1) {
            int index = input.indexOf(substring, fromIndex);

            if (index != -1) {
                //System.out.println("子串 \"" + substring + "\" 在字符串中的位置是: " + index);
                indexes.add(index);
                fromIndex = index + 1;  // 更新起始搜索位置
            } else {
                break;  // 未找到子串，退出循环
            }
        }
        for(int i:indexes){
            int t = 0;
            int flag = 0;
            String pattern1 = "";
            String patternRev = "";
            for(int j = i-1;j>=0;j--){
                if(input.charAt(j)==')'){
                    t++;
                    flag = 1;
                }else if(input.charAt(j)=='('){
                    t--;
                    flag = 1;
                }
                pattern1 = pattern1+input.charAt(j);
                if(flag==1 && t==0){
                    break;
                }
            }
            for(int x = pattern1.length()-1;x>=0;x--){
                patternRev = patternRev+pattern1.charAt(x);
            }
            patterns.add(parseUnaryOPType(substring,patternRev));
            System.out.println(patternRev);
        }
        return patterns;
    }
    public static List<String> parseUnaryF(String substring,String input){
        List<String> patterns = new ArrayList<>();
        int fromIndex = input.indexOf(substring);
        HashSet<Integer> indexes = new HashSet<>();

        while (fromIndex < input.length() && fromIndex != -1) {
            int index = input.indexOf(substring, fromIndex);

            if (index != -1) {
                //System.out.println("子串 \"" + substring + "\" 在字符串中的位置是: " + index);
                indexes.add(index);
                fromIndex = index + 1;  // 更新起始搜索位置
            } else {
                break;  // 未找到子串，退出循环
            }
        }
        for(int i:indexes){
            int t = 0;
            int flag = 0;
            String pattern2 = "";
            for(int j = i+substring.length();j<input.length();j++){
                if(input.charAt(j)=='('){
                    t++;
                    flag = 1;
                }else if(input.charAt(j)==')'){
                    t--;
                    flag = 1;
                }
                pattern2 = pattern2+input.charAt(j);
                if(flag==1 && t==0){
                    break;
                }
            }
            patterns.add(parseUnaryFType(substring,pattern2));
            System.out.println(pattern2);
        }
        return patterns;
    }
    public static List<String> parseBinaryF(String substring,String input){
        List<String> patterns = new ArrayList<>();
        int fromIndex = input.indexOf(substring);
        HashSet<Integer> indexes = new HashSet<>();

        while (fromIndex < input.length() && fromIndex != -1) {
            int index = input.indexOf(substring, fromIndex);

            if (index != -1) {
                //System.out.println("子串 \"" + substring + "\" 在字符串中的位置是: " + index);
                indexes.add(index);
                fromIndex = index + 1;  // 更新起始搜索位置
            } else {
                break;  // 未找到子串，退出循环
            }
        }
        for(int i:indexes){
            int t = 0;
            int flag = 0;
            String pattern = "";
            String pattern1 = "";
            String pattern2 = "";
            for(int j = i+substring.length();j<input.length();j++){
                if(input.charAt(j)=='('){
                    t++;
                    flag = 1;
                }else if(input.charAt(j)==')'){
                    t--;
                    flag = 1;
                }
                pattern = pattern+input.charAt(j);
                if(flag==1 && t==0){
                    break;
                }
            }
            int count = 0;
            int index = 0;
            for(int y = 0;y<pattern.length();y++){
                if(pattern.charAt(y)=='('){
                    count++;
                }else if(pattern.charAt(y)==')'){
                    count--;
                }
                if(count == 1 && pattern.charAt(y)==','){
                    index = y;
                    break;
                }
            }
            int first = 0;
            int last = 0;
            for(int g = 0;g<pattern.length();g++){
                if(pattern.charAt(g)=='('){
                    first = g;
                    break;
                }
            }
            for(int g = pattern.length();g>0;g--){
                if(pattern.charAt(g)==')'){
                    last = g;
                    break;
                }
            }
            pattern1 = pattern.substring(first+1,index);
            pattern2 = pattern.substring(index+1,last);
            patterns.add(parseBinaryFType(substring,pattern1,pattern2));
            System.out.println(pattern2);
        }
        return patterns;
    }



    public static List<String> parseBinaryOP(String substring,String input){
        List<String> patterns = new ArrayList<>();
        int fromIndex = input.indexOf(substring);
        HashSet<Integer> indexes = new HashSet<>();

        while (fromIndex < input.length() && fromIndex != -1) {
            int index = input.indexOf(substring, fromIndex);

            if (index != -1) {
                //System.out.println("子串 \"" + substring + "\" 在字符串中的位置是: " + index);
                indexes.add(index);
                fromIndex = index + 1;  // 更新起始搜索位置
            } else {
                break;  // 未找到子串，退出循环
            }
        }
        for(int i:indexes){
            int t = 0;
            int flag = 0;
            String pattern1 = "";
            String patternRev = "";
            String pattern2 = "";
            for(int j = i-1;j>=0;j--){
                if(input.charAt(j)==')'){
                    t++;
                    flag = 1;
                }else if(input.charAt(j)=='('){
                    t--;
                    flag = 1;
                }
                pattern1 = pattern1+input.charAt(j);
                if(flag==1 && t==0){
                    break;
                }
            }
            t=0;
            flag=0;
            for(int x = pattern1.length()-1;x>=0;x--){
                patternRev = patternRev+pattern1.charAt(x);
            }
            for(int j = i+3;j<input.length();j++){
                if(input.charAt(j)=='('){
                    t++;
                    flag = 1;
                }else if(input.charAt(j)==')'){
                    t--;
                    flag = 1;
                }
                pattern2 = pattern2+input.charAt(j);
                if(flag==1 && t==0){
                    break;
                }
            }
            patterns.add(parseBinaryOPType(substring,patternRev,pattern2));
            System.out.println(patternRev+" "+pattern2);
        }
        return patterns;
    }
    private static String parseBinaryOPType(String keyword,String pattern1,String pattern2){
        return parseType(pattern1)+" "+keyword+" "+parseType(pattern2);
    }
    private static String parseUnaryOPType(String keyword,String pattern1){
        return "("+parseType(pattern1)+")"+keyword;
    }
    private static String parseUnaryFType(String keyword,String pattern1){
        return keyword+"("+parseType(pattern1)+")";
    }
    private static String parseBinaryFType(String keyword,String pattern1,String pattern2){
        return keyword+"("+parseType(pattern1)+","+parseType(pattern2)+")";
    }
    private static String parseType(String pattern){
        //String patternTemp = pattern.replace("\\s+","");
        Pattern patternx = Pattern.compile(intPattern);
        Matcher matcher = patternx.matcher(pattern);

        Pattern patternx1 = Pattern.compile(alphanumericPattern);
        Matcher matcher1 = patternx1.matcher(pattern);

        Pattern patternx2 = Pattern.compile(dotSeparatedPattern);
        Matcher matcher2 = patternx2.matcher(pattern);
        if(pattern.equals("null")){
            return "null";
        }else if(pattern.equals("true")||pattern.equals("false")){
            return "boolean";
        }else if(matcher.matches()){
            return "int";
        }else if(matcher1.matches()){
            return "string";
        }else if(matcher2.matches()){
            return "column";
        }else{
            return "expression";
        }
    }
}
