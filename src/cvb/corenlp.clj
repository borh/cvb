(ns cvb.corenlp
  (:require
    [loom.graph :as lg]
    [loom.attr :as la]
    [clojure.set :as set]
    [schema.core :as s])
  (:import
    [edu.stanford.nlp.pipeline StanfordCoreNLP Annotation]
    #_[java.io StringReader]
    #_[edu.stanford.nlp.process
     DocumentPreprocessor
     PTBTokenizer]
    #_[edu.stanford.nlp.ling
     Word]
    #_[edu.stanford.nlp.tagger.maxent
     MaxentTagger]
    #_[edu.stanford.nlp.trees
     LabeledScoredTreeNode
     PennTreebankLanguagePack
     LabeledScoredTreeReaderFactory]
    #_[edu.stanford.nlp.parser.lexparser
     LexicalizedParser]
    #_[java.util ArrayList Properties]
    [java.util Properties]
    [edu.stanford.nlp.ling
     CoreAnnotations$SentencesAnnotation
     CoreAnnotations$TokensAnnotation
     CoreAnnotations$TextAnnotation
     CoreAnnotations$PartOfSpeechAnnotation
     CoreAnnotations$NamedEntityTagAnnotation
     CoreAnnotations$TokensAnnotation
     CoreAnnotations$TextAnnotation
     CoreAnnotations$PartOfSpeechAnnotation
     CoreAnnotations$NamedEntityTagAnnotation
     CoreAnnotations$LemmaAnnotation]))

(comment
    ;// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
    ;Properties props = new Properties();
    ;props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
    ;StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    ;
    ;// read some text in the text variable
    ;String text = ... // Add your text here!
    ;
    ;// create an empty Annotation just with the given text
    ;Annotation document = new Annotation(text);
    ;
    ;// run all Annotators on this text
    ;pipeline.annotate(document);
    ;
    ;// these are all the sentences in this document
    ;// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
    ;List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    ;
    ;for(CoreMap sentence: sentences) {
    ;  // traversing the words in the current sentence
    ;  // a CoreLabel is a CoreMap with additional token-specific methods
    ;  for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
    ;    // this is the text of the token
    ;    String word = token.get(TextAnnotation.class);
    ;    // this is the POS tag of the token
    ;    String pos = token.get(PartOfSpeechAnnotation.class);
    ;    // this is the NER label of the token
    ;    String ne = token.get(NamedEntityTagAnnotation.class);
    ;  }
    ;
    ;  // this is the parse tree of the current sentence
    ;  Tree tree = sentence.get(TreeAnnotation.class);
    ;
    ;  // this is the Stanford dependency graph of the current sentence
    ;  SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
    ;}
    ;
    ;// This is the coreference link graph
    ;// Each chain stores a set of mentions that link to each other,
    ;// along with a method for getting the most representative mention
    ;// Both sentence and token offsets start at 1!
    ;Map<Integer, CorefChain> graph =
    ;  document.get(CorefChainAnnotation.class);
  )

(s/defrecord Token
  [word  :- s/Str
   lemma :- s/Str
   pos   :- s/Str
   ne    :- s/Str])

(s/defn create-pipeline :- StanfordCoreNLP
  [options :- (s/maybe s/Str)]
  (let [props (Properties.)]
    (.setProperty props
                  "annotators"
                  (if options
                    options
                    "tokenize, ssplit, pos, lemma, ner, parse, dcoref"))
    (StanfordCoreNLP. props)))

(s/defn process :- [[Token]]
  [pipeline :- StanfordCoreNLP
   text :- s/Str]
  (let [document (Annotation. text)]
    (.annotate pipeline document)
    (let [sentences (.get document CoreAnnotations$SentencesAnnotation)]
      (for [sentence sentences]
        (for [token (.get sentence CoreAnnotations$TokensAnnotation)]
          (Token.
            (.get token CoreAnnotations$TextAnnotation)
            (.get token CoreAnnotations$LemmaAnnotation)
            (.get token CoreAnnotations$PartOfSpeechAnnotation) ;; http://www.comp.leeds.ac.uk/amalgam/tagsets/upenn.html
            (.get token CoreAnnotations$NamedEntityTagAnnotation)))))))

(comment
  (use 'criterium.core)
  (let [pipeline (create-pipeline nil)]
    (bench (process pipeline "This is a book. There are many like it, but this one is mine. It is about President Barack Obama."))))
