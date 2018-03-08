# coding:utf-8
import os
import re
from nltk.stem import PorterStemmer
import sys
import time
import math
reload(sys)
sys.setdefaultencoding("utf-8")

_STOP_WORDS = frozenset([
    'a', 'about', 'above', 'above', 'across', 'after', 'afterwards', 'again',
    'against', 'all', 'almost', 'alone', 'along', 'already', 'also', 'although',
    'always', 'am', 'among', 'amongst', 'amoungst', 'amount', 'an', 'and', 'another',
    'any', 'anyhow', 'anyone', 'anything', 'anyway', 'anywhere', 'are', 'around', 'as',
    'at', 'back', 'be', 'became', 'because', 'become', 'becomes', 'becoming', 'been',
    'before', 'beforehand', 'behind', 'being', 'below', 'beside', 'besides',
    'between', 'beyond', 'bill', 'both', 'bottom', 'but', 'by', 'call', 'can',
    'cannot', 'cant', 'co', 'con', 'could', 'couldnt', 'cry', 'de', 'describe',
    'detail', 'do', 'done', 'down', 'due', 'during', 'each', 'eg', 'eight',
    'either', 'eleven', 'else', 'elsewhere', 'empty', 'enough', 'etc', 'even',
    'ever', 'every', 'everyone', 'everything', 'everywhere', 'except', 'few',
    'fifteen', 'fify', 'fill', 'find', 'fire', 'first', 'five', 'for', 'former',
    'formerly', 'forty', 'found', 'four', 'from', 'front', 'full', 'further', 'get',
    'give', 'go', 'had', 'has', 'hasnt', 'have', 'he', 'hence', 'her', 'here',
    'hereafter', 'hereby', 'herein', 'hereupon', 'hers', 'herself', 'him',
    'himself', 'his', 'how', 'however', 'hundred', 'ie', 'if', 'in', 'inc',
    'indeed', 'interest', 'into', 'is', 'it', 'its', 'itself', 'keep', 'last',
    'latter', 'latterly', 'least', 'less', 'ltd', 'made', 'many', 'may', 'me',
    'meanwhile', 'might', 'mill', 'mine', 'more', 'moreover', 'most', 'mostly',
    'move', 'much', 'must', 'my', 'myself', 'name', 'namely', 'neither', 'never',
    'nevertheless', 'next', 'nine', 'no', 'nobody', 'none', 'noone', 'nor', 'not',
    'nothing', 'now', 'nowhere', 'of', 'off', 'often', 'on', 'once', 'one', 'only',
    'onto', 'or', 'other', 'others', 'otherwise', 'our', 'ours', 'ourselves', 'out',
    'over', 'own', 'part', 'per', 'perhaps', 'please', 'put', 'rather', 're', 'same',
    'see', 'seem', 'seemed', 'seeming', 'seems', 'serious', 'several', 'she',
    'should', 'show', 'side', 'since', 'sincere', 'six', 'sixty', 'so', 'some',
    'somehow', 'someone', 'something', 'sometime', 'sometimes', 'somewhere',
    'still', 'such', 'system', 'take', 'ten', 'than', 'that', 'the', 'their',
    'them', 'themselves', 'then', 'thence', 'there', 'thereafter', 'thereby',
    'therefore', 'therein', 'thereupon', 'these', 'they', 'thickv', 'thin', 'third',
    'this', 'those', 'though', 'three', 'through', 'throughout', 'thru', 'thus',
    'to', 'together', 'too', 'top', 'toward', 'towards', 'twelve', 'twenty', 'two',
    'un', 'under', 'until', 'up', 'upon', 'us', 'very', 'via', 'was', 'we', 'well',
    'were', 'what', 'whatever', 'when', 'whence', 'whenever', 'where', 'whereafter',
    'whereas', 'whereby', 'wherein', 'whereupon', 'wherever', 'whether', 'which',
    'while', 'whither', 'who', 'whoever', 'whole', 'whom', 'whose', 'why', 'will',
    'with', 'within', 'without', 'would', 'yet', 'you', 'your', 'yours', 'yourself',
    'yourselves', 'the'])


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

