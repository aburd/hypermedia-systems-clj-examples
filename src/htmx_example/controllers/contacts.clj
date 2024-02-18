(ns htmx-example.controllers.contacts
  (:require [htmx-example.models.contact :as contact]
            [htmx-example.response :refer [success]]
            [htmx-example.views.contacts :refer [contacts-page]]
            [hiccup2.core :as h]))

(defn get-contacts-handler
  [{{{:keys [search]} :query} :parameters}]
  (let [contacts (contact/search search)
        html (contacts-page contacts search)]
    (success
     (str (h/html html)))))
