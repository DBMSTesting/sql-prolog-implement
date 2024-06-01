package sqlancer.coverage;

//import sqlancer.Main;
//
//import java.io.*;
import java.util.HashMap;
//import java.util.Iterator;

public class Coverage {
//    public static void CoverageCaculation(String fileName) throws IOException {
//        HashMap<String, Integer> keywords = new HashMap<String, Integer>();
//        keywords = setMap(keywords);
//        //计算模式覆盖度，计算关键字覆盖度，写入两个文件中
//        File file = new File(fileName);
//        FileReader fr = new FileReader(file);
//        BufferedReader br = new BufferedReader(fr);
//        String line;
//        String number = "";
//        while((line = br.readLine()) != null){
//            //对每一行的数据进行关键字统计
//            Iterator<String> iterator = keywords.keySet().iterator();
//            while (iterator.hasNext()) {
//                String key = iterator.next();
//                if(line.contains(key)){
//                    number = number + keywords.get(key);
//                }
//                //System.out.println("key:" + key + ",vaule:" + keywords.get(key));
//            }
//
//
//            //模式匹配，对该语句的模式+1
//            if(Main.patternsCoverage.containsKey(number)){
//                int cov = Main.patternsCoverage.get(number);
//                cov = cov+1;
//                Main.patternsCoverage.remove(number);
//                Main.patternsCoverage.put(number,cov);
//            }else{
//                Main.patternsCoverage.put(number,1);
//            }
//
//
//
//        }
//        //统计每种模式出现的次数，返回指导方法
//    }
    public static HashMap<String,Integer> setMap(HashMap<String,Integer> map){
        map.put("select",1);
        return map;
    }
}
