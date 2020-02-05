package rtaUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import rta.Location;
import rta.RTA;
import rta.TimeGuard;
import rta.Transition;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RTABuilder {

    public static RTA getRTAFromJsonFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String str = null;
        StringBuilder json = new StringBuilder();
        while ((str = reader.readLine()) != null){
            json.append(str);
        }
        return getRTAFromJson(json.toString());
    }

    public static RTA getRTAFromJson(String json){
        JSONObject jsonObject = JSON.parseObject(json);
        String name = jsonObject.getString("name");

        JSONArray jsonArray = jsonObject.getJSONArray("sigma");
        Set<String> sigma = new HashSet<>();
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()){
            sigma.add((String)iterator.next());
        }

        List<Location> locationList = new ArrayList<>();
        JSONArray locationArray = jsonObject.getJSONArray("l");
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < locationArray.size(); i ++){
            list.add(locationArray.getIntValue(i));
        }
        JSONArray acceptArray = jsonObject.getJSONArray("accept");
        Set<Integer> set = new HashSet<>();
        for(int i = 0; i < acceptArray.size(); i ++){
            set.add(acceptArray.getIntValue(i));
        }
        int initId = jsonObject.getInteger("init");
        for(int id:list){
            Location location = new Location(id);
            location.setName(""+id);
            if(set.contains(id)){
                location.setAccept(true);
            }else {
                location.setAccept(false);
            }
            if(id == initId){
                location.setInit(true);
            }else {
                location.setInit(false);
            }
            locationList.add(location);
        }

        Map<Integer, Location> map = new HashMap<>();
        for(Location l: locationList){
            map.put(l.getId(),l);
        }

        JSONObject tranJsonObject = jsonObject.getJSONObject("tran");

        int size = tranJsonObject.size();
        List<Transition> transitionList = new ArrayList<>();
        for(int i = 0; i < size; i++){
            JSONArray array = tranJsonObject.getJSONArray(String.valueOf(i));
            int sourceId = array.getInteger(0);
            Location sourceLocation =  map.get(sourceId);
            String action = array.getString(1);
            TimeGuard timeGuard = new TimeGuard(array.getString(2));
            int targetId = array.getInteger(3);
            Location targetLocation = map.get(targetId);
            Transition transition = new Transition(sourceLocation,targetLocation,timeGuard,action);
            transitionList.add(transition);
        }

        RTA rta =  new RTA(name,sigma,locationList,transitionList);

        return RTABuilder.completeRTA(rta);
    }


    public static RTA completeRTA(RTA rta){
        Set<String> sigma = rta.getSigma();
        List<Location> oldLocationList = rta.getLocationList();
        List<Transition> oldTransitionList = rta.getTransitionList();
        Location sinkLocation = new Location(oldLocationList.size()+1,"sink",false,false);
        List<Location> locationList = new ArrayList<>();
        List<Transition> transitionList = new ArrayList<>();
        locationList.addAll(oldLocationList);
        locationList.add(sinkLocation);
        transitionList.addAll(oldTransitionList);
        for(String action:sigma){
            TimeGuard timeGuard = new TimeGuard(false,true,0, TimeGuard.MAX_TIME);
            Transition transition = new Transition(sinkLocation,sinkLocation,timeGuard,action);
            transitionList.add(transition);
        }
        for(Location l:oldLocationList){
            for(String action:sigma){
                List<Transition> transitionList1 = rta.getTransitions(l,action,null);
                TimeGuard timeGuard = new TimeGuard(false,true,0,0);
                Transition pre = new Transition(sinkLocation,sinkLocation,timeGuard,action);
                for(int i = 0; i < transitionList1.size(); i++){
                    Transition current = transitionList1.get(i);
                    TimeGuard preTimeGuard = pre.getTimeGuard();
                    TimeGuard currentTimeGuard = current.getTimeGuard();
                    boolean var0 = preTimeGuard.getRight() ==0 && currentTimeGuard.getLeft()==0;
                    boolean var1 = currentTimeGuard.getLeft()==preTimeGuard.getRight();
                    boolean var2 = currentTimeGuard.isLeftOpen();
                    boolean var3 = !preTimeGuard.isRightOpen();
                    if(var0 || (var1 && var2 && var3)){
                        pre = current;
                    }else {
                        boolean leftO = !preTimeGuard.isRightOpen();
                        boolean rightO = !currentTimeGuard.isLeftOpen();
                        int left = preTimeGuard.getRight();
                        int right = currentTimeGuard.getLeft();
                        TimeGuard timeGuard1 = new TimeGuard(leftO,rightO,left,right);
                        Transition t = new Transition(l,sinkLocation,timeGuard1,action);
                        transitionList.add(t);
                        pre = current;
                    }
                }
                TimeGuard preTimeGuard = pre.getTimeGuard();
                if(preTimeGuard.getRight()!= TimeGuard.MAX_TIME){
                    boolean leftO = !preTimeGuard.isRightOpen();
                    boolean rightO = false;
                    int left = preTimeGuard.getRight();
                    int right = TimeGuard.MAX_TIME;
                    TimeGuard timeGuard1 = new TimeGuard(leftO,rightO,left,right);
                    Transition t = new Transition(l,sinkLocation,timeGuard1,action);
                    transitionList.add(t);
                }
            }
        }
        RTA rta1 =    new RTA(rta.getName(),sigma,locationList,transitionList);
        List<Transition> toSinkTransitionList = rta1.getTransitions(null,null,sinkLocation);
        if(toSinkTransitionList.size() == sigma.size()){
            return rta;
        }
        return rta1;
    }


}
