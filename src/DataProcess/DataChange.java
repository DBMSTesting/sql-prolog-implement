package DataProcess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataChange {
    public static Pattern pattern = Pattern.compile("^[-+]?[0-9]+(\\.[0-9]+)?$");
    public static String dataChange(String x,String tempResult,String column){
        if(!pattern.matcher(x).matches() ){
            if(x.contains(".") && !(x.substring(0,1).equals("'") && x.substring(x.length()-1,x.length()).equals("'"))){
                x=stringToData(x,tempResult,column);
            }
            if((x.equals("true"))||(x.equals("TRUE"))){
                x = "1";
            }else if((x.equals("false"))||(x.equals("FALSE"))){
                x = "0";
            }else if((x.equals("null"))||(x.equals("NULL"))){
                x = "null";
            }else if((x.equals("notnull"))||(x.equals("NOTNULL"))){
                x = "notnull";
            }else if((x.equals("unknown"))||(x.equals("UNKNOWN"))){
                x = "unknown";
            }else if(x.substring(0,1).equals("^") && x.substring(x.length()-1,x.length()).equals("^")){
                if(pattern.matcher(x.substring(1,x.length()-1)).matches()){
                    x = x.substring(1,x.length()-1);
                }else if(x.length()>4 && x.substring(1,3).equals("\n")){
                    x = x.substring(3,x.length()-1);
                }else{
                    x = "0";
                }
            }else if(isValidNumberFormat(x)){
                x = extractNumber(x);
            }
//            else if(pattern.matcher(x).matches()){
//                System.out.println("hhhhhhhhh"+x);
//                x =x;
//            }
            else{
                System.out.println("gggggggg"+x);
                x = x;
                //flag = 1;
            }
        }return x;
    }
    public static boolean isValidNumberFormat(String input) {
        String pattern = "^[+-]?\\d+(\\.\\d+)?";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);

        return matcher.matches();
    }
    public static String extractNumber(String input) {
        String numberString = "";
        boolean foundDecimalPoint = false;
        boolean foundDigits = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (Character.isDigit(c)) {
                numberString += c;
                foundDigits = true;
            } else if (!foundDigits && (c == '+' || c == '-')) {
                numberString += c;
            } else if (!foundDecimalPoint && c == '.') {
                numberString += c;
                foundDecimalPoint = true;
            } else {
                break;
            }
        }

        return numberString;
    }
    public static String stringToData(String x,String tempResult,String column){
        //System.out.println(x);
        x = x.replace(".","^");
        tempResult=tempResult.replaceAll(" ","");
        column=column.replaceAll(" ","");
        column = column.substring(1,column.length()-1);
        String []column_item=column.split(",");
        String []data_item=tempResult.split(",");
        //System.out.println(item[0]);
        int index=0;
        for(int i =0;i<column_item.length;i++){
            if(column_item[i].equals(x)){
                //System.out.println("index:"+x);
                index=i;
            }
        }
        return data_item[index];
    }
}
