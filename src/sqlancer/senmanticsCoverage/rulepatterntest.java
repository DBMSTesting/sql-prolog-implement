package sqlancer.senmanticsCoverage;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class rulepatterntest {
    private static void matchNested(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);

        // 遍历匹配结果
        while (matcher.find()) {
            String content1 = matcher.group(1);
            String content2 = matcher.group(2);

            System.out.println("括号内容1: " + content1);
            System.out.println("括号内容2: " + content2);
            System.out.println();

            // 递归匹配括号内的内容
            matchNested(content1, pattern);
            matchNested(content2, pattern);
        }
    }
    public static void main(String[] args) {
        //String input = "select * from t where (mod(((t.a)>(t.b)),1)) and (null)";

//        // 定义正则表达式，匹配关键字和参数
//        String regex = "\\b(mod|and|>)\\b(?:\\(([^)]+)\\))?";
//
//        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(sql);
//
//        // 遍历匹配结果
//        while (matcher.find()) {
//            String keyword = matcher.group(1);
//            String params = matcher.group(2);
//
//            System.out.println("关键字: " + keyword);
//            System.out.println("参数: " + params);
//            System.out.println();
//        }

        String input = "((A) and (B)) and ((C) and (D or E))";
//        String input = "((A) and (B))";
//
//        // 定义正则表达式，匹配括号内的内容
//        String regex = "\\((.*?)\\s+and\\s+(.*?)\\)";
//        Pattern pattern = Pattern.compile(regex);
//
//        // 递归匹配
//        matchNested(input, pattern);
        String substring = "and";
        int fromIndex = input.indexOf(substring);
        HashSet<Integer>indexes = new HashSet<>();

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
            System.out.println(patternRev+" "+pattern2);
        }
    }
}
