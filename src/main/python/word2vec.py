import gensim.models.word2vec as word2vec
import gensim.models.word2vec.LineSentence as line_sentence

filepath = "../superSmallRandomWalks.txt"

#line_sentence = Simple format: one sentence = one line; words already preprocessed and separated by whitespace.
sentences = line_sentence(filepath)
model = word2vec(sentences, size=100, window=5, min_count=5, workers=4)
# To save the current model:
# model.save(fname)
# To load an existing model:
# model = Word2Vec.load(fname)