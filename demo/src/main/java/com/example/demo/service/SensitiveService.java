package com.example.demo.service;

import com.example.demo.controller.QuestionController;
import org.apache.commons.lang.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {//？？InitializingBean是个初始化bean，
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    private TrieNode rootNode=new TrieNode();

    @Override
    public void afterPropertiesSet() throws Exception {//初始化SensitiveWords.txt文件
        try {
            InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader=new InputStreamReader(is);
            BufferedReader bufferedReader=new BufferedReader(reader);
            String lineText;
            while ((lineText=bufferedReader.readLine())!=null){
                addWord(lineText.trim());
            }
            reader.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败"+e.getMessage());
        }
    }

    public String filter(String text){
        if(StringUtils.isEmpty(text)){
            return text;
        }
        StringBuilder result=new StringBuilder();//最终收集的字符串

        String replacement="***";
        //三个指针
        TrieNode tempNode=rootNode;
        int begin=0;
        int position=0;

        while (position<text.length()){
            char c=text.charAt(position);
            if(isSymbol(c)){
                if (tempNode==rootNode){
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;

            }
            tempNode=tempNode.getSubNode(c);
            if(tempNode==null){//如果当前没有找到敏感词，说明以begin下标开头的肯定不是一个敏感词
                result.append(text.charAt(begin));//所有把最先的以begin开头的字符加入result
                position=begin+1;//position指向begin下一个
                begin=position;//begin指向position
                //position和begin相当于同时到了刚刚那个begin的下一个位置
                tempNode=rootNode;//当前树上的指针结点还原根节点
            }else if(tempNode.isKeyWordEnd()==true){//找到敏感词，并且是以position位置为止的敏感词结尾，即完整的一个敏感词
                result.append(replacement);//发现敏感词，把replacement替换掉
                position=position+1;//position到下一位置
                begin=position;//begin到position的位置
                //此时的position和begin 到了整个敏感词的后一个位置
                tempNode=rootNode;//树上指针还原根节点
            }else {//找到敏感词，但没有发现end=true，说明还没有结束，不知道是不是一个完整的敏感词，如敏感词我草你妈，此时只找到了我草，还不知道后面是不是你妈，所以要继续往后找
                position=position+1;
            }
        }
        result.append(text.substring(begin));//到最后的时候都没发现，把begin到尾到加入result
        return  result.toString();

    }


    // 建树，增加关键词，敏感词
    private void addWord(String lineText){//lineText是放进去的敏感词
        TrieNode tempNode=rootNode;
        for(int i=0;i<lineText.length();++i){
            Character c=lineText.charAt(i);
            if (isSymbol(c))//如果敏感词词汇里是色 情，加进来的还是色情，就把空格过滤掉了
                continue;
            TrieNode node=tempNode.getSubNode(c);
            if (node==null){
                node=new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode=node;
            if (i==lineText.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    //定义非法词，即不是东亚文字都是非法词，如空格之类的，这样“色 情”中间有空格的话，也会过滤掉
    private boolean isSymbol(char c){
        int ic=(int)c;
        //0x2E90,0x9FFF 东亚文字的范围
        return !CharUtils.isAsciiAlphanumeric(c)&&(ic<0x2E90||ic>0x9FFF);//如果不是东亚文字 都会被过滤掉
    }
    private class TrieNode{
        private boolean end=false;//是否是关键词的结尾
        private Map<Character,TrieNode> subNodes=new HashMap<Character,TrieNode>();//当前结点下所有的子节点
        public void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }
        TrieNode getSubNode(Character key){
            return  subNodes.get(key);
        }
        boolean isKeyWordEnd(){
            return end;
        }
        void setKeyWordEnd(boolean end){
            this.end=end;
        }
    }
    public static void main(String args[]){
        SensitiveService s=new SensitiveService();
        s.addWord("色情");
        s.addWord("赌博");
        System.out.println(s.filter("hi  色@@@情哈哈赌 博哈赌色"));
        System.out.println(s.filter("色sadasdasdsadas情"));
        //sout,快捷键
    }
}
