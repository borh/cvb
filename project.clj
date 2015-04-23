(defproject cvb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-beta1"]

                 ;;[clojure-opennlp "0.3.3"]

                 [t6/snippets "0.1.0-SNAPSHOT"]
                 [t6/snippets-corenlp "0.1.0-SNAPSHOT"]
                 [t6/snippets-clearnlp "0.1.0-SNAPSHOT"]

                 ;;[edu.stanford.nlp/stanford-corenlp "3.5.1"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.1" :classifier "sources"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.1" :classifier "models"]
                 [cc.artifice/loom "0.1.3"]

                 [prismatic/schema "0.4.0"]]
  :main cvb.core)
