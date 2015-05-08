(ns cvb.core
  (:require [schema.core :as s]
            [plumbing.core :refer [interleave-all]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [cvb.corenlp :as c]
            [loom.graph :as lg]
            [loom.io :as li])
  (:import [java.io File]
           [cvb.corenlp FullToken]
           [edu.stanford.nlp.pipeline StanfordCoreNLP]
           [org.apache.commons.compress.compressors.xz XZCompressorInputStream]))

(s/set-fn-validation! true)

(defonce pipeline (c/create-pipeline nil))

(def opt s/optional-key)
(s/defschema Document
  {:title                   s/Str
   :url                     s/Str ;; TODO java.net.URL
   :text                    s/Str
   :student-summary         s/Str

   (opt :original-title)    s/Str
   (opt :original-url)      s/Str ;; TODO java.net.URL
   (opt :original-abstract) s/Str
   (opt :original-text)     s/Str})

(s/defrecord Token
  [lemma :- s/Str
   pos   :- s/Str])

(defn ->minimal-token
  [{:keys [lemma pos]}]
  (Token. lemma pos))

(s/defn make-document :- Document
  [f :- File]
  (with-open
    [xz-in (-> f
               io/input-stream
               XZCompressorInputStream.)]
    (edn/read-string (slurp xz-in))))

(s/defn read-corpus :- [Document]
  [corpus-dir :- s/Str]
  (let [files (-> corpus-dir io/file file-seq)]
    (sequence
      (comp
        (filter #(= ".xz" (fs/extension %)))
        (map make-document))
      files)))

(s/defn file->token-sentences :- [[FullToken]]
  [pipeline :- StanfordCoreNLP
   doc :- Document]
  (c/process pipeline (:text doc)))

(s/defn tokens->chains
  [tokens :- [Token]]
  (interleave-all (partition 2 tokens) (partition 2 (drop 1 tokens))))

(s/defn token-sentences->graph :- s/Any
  [token-sentences :- [[FullToken]]]
  (->> token-sentences
       (flatten)
       (filter (fn [{:keys [ne]}] (if (= ne "O") true)))
       (map ->minimal-token)
       (tokens->chains)
       (lg/graph)))

(s/defn token-sentences->frequencies :- {Token s/Int}
  [token-sentences :- [[FullToken]]]
  (->> token-sentences
       (flatten)
       (filter (fn [{:keys [ne]}] (if (= ne "O") true)))
       (map ->minimal-token)
       (frequencies)))

(s/defn corpus->graph :- s/Any
  [corpus-dir :- s/Str]
  (let [files (read-corpus corpus-dir)]
    (transduce
      (comp
        (map (partial file->token-sentences pipeline))
        (map token-sentences->graph))
      lg/graph
      files)))

(s/defn corpus->frequencies :- {Token s/Int}
  [corpus-dir :- s/Str]
  (let [;; pipeline (c/create-pipeline nil) ;; FIXME upgrade to component
        files (read-corpus corpus-dir)]
    (transduce
      (comp
        (map (partial file->token-sentences pipeline))
        (map token-sentences->frequencies))
      (partial merge-with +)
      {}
      files)
    #_(->> files
         (filter #(= ".xz" (fs/extension %)))
         (map make-document)                                ;; TODO parallel execution
         (pmap (partial file->frequencies pipeline))
         (apply merge-with +))))

(comment
  (use 'criterium.core)
  (corpus->frequencies "corpus/")
  (corpus->graph "corpus/")
  (li/view (corpus->graph "corpus/"))
  (corpus->frequencies "~/Dropbox/Osaka/Courses/Spring 2015/ESP/corpus/") ;; TODO
  ;; w/parse+dcoref time/document: ~9s single-thread, 6s with pmap (for 2 docs)
  ;; w/o parse+dcoref 180ms/document
  (file->frequencies (make-document (io/file "corpus/topic-1-1.edn"))))