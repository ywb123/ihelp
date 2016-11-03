/*package com.slide.testslide1;

import java.util.ArrayList;

public class SplitChineseCharacter {

}
class Split {
    private String[] dictionary = {"����","����","����","ǰ��",
    		                       "��","��","��","��","��",
    		                       "����","������","������","����һ","���ڶ�","������","������","������","������","��ĩ",
    		                       "����","����","�ѹ�","����","����","����","���",
    		                       "����","�϶�","�ϰ�","����","����","ʳ��","ͼ���","����","��һ","����",
    		                       "ȡ���","����","�Զ���","УҽԺ","����",};  //�ʵ�
    private String input = null;	
    public Split(String input) {	
        this.input = input;
    }

    public ArrayList<String> start() {
        String temp = null;
        ArrayList<String> key=new  ArrayList<String>();
        
        for(int i=0;i<this.input.length();i++) {
            temp = this.input.substring(i);  // ÿ�δ��ַ������ײ���ȡһ���֣����浽temp��
//          System.out.println("*****" + temp + "*********" + this.input);
            // ����ô����ֵ��У� ��ɾ���ôʲ���ԭʼ�ַ����н�ȡ�ô�
            if(this.isInDictionary(temp)) {
                //System.out.println(temp);
            	key.add(temp);
                this.input = this.input.replace(temp, "");
                i = -1;  // i=-1����ΪҪ���²��ң� ��Ҫ��ִ��ѭ���е�i++
            }
        }
         
        // ��ǰѭ����ϣ��ʵ�ĩβ��ȥһ���֣�����ѭ���� ֱ���ʱ�Ϊ��
        if(null != this.input && !"".equals(this.input)) {
            this.input = this.input.substring(0,this.input.length()-1);
            this.start();
        }
		return key;
    }	   
    //�жϵ�ǰ���Ƿ����ֵ���
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
    		                       "������","������","����һ","���ڶ�","������","������","������","������","��ĩ",
    		                       "У��","У�ſ�","��Է","��Է","�ߺ�","�Ļ���","������","������",
    		                       "����","�϶�","�ϰ�","����","С����","����","ʳ��","ͼ���","����","��һ","�̶�","����","����","����",
    		                       "ȡ���","����","�Զ���","УҽԺ","����",
    		                       };  //�ʵ�
    private String input = null;	
    public Split(String input) {	
        this.input = input;
    }

    public ArrayList<String> start() {
        String temp = null;
        ArrayList<String> key=new  ArrayList<String>();
        
        for(int i=0;i<this.input.length();i++) {
            temp = this.input.substring(i);  // ÿ�δ��ַ������ײ���ȡһ���֣����浽temp��
//          System.out.println("*****" + temp + "*********" + this.input);
            // ����ô����ֵ��У� ��ɾ���ôʲ���ԭʼ�ַ����н�ȡ�ô�
            if(this.isInDictionary(temp)) {
                //System.out.println(temp);
            	key.add(temp);
                this.input = this.input.replace(temp, "");
                i = -1;  // i=-1����ΪҪ���²��ң� ��Ҫ��ִ��ѭ���е�i++
            }
        }
         
        // ��ǰѭ����ϣ��ʵ�ĩβ��ȥһ���֣�����ѭ���� ֱ���ʱ�Ϊ��
        if(null != this.input && !"".equals(this.input)) {
            this.input = this.input.substring(0,this.input.length()-1);
            this.start();
        }
		return key;
    }	   
    //�жϵ�ǰ���Ƿ����ֵ���
    public boolean isInDictionary(String temp) {
        for(int i=0;i<this.dictionary.length;i++) {
       if(temp.equals(this.dictionary[i])) {
                return true;
            }
        }
        return false;
    }
}