#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Thu Mar 15 21:07:11 2018

@author: snorlax
"""

from google import google
import math
import os


def googleSearch(word):
    result = []   
    search_result = google.search(word+" site:ics.uci.edu", 5)
    tsv = open("bookkeeping.tsv").read()
    for GoogleResult in search_result:
        str = GoogleResult.link
        if "https" in str:
            str = str[8:]
        else:
            str = str[7:]
        if str[-1] == '/':
            
            str = str[:-1]
            
            
        if str in tsv:
            
            result.append(str)
    
    return result
def parse(search_dir):
    words =[]
    urls = {}
    for (root, dirs, files) in os.walk(search_dir):
        for filename in files:
            if filename != ".DS_Store":
                words.append(filename)              
                f = open(root +'/'+ filename)
                url = []
                for line in f:
                    string = line.strip('\n').split(":")[1]
                    if string[-1] == '/':
                        string = string[:-1]
                    url.append(string)
                urls[filename] = url
    return words,urls

def NDCG(url,dict):
    length = len(dict)
    #print length
    result = []
    ideal = 0.0
    
    sum = 0.0
    value = 0.0
    for i in range(len(url)):
        if i == 0:
            if dict.has_key(url[i]):
                           
                sum += dict[url[i]]
                ideal += length
                value = sum/ideal
            else:
                ideal += length
        elif dict.has_key(url[i]):
            sum += dict[url[i]]/math.log(i+1,2)
            ideal += (length-i)/math.log(i+1,2)
            value = sum/ideal
        else:
            ideal += (length-i)/math.log(i+1,2)
            value = sum/ideal
        #print str(sum)+","+str(ideal)
        print "%.2f " % value,
        result.append(value)
    return result

def getrelavance(search_results):
    result = {}
    length = len(search_results)
    for i in range(length):
        result[search_results[i]] = length - i      
    return result

    
    
if __name__ == '__main__':
    search_dir = "search_output"
    words,urls = parse(search_dir)  
    rr = [0.0,0.0,0.0,0.0,0.0]
    for word in words:
        print word +": ",
        search_results = googleSearch(word)
        
        dict = getrelavance(search_results)
        
        
        result= NDCG(urls[word],dict)
        print
        for i in range(len(result)):
            rr[i] += result[i]
        #break
    for i in range(len(result)):
        rr[i]/=10
    print "NDCG: ",
    for x in rr:
        print "%.2f " % x,