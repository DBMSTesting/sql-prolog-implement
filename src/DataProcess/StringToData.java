package DataProcess;

public class StringToData {
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
