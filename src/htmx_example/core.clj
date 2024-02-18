(ns htmx-example.core
  (:require [htmx-example.server :as server])
  (:gen-class))

(defn -main
  [& args]
  (println "Starting server")
  (server/start))
