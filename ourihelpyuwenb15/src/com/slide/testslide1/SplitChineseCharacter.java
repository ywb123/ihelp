/*package com.slide.testslide1;

import java.util.ArrayList;

public class SplitChineseCharacter {

}
class Split {
    private String[] dictionary = {"今天","明天","后天","前天",
    		                       "你","我","他","她","是",
    		                       "星期","星期六","星期日","星期一","星期二","星期三","星期四","星期五","星期六","周末",
    		                       "开心","伤心","难过","悲伤","忧郁","高兴","疯狂",
    		                       "南五","南二","南八","南三","超市","食堂","图书馆","教三","教一","宿舍",
    		                       "取快递","带饭","吃东西","校医院","借书",};  //词典
    private String input = null;	
    public Split(String input) {	
        this.input = input;
    }

    public ArrayList<String> start() {
        String temp = null;
        ArrayList<String> key=new  ArrayList<String>();
        
        for(int i=0;i<this.input.length();i++) {
            temp = this.input.substring(i);  // 每次从字符串的首部截取一个字，并存到temp中
//          System.out.println("*****" + temp + "*********" + this.input);
            // 如果该词在字典中， 则删除该词并在原始字符串中截取该词
            if(this.isInDictionary(temp)) {
                //System.out.println(temp);
            	key.add(temp);
                this.input = this.input.replace(temp, "");
                i = -1;  // i=-1是因为要重新查找， 而要先执行循环中的i++
            }
        }
         
        // 当前循环完毕，词的末尾截去一个字，继续循环， 直到词变为空
        if(null != this.input && !"".equals(this.input)) {
            this.input = this.input.substring(0,this.input.length()-1);
            this.start();
        }
		return key;
    }	   
    //判断当前词是否在字典中
    public boolean isInDictionary(String temp) {
        for(int i=0;i<this.dictionary.length;i++) {
       if(temp.equals(this.dictionary[i])) {
                return true;
            }
        }
        return false;
    }
}*/
package com.slide.testslide1;

import java.util.ArrayList;

public class SplitChineseCharacter {

}
class Split {
    private String[] dictionary = {
    		                       "星期六","星期日","星期一","星期二","星期三","星期四","星期五","星期六","周末",
    		                       "校门","校门口","南苑","北苑","沁湖","文化街","东澜岸","体育馆",
    		                       "南五","南二","南八","南三","小超市","大超市","食堂","图书馆","教四","教一","教二","教三","教五","教六",
    		                       "取快递","带饭","吃东西","校医院","借书",
    		                       };  //词典
    private String input = null;	
    public Split(String input) {	
        this.input = input;
    }

    public ArrayList<String> start() {
        String temp = null;
        ArrayList<String> key=new  ArrayList<String>();
        
        for(int i=0;i<this.input.length();i++) {
            temp = this.input.substring(i);  // 每次从字符串的首部截取一个字，并存到temp中
//          System.out.println("*****" + temp + "*********" + this.input);
            // 如果该词在字典中， 则删除该词并在原始字符串中截取该词
            if(this.isInDictionary(temp)) {
                //System.out.println(temp);
            	key.add(temp);
                this.input = this.input.replace(temp, "");
                i = -1;  // i=-1是因为要重新查找， 而要先执行循环中的i++
            }
        }
         
        // 当前循环完毕，词的末尾截去一个字，继续循环， 直到词变为空
        if(null != this.input && !"".equals(this.input)) {
            this.input = this.input.substring(0,this.input.length()-1);
            this.start();
        }
		return key;
    }	   
    //判断当前词是否在字典中
    public boolean isInDictionary(String temp) {
        for(int i=0;i<this.dictionary.length;i++) {
       if(temp.equals(this.dictionary[i])) {
                return true;
            }
        }
        return false;
    }
}