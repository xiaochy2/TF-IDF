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
    search_result = google.search(word+" site:ics.uci.edu", 10)
    for GoogleResult in search_result:
        print GoogleResult.link[7:-1]
        result.append(GoogleResult.link[7:-1])
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
                    url.append(line.split(":")[1])
                urls[filename] = url
    return words,urls

def DCG(url,dict):
    sum = 0
    for i in range(len(url)):
        if dict.has_key(url[i]):
            if i == 0:               
                sum += dict[url[i]]
            else:
                sum += dict[url[i]]/math.log(i+1)
    return sum

def getrelavance(search_results):
    result = {}
    length = len(search_results)
    for i in range(length):
        result[search_results[i]] = length - i      
    return result

    
    
if __name__ == '__main__':
    search_dir = "search_output"
    words,urls = parse(search_dir)  
    NDCG_avg = 0.0
    for word in words:
        search_results = googleSearch(word)
        dict = getrelavance(search_results)
        search_DCG = DCG(urls[word],dict)
        NDCG = search_DCG/DCG(googleSearch(word),dict)
        print NDCG
        NDCG_avg += NDCG
    NDCG_avg/=10
    print NDCG_avg