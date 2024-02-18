(ns htmx-example.controllers.contacts
  (:require [htmx-example.models.contact :as contact]
            [htmx-example.response :refer [success redirect]]
            [htmx-example.views.contacts :refer [contacts-page contacts-new-page]]
            [hiccup2.core :as h]))

(defn contacts-handler
  [{{{:keys [search]} :query} :parameters}]
  (let [contacts (contact/search search)
        html (contacts-page contacts search)]
    (success
     (str (h/html html)))))

(defn contacts-new-handler
  [_req]
  (let [html (contacts-new-page)]
    (success (str (h/html html)))))

(defn contacts-create-handler
  [{{{:keys [last_name first_name phone email]} :form} :parameters}]
  (try
    (contact/add {:first first_name :last last_name :email email :phone phone})
    (catch Exception e
      (println e)
      (redirect "/contacts/new"))))

