package sqlancer.senmanticsCoverage;

import sqlancer.Main;

import java.util.*;

public class CompositeRuleCoverage {
    private static HashSet<String> unary_operator = new HashSet<>();
    private static HashSet<String> unary_operator_before = new HashSet<>();
    private static HashSet<String> binary_operator = new HashSet<>();

    private static HashSet<String> unary_function = new HashSet<>();
    private static HashSet<String> binary_function = new HashSet<>();
    private static HashSet<String> third_function = new HashSet<>();
    public static void initRuleMap(){
        //给出几种关键字的参数数据类型 null 字符串 int float expression boolean


        HashSet<String> rules = new HashSet<>();
        rules.addAll(Arrays.asList("null", "int", "expression","boolean","string","column"));

        for(String uo:unary_operator){
            for(String rule:rules){
                Main.Rule.add("("+rule+")"+uo);
            }
        }
        for(String uf:unary_function){
            for(String rule:rules){
                Main.Rule.add(uf+"("+rule+")");
            }
        }
        for(String uob:unary_operator_before){
            for(String rule:rules){
                Main.Rule.add(uob+"("+rule+")");
            }
        }
        for(String bo:binary_operator){
            for(String rule1:rules){
                for(String rule2:rules){
                    Main.Rule.add(rule1+" "+bo+" "+rule2);
                }
            }
        }
        for(String bf:binary_function){
            for(String rule1:rules){
                for(String rule2:rules){
                    Main.Rule.add(bf+"("+rule1+","+rule2+")");
                }
            }
        }
        for(String tf:third_function){
            for(String rule1:rules){
                for(String rule2:rules){
                    for(String rule3:rules){
                        Main.Rule.add(tf+"("+rule1+","+rule2+","+rule3+")");
                    }

                }
            }
        }
    }
    public static boolean comrulecov(String sql){
        //先提炼出sql中的关键字，然后将规则提炼出，整理出模式，每条sql语句必须要触发一个新的规则
        sql = sql.toLowerCase();
        Iterator<String> iterator1 = Main.Keyword.iterator();
        String keyword = "";
        HashSet<String> keywordInclude = new HashSet<>();
        boolean flag = false;
        while (iterator1.hasNext()){
            String elemant = iterator1.next();
            if(sql.contains(elemant)){
                keyword = elemant;
                keywordInclude.add(keyword);
            }
        }
        String pattern = "";
        for(String key:keywordInclude){
            //根据这个关键字整理模式
            List<String> patterns = new ArrayList<>();
            if(binary_operator.contains(key)){
                patterns = ParseRule.parseBinaryOP(key,sql);
            }else if(unary_operator.contains(key)){
                patterns = ParseRule.parseUnaryOP(key,sql);
            }else if(unary_function.contains(key)){
                patterns = ParseRule.parseUnaryF(key,sql);
            }else if(unary_operator_before.contains(key)){
                patterns = ParseRule.parseUnaryF(key,sql);
            }else if(binary_function.contains(key)){
                patterns = ParseRule.parseBinaryF(key,sql);
            }
            for(String p:patterns){
                pattern = pattern+p;
            }

        }
        if(!Main.CompositeRuleCover.contains(pattern)){
            Main.CompositeRuleCover.add(pattern);
            flag = true;
        }
        return flag;
    }
}
