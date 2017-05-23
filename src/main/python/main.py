import word2vec as w2v

filepath = "../../../../superSmallRandomWalks.txt"
word2vec_creator = w2v.word2vec(filepath)
word_vectors = word2vec_creator.create_wordvectors()