(defproject htmx-example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring "1.11.0"]
                 [metosin/reitit "0.7.0-alpha7"]
                 [metosin/muuntaja "0.6.8"]
                 [hiccup "2.0.0-RC3"]
                 [expound "0.9.0"]
                 [faker "0.2.2"]]
  :main ^:skip-aot htmx-example.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev {:dependencies [[com.github.flow-storm/clojure "RELEASE"]
                                  [com.github.flow-storm/flow-storm-dbg "RELEASE"]]
                   :exclusions [org.clojure/clojure] ;; for disabling the official compiler
                   :jvm-opts ["-Dclojure.storm.instrumentEnable=true"
                              "-Dclojure.storm.instrumentOnlyPrefixes=htmx-example.,reitit.ring."]}})
