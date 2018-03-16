# -*- coding: utf-8 -*-

from google import google


word = "machine learning"
search_result = google.search(word+" site:ics.uci.edu", 1)
for GoogleResult in search_result:
    print GoogleResult.link[7:-1]
    