(ns htmx-example.controllers.archives
  (:require [htmx-example.models.archive :as archive]
            [htmx-example.response :refer [success not-found]]
            [htmx-example.views.archives :as views]
            [hiccup2.core :as h]))

(defn archives-handler
  [_req]
  (let [html (views/archives-page)]
    (success
     (str (h/html html)))))

(defn archive-handler
  [{{{:keys [archive-id]} :path} :parameters}]
  (let [archive (archive/get-archive-file archive-id)]
    (if (nil? archive)
      (not-found)
      (success (str (h/html (views/archive-page archive-id)))))))

(defn archives-create-handler
  [{{{:keys [archive_id dir_path]} :form} :parameters}]
  (archive/create-archive! archive_id dir_path)
  (success
   (str (h/html (views/archive-status archive_id)))))

(defn archive-get-status-handler
  [{{{:keys [archive-id]} :path} :parameters}]
  (success
   (str (h/html (views/archive-status archive-id)))))

(defn archive-download-handler
  [{{{:keys [archive-id]} :path} :parameters}]
  (success
   (archive/get-archive-file archive-id)
   {:mime-type "application/gzip"}))

