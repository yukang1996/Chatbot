/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package yk.chatbot;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author User
 */

public class BackEnd {
    private String sentence;
    private String emotion = "neutral";
    private String[][] lemma;
    private boolean QnegateDetected = false;
    private ArrayList<Integer> SnegateDetected = new ArrayList<>(); 

    
    public ArrayList Initialise() throws FileNotFoundException{
        FileInputStream fis = new FileInputStream("CBMemory.txt");
        Scanner n = new Scanner(fis);
        ArrayList al = new ArrayList();
        while(n.hasNextLine()){
            al.add(n.nextLine());
        }
        this.lemma = this.initLemmatization();
        System.out.println("Ready~~~");
        return al;
    }
    
    public String SetSentence(String sentence){
        this.sentence = sentence;
        return this.sentence;
    }
    
    public String GetSentence(){
        return this.sentence;
    }
    
    public String GetEmotion(){
        return this.emotion;
    }
    
    public String GetSentenceType(String sentence) throws FileNotFoundException{
        String type;
        if(sentence.contains("?")){
            type = "questions";
        }
        else if(sentence.contains("!")){
            type = "exclamations";
        }
        else if(sentence.matches("(^\\d).*") || sentence.matches("(^\\W).*")){
            type = "calculator";
        }
        else{
            type = "statements";
        }
        return type;
    }

    
    public String MakeDecision(String array[][],ArrayList al,String type, String sentence) throws FileNotFoundException, IOException{
        String reply = "";
        if(type.equalsIgnoreCase("questions")){
            //take out action of question
            System.out.println("Question");
            sentence = this.RemoveSymbols(sentence);
            String lemSentence = this.Lemmatize(sentence);
            System.out.println("lemSeeeeeeeeeeee: "+lemSentence);
            this.checkQnegate(sentence);
            ArrayList keyWords = this.RemoveStopWords(lemSentence);
            String wH = checkWH(sentence);
            reply = PredictReply(wH,al,keyWords);
            
        }
        else if(type.equalsIgnoreCase("calculator")){
            System.out.println("Calculator");
            reply = String.valueOf(Calculator(sentence));
            
            
        }
        else{
            System.out.println("Statement / Exclamation");
            this.emotion = this.emotionPrediction(array);
            String lemSentence =this.Lemmatize(sentence);
            System.out.println(lemSentence);
            System.out.println(this.emotion);
            al.add(sentence);
            al.add(lemSentence);
            PrintWriter pw = new PrintWriter(new FileWriter("CBMemory.txt",true));
            pw.println(sentence);
            pw.println(lemSentence);
            pw.flush();
            
            pw.close();
            
            reply = "Noted!";
        }
        reply = this.changeInU(reply);
        return reply;
    }
    
    public String RemoveSymbols(String sentence){
        String []symbols = {".","?","!",";",":"};
        for (int i = 0; i < symbols.length; i++) {
            if(sentence.contains(symbols[i])){
                System.out.println("without symbol: "+symbols[i]);
                sentence = sentence.replace(symbols[i], "");
            }
        }
        System.out.println("Symbols: "+sentence);
        return sentence;
    }
    
