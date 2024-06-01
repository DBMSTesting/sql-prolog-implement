package GetDatabaseData;

import java_prolog.ScriptPrologCommandOrLogic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SWIPrologExample {
    public static void main(String[] args) {
        try {
            // 创建外部进程并启动SWI-Prolog
            ProcessBuilder processBuilder = new ProcessBuilder("/opt/local/bin/swipl", "-q");
            Process prologProcess = processBuilder.start();
            System.out.println("hhhh");

//            public static String prologCommand = "/opt/local/bin/swipl";
//            public static String prologMainFile = "/Users/tashou/sql-prolog/src/prolog";
//            Process p;
//            p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
//            OutputStream out = p.getOutputStream ();
//            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//            out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/in.pl'].\n").getBytes());
//            out.write(("in_clause("+x+","+str+",0,Z).\n").getBytes());
//            System.out.println("in_clause("+x+","+str+",0,Z).");
//            out.flush();
//            out.close();

            // 获取SWI-Prolog的输入输出流
            BufferedReader prologInput = new BufferedReader(new InputStreamReader(prologProcess.getInputStream()));
            BufferedReader prologError = new BufferedReader(new InputStreamReader(prologProcess.getErrorStream()));
            PrintWriter prologOutput = new PrintWriter(new OutputStreamWriter(prologProcess.getOutputStream()));

            System.out.println("tttt");
            // 从SWI-Prolog的输出中读取欢迎消息或其他信息
//            String welcomeMessage = prologInput.readLine();
//            System.out.println("yyyy");
//            System.out.println(welcomeMessage);

            // 向SWI-Prolog提问并获取结果
            String query = "likes(john, X).";
            String result = askProlog(prologInput, prologOutput, query);
            System.out.println(result);

            // 根据结果继续提问
            if (result.startsWith("yes")) {
                // 解析结果，提取变量的值
                String[] parts = result.split("\\(");
                if (parts.length > 1) {
                    String variable = parts[1].substring(0, parts[1].indexOf(")"));
                    System.out.println("The value of X is: " + variable);
                } else {
                    System.out.println("Unable to extract variable value.");
                }
            } else {
                System.out.println("No more results.");
            }

            // 关闭与SWI-Prolog的连接
            prologOutput.println("halt.");
            prologOutput.flush();
            prologOutput.close();
            prologInput.close();
            prologError.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String askProlog(BufferedReader input, PrintWriter output, String query) throws IOException {
        // 向SWI-Prolog发送查询
        output.println(query);
        output.flush();
        System.out.println("yyyy");

        // 读取SWI-Prolog的响应
        List<String> response = new ArrayList<>();
        String line;
        while ((line = input.readLine()) != null) {
            response.add(line);
            System.out.println(line);
            if (line.equals("yes.") || line.equals("no."))
                break;
        }
        System.out.println("uuuu");

        // 将响应转换为字符串
        StringBuilder result = new StringBuilder();
        for (String responseLine : response) {
            result.append(responseLine).append("\n");
        }
        return result.toString();
    }
}

