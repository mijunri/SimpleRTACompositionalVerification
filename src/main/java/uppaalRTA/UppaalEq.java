package uppaalRTA;

import rta.RTA;
import rta.RTAUtil;
import rta.TimeWord;
import rta.TimeWords;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UppaalEq {
    private RTA m1;
    private RTA m2;
    private RTA p;
    public UppaalEq(RTA m1, RTA m2, RTA p) {
        this.m1 = m1;
        this.m2 = m2;
        this.p = p;
    }

    public TimeWords findCounterEa(RTA assume){
        UppaalNTA1 uppaalNTA1 = new UppaalNTA1(assume,m1,p);
        uppaalNTA1.toXml();
        uppaalNTA1.generateMemProperty();
        uppaalNTA1.toBat();
        if(UppaalCheck.isSatisFied()){
            return null;
        }
        else {
            try{
                System.out.println("请给出一个反例：先给出反例长度n，再给出n行输入：");
                BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
                int n = Integer.parseInt(rd.readLine());
                List<TimeWord> words = new ArrayList<>();
                for(int i = 0; i < n ;i++){
                    String[] str = rd.readLine().split(",");
                    TimeWord word = new TimeWord(str[0],Double.parseDouble(str[1]));
                    words.add(word);
                }
                TimeWords timeWords = new TimeWords(words);
                return timeWords;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }



    public TimeWords findCounterExample(RTA assumpthion) {
        RTA completeAssumption = RTAUtil.toCompleteRTA(assumpthion);
        UppaalNTA1 uppaalNTA1 = new UppaalNTA1(m2,completeAssumption);
        uppaalNTA1.toXml();
        uppaalNTA1.generateMemProperty();
        uppaalNTA1.toBat();
        if(UppaalCheck.isSatisFied()){
            System.out.println("找到假设成功");
            System.out.println(assumpthion);
            return null;
        }
        else {
            try{
                System.out.println("请给出一个反例：先给出反例长度n，再给出n行输入：");
                BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
                int n = Integer.parseInt(rd.readLine());
                List<TimeWord> words = new ArrayList<>();
                for(int i = 0; i < n ;i++){
                    String[] str = rd.readLine().split(",");
                    TimeWord word = new TimeWord(str[0],Double.parseDouble(str[1]));
                    words.add(word);
                }
                TimeWords timeWords = new TimeWords(words);
                return timeWords;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