    public ArrayList RemoveStopWords(String sentence){
        ArrayList notStopWords = new ArrayList();
        String []stopWords = {"a","an","the","do","does","did"};
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
        return notStopWords;
    }
    //check 1 print 0
    private String PredictReply(String wH,ArrayList al,ArrayList keyWords) throws FileNotFoundException {
        //transfer memory arraylist to normal array for split
        String array[][] = new String[al.size()][];
        String temp [] = new String [al.size()];
        double word_freq_t[] = new double [al.size()];
        double total_word_sentence[] = new double[al.size()];
        double total_sentence = al.size()/2;
        double total_sentence_t = 0;
        for (int i = 1; i < al.size(); i+=2) {
            temp[i-1] = (String)al.get(i-1);
            temp[i] = (String) al.get(i);
            array[i] = temp[i].split(" ");
        }
        //sentence
        for (int i = 1; i < al.size(); i+=2) {
            boolean containTerm = false;
            
            //words
            total_word_sentence[i] = array[i].length;
            for (int j = 0; j < array[i].length; j++) {
                //keywords
                for (int k = 0; k < keyWords.size(); k++) {
                    if(array[i][j].equalsIgnoreCase((String) keyWords.get(k))){
                        word_freq_t[i]++;
                        containTerm = true;
                        break;
                    }
                    
                    else{
                    }
                }
                System.out.println(array[i][j]+" vs ");
                if(array[i][j].equalsIgnoreCase("not") || array[i][j].equalsIgnoreCase("no")){
                        //aaaaaaaaaaaaaaaaaaaaaaaaa
                        this.SnegateDetected.add(i);
                        System.out.println("Not detected in "+array[i]);
                    }
                if(containTerm == true){
                    total_sentence_t++;   
                }
                
            }
            /*for (int k = 0; k < keyWords.size(); k++) {
                    if(containTerm[k] == false){
                        word_freq_t[i] =0;
                    }
                    else{
                    }
                
                }*/
        }
        double tf[] = new double [array.length];
        System.out.println(total_sentence+" / "+total_sentence_t);
        double idf = log(total_sentence/ total_sentence_t+1);
        System.out.println("xxxxx"+idf);
        double max = -1;
        int holder = 0;
        double Notbonus = 0;
        for (int i = 1; i < array.length; i+=2) {
            for (int j = 0; j < this.SnegateDetected.size(); j++) {
                //statement gt not, question gt not +1
                if(i == this.SnegateDetected.get(j) && this.QnegateDetected == true){
                    System.out.println("qnegate: "+this.QnegateDetected);
                    Notbonus = 1;
                }
                //statement go not, question no not -1
                else if(i == this.SnegateDetected.get(j) && this.QnegateDetected == false){
                    System.out.println("qnegate: "+this.QnegateDetected);
                    Notbonus = -1;
                }
                else{
                    Notbonus = 0;
                }
                
            }
            tf[i] = word_freq_t[i] / total_word_sentence[i];
            tf[i] = tf[i] * (idf+1);
            tf[i] += Notbonus;
            if(tf[i] > max){
                holder = i-1;
                max = tf[i];
            }
            
            
           
        }
        
        String finalreply = "";
        ArrayList <Integer>sameArray = new ArrayList<>();
        //threshold ?
        double threshold = 0.1;
        System.out.println(max < threshold);
        if(max == Double.NaN || (max < threshold)){
            System.out.println("sad case");
            finalreply = "Sry, YKChatbot don't understand.";
        }
        else{
            try{
                for (int i = 0; i < array.length; i+=2) {
                    System.out.println(temp[i]+" = "+tf[i+1]);
                    if(tf[i+1] == max && max >0){
                        System.out.println("Me too: "+temp[i]);
                        sameArray.add(i);
                    }
                }
                String final2reply= "";
                for (int i = 0; i < sameArray.size(); i++) {
                    final2reply += BreakComponent(keyWords,wH,temp[sameArray.get(i)],temp[sameArray.get(i)+1]).trim();
                    if(i <sameArray.size()-1){
                        final2reply += " and ";
                    }
                }
                finalreply = final2reply;
               
            }
            catch(Exception e){
                finalreply = "Sry, YKChatbot don't understand.";
            }
            

        }
        return finalreply;
    }

    
    public String changeInU(String reply){
        String array[] = reply.split(" ");
        String Iwords[] = {"me","i","my","mine","am"};
        String Uwords[] = {"you","you","your","yours","are"};
        String changedReply = "";
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < Iwords.length; j++) {
                if(array[i].equalsIgnoreCase(Iwords[j])){
                    array[i] = Uwords[j];
                    break;
                }
                else if(array[i].equalsIgnoreCase(Uwords[j])){
                    array[i] = Iwords[j];
                    break;
                }
            }
            changedReply += array[i]+" ";
        }
        return changedReply;
    }
    
    public String Calculator(String equation) {
        String final_result = "";
        try{
           
            equation = equation.replaceAll(" ", "");
            double result = 0.0;
            String noMinus = equation.replace("-", "+-");
            String[] byPluses = noMinus.split("\\+");

            for (String multipl : byPluses) {
                String[] byMultipl = multipl.split("\\*");
                double multiplResult = 1.0;
                for (String operand : byMultipl) {
                    if (operand.contains("/")) {
                        String[] division = operand.split("\\/");
                        double divident = Double.parseDouble(division[0]);
                        for (int i = 1; i < division.length; i++) {
                            divident /= Double.parseDouble(division[i]);
                        }
                        multiplResult *= divident;
                    } else {
                        multiplResult *= Double.parseDouble(operand);
                    }
                }
                result += multiplResult;
            }
            final_result = String.valueOf(result);
        }
        catch(Exception e){
            final_result = "Sry,YKChatbot is a chatbot, not a Calculator, simple Math like + - x / only";
        }
        return final_result;
    }
    
    public String[][] EmotionInitialise(){
        try{
            FileInputStream fis = new FileInputStream("AFINN-111.txt");
            Scanner n = new Scanner(fis);
            ArrayList al = new ArrayList();
            while(n.hasNextLine()){
                al.add(n.nextLine());
            }
            String array[][] = new String[al.size()][2];
            for (int i = 0; i < al.size(); i++) {
                array[i] =  al.get(i).toString().split("\\t");
            }
            System.out.println("Emotion ready...");
            
            return array;
        }
        catch(Exception e){
            System.out.println("file not found.");
        }
        return null;
    }
    
    public String emotionPrediction(String array[][]){
        boolean negate = false;
        String sent[] = this.sentence.split(" ");
        String emotion = "neutral";
        int score = 0;        
        for (int i = 0; i < sent.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if(sent[i].equalsIgnoreCase(array[j][0])){
                    score += Integer.parseInt(array[j][1]);
                }
                
            }
            if(sent[i].equalsIgnoreCase("not") || sent[i].equalsIgnoreCase("no")){
                negate = true;
                }
        }
        if(negate == true){
            score *= -1;
        }
        System.out.println(score);
        if(score > 0){
            emotion = "positive";
        }
        else if(score < 0){
            emotion = "negative";
        }
        return emotion;
    }
    
    
    
    public String[][] initLemmatization() throws FileNotFoundException{
        System.out.println("lemmmmmmmmmmmmaaaaaaaa");
        FileInputStream fis = new FileInputStream("lemmatization.txt");
        Scanner n = new Scanner(fis);
        ArrayList<String> al = new ArrayList<>();
        while(n.hasNextLine()){
            al.add(n.nextLine());
        }
        String array[][] = new String[al.size()][2];
        for (int i = 0; i < al.size(); i++) {
            array[i] = al.get(i).split("\\s+");
        }
        
        return array;
    }
    
    public String Lemmatize(String sentence){
        System.out.println("test "+sentence);
        String words[] = sentence.split(" ");
        String lemmaWord = "";
        String temp = "";
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < lemma.length; j++) {
                if(words[i].equalsIgnoreCase(lemma[j][1])){
                    temp = lemma[j][0];
                    break;
                }
                else{
                    temp = words[i];
                }
                
            }
            lemmaWord += temp+" ";
        }
        System.out.println("lemma "+lemmaWord);
        return lemmaWord;
    }    

    private String checkWH(String sentence) {
        String wH = "";
        String keyWords[] = sentence.split(" ");
        for (int i = 0; i < keyWords.length; i++) {
            if(keyWords[i].equalsIgnoreCase("what")){
                wH = "what";
                break;
            }
            else if(keyWords[i].equalsIgnoreCase("who")){
                wH = "who";
                break;
            }
            else if(keyWords[i].equalsIgnoreCase("where")){
                wH = "where";
                break;
            }
            else if(keyWords[i].equalsIgnoreCase("why")){
                wH = "why";
                break;
            }
            else{
                wH = "";
            }
        }
        return wH;
    }

    private String BreakComponent(ArrayList keyWords,String wH, String reply,String processReply) throws FileNotFoundException {
        String finalReply = "";
        String who = "";
        String what = "";
        String why = "";
        String where = "";
        System.out.println(processReply);
        System.out.println("Show keyword");
        for (int i = 0; i < keyWords.size(); i++) {
            System.out.println(keyWords.get(i));
        }
        String words[] = processReply.split(" ");
        String Showwords[] = reply.split(" ");
        int verbAtLocation = checkVerb(words);
        System.out.println("index: "+verbAtLocation);
        System.out.println("verb: "+Showwords[verbAtLocation]);
        //what,who,where,why
        for (int i = 0; i < verbAtLocation; i++) {
//            System.out.println("words: "+words[i]);
            //negate
            if(Showwords[i].equalsIgnoreCase("not") || Showwords[i].equalsIgnoreCase("no")){
                System.out.println("Not/No detected.Throwing...");
            }
            else{
                who += Showwords[i]+" ";
            }
            
        }
        for (int i = verbAtLocation; i < Showwords.length; i++) {
//            System.out.println("words: "+words[i]);
            what += Showwords[i]+" ";
        }
        if(wH.equalsIgnoreCase("who")){
            System.out.println("WHooooooo");
            finalReply = who;
            for (int i = 0; i < keyWords.size(); i++) {
                if(finalReply.contains((String) keyWords.get(i))){
                    System.out.println("keywords same, change to wat");
                    wH = "what";
                }
            }
        }
        boolean noWhy = false;
        System.out.println("WHyyyyyyyy");
            String whyWords[] = {" because "," as "," due to "," for "};
            Stack <String>st2 = new Stack<>();
            for (int i = 0; i < whyWords.length; i++) {
                int index = processReply.indexOf(whyWords[i]);
                System.out.println("index yy: "+index );
                
                if(index > -1){
                    String temp[] = processReply.substring(index).split(" ");
                    String yhw[] = reply.split(" ");
                    for (int j = 0; j < temp.length; j++) {
                        System.out.println("temp: "+temp[j]);
                        System.out.println("yhw: "+yhw[j]);
                    }
                    for (int j = yhw.length-1; j > yhw.length-temp.length; j--) {
                        st2.add(yhw[j]);
                        System.out.println(st2.toString());
                    }
                    int tempI = st2.size();
                    for (int j = 0; j < tempI; j++) {
                        System.out.println("why: "+why);
                        why += st2.pop()+" ";
                    }
                    System.out.println(" whyyyyyyyyyyyyyyyyyyyyy found la");
                    noWhy = false;
                    break;
                }
                else{
                    System.out.println(whyWords[i]+" not found");
                    why = "";
                    noWhy = true;
                }
            }
            String whyReply = why;
            
        if(wH.equalsIgnoreCase("why")){
            if(whyReply.equalsIgnoreCase("")){
                whyReply ="Sry, YKChatbot don't understand.";
            }
            finalReply = whyReply;
        }
        
        if(wH.equalsIgnoreCase("where")){
            System.out.println("WHereeeeee");
            String whereDict[] = {"at","in","under","above","from","to","under","behind","on"};
            ArrayList<String> whereList = new ArrayList<>();
            boolean ending = false;
            for (int i = 0; i < keyWords.size(); i++) {
                for (int j = 0; j < whereDict.length; j++) {
                    System.out.println(keyWords.get(i)+" vs "+whereDict[j]);
                    if(keyWords.get(i).equals(whereDict[j])){
                        whereList.add((String) keyWords.get(i));
                        ending = true;
                        break;
                    }
                    
                }
                if(ending == true){
                    break;
                }
            }
            if(ending == false){
                Collections.addAll(whereList,whereDict);
                
            }
            for (int i = 0; i < whereList.size(); i++) {
                    System.out.println("!!! "+whereList.get(i));
            }
            Stack <String>st = new Stack<>();
            for (int i = 0; i < whereList.size(); i++) {
                System.out.println(whereList.get(i));
                int index = processReply.indexOf(whereList.get(i));
                System.out.println("indexxxx: "+index);
                if(index > -1){
                    String temp[] = processReply.substring(index).split(" ");
                    String erehw[] = reply.split(" ");
                    System.out.println("temppppl: "+temp.length);
                    System.out.println("lengtthhhh "+erehw.length);
                    for (int j = erehw.length-1; j >= erehw.length-temp.length ; j--) {
                        st.add(erehw[j]);
                        System.out.println(st.toString());
                    }
                    
                    break;
                }
            }
            int tempI = st.size();
            String wherewhere[] = new String[st.size()];
            System.out.println("tempI: "+tempI);
                for (int j = 0; j < tempI ; j++) {
                    wherewhere[j] = st.pop();
            }
            String whywhy[] = whyReply.split(" ");
            String whereFinal = "";
            System.out.println("why : "+whyReply);
            System.out.println("whywhy length: "+whywhy.length);
            for (int i = 0; i < wherewhere.length; i++) {
                if(whywhy[0].equalsIgnoreCase(wherewhere[i])){
                    break;
                }
                else{
                    System.out.println("fffffffff "+wherewhere[i]);
                    whereFinal += wherewhere[i] +" ";
                }
            }
            finalReply = whereFinal;
        }
             
        
        if(wH.equalsIgnoreCase("what")){
            System.out.println("WHattttttt");
            finalReply = what;
        }
        
            
        System.out.println("final: "+finalReply);
        return finalReply;
    }

    private int checkVerb(String[] words) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(".\\pos_VERB.txt");
        Scanner n = new Scanner(fis);
        String who = "";
        ArrayList<String> al = new ArrayList<>();
        while(n.hasNext()){
            al.add(n.nextLine());
        }
        System.out.println("verb contain: "+al.toString());
        int index = -1;
        wordCount:
            for (int i = 0; i < words.length; i++) {
                for (int j = 0; j < al.size(); j++) {
                    if(words[i].equalsIgnoreCase(al.get(j))){
                        System.out.println("index "+i+" found");
                        index = i;
                        break wordCount;
                        //break
                    }
                }
            }
//        for (int i = 0; i < index; i++) {
//            who += words[i];
//        }
        if(index == -1){
            index = 1;
        }
        return index;
    }

    void ForgetEverything() throws FileNotFoundException {
        this.emotion = "neutral";
        PrintWriter pw = new PrintWriter(".\\CBMemory.txt");
        pw.close();
    }
    
    public void checkQnegate(String sentence){
        boolean toggle = false;
        if(sentence.indexOf("not ")>-1 || sentence.indexOf("no ")>-1){
            toggle = true;
        }
        this.QnegateDetected = toggle;
        
    }
    
    
        
    }
        
    
    
    
    
    
    
    




