package Comparison;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Compare {
    public static void CompareResult(int j) throws IOException {
        String fileName1 = "test_result_mysql_"+j+".txt";
        File file1 = new File(fileName1);
        FileReader fr1 = new FileReader(file1);
        BufferedReader br1 = new BufferedReader(fr1);
        String line1;

        String fileName2 = "test_result_prolog_"+j+".txt";
        File file2 = new File(fileName2);
        FileReader fr2 = new FileReader(file2);
        BufferedReader br2 = new BufferedReader(fr2);
        String line2;

        File filewriteCom = new File("compare_result_"+j+".txt");
        if(!filewriteCom.exists()){
            filewriteCom.createNewFile();
        }
        FileWriter fileWriterCom = new FileWriter(filewriteCom.getAbsoluteFile());
        BufferedWriter bwCom = new BufferedWriter(fileWriterCom);

        compare(br1,br2,bwCom);

        bwCom.close();
    }
    public static void FalseFilter(int t) throws IOException {
        String fileName1 = "/Users/tashou/PycharmProjects/sqlParse/database"+t+".txt";
        File file1 = new File(fileName1);
        FileReader fr1 = new FileReader(file1);
        BufferedReader br1 = new BufferedReader(fr1);
        String line1;

        String fileName2 = "compare_result_"+t+".txt";
        File file2 = new File(fileName2);
        FileReader fr2 = new FileReader(file2);
        BufferedReader br2 = new BufferedReader(fr2);
        String line2;

        File filewriteCom = new File("false_result_"+t+".txt");
        if(!filewriteCom.exists()){
            filewriteCom.createNewFile();
        }
        FileWriter fileWriterCom = new FileWriter(filewriteCom.getAbsoluteFile());
        BufferedWriter bwCom = new BufferedWriter(fileWriterCom);

        select_false(br1,br2,bwCom);

        bwCom.close();
    }
    public static void select_false(BufferedReader br1,BufferedReader br2,BufferedWriter bw) throws IOException {
        String line1;
        String line2;
        List<String> list1 =  new ArrayList<>();
        List<String> list2 =  new ArrayList<>();
        while((line1 = br1.readLine()) != null && (line2 = br2.readLine()) != null){
            //System.out.println(line2);
            if(!line2.equals("引号错误") && line2.substring(line2.length()-5,line2.length()).equals("false") && (!line1.contains("XOR") && !line1.contains("LIKE")&& !line1.contains("IN")&& !line1.contains("OR")&& !line1.contains("FALSE")&& !line1.contains("TRUE"))){
                bw.write(line1+"; "+line2);
                bw.write('\n');
            }
        }
    }
    public static void compareResult(String result1,String result2,BufferedWriter bw) throws IOException {
        List<List> list1 =  new ArrayList<>();
        List<List> list2 =  new ArrayList<>();
        if(result2.equals("引号错误")){
            bw.write("引号错误");
            bw.write('\n');
        }else{
            list1 = StringToList(result1);
            list2 = StringToList(result2);
            boolean result = compareList(list1,list2);
            if(!result){
                bw.write(result1+","+result2+","+"false");
            }
//            else{
//                bw.write(line1+","+line2+","+"true");
//            }
            bw.write('\n');
        }
    }
    public static void compare(BufferedReader br1,BufferedReader br2,BufferedWriter bw) throws IOException {
        String line1;
        String line2;
        List<List> list1 =  new ArrayList<>();
        List<List> list2 =  new ArrayList<>();
        while((line1 = br1.readLine()) != null && (line2 = br2.readLine()) != null){
            if(line2.equals("引号错误")){
                bw.write("引号错误");
                bw.write('\n');
                continue;
            }
            list1 = StringToList(line1);
            list2 = StringToList(line2);
            boolean result = compareList(list1,list2);
            if(!result){
                bw.write(line1+","+line2+","+"false");
            }else{
                bw.write(line1+","+line2+","+"true");
            }
            bw.write('\n');
        }
    }
    public static String datetime(String str){
        String strTemp = "";
        for(int i=0;i<str.length();i++){
            if(i + 8<str.length()){
                if(str.substring(i,i+8).equals("datetime")){
                    for(int j=i;j<str.length();j++){
                        if(str.substring(j,j+1).equals(")")){
                            String newStr = (str.substring(i,j+1).replace(", ","^"));
                            newStr = (newStr.replace(".","^"));
                            strTemp+=newStr;
                            i = j;
                            break;
                        }
                    }
                }else{
                    strTemp += str.substring(i,i+1);
                }
            }else{
                strTemp += str.substring(i,str.length());
                break;
            }
        }
        return strTemp;
    }
    public static List<List> StringToList(String str){
        str = datetime(str);
        str = str.replaceAll("\\s*","");
        //str = str.trim();

        str = str.replace(",(",",[");
        str = str.replace(",)","]");
        str = str.replace("),[","],[");
        List<List>listArray = new ArrayList<>();
        System.out.println(str);
        if(str.equals("[]")){
            listArray.add(null);
        }else{
            String []items = str.substring(2,str.length()-2).split("],\\[");
            for(int i = 0;i<items.length;i++){
                String []item = items[i].split(",");
                List<String>list = new ArrayList<>();
                for(int j = 0;j<item.length;j++){
                    list.add(item[j]);
                }
                listArray.add(list);
            }
        }
        return listArray;

//        String []items = str.substring(1,str.length()-1).split(",");
//        List<String>list = new ArrayList<>();
//        for(String item : items){
//            if(item.equals(" TRUE") || item.equals(" true")){
//                list.add(" true");
//            }else if(item.equals(" FALSE") || item.equals(" false")){
//                list.add(" false");
//            }else if(item.equals(" NULL") || item.equals(" null")){
//                list.add(" null");
//            }else if(item.equals("TRUE") || item.equals("true")){
//                list.add("true");
//            }else if(item.equals("false") || item.equals("FALSE")){
//                list.add("false");
//            }else if(item.equals("NULL") || item.equals("null")){
//                list.add("null");
//            }else{
//                list.add(item);
//            }
//        }
//        return list;
    }
    public static boolean compareList(List<List>list1,List<List>list2){
//        if (list1 == list2) {
//            return true;
//        }
//        if ((list1 == null && list2 != null && list2.size() == 0)
//                || (list2 == null && list1 != null && list1.size() == 0)) {
//            return true;
//        }
//        if (list1.size() != list2.size()) {
//            return false;
//        }
//        if (!list1.containsAll(list2)) {
//            return false;
//        }
//        return true;
        System.out.println(list1.size()+" "+list2.size());
        if(list1.size()==list2.size()){
            return true;
        }else{
            return false;
        }
    }
}
