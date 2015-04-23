(ns cvb.core
  (:require [schema.core :as s]
            [cvb.corenlp :as c]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs])
  (:import [java.io File]
           [cvb.corenlp FullToken]
           [edu.stanford.nlp.pipeline StanfordCoreNLP]
           [org.apache.commons.compress.compressors.xz XZCompressorInputStream]))

(s/set-fn-validation! true)

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
  [t]
  (Token. (:lemma t) (:pos t)))

(s/defn make-document :- Document
  [f :- File]
  (with-open
    [xz-in (-> f
               io/input-stream
               XZCompressorInputStream.)]
    (edn/read-string (slurp xz-in))))

(s/defn file->frequencies :- {Token s/Int}
  [pipeline :- StanfordCoreNLP
   doc :- Document]
  (->> (:text doc)
       (c/process pipeline)
       (flatten)
       (filter (fn [{:keys [ne]}] (if (= ne "O") true)))
       (map ->minimal-token)
       (frequencies)))

(s/defn corpus->frequencies :- {Token s/Int}
  [corpus-dir :- s/Str]
  (let [pipeline (c/create-pipeline nil)]
    (->> corpus-dir
         io/file
         file-seq
         (filter #(= ".xz" (fs/extension %)))
         (map make-document)                                ;; TODO parallel execution
         (pmap (partial file->frequencies pipeline))
         (apply merge-with +))))

(comment
  (use 'criterium.core)
  (corpus->frequencies "corpus/")
  ;; time/document: ~9s single-thread, 6s with pmap (for 2 docs)
  (file->frequencies (make-document (io/file "corpus/topic-1-1.edn"))))