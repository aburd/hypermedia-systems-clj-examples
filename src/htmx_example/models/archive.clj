(ns htmx-example.models.archive
  (:require [babashka.process :refer [shell process]]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.core.async :refer [thread go <!]]))

(def archive-files (atom {}))

(defn reset-archive-file!
  [key]
  (swap! archive-files assoc key {:out-file-path nil
                                  :archive-file-path nil
                                  :dir-to-archive-path nil
                                  :status :waiting}))

(defn- archive-dir
  "archive a dir and stream the output"
  [{:keys [out-file-path archive-file-path dir-to-archive-path]}]
  (-> (shell
       {:out :write
        :out-file (io/file out-file-path)}
       "tar" "-czvf" archive-file-path dir-to-archive-path)))

(defn- file-count
  [dir]
  (-> (process "tree" "-a" dir "--noreport")
      (process {:out :string} "wc -l")
      deref
      :out
      s/trim
      Integer/parseInt))

(defn- file-line-count
  [path]
  (count (s/split-lines (slurp path))))

(defn progress-percent
  [name]
  (let [archive-file (get @archive-files name)]
    (when (nil? archive-file)
      (throw (Exception. (str "unknown archive-file: " name))))
    (* 100.0
       (/
        (file-line-count (:out-file-path archive-file))
        (file-count (:dir-to-archive-path archive-file))))))

(defn- run-archive!
  [key archive-file-path out-file-path dir-to-archive-path]
  (do
    (swap! archive-files assoc-in [key :archive-file-path] archive-file-path)
    (swap! archive-files assoc-in [key :out-file-path] out-file-path)
    (swap! archive-files assoc-in [key :dir-to-archive-path] dir-to-archive-path)
    (let [c (thread
              (archive-dir (get @archive-files key)))]
      (go
        (<! c)
        (swap! archive-files assoc-in [key :status] :done)))
    (swap! archive-files assoc-in [key :status] :running)))

(defn get-archive-file
  "get a java File for the given key"
  [key]
  (let [archive-file-info (get @archive-files key)]
    (when (nil? archive-file-info)
      (throw (Exception. (format "%s is an unknown archive" key))))
    (let [{:keys [status archive-file-path]} archive-file-info]
      (when (not= status :done)
        (throw (Exception. (format "%s is not ready, status is %s" key status))))
      (when (not (.exists (io/file archive-file-path)))
        (throw (Exception. (format "archive for \"%s\" is somehow corrpted" key))))
      (io/file archive-file-path))))

(defn get-archive-files
  []
  (keys @archive-files))

(defn get-status
  "get status for the given key"
  [archive-id]
  (let [archive-file-info (get @archive-files archive-id)]
    (when (nil? archive-file-info)
      (throw (Exception. (format "%s is an unknown archive" key))))
    (:status archive-file-info)))

(defn get-output
  "get output for the given key"
  [key]
  (let [archive-file-info (get @archive-files key)]
    (when (nil? archive-file-info)
      (throw (Exception. (format "%s is an unknown archive" key))))
    (slurp (:out-file-path archive-file-info))))

(defn create-archive!
  "take some name for a archive and a path to a directory and then create an archive with the contents of they directory"
  [out-name dir-to-archive-path]
  (when (not (.exists (io/file dir-to-archive-path)))
    (throw (Exception. (format "cannot archive %s, which does not exist" dir-to-archive-path))))
  (reset-archive-file! out-name)
  (let [archive-file-path (format "resources/downloads/%s.tar.gz" out-name)
        out-file-path (format "/tmp/%s-out.txt" out-name)
        f (io/file archive-file-path)]
    (when (.exists f)
      (.delete f))
    (run-archive! out-name archive-file-path out-file-path dir-to-archive-path)))
