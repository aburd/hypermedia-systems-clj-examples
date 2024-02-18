(ns htmx-example.controllers.contacts
  (:require [htmx-example.models.contact :as contact]
            [htmx-example.response :refer [success redirect not-found]]
            [htmx-example.views.contacts :as views]
            [hiccup2.core :as h]))

(defn contacts-handler
  [{{{:keys [search]} :query} :parameters}]
  (let [contacts (contact/search search)
        html (views/contacts-page contacts search)]
    (success
     (str (h/html html)))))

(defn contacts-new-handler
  [_req]
  (let [html (views/contacts-new-page)]
    (success (str (h/html html)))))

(defn contacts-show-handler
  [{{{:keys [contact-id]} :path} :parameters}]
  (let [contact (contact/get contact-id)]
    (if (nil? contact)
      (not-found)
      (success (str (h/html (views/contacts-show-page contact)))))))

(defn contacts-edit-handler
  [{{{:keys [contact-id]} :path} :parameters}]
  (let [contact (contact/get contact-id)]
    (if (nil? contact)
      (not-found)
      (success (str (h/html (views/contacts-edit-page contact)))))))

(defn contacts-create-handler
  [{{{:keys [last_name first_name phone email]} :form} :parameters}]
  (try
    (contact/add {:first first_name :last last_name :email email :phone phone})
    (catch Exception e
      (println e)
      (redirect "/contacts/new"))))

