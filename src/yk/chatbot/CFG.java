/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yk.chatbot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author User
 */
public class CFG {
    HashMap <String,ArrayList<String>>map = new HashMap<>();
    String sentence;
    
    public CFG(String sentence) throws FileNotFoundException{
        this.sentence = sentence;
        ArrayList<String> arrayS = new ArrayList();
        arrayS.add("NP VP");
        map.put("S",arrayS);
        ArrayList<String> arrayNP = new ArrayList();
        arrayNP.add("Det N");
        arrayNP.add("N");
        arrayNP.add("Det Adj N");
        map.put("NP", arrayNP);
        ArrayList<String> arrayVP = new ArrayList();
        arrayVP.add("V");
        arrayVP.add("V NP");
        map.put("VP", arrayVP);
        ArrayList <String>arrayDet = new ArrayList();
        arrayDet.add("a");
        arrayDet.add("an");
        arrayDet.add("the");
        map.put("Det", arrayDet);
        ArrayList <String>arrayN = new ArrayList();
        FileInputStream fisN = new FileInputStream("pos_NOUN.txt");
        Scanner sN = new Scanner(fisN);
        while(sN.hasNextLine()){
            arrayN.add(sN.nextLine());
        }
        map.put("N", arrayN);
        ArrayList <String>arrayV = new ArrayList();
        FileInputStream fisV = new FileInputStream("pos_VERB.txt");
        Scanner sV = new Scanner(fisV);
        while(sV.hasNextLine()){
            arrayV.add(sV.nextLine());
        }
        map.put("V", arrayV);
        System.out.println(map);
        Stack<String> exp = new Stack<>();
        expand(sentence,"S",exp);
        System.out.println(exp.toString());
       
    }
    
    public int expand(String sentence,String start,Stack<String> expansion){
        int trigger = 0;
        System.out.println("############################################");
        if(map.containsKey(start)){
            int done = 0;
            for (int i = 0; i < map.get(start).size(); i++) {
                System.out.println("path: "+i);
                if(trigger == 1){
                    done++;
                    System.out.println("11111111111111");
                    String temp[] = this.sentence.split(" ");
                    this.sentence = "";
                    for (int j = 1; j < temp.length; j++) {
                        this.sentence += temp[j]+" ";
                        
                    }
                    System.out.println("Sentence: "+this.sentence);
                    break;
                }
                else if(trigger == -1){
                            System.out.println("---------1. Skip to next");
                            break;
                        }
                
                trigger = 0;
                String possible = map.get(start).get(i);
                System.out.println("@@ "+possible);
                String term[] = possible.split(" ");
                for (int j = 0; j < term.length; j++) {
                        System.out.println("Term: "+term[j]);
                        
                        trigger = expand(this.sentence,term[j],expansion);
                        System.out.println("aaaaaaaaaaaaaaaaaa");
                        
                    }
                    
                
            }
           
        }
        else{
            String sentenceSplit[] = this.sentence.split(" ");
            System.out.println(sentenceSplit[0]);
            if(sentenceSplit[0].equalsIgnoreCase(start)){
                expansion.push(start);
                System.out.println("exp: "+expansion.toString());
                trigger = 1;
            }
            else{
                trigger = -1;
            }
            
        }
        return trigger;
    }
    
    
}
