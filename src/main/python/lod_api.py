import requests
import time
import urllib
import tsv_reader as tsvr

endpoint_subject = 'http://webscale.cc:3001/default?'
filepath_movies_train = "../../../../mc-movies.pruned.train.tsv"

def request_retry(endpoint, params, headers=None, times=3):
    count = 0
    while count < times:
        if headers:
            resp = requests.get(endpoint, params=params, headers=headers)
        else:
            resp = requests.get(endpoint, params=params)
        try:
            resp.raise_for_status()
            return resp
        except:
            times += 1
            time.sleep(1)
    print("Could not connect to Yelp API after 3 tries.")

def main(endpoint, filepath_train):
    tsv_reader = tsvr.tsvReader(filepath_train)
    words, labels = tsv_reader.retrieve_words_and_labels()
    for word in words:
        # No cleaning necessary (will
        params = {'subject': word}
        response = request_retry(endpoint, params=params)
        print(response.content)
        time.sleep(1)

#http://purl.org/dc/elements/1.0/Title
#http%3A//purl.org/dc/elements/1.0/Title

main(endpoint_subject, filepath_movies_train)

request_retry(endpoint=endpoint_subject)