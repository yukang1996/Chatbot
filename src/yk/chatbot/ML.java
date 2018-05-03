/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yk.chatbot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author User
 */
public class ML {
    BackEnd be = new BackEnd();
    public void Training_set(String sentence){
        
        String happy_s[] = new String[6];
        happy_s[0] = "i am happy";
        happy_s[1] = "i am not sad";
        happy_s[2] = "i am not angry";
        happy_s[3] = "she is pretty";
        happy_s[4] = "she is awfully pretty";
        happy_s[5] = "she is pretty";
        happy_s[6] = "she is happy";
        
//        happy_s[0] = "i am happy because i have a girlfriend";
//        happy_s[1] = "i could hear the children happy laughter in the other room.";
//        happy_s[2] = "eating delicious food makes me feel joy";
//        happy_s[3] = "she is awfully pretty";
//        happy_s[4] = "the dog is playing with the cat";
//        happy_s[5] = "she is pretty";
//        happy_s[6] = "you are sexy";
//        happy_s[7] = "you are handsome thin and tall";
//        happy_s[8] = "you are fucking amazing";
//        happy_s[9] = "you have girlfriend";
//        happy_s[10] = "he is not fat";
//        happy_s[11] = "he is not ugly";
//        happy_s[12] = "he is good";
//        happy_s[13] = "he is not bad";
//        happy_s[14] = "the cat is cute";
//        happy_s[15] = "a good person";
//        happy_s[16] = "a cute girl";
//        happy_s[17] = "a kind person";
        
        
        
        String sad_s[] = new String[4];
        sad_s[0] = "i am sad";
        sad_s[1] = "i am not happy";
        sad_s[2] = "she is not pretty";
        sad_s[3] = "she is pretty awful";
//        sad_s[0] = "i am sad because i loss my girlfriend";
//        sad_s[1] = "the man pass away.";
//        sad_s[2] = "i do not have enough money";
//        sad_s[3] = "she is pretty awful";
//        sad_s[4] = "the dog is biting the cat";
//        sad_s[5] = "you are ugly fat useless and noob";
//        sad_s[6] = "the ugly duck cannot swim";
//        sad_s[7] = "you do not have girlfriend";
//        sad_s[8] = "i have no girlfriend";
//        sad_s[9] = "he is not good";
//        sad_s[10] = "he is bad";
//        sad_s[11] = "a bad person";
//        sad_s[12] = "a stingy person";
        
        String angry_s[] = new String[5];
        angry_s[0] = "i am angry because my girlfriend cheat on me";
        angry_s[1] = "fuck you.";
        angry_s[2] = "the angry man scold and punish his son for not doing his homework";
        angry_s[3] = "please go away";
        angry_s[4] = "bitch fuck you";

        HashMap<String,Integer> h_dict = new HashMap<>();
        HashMap<String,Integer> s_dict = new HashMap<>();
        HashMap<String,Integer> a_dict = new HashMap<>();
        
        
        
        for (int i = 0; i < happy_s.length; i++) {
            String happy_s2 = this.removeStopWordstoString(happy_s[i]);
            String words[] = happy_s2.split(" ");
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            for (int j = 0; j < words.length; j++) {
                System.out.println(words[j]);
            }
            for (String word: words){
                Integer f = h_dict.get(word);
                if(f == null){
                    h_dict.put(word, 1);
                }
                else{
                    h_dict.put(word, f+1);
                }
            }
        }
        System.out.println("h: "+h_dict);
        
        for (int i = 0; i < sad_s.length; i++) {
            String sad_s2 = this.removeStopWordstoString(sad_s[i]);
            String words[] = sad_s2.split(" ");
            for (String word: words){
                Integer f = s_dict.get(word);
                if(f == null){
                    s_dict.put(word, 1);
                }
                else{
                    s_dict.put(word, f+1);
                }
            }
        }
        System.out.println("s: "+s_dict);
        
        for (int i = 0; i < angry_s.length; i++) {
            String angry_s2 = this.removeStopWordstoString(angry_s[i]);
            String words[] = angry_s2.split(" ");
            for (String word: words){
                Integer f = a_dict.get(word);
                if(f == null){
                    a_dict.put(word, 1);
                }
                else{
                    a_dict.put(word, f+1);
                }
            }
        }
        System.out.println("a: "+a_dict);
        
//        HashMap<String,Integer> corpus = new HashMap<>();
//        for(String word: h_dict.keySet()){
//            Integer f = s_dict.get(word);
//            if(f == null){
//                corpus.put(word, h_dict.get(word));
//            }
//            else{
//                corpus.put(word, h_dict.get(word)+f);
//            }
//        }
//        for(String word : s_dict.keySet()){
//            if(corpus.get(word) == null){
//                corpus.put(word, s_dict.get(word));
//            }
//        }
//        
//        for(String word:a_dict.keySet()){
//            Integer f = corpus.get(word);
//            if(f == null){
//                corpus.put(word, a_dict.get(word));
//            }
//            else{
//                corpus.put(word, a_dict.get(word)+f);
//            }
//        }
//        System.out.println("hsize:"+h_dict.size());
//        System.out.println("ssize:"+s_dict.size());
//        System.out.println("asize:"+a_dict.size());
//        System.out.println(corpus);
        int h = 0;
        int s = 0;
        int a = 0;
        sentence = sentence.toLowerCase();
        String sente[] = sentence.split(" ");
        for (int i = 0; i < sente.length; i++) {
            System.out.println(sente[i]);
            if(h_dict.containsKey(sente[i]) == true){
//                h += h_dict.get(sente[i]);
                  h++;
            }
            if(s_dict.containsKey(sente[i]) == true){
//                s += s_dict.get(sente[i]);
                s++;
            }
            if(a_dict.containsKey(sente[i]) == true){
//                a += a_dict.get(sente[i]);
                  a++;
            }
        }
        int max = 0;
        String maxV = "neutral";
        
        
        int N = 2;
        ArrayList sadList = new ArrayList();
        ArrayList happyList = new ArrayList();
        ArrayList angryList = new ArrayList();
        for (int i = 0; i < sad_s.length; i++) {
            String sad_s2 = this.removeStopWordstoString(sad_s[i]);
            generateNgrams(N,sad_s2,sadList);
            }
        for (int i = 0; i < happy_s.length; i++) {
            String happy_s2 = this.removeStopWordstoString(happy_s[i]);
            generateNgrams(N,happy_s2,happyList);
            }
        for (int i = 0; i < angry_s.length; i++) {
            String angry_s2 = this.removeStopWordstoString(angry_s[i]);
            generateNgrams(N,angry_s2,angryList);
            }
        System.out.println("hList: "+happyList);
        System.out.println("sList: "+sadList);
        System.out.println("aList: "+angryList);
        
        String total_sent = this.removeStopWordstoString(sentence);
        String sentence2 = total_sent.replaceAll(" ", "");
            for (int j = 0; j < happyList.size(); j++) {
                System.out.println(happyList.get(j));
                if(sentence2.contains((CharSequence) happyList.get(j))){
                    h+=10;
                    System.out.println("10happy");
                }
            }
            for (int j = 0; j < sadList.size(); j++) {
                
                System.out.println(sadList.get(j));
                if(sentence2.contains((CharSequence) sadList.get(j))){
                    s+=10;
                    System.out.println("10sad");
                }
            }
            for (int j = 0; j < angryList.size(); j++) {
                if(sentence2.contains((CharSequence)angryList.get(j))){
                    a+=10;
                    System.out.println("10angry");
                }
            }
        
        System.out.println("h2: "+h);
        System.out.println("s2: "+s);
        System.out.println("a2: "+a);
        if(h > max){
            max = h;
            maxV = "happy";
        }
        if(s > max){
            max = s;
            maxV = "sad";
        }
        if(a > max){
            max = a;
            maxV = "angry";
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("Emotion: "+maxV);
        
        
    }
    
    public void generateNgrams(int N,String sent,ArrayList sadList){
        String tokens[] = sent.split("\\s+");
        System.out.println(tokens.length);
        //generate n-grams
        for (int i = 0; i < (tokens.length-N+1); i++) {
            String s = "";
            int start = i;
            int end = i+N;
            for(int j = start;j<end;j++){
                s = s+""+tokens[j];
            }
            sadList.add(s);
        }
    }

    public String removeStopWordstoString(String sentence){
        ArrayList notStopWords = new ArrayList();
        String []stopWords = {"a","an","the","he","she","it","i","you","we","they"};
        String array[] = sentence.split(" ");
        
        for (int i = 0; i < array.length; i++) {
            boolean stopW = false;
            for (int j = 0; j < stopWords.length; j++) {
                if(array[i].equalsIgnoreCase(stopWords[j])){
                    stopW = true;
                    break;
                }
                
            }
            if (stopW == false) {
                notStopWords.add(array[i]);
            }
            
        }
        System.out.println(notStopWords.toString());
        ArrayList sentence2 = notStopWords;
        String sentence3 = "";
        for (int i = 0; i < sentence2.size(); i++) {
            sentence3 += sentence2.get(i)+" ";
        }
        return sentence3;
    }
    
    
   
    
}
