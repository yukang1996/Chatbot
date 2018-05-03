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
        //System.out.println(this.Emotion(sentence));
        //ML m = new ML();
        //m.Training_set(sentence);
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
            ArrayList keyWords = this.RemoveStopWords(lemSentence);
            reply = PredictReply(al,keyWords);
            
        }
        else if(type.equalsIgnoreCase("calculator")){
            System.out.println("Calculator");
            reply = String.valueOf(Calculator(sentence));
            
            
        }
        else{
            System.out.println("Statement / Exclamation");
            this.emotion = this.emotionPrediction(array);
            String lemSentence =this.Lemmatize(sentence);
            System.out.println("1231321321321321321");
            System.out.println(lemSentence);
            System.out.println(this.emotion);
            al.add(sentence);
            al.add(lemSentence);
            PrintWriter pw = new PrintWriter(new FileWriter("CBMemory.txt",true));
            pw.println(sentence);
            pw.println(lemSentence);
            pw.flush();
            
            pw.close();
            
            reply = "Thank you!";
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
        String []stopWords = {"a","about","above","across","after","afterwards","again","against","all","almost","alone","along","already","also",
            "although","always","am","among","amongst","amoungst","amount","an","and","another","any","anyhow","anyone","anything","anyway",
            "anywhere","are","around","as","at","back","be","became","because","become","becomes","becoming","been","before","beforehand","behind",
            "being","below","beside","besides","between","beyond","bill","both","bottom","but","by","call","can","cannot","cant","co","computer",
            "con","could","couldnt","cry","de","describe","detail","do","done","down","due","during","each","eg","eight","either","eleven","else",
            "elsewhere","empty","enough","etc","even","ever","every","everyone","everything","everywhere","except","few","fifteen","fify","fill",
            "find","fire","first","five","for","former","formerly","forty","found","four","from","front","full","further","get","give","go","had",
            "has","hasnt","have","he","hence","her","here","hereafter","hereby","herein","hereupon","hers","herse","him","himse","his","how",
            "however","hundred","i","ie","if","in","inc","indeed","interest","into","is","it","its","itse","keep","last","latter","latterly",
             "least","less","ltd","made","many","may","me","meanwhile","might","mill","mine","more","moreover","most","mostly","move","much","must",
             "my","myse","namely","neither","never","nevertheless","next","nine","nobody","none","noone","nor","nothing","now",
            "nowhere","of","off","often","on","once","one","only","onto","or","other","others","otherwise","our","ours","ourselves","out","over","own",
            "part","per","perhaps","please","put","rather","re","same","see","seem","seemed","seeming","seems","serious","several","she","should","show",
            "side","since","sincere","six","sixty","so","some","somehow","someone","something","sometime","sometimes","somewhere","still","such","system",
            "take","ten","than","that","the","their","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","thereupon",
            "these","they","thick","third","this","those","though","three","through","throughout","thru","thus","to","together","too","top","toward",
            "towards","twelve","twenty","two","un","under","until","up","upon","us","very","via","was","we","well","were","what","whatever","when",
            "whence","whenever","where","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who",
            "whoever","whole","whom","whose","why","will","with","within","without","would","yet","you","your","yours","yourself","yourselves"};
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
    private String PredictReply(ArrayList al,ArrayList keyWords) {
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
        double idf = log(total_sentence+1/ total_sentence_t);
        System.out.println(idf);
        double max = -1;
        int holder = 0;
        for (int i = 1; i < array.length; i+=2) {
            tf[i] = word_freq_t[i] / total_word_sentence[i];
            tf[i] = tf[i] * (idf+1);
            if(tf[i] > max){
                holder = i-1;
                max = tf[i-1];
            }
        }
        
       
        
        
        for (int i = 0; i < array.length; i+=2) {
            System.out.println(temp[i]+" = "+tf[i+1]);    
        }
        
        return temp[holder];
    }

    
    public String changeInU(String reply){
        String array[] = reply.split(" ");
        String Iwords[] = {"i","me","my","mine"};
        String Uwords[] = {"you","you","your","yours"};
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
    
    public void PredictPOS() throws FileNotFoundException{
        
        CFG cfg = new CFG(this.sentence);
        
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
        
    }
        
    
    
    
    
    
    
    




