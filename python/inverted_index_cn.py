# coding:utf-8
import os
import re
from nltk.stem import PorterStemmer
import sys
import time
import math
reload(sys)
sys.setdefaultencoding("utf-8")

_STOP_WORDS = frozenset(["a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"])


def word_split(text):
    #print text
    word_list = []
    token = []
    ps=PorterStemmer()
    
    token += re.sub("[^A-Za-z0-9]", " ", text).split()
    #print token
    
    time = {}
    ind = 0
    for i,c in enumerate(token):

        if c in time:  # record appear time
            time[c] += 1
        else:
            time[c] = 1
            
        ind = text.index(c, ind)
        word_list.append((len(word_list), (ind, ps.stem(c.lower()))))  # include normalize
        ind += 1
    #print word_list
    return word_list


def words_cleanup(words):
    cleaned_words = []
    for index, (offset, word) in words:  # words-(word index for search,(letter offset for display,word))
        if word in _STOP_WORDS:
            continue
        
        cleaned_words.append((index, (offset, word)))
    return cleaned_words


def word_index(text):
    words = word_split(text)
    words = words_cleanup(words)
    return words


def inverted_index(text):
    inverted = {}

    for index, (offset, word) in word_index(text):
        locations = inverted.setdefault(word, [])
        locations.append((index, offset))
    #print locations
    return inverted


def inverted_index_add(inverted, doc_id, doc_index):
    for word, locations in doc_index.iteritems():
        indices = inverted.setdefault(word, {})
        indices[doc_id] = locations
    return inverted

def tf(numOfp):
    return 1+math.log10(numOfp)
    
    
def idf(N,leng):
    return math.log10(N/float(leng))

def write_to_file(result,N):
    output=open('index.xml','w')
    try:
        output.truncate()
        print "Writing to file..."
        output.write("<?xml version='1.0' encoding='ISO-8859-1'?>")
        output.write("<!-- index: contains all tokens -->")
        output.write("<!-- w: stand for single token -->")
        output.write("<!-- n: name of the token -->")
        output.write("<!-- ds: stand for documents that contain the token, one token can exists in lots of documents -->")
        output.write("<!-- d: stand for one single document that contains this token-->")
        output.write("<!-- dn: one single document name-->")
        output.write("<!-- tf-idf: tf-idf -->")
        output.write("<!-- p: position -->")
        output.write("<!-- wp: word position -->")
        output.write("<!-- lp: letter position-->")
        output.write("<index>")
        
       
        for (word,element) in result:
            output.write("<w>")
            output.write("<n>"+word+"</n>")
            output.write("<ds>")
            
            #i = len(element)
            
            df = idf(N,len(element))
            
            for file in element:
                output.write("<d>")
                output.write("<dn>"+file+"</dn>")
                
                output.write("<tf-idf>"+str(tf(len(element[file]))*df)+"</tf-idf>")
                
                for position in element[file]:
                    output.write("<p>")
                    output.write("<wp>"+str(position[0])+"</wp>")
                    output.write("<lp>"+str(position[1])+"</lp>")
                    output.write("</p>")
                output.write("</d>")
            output.write("</ds>")
            output.write("</w>")
                #i-=1
                #if i>0:
                   # output1.write(",")
        output.write("</index>")
        
        
        
    finally:
        output.close()
        print "Finish writing!"
        
        
if __name__ == '__main__':
    
    # Build Inverted-Index for documents
    inverted = {}
    
    #root_dir = 'documents_test'
    root_dir = '/Users/snorlax/Downloads/WEBPAGES_CLEAN'
    
    documents = {}
    
    for (root, dirs, files) in os.walk(root_dir):
        for filename in files:
            if filename != ".DS_Store" and filename != "bookkeeping.json" and filename != "bookkeeping.tsv":
                #print filename
                f = open(root +'/'+ filename).read()
                documents.setdefault((root[len(root_dir)+1:] +'/'+ filename), f)
    N = len(documents)
    count = 0
    begin = time.clock()
    start = time.clock()
    #print len(documents)
    for doc_id, text in documents.iteritems():
        count += 1
        doc_index = inverted_index(text)
        inverted_index_add(inverted, doc_id, doc_index)
        if count%400 == 0:
            elapsed = (time.clock() - start)
            print str(count) + "/"+str(N) +" documents have been processed, " + str(elapsed) + "s for these 400 documents"
            start = time.clock()
            
    print "Finish all "+str(N)+" files, " + "overall time: " + str(time.clock()-begin) + "s"
    
    # Print Inverted-Index
    #for word, doc_locations in inverted.iteritems():
        #print word, doc_locations

   
    result=sorted(inverted.items(),key=lambda k:k[0])
    write_to_file(result,N)
