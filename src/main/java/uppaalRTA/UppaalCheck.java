package uppaalRTA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class UppaalCheck {

    public static void execBat(){
        Runtime rt = Runtime.getRuntime();
        StringBuilder sb =new StringBuilder();
        sb.append("cmd.exe /c start .\\uppaalRun.bat");
        Process process = null;
        try{
            process = rt.exec(sb.toString());
            process.waitFor();
        }catch (Exception e){
            e.printStackTrace();
        }
        int i = process.exitValue();
        if (i == 0) {
            System.out.println("执行完成.");
        } else {
            System.out.println("执行失败.");
        }
        process.destroy();
        process = null;
        UppaalCheck.killProcess();
    }

    private static void killProcess() {
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        try {
            rt.exec("cmd.exe /C start wmic process where name='cmd.exe' call terminate");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSatisFied(){
        try{
            execBat();
            Thread.sleep(1000);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("uppaalNta.txt")));
            String str = null;
            while ((str=bufferedReader.readLine())!=null){
                if(str.indexOf("NOT")!=-1){
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
