(defproject cvb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]

                 [prismatic/schema "0.4.3"]
                 [prismatic/plumbing "0.4.4"]
                 [me.raynes/fs "1.4.6"]
                 [org.apache.commons/commons-compress "1.9"]
                 [org.tukaani/xz "1.5"]

                 [aysylu/loom "0.5.0"]

                 [edu.stanford.nlp/stanford-corenlp "3.5.2"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.2" :classifier "models"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.2" :classifier "sources"]]
  :main cvb.core)
