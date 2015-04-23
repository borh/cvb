(defproject cvb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-beta1"]

                 [prismatic/schema "0.4.0"]
                 [me.raynes/fs "1.4.6"]
                 [org.apache.commons/commons-compress "1.9"]
                 [org.tukaani/xz "1.5"]

                 [edu.stanford.nlp/stanford-corenlp "3.5.1"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.1" :classifier "sources"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.1" :classifier "models"]]
  :main cvb.core)
