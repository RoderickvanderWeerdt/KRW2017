import word2vec as w2v
import tsv_reader as tsvr
import svm

filepath_random_walks = "../../../../superSmallRandomWalks.txt"
filepath_movies_train = "../../../../mc-movies.pruned.train.tsv"
filepath_movies_test = "../../../../mc-movies.pruned.test.tsv"

def prepare_for_svm(tsv_file):
    tsv_reader = tsvr.tsvReader(filepath_movies_train)
    words, labels = tsv_reader.retrieve_words_and_labels()
    word2vec_creator = w2v.word2vec(filepath_random_walks, words, labels)
    word_vectors, words, labels = word2vec_creator.create_wordvectors()
    return word_vectors, words, labels

def train_setup(filepath_train):
    word_vectors, words, labels = prepare_for_svm(filepath_train)
    classifier = svm.svmClassifier(word_vectors, labels)
    classifier.create_svm_model()

def test_setup(filepath_test):
    word_vectors, words, labels = prepare_for_svm(filepath_test)
    classifier = svm.svmClassifier(word_vectors, labels)
    classifier.validate_svm_model()

def main():
    train_setup(filepath_movies_train)
    test_setup(filepath_movies_test)

if __name__ == "__main__": main()