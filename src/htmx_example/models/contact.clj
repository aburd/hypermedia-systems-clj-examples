(ns htmx-example.models.contact
  (:require
   [faker.name :as name]
   [faker.phone-number :as phone]
   [faker.internet :as internet]))

(defn generate-contact []
  {:id (rand-int 1000) :first (name/first-name) :last (name/last-name) :phone (first (phone/phone-numbers)) :email (internet/email)})

(def contacts
  (take
   10
   (repeatedly
    #(generate-contact))))

(defn search
  [search]
  (vec (if (or (nil? search) (empty? search))
         contacts
         (filter #(or (= search (:first %)) (= search (:last %))) contacts))))