def search(inverted, query):
    words = [word for _, (offset, word) in word_index(query) if word in inverted]  # query_words_list
    results = [set(inverted[word].keys()) for word in words]
    # x = map(lambda old: old+1, x) 
    doc_set = reduce(lambda x, y: x & y, results) if results else []
    precise_doc_dic = {}
    if doc_set:
        for doc in doc_set:
            index_list = [[indoff[0] for indoff in inverted[word][doc]] for word in words]
            offset_list = [[indoff[1] for indoff in inverted[word][doc]] for word in words]

            precise_doc_dic = precise(precise_doc_dic, doc, index_list, offset_list, 1)  # 词组查询
            precise_doc_dic = precise(precise_doc_dic, doc, index_list, offset_list, 2)  # 临近查询
            precise_doc_dic = precise(precise_doc_dic, doc, index_list, offset_list, 3)  # 临近查询

        return precise_doc_dic
    else:
        return {}


def precise(precise_doc_dic, doc, index_list, offset_list, range):
    if precise_doc_dic:
        if range != 1:
            return precise_doc_dic  # 如果已找到词组,不需再进行临近查询
    phrase_index = reduce(lambda x, y: set(map(lambda old: old + range, x)) & set(y), index_list)
    phrase_index = map(lambda x: x - len(index_list) - range + 2, phrase_index)

    if len(phrase_index):
        phrase_offset = []
        for po in phrase_index:
            phrase_offset.append(offset_list[0][index_list[0].index(po)])  # offset_list[0]代表第一个单词的字母偏移list
        precise_doc_dic[doc] = phrase_offset
    return precise_doc_dic

def write_to_file(result,N):
    output=open('index.xml','w')
    try:
        output.truncate()
        print "Writing to file..."
        output.write("<?xml version='1.0' encoding='ISO-8859-1'?>"+"\n")
        output.write("<!-- index: contains all tokens -->"+"\n")
        output.write("<!-- w: stand for single token -->"+"\n")
        output.write("<!-- n: name of the token -->"+"\n")
        output.write("<!-- ds: stand for documents that contain the token, one token can exists in lots of documents -->"+"\n")
        output.write("<!-- d: stand for one single document that contains this token-->"+"\n")
        output.write("<!-- dn: one single document name-->"+"\n")
        output.write("<!-- tf-idf: tf-idf -->"+"\n")
        output.write("<!-- p: position -->"+"\n")
        output.write("<!-- wp: word position -->"+"\n")
        output.write("<!-- lp: letter position-->"+"\n")
        output.write("<index>"+"\n")
        
       
        for (word,element) in result:
            output.write("<w>"+"\n")
            output.write("<n>"+word+"</n>"+"\n")
            output.write("<ds>"+"\n")
            
            #i = len(element)
            
            df = idf(N,len(element))
            
            for file in element:
                output.write("<d>"+"\n")
                output.write("<dn>"+file+"</dn>"+"\n")
                
                output.write("<tf-idf>"+str(tf(len(element[file]))*df)+"</tf-idf>"+"\n")
                
                for position in element[file]:
                    output.write("<p>"+"\n")
                    output.write("<wp>"+str(position[0])+"</wp>"+"\n")
                    output.write("<lp>"+str(position[1])+"</lp>"+"\n")
                    output.write("</p>"+"\n")
                output.write("</d>"+"\n")
            output.write("</ds>"+"\n")
            output.write("</w>"+"\n")
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
    
    root_dir = 'documents_test'
    #root_dir = '/Users/snorlax/Downloads/WEBPAGES_CLEAN'
    
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
            print str(count) + " documents have been processed, " + str(elapsed) + "s for these 400 documents"
            start = time.clock()
            
    print "Finish all files, " + "overall time: " + str(time.clock()-begin) + "s"
    #
    # Print Inverted-Index
    #for word, doc_locations in inverted.iteritems():
        #print word, doc_locations

    # Search something and print results
    result=sorted(inverted.items(),key=lambda k:k[0])
    write_to_file(result,N)
'''
    queries = ['mondego','machine learning','software engineering','security','student affairs','graduate courses']
    for query in queries:
        result_docs = search(inverted, query)
        print "Search for '%s': %s" % (query, u','.join(result_docs.keys()))  # %s是str()输出字符串%r是repr()输出对象
        def extract_text(doc, index):
            return documents[doc].decode('utf-8')[index:index + 30].replace('\n', ' ')
        if result_docs:
            for doc, offsets in result_docs.items():
                for offset in offsets:
                    print '   - %s...' % extract_text(doc, offset)
        else:
            print 'Nothing found!'

        print
'''