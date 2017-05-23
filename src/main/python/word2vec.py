import gensim.models.word2vec as genw2v

class word2vec:
    def __init__(self, filepath):
        self.filepath = filepath

    def create_wordvectors(self):
        #line_sentence = Simple format: one sentence = one line; words already preprocessed and separated by whitespace.
        sentences = genw2v.LineSentence(self.filepath)
        # To load an existing model:
        # model = Word2Vec.load("../w2v_model")
        model = genw2v.Word2Vec(sentences, size=100, window=5, min_count=1, workers=4)
        # To save the current model:
        # model.save("../w2v_model")
        word_vectors = model.wv
        print(word_vectors)
        return word_vectors