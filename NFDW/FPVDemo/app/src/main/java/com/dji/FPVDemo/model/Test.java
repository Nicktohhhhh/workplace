package com.dji.FPVDemo.model;

/**
 * Created by xiaoT on 2017/2/10.
 */

public class Test {
    private String _id;
    private String time;
    private String dist1;
    private String dist2;
    private String place;
    private String sign;
    private String shortest_dis;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDist2() {
        return dist2;
    }

    public void setDist2(String dist2) {
        this.dist2 = dist2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDist1() {
        return dist1;
    }

    public void setDist1(String dist1) {
        this.dist1 = dist1;
    }

    public String getSign(){return sign;}

    public void setSign(String sign){ this.sign=sign;}

    public String getShortest_dis(){return shortest_dis;}

    public void setShortest_dis(String shortest_dis){this.shortest_dis=shortest_dis;}

}
