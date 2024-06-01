package DataProcess;

public class GetStr {
    public static String getStr(String str){
        //System.out.println(str);
        String []array=str.split("\\s+");

        int startIndex=0;
        int endIndex=0;
        for(int i=0;i<array.length;i++){

            if(array[i].equals("Z")){
                if(array[i+1].equals("=")){
                    startIndex=(i+2);

                }
            }
            if(array[i].equals("EOF:") ){
                endIndex=(i-1);

                break;
            }
        }
        String newStr="";
        for(int j=startIndex;j<(endIndex+1);j++){
            newStr+=array[j];
        }
//        System.out.println("getstr结果：");
//        System.out.println(newStr);
        return newStr;
    }
}
