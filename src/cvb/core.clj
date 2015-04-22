(ns cvb.core
  (:require [t6.snippets.core :as snippets]
            t6.snippets.nlp.corenlp
            t6.snippets.nlp.clearnlp

            [schema.core :as s]))

(s/set-fn-validation! true)

(comment
  (def get-sentences (make-sentence-detector "models/en-sent.bin"))
  (def tokenize (make-tokenizer "models/en-token.bin"))
  (def pos-tag (make-pos-tagger "models/en-pos-maxent.bin"))
  (def name-find (make-name-finder "models/en-ner-person.bin"))
  ;; FIXME en-ner-date.bin  en-ner-location.bin  en-ner-money.bin  en-ner-organization.bin  en-ner-percentage.bin
  ;;(def location-find (make-location-finder "models/en-ner-location.bin"))
  )

;; TODO

(s/defrecord Token
  [surface :- s/Str
   pos :- s/Str])

;;

;; TODO following should lemmatize tokens; consider frequencies as another step in pipeline

(comment
  (s/defn text->frequencies-1 :- {[s/Str] s/Int}
    [text :- s/Str]
    (let [tokens (mapcat tokenize (get-sentences text))
          exclude-set (set (concat (name-find tokens)
                                   #_(mapcat location-find sentences)))]
      (->> tokens
           pos-tag
           (remove (fn [[k _]] (exclude-set k)))
           frequencies))))

(s/defn text->frequencies-2 :- {s/Str s/Int}
  [text :- s/Str]
  (->> (snippets/create {:pipeline {:type #_:clearnlp :corenlp}, :text text})
       :tokens
       (mapcat (partial map :token))
       frequencies))

(comment
  (use 'criterium.core)
  (bench (text->frequencies-1 "This is a book. There are many like it, but this one is mine."))
  (bench (text->frequencies-2 "This is a book. There are many like it, but this one is mine.")))