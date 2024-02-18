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

(defn contact-handler
  [{{{:keys [contact-id]} :path} :parameters}]
  (let [contact (contact/get contact-id)]
    (if (nil? contact)
      (not-found)
      (success (str (h/html (views/contact-page contact)))))))

(defn contacts-delete-handler
  [{{{:keys [contact-id]} :path} :parameters}]
  (let [contact (contact/get contact-id)]
    (if (nil? contact)
      (not-found)
      (do
        (contact/delete contact-id)
        (success nil)))))

(defn contacts-edit-handler
  [{{{:keys [contact-id]} :path} :parameters}]
  (let [contact (contact/get contact-id)]
    (if (nil? contact)
      (not-found)
      (success (str (h/html (views/contact-row-edit contact)))))))

(defn contacts-update-handler
  [{{{:keys [contact-id]} :path
     {:keys [last first phone email]} :form}
    :parameters}]
  (let [contact (contact/get contact-id)]
    (if (nil? contact)
      (not-found)
      (try
        (contact/update {:id contact-id :first first :last last :email email :phone phone})
        (success
         (str (h/html (views/contact-row (contact/get contact-id)))))
        (catch Exception e
          (println e)
          {:status 500})))))

(defn contacts-create-handler
  [{{{:keys [last_name first_name phone email]} :form} :parameters}]
  (try
    (contact/add {:first first_name :last last_name :email email :phone phone})
    (redirect "/contacts")
    (catch Exception e
      (println e)
      (redirect "/contact"))))

